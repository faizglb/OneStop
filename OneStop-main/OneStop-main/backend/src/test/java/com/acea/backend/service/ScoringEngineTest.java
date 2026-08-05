package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.assessment.*;
import com.acea.backend.service.impl.ScoringEngineImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScoringEngine — Unit Tests")
class ScoringEngineTest {

    @Mock private SectionScoreRepository sectionScoreRepository;
    @Mock private TraitScoreRepository   traitScoreRepository;

    @InjectMocks
    private ScoringEngineImpl scoringEngine;

    private AssessmentResult aptitudeResult;
    private AssessmentResult personalityResult;

    @BeforeEach
    void setUp() {
        User student = User.builder().id(UUID.randomUUID()).email("test@acea.com").build();

        Assessment aptitudeAssessment = Assessment.builder()
            .id(UUID.randomUUID())
            .assessmentType(AssessmentType.APTITUDE)
            .title("Aptitude Assessment")
            .description("desc")
            .instructions("instr")
            .timeLimitMinutes(60)
            .totalQuestions(10)
            .passingScore(60)
            .status(AssessmentStatus.ACTIVE)
            .build();

        aptitudeResult = AssessmentResult.builder()
            .id(UUID.randomUUID())
            .student(student)
            .assessment(aptitudeAssessment)
            .assessmentType(AssessmentType.APTITUDE)
            .startedAt(Instant.now())
            .status(ResultStatus.IN_PROGRESS)
            .build();

        Assessment personalityAssessment = Assessment.builder()
            .id(UUID.randomUUID())
            .assessmentType(AssessmentType.PERSONALITY)
            .title("Personality Assessment")
            .description("desc")
            .instructions("instr")
            .timeLimitMinutes(40)
            .totalQuestions(25)
            .passingScore(0)
            .status(AssessmentStatus.ACTIVE)
            .build();

        personalityResult = AssessmentResult.builder()
            .id(UUID.randomUUID())
            .student(student)
            .assessment(personalityAssessment)
            .assessmentType(AssessmentType.PERSONALITY)
            .startedAt(Instant.now())
            .status(ResultStatus.IN_PROGRESS)
            .build();
    }

    // ── Aptitude Scoring ───────────────────────────────────────────────────────

    @Test
    @DisplayName("scoreAptitude — all correct answers → 100%")
    void scoreAptitude_allCorrect_yields100Percent() {
        List<ResultAnswer> answers = buildAptitudeAnswers(aptitudeResult, 5, 5); // 5 correct, 0 wrong

        given(sectionScoreRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        List<SectionScoreResponse> scores = scoringEngine.scoreAptitude(aptitudeResult, answers);

        assertThat(scores).hasSize(1); // all in same category
        assertThat(scores.get(0).percentage()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(scores.get(0).isStrength()).isTrue();
    }

    @Test
    @DisplayName("scoreAptitude — all wrong answers → 0%")
    void scoreAptitude_allWrong_yields0Percent() {
        List<ResultAnswer> answers = buildAptitudeAnswers(aptitudeResult, 0, 5); // 0 correct, 5 wrong

        given(sectionScoreRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        List<SectionScoreResponse> scores = scoringEngine.scoreAptitude(aptitudeResult, answers);

        assertThat(scores.get(0).percentage()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(scores.get(0).isStrength()).isFalse();
    }

    @Test
    @DisplayName("scoreAptitude — 60% correct → marked as not a strength")
    void scoreAptitude_60Percent_notStrength() {
        List<ResultAnswer> answers = buildAptitudeAnswers(aptitudeResult, 3, 5); // 3/5 = 60%

        given(sectionScoreRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        List<SectionScoreResponse> scores = scoringEngine.scoreAptitude(aptitudeResult, answers);

        assertThat(scores.get(0).percentage()).isEqualByComparingTo(BigDecimal.valueOf(60));
        // 60% < 70% threshold → not a strength
        assertThat(scores.get(0).isStrength()).isFalse();
    }

    @Test
    @DisplayName("scoreAptitude — 80% correct → marked as strength")
    void scoreAptitude_80Percent_isStrength() {
        List<ResultAnswer> answers = buildAptitudeAnswers(aptitudeResult, 4, 5); // 4/5 = 80%

        given(sectionScoreRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        List<SectionScoreResponse> scores = scoringEngine.scoreAptitude(aptitudeResult, answers);

        assertThat(scores.get(0).percentage()).isEqualByComparingTo(BigDecimal.valueOf(80));
        assertThat(scores.get(0).isStrength()).isTrue();
    }

    // ── Personality Scoring ────────────────────────────────────────────────────

    @Test
    @DisplayName("scorePersonality — all 5 (Strongly Agree) → 100% normalized")
    void scorePersonality_allHighest_yields100Percent() {
        List<ResultAnswer> answers = buildPersonalityAnswers(personalityResult, 5.0);

        given(traitScoreRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        List<TraitScoreResponse> scores = scoringEngine.scorePersonality(personalityResult, answers);

        assertThat(scores).isNotEmpty();
        assertThat(scores.get(0).normalizedScore()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(scores.get(0).level()).isEqualTo("HIGH");
    }

    @Test
    @DisplayName("scorePersonality — all 1 (Strongly Disagree) → LOW level")
    void scorePersonality_allLowest_yieldsLowLevel() {
        List<ResultAnswer> answers = buildPersonalityAnswers(personalityResult, 1.0);

        given(traitScoreRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        List<TraitScoreResponse> scores = scoringEngine.scorePersonality(personalityResult, answers);

        assertThat(scores.get(0).normalizedScore()).isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(scores.get(0).level()).isEqualTo("LOW");
    }

    @Test
    @DisplayName("scorePersonality — generates non-null description for all traits")
    void scorePersonality_generatesDescriptions() {
        List<ResultAnswer> answers = buildPersonalityAnswers(personalityResult, 3.0);

        given(traitScoreRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        List<TraitScoreResponse> scores = scoringEngine.scorePersonality(personalityResult, answers);

        assertThat(scores).allSatisfy(ts -> assertThat(ts.description()).isNotBlank());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private List<ResultAnswer> buildAptitudeAnswers(
            AssessmentResult result, int correctCount, int totalCount) {

        QuestionCategory category = QuestionCategory.builder()
            .id(UUID.randomUUID())
            .name("Logical Reasoning")
            .displayOrder(1)
            .weightPct(BigDecimal.valueOf(100))
            .build();

        List<ResultAnswer> answers = new ArrayList<>();
        for (int i = 0; i < totalCount; i++) {
            boolean isCorrect = i < correctCount;

            Option option = Option.builder()
                .id(UUID.randomUUID())
                .optionText("Option " + i)
                .isCorrect(isCorrect)
                .weight(BigDecimal.ZERO)
                .optionOrder(1)
                .build();

            Question question = Question.builder()
                .id(UUID.randomUUID())
                .assessment(result.getAssessment())
                .category(category)
                .questionText("Q" + i)
                .difficulty(Difficulty.EASY)
                .questionOrder(i)
                .marks(1)
                .isActive(true)
                .build();

            option.setQuestion(question);

            answers.add(ResultAnswer.builder()
                .id(UUID.randomUUID())
                .result(result)
                .question(question)
                .selectedOption(option)
                .scoreAwarded(BigDecimal.ZERO)
                .build());
        }
        return answers;
    }

    private List<ResultAnswer> buildPersonalityAnswers(AssessmentResult result, double weight) {
        QuestionCategory openness = QuestionCategory.builder()
            .id(UUID.randomUUID())
            .name("Openness")
            .displayOrder(1)
            .weightPct(BigDecimal.valueOf(20))
            .traitKey("OPENNESS")
            .build();

        List<ResultAnswer> answers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Option option = Option.builder()
                .id(UUID.randomUUID())
                .optionText("Agree")
                .isCorrect(false)
                .weight(BigDecimal.valueOf(weight))
                .optionOrder(4)
                .build();

            Question question = Question.builder()
                .id(UUID.randomUUID())
                .assessment(result.getAssessment())
                .category(openness)
                .questionText("Q" + i)
                .difficulty(Difficulty.EASY)
                .questionOrder(i)
                .marks(1)
                .isActive(true)
                .build();

            option.setQuestion(question);

            answers.add(ResultAnswer.builder()
                .id(UUID.randomUUID())
                .result(result)
                .question(question)
                .selectedOption(option)
                .scoreAwarded(BigDecimal.ZERO)
                .build());
        }
        return answers;
    }
}
