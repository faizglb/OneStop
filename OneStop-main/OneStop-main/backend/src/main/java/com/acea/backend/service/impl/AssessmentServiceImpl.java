package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.AssessmentStatus;
import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.assessment.*;
import com.acea.backend.exception.*;
import com.acea.backend.service.AssessmentService;
import com.acea.backend.service.ScoringEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentServiceImpl implements AssessmentService {

    private static final BigDecimal STRENGTH_THRESHOLD = BigDecimal.valueOf(70);
    private static final BigDecimal WEAKNESS_THRESHOLD = BigDecimal.valueOf(50);

    private final AssessmentRepository       assessmentRepository;
    private final QuestionRepository         questionRepository;
    private final OptionRepository           optionRepository;
    private final AssessmentResultRepository resultRepository;
    private final ResultAnswerRepository     answerRepository;
    private final SectionScoreRepository     sectionScoreRepository;
    private final TraitScoreRepository       traitScoreRepository;
    private final UserRepository             userRepository;
    private final ScoringEngine              scoringEngine;

    // ── List / Get ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentSummaryResponse> getAvailableAssessments() {
        return assessmentRepository.findByStatusOrderByAssessmentTypeAsc(AssessmentStatus.ACTIVE)
            .stream()
            .map(this::toSummary)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentSummaryResponse getAssessmentById(UUID assessmentId) {
        Assessment a = assessmentRepository.findByIdAndStatus(assessmentId, AssessmentStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + assessmentId));
        return toSummary(a);
    }

    // ── Start / Resume ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StartSessionResponse startAssessment(UUID userId, StartAssessmentRequest request) {
        Assessment assessment = assessmentRepository
            .findByIdAndStatus(request.assessmentId(), AssessmentStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + request.assessmentId()));

        // Check for an existing IN_PROGRESS session → resume it
        Optional<AssessmentResult> existingSession = resultRepository
            .findByStudentIdAndAssessmentIdAndStatus(userId, assessment.getId(), ResultStatus.IN_PROGRESS);

        if (existingSession.isPresent()) {
            log.info("Resuming existing session resultId={} for user={}", existingSession.get().getId(), userId);
            return buildSessionResponse(existingSession.get(), assessment, true);
        }

        // Create a fresh session
        User student = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        AssessmentResult result = AssessmentResult.builder()
            .student(student)
            .assessment(assessment)
            .assessmentType(assessment.getAssessmentType())
            .startedAt(Instant.now())
            .status(ResultStatus.IN_PROGRESS)
            .build();

        result = resultRepository.save(result);
        log.info("Started new assessment session resultId={} for user={}", result.getId(), userId);

        return buildSessionResponse(result, assessment, false);
    }

    // ── Save Answer ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void saveAnswer(UUID userId, SaveAnswerRequest request) {
        AssessmentResult result = getValidInProgressResult(userId, request.resultId());

        Question question = questionRepository.findById(request.questionId())
            .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_ANSWER_SUBMISSION, "Question not found: " + request.questionId()));

        // Validate question belongs to this assessment
        if (!question.getAssessment().getId().equals(result.getAssessment().getId())) {
            throw new BadRequestException(ErrorCode.INVALID_ANSWER_SUBMISSION, "Question does not belong to this assessment");
        }

        Option selectedOption = null;
        if (request.selectedOptionId() != null) {
            selectedOption = optionRepository.findByIdAndQuestionId(request.selectedOptionId(), question.getId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_ANSWER_SUBMISSION, "Option does not belong to this question"));
        }

        // Upsert the answer (unique on result + question)
        Optional<ResultAnswer> existing = answerRepository.findByResultIdAndQuestionId(result.getId(), question.getId());

        if (existing.isPresent()) {
            ResultAnswer ra = existing.get();
            ra.setSelectedOption(selectedOption);
            ra.setAnsweredAt(Instant.now());
            if (request.isFlagged() != null) ra.setIsFlagged(request.isFlagged());
            answerRepository.save(ra);
        } else {
            ResultAnswer ra = ResultAnswer.builder()
                .result(result)
                .question(question)
                .selectedOption(selectedOption)
                .isFlagged(Boolean.TRUE.equals(request.isFlagged()))
                .build();
            answerRepository.save(ra);
        }
    }

    // ── Submit ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AssessmentResultResponse submitAssessment(UUID userId, SubmitAssessmentRequest request) {
        AssessmentResult result = getValidInProgressResult(userId, request.resultId());

        // Load all answers (including unanswered = not in table, which means 0 score)
        List<ResultAnswer> answers = answerRepository.findByResultIdOrderByAnsweredAtAsc(result.getId());

        // ── Score based on type ──────────────────────────────────────────────────
        List<SectionScoreResponse> sectionScores = List.of();
        List<TraitScoreResponse> traitScores = List.of();

        AssessmentType type = result.getAssessmentType();

        if (type == AssessmentType.APTITUDE) {
            sectionScores = scoringEngine.scoreAptitude(result, answers);
        } else if (type == AssessmentType.PERSONALITY) {
            traitScores = scoringEngine.scorePersonality(result, answers);
        } else if (type == AssessmentType.INTEREST) {
            traitScores = scoringEngine.scoreInterest(result, answers);
        }

        // ── Compute overall totals ───────────────────────────────────────────────
        BigDecimal totalScore = BigDecimal.ZERO;
        BigDecimal maxScore   = BigDecimal.ZERO;

        if (type == AssessmentType.APTITUDE) {
            // Aptitude: sum of correct marks
            for (ResultAnswer ra : answers) {
                BigDecimal marks = BigDecimal.valueOf(ra.getQuestion().getMarks());
                maxScore = maxScore.add(marks);
                if (ra.getSelectedOption() != null && ra.getSelectedOption().getIsCorrect()) {
                    totalScore = totalScore.add(marks);
                    ra.setScoreAwarded(marks);
                }
            }
            // Also include max from questions not answered
            List<Question> allQuestions = questionRepository
                .findActiveQuestionsWithOptions(result.getAssessment().getId());
            BigDecimal totalMax = allQuestions.stream()
                .map(q -> BigDecimal.valueOf(q.getMarks()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            maxScore = totalMax;
        } else {
            // Personality / Interest: sum raw weights
            for (ResultAnswer ra : answers) {
                if (ra.getSelectedOption() != null) {
                    totalScore = totalScore.add(ra.getSelectedOption().getWeight());
                }
                maxScore = maxScore.add(BigDecimal.valueOf(5)); // max Likert
            }
        }

        BigDecimal percentage = maxScore.compareTo(BigDecimal.ZERO) > 0
            ? totalScore.divide(maxScore, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        // Estimated percentile based on percentage bands (raw heuristic, not comparative)
        BigDecimal percentile = estimatePercentile(percentage);

        // ── Finalize result ──────────────────────────────────────────────────────
        result.setTotalScore(totalScore);
        result.setMaxScore(maxScore);
        result.setPercentage(percentage);
        result.setPercentile(percentile);
        result.setTimeTakenSecs(request.timeTakenSecs());
        result.setStatus(ResultStatus.COMPLETED);
        result.setCompletedAt(Instant.now());
        resultRepository.save(result);

        answerRepository.saveAll(answers);

        log.info("Assessment submitted: resultId={} type={} score={}%", result.getId(), type, percentage);

        return buildResultResponse(result, sectionScores, traitScores);
    }

    // ── Get Result ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public AssessmentResultResponse getResult(UUID userId, UUID resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
            .orElseThrow(() -> new ResourceNotFoundException("Result not found: " + resultId));

        if (!result.getStudent().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to result: " + resultId);
        }

        List<SectionScoreResponse> sectionScores = sectionScoreRepository
            .findByResultIdOrderByCategoryNameAsc(resultId)
            .stream()
            .map(ss -> new SectionScoreResponse(
                ss.getCategory().getId(),
                ss.getCategoryName(),
                ss.getRawScore(),
                ss.getMaxScore(),
                ss.getPercentage(),
                ss.getIsStrength()
            ))
            .toList();

        List<TraitScoreResponse> traitScores = traitScoreRepository
            .findByResultIdOrderByNormalizedScoreDesc(resultId)
            .stream()
            .map(ts -> new TraitScoreResponse(
                ts.getTraitKey(),
                ts.getTraitLabel(),
                ts.getNormalizedScore(),
                ts.getLevel(),
                ts.getDescription()
            ))
            .toList();

        return buildResultResponse(result, sectionScores, traitScores);
    }

    // ── History ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentHistoryResponse> getHistory(UUID userId) {
        return resultRepository.findByStudentIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(r -> new AssessmentHistoryResponse(
                r.getId(),
                r.getAssessment().getId(),
                r.getAssessmentType(),
                r.getAssessment().getTitle(),
                r.getPercentage(),
                r.getStatus(),
                r.getTimeTakenSecs(),
                r.getCompletedAt(),
                r.getStartedAt()
            ))
            .toList();
    }

    // ── Private Helpers ────────────────────────────────────────────────────────

    private AssessmentResult getValidInProgressResult(UUID userId, UUID resultId) {
        AssessmentResult result = resultRepository.findById(resultId)
            .orElseThrow(() -> new ResourceNotFoundException("Assessment session not found: " + resultId));

        if (!result.getStudent().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to session: " + resultId);
        }

        if (result.getStatus() == ResultStatus.COMPLETED) {
            throw new BadRequestException(ErrorCode.ASSESSMENT_ALREADY_COMPLETED, "This assessment has already been completed");
        }

        if (result.getStatus() == ResultStatus.ABANDONED) {
            throw new BadRequestException(ErrorCode.ASSESSMENT_ALREADY_COMPLETED, "This assessment session has been abandoned");
        }

        // Time limit check (server-side enforcement)
        Instant deadline = result.getStartedAt()
            .plus(result.getAssessment().getTimeLimitMinutes(), ChronoUnit.MINUTES);
        if (Instant.now().isAfter(deadline)) {
            // Auto-mark as abandoned
            result.setStatus(ResultStatus.ABANDONED);
            result.setCompletedAt(Instant.now());
            resultRepository.save(result);
            throw new AssessmentExpiredException("Assessment time limit has expired");
        }

        return result;
    }

    private StartSessionResponse buildSessionResponse(AssessmentResult result, Assessment assessment, boolean resumed) {
        List<Question> questions = questionRepository.findActiveQuestionsWithOptions(assessment.getId());

        // Randomize question order for new sessions; preserve order for resumed ones
        if (!resumed) {
            Collections.shuffle(questions);
        }

        List<QuestionResponse> questionResponses = questions.stream()
            .map(this::toQuestionResponse)
            .toList();

        Instant expiresAt = result.getStartedAt()
            .plus(assessment.getTimeLimitMinutes(), ChronoUnit.MINUTES);

        return new StartSessionResponse(
            result.getId(),
            assessment.getId(),
            assessment.getAssessmentType(),
            assessment.getTitle(),
            assessment.getTimeLimitMinutes(),
            assessment.getTotalQuestions(),
            result.getStartedAt(),
            expiresAt,
            resumed,
            questionResponses
        );
    }

    private AssessmentResultResponse buildResultResponse(
            AssessmentResult result,
            List<SectionScoreResponse> sectionScores,
            List<TraitScoreResponse> traitScores) {

        // Derive strengths / weaknesses
        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();

        for (SectionScoreResponse ss : sectionScores) {
            if (ss.isStrength()) strengths.add(ss.categoryName());
            else if (ss.percentage().compareTo(WEAKNESS_THRESHOLD) < 0) weaknesses.add(ss.categoryName());
        }

        // For personality / interest — top 2 are strengths, bottom 1 is growth area
        if (strengths.isEmpty() && !traitScores.isEmpty()) {
            int topCount = Math.min(2, traitScores.size());
            traitScores.subList(0, topCount).forEach(ts -> strengths.add(ts.traitLabel()));
            if (traitScores.size() > topCount) {
                TraitScoreResponse weakest = traitScores.get(traitScores.size() - 1);
                weaknesses.add(weakest.traitLabel());
            }
        }

        // Build chart-ready data (labels + values)
        List<String> chartLabels = new ArrayList<>();
        List<Double> chartValues = new ArrayList<>();

        if (!sectionScores.isEmpty()) {
            sectionScores.forEach(ss -> {
                chartLabels.add(ss.categoryName());
                chartValues.add(ss.percentage().doubleValue());
            });
        } else {
            traitScores.forEach(ts -> {
                chartLabels.add(ts.traitLabel());
                chartValues.add(ts.normalizedScore().doubleValue());
            });
        }

        return new AssessmentResultResponse(
            result.getId(),
            result.getAssessment().getId(),
            result.getAssessmentType(),
            result.getAssessment().getTitle(),
            result.getTotalScore(),
            result.getMaxScore(),
            result.getPercentage(),
            result.getPercentile(),
            result.getStatus(),
            result.getTimeTakenSecs(),
            result.getCompletedAt(),
            sectionScores,
            traitScores,
            strengths,
            weaknesses,
            chartLabels,
            chartValues
        );
    }

    private QuestionResponse toQuestionResponse(Question q) {
        List<OptionResponse> options = q.getOptions().stream()
            .sorted(Comparator.comparingInt(Option::getOptionOrder))
            .map(o -> new OptionResponse(o.getId(), o.getOptionText(), o.getOptionOrder()))
            .toList();

        return new QuestionResponse(
            q.getId(),
            q.getCategory().getId(),
            q.getCategory().getName(),
            q.getQuestionText(),
            q.getDifficulty(),
            q.getMarks(),
            q.getQuestionOrder(),
            options
        );
    }

    private AssessmentSummaryResponse toSummary(Assessment a) {
        return new AssessmentSummaryResponse(
            a.getId(),
            a.getAssessmentType(),
            a.getTitle(),
            a.getDescription(),
            a.getTimeLimitMinutes(),
            a.getTotalQuestions(),
            a.getStatus()
        );
    }

    /**
     * Heuristic percentile estimate based on score percentage.
     * This is NOT a comparative percentile against other students.
     * Sprint 4 will implement statistical percentile once data accumulates.
     */
    private BigDecimal estimatePercentile(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) return BigDecimal.valueOf(95);
        if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) return BigDecimal.valueOf(85);
        if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) return BigDecimal.valueOf(72);
        if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) return BigDecimal.valueOf(58);
        if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) return BigDecimal.valueOf(42);
        return BigDecimal.valueOf(20);
    }
}
