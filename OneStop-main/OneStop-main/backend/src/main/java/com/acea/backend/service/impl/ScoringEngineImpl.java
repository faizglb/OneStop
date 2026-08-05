package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.repository.SectionScoreRepository;
import com.acea.backend.domain.repository.TraitScoreRepository;
import com.acea.backend.dto.assessment.SectionScoreResponse;
import com.acea.backend.dto.assessment.TraitScoreResponse;
import com.acea.backend.service.ScoringEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stateless, pure scoring implementation.
 *
 * <p>Aptitude scoring: correct/wrong per question → marks per section → weighted overall.
 * <p>Personality scoring: Likert weight sum per OCEAN trait → normalized 0-100.
 * <p>Interest scoring: Likert weight sum per RIASEC category → normalized 0-100.
 *
 * <p>NO career recommendation logic is present in this class.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringEngineImpl implements ScoringEngine {

    // ── Thresholds ──────────────────────────────────────────────────────────
    private static final BigDecimal STRENGTH_THRESHOLD  = BigDecimal.valueOf(70); // >= 70% = strength
    private static final BigDecimal WEAKNESS_THRESHOLD  = BigDecimal.valueOf(50); // < 50% = weakness
    private static final BigDecimal HIGH_TRAIT_THRESHOLD = BigDecimal.valueOf(67);
    private static final BigDecimal LOW_TRAIT_THRESHOLD  = BigDecimal.valueOf(33);

    private final SectionScoreRepository sectionScoreRepository;
    private final TraitScoreRepository   traitScoreRepository;

    // ── Aptitude ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public List<SectionScoreResponse> scoreAptitude(AssessmentResult result, List<ResultAnswer> answers) {
        log.info("Scoring aptitude result={}", result.getId());

        // Group answers by category
        Map<QuestionCategory, List<ResultAnswer>> byCategory = answers.stream()
            .collect(Collectors.groupingBy(ra -> ra.getQuestion().getCategory()));

        List<SectionScore> sectionScores = new ArrayList<>();

        for (Map.Entry<QuestionCategory, List<ResultAnswer>> entry : byCategory.entrySet()) {
            QuestionCategory category = entry.getKey();
            List<ResultAnswer> categoryAnswers = entry.getValue();

            BigDecimal rawScore  = BigDecimal.ZERO;
            BigDecimal maxScore  = BigDecimal.ZERO;

            for (ResultAnswer ra : categoryAnswers) {
                Question q     = ra.getQuestion();
                BigDecimal qMax = BigDecimal.valueOf(q.getMarks());
                maxScore = maxScore.add(qMax);

                if (ra.getSelectedOption() != null && ra.getSelectedOption().getIsCorrect()) {
                    rawScore = rawScore.add(qMax);
                }
            }

            BigDecimal pct = maxScore.compareTo(BigDecimal.ZERO) > 0
                ? rawScore.divide(maxScore, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            SectionScore ss = SectionScore.builder()
                .result(result)
                .category(category)
                .categoryName(category.getName())
                .rawScore(rawScore)
                .maxScore(maxScore)
                .percentage(pct)
                .isStrength(pct.compareTo(STRENGTH_THRESHOLD) >= 0)
                .build();

            sectionScores.add(ss);
        }

        // Sort by category display order for consistent ordering
        sectionScores.sort(Comparator.comparing(ss -> ss.getCategory().getDisplayOrder()));
        sectionScoreRepository.saveAll(sectionScores);

        log.debug("Aptitude: saved {} section scores for result={}", sectionScores.size(), result.getId());

        return sectionScores.stream()
            .map(ss -> new SectionScoreResponse(
                ss.getCategory().getId(),
                ss.getCategoryName(),
                ss.getRawScore(),
                ss.getMaxScore(),
                ss.getPercentage(),
                ss.getIsStrength()
            ))
            .toList();
    }

    // ── Personality (Big Five / OCEAN) ────────────────────────────────────────

    @Override
    @Transactional
    public List<TraitScoreResponse> scorePersonality(AssessmentResult result, List<ResultAnswer> answers) {
        log.info("Scoring personality result={}", result.getId());
        return scoreWeightedTraits(result, answers, getPersonalityLabels());
    }

    // ── Interest (Holland RIASEC) ─────────────────────────────────────────────

    @Override
    @Transactional
    public List<TraitScoreResponse> scoreInterest(AssessmentResult result, List<ResultAnswer> answers) {
        log.info("Scoring interest result={}", result.getId());
        return scoreWeightedTraits(result, answers, getRiasecLabels());
    }

    // ── Shared Weighted Likert Logic ──────────────────────────────────────────

    private List<TraitScoreResponse> scoreWeightedTraits(
            AssessmentResult result,
            List<ResultAnswer> answers,
            Map<String, String> traitLabels) {

        // Group by category / trait key
        Map<QuestionCategory, List<ResultAnswer>> byCategory = answers.stream()
            .collect(Collectors.groupingBy(ra -> ra.getQuestion().getCategory()));

        List<TraitScore> traitScores = new ArrayList<>();

        for (Map.Entry<QuestionCategory, List<ResultAnswer>> entry : byCategory.entrySet()) {
            QuestionCategory category = entry.getKey();
            String traitKey = category.getTraitKey();
            List<ResultAnswer> categoryAnswers = entry.getValue();

            BigDecimal rawScore = BigDecimal.ZERO;
            // Max per question = 5 (Likert max), total max = questionCount × 5
            BigDecimal maxScore = BigDecimal.valueOf((long) categoryAnswers.size() * 5);

            for (ResultAnswer ra : categoryAnswers) {
                if (ra.getSelectedOption() != null) {
                    rawScore = rawScore.add(ra.getSelectedOption().getWeight());
                }
            }

            BigDecimal normalized = maxScore.compareTo(BigDecimal.ZERO) > 0
                ? rawScore.divide(maxScore, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            String level = computeLevel(normalized);
            String label = traitLabels.getOrDefault(traitKey, category.getName());

            TraitScore ts = TraitScore.builder()
                .result(result)
                .traitKey(traitKey)
                .traitLabel(label)
                .rawScore(rawScore)
                .maxScore(maxScore)
                .normalizedScore(normalized)
                .level(level)
                .description(buildTraitDescription(traitKey, label, level))
                .build();

            traitScores.add(ts);
        }

        // Sort by normalized score descending
        traitScores.sort(Comparator.comparing(TraitScore::getNormalizedScore).reversed());
        traitScoreRepository.saveAll(traitScores);

        return traitScores.stream()
            .map(ts -> new TraitScoreResponse(
                ts.getTraitKey(),
                ts.getTraitLabel(),
                ts.getNormalizedScore(),
                ts.getLevel(),
                ts.getDescription()
            ))
            .toList();
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String computeLevel(BigDecimal score) {
        if (score.compareTo(HIGH_TRAIT_THRESHOLD) >= 0) return "HIGH";
        if (score.compareTo(LOW_TRAIT_THRESHOLD)  < 0)  return "LOW";
        return "MODERATE";
    }

    private String buildTraitDescription(String traitKey, String label, String level) {
        return switch (traitKey) {
            case "OPENNESS"           -> level.equals("HIGH") ? "You are highly imaginative and open to new ideas and experiences."
                                       : level.equals("LOW")  ? "You prefer conventional and familiar approaches."
                                       : "You balance openness to new experiences with appreciation for the familiar.";
            case "CONSCIENTIOUSNESS"  -> level.equals("HIGH") ? "You are well-organized, dependable, and goal-directed."
                                       : level.equals("LOW")  ? "You prefer flexibility and may sometimes find structure limiting."
                                       : "You balance structure with flexibility in your approach to tasks.";
            case "EXTRAVERSION"       -> level.equals("HIGH") ? "You thrive in social settings and draw energy from interactions."
                                       : level.equals("LOW")  ? "You prefer solitary activities and may find social situations draining."
                                       : "You enjoy both social interactions and time alone, adapting to context.";
            case "AGREEABLENESS"      -> level.equals("HIGH") ? "You are warm, cooperative, and prioritize harmony in relationships."
                                       : level.equals("LOW")  ? "You tend to be more competitive and skeptical in your interactions."
                                       : "You balance cooperation with independence in your relationships.";
            case "EMOTIONAL_STABILITY"-> level.equals("HIGH") ? "You remain calm and composed under pressure with strong emotional resilience."
                                       : level.equals("LOW")  ? "You may experience strong emotional reactions and stress more intensely."
                                       : "You have moderate emotional resilience and generally manage stress effectively.";
            case "REALISTIC"          -> "Your interest in practical, hands-on activities and working with tools and technology is " + level.toLowerCase() + ".";
            case "INVESTIGATIVE"      -> "Your inclination for research, analysis, and intellectual problem-solving is " + level.toLowerCase() + ".";
            case "ARTISTIC"           -> "Your preference for creative, expressive, and aesthetic activities is " + level.toLowerCase() + ".";
            case "SOCIAL"             -> "Your interest in helping, teaching, and working with people is " + level.toLowerCase() + ".";
            case "ENTERPRISING"       -> "Your drive for leadership, persuasion, and entrepreneurial activities is " + level.toLowerCase() + ".";
            case "CONVENTIONAL"       -> "Your preference for organized, systematic, and data-driven work is " + level.toLowerCase() + ".";
            default                   -> label + " level: " + level.toLowerCase() + ".";
        };
    }

    private Map<String, String> getPersonalityLabels() {
        return Map.of(
            "OPENNESS",            "Openness to Experience",
            "CONSCIENTIOUSNESS",   "Conscientiousness",
            "EXTRAVERSION",        "Extraversion",
            "AGREEABLENESS",       "Agreeableness",
            "EMOTIONAL_STABILITY", "Emotional Stability"
        );
    }

    private Map<String, String> getRiasecLabels() {
        return Map.of(
            "REALISTIC",    "Realistic (Doer)",
            "INVESTIGATIVE","Investigative (Thinker)",
            "ARTISTIC",     "Artistic (Creator)",
            "SOCIAL",       "Social (Helper)",
            "ENTERPRISING", "Enterprising (Persuader)",
            "CONVENTIONAL", "Conventional (Organizer)"
        );
    }
}
