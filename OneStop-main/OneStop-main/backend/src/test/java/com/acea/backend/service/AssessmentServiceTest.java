package com.acea.backend.service;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.*;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.assessment.*;
import com.acea.backend.exception.*;
import com.acea.backend.service.impl.AssessmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssessmentService — Unit Tests")
class AssessmentServiceTest {

    @Mock private AssessmentRepository       assessmentRepository;
    @Mock private QuestionRepository         questionRepository;
    @Mock private OptionRepository           optionRepository;
    @Mock private AssessmentResultRepository resultRepository;
    @Mock private ResultAnswerRepository     answerRepository;
    @Mock private SectionScoreRepository     sectionScoreRepository;
    @Mock private TraitScoreRepository       traitScoreRepository;
    @Mock private UserRepository             userRepository;
    @Mock private ScoringEngine              scoringEngine;

    @InjectMocks
    private AssessmentServiceImpl assessmentService;

    private UUID userId;
    private UUID assessmentId;
    private User student;
    private Assessment assessment;

    @BeforeEach
    void setUp() {
        userId       = UUID.randomUUID();
        assessmentId = UUID.randomUUID();

        student = User.builder()
            .id(userId)
            .email("test@acea.com")
            .build();

        assessment = Assessment.builder()
            .id(assessmentId)
            .assessmentType(AssessmentType.APTITUDE)
            .title("Aptitude Assessment")
            .description("Test description")
            .instructions("Test instructions")
            .timeLimitMinutes(60)
            .totalQuestions(40)
            .passingScore(60)
            .status(AssessmentStatus.ACTIVE)
            .build();
    }

    // ── getAvailableAssessments ────────────────────────────────────────────────

    @Test
    @DisplayName("getAvailableAssessments — returns all ACTIVE assessments")
    void getAvailableAssessments_returnsActiveAssessments() {
        given(assessmentRepository.findByStatusOrderByAssessmentTypeAsc(AssessmentStatus.ACTIVE))
            .willReturn(List.of(assessment));

        List<AssessmentSummaryResponse> result = assessmentService.getAvailableAssessments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Aptitude Assessment");
    }

    // ── startAssessment ────────────────────────────────────────────────────────

    @Test
    @DisplayName("startAssessment — creates new session when none exists")
    void startAssessment_createsNewSession() {
        StartAssessmentRequest request = new StartAssessmentRequest(assessmentId);

        given(assessmentRepository.findByIdAndStatus(assessmentId, AssessmentStatus.ACTIVE))
            .willReturn(Optional.of(assessment));
        given(resultRepository.findByStudentIdAndAssessmentIdAndStatus(userId, assessmentId, ResultStatus.IN_PROGRESS))
            .willReturn(Optional.empty());
        given(userRepository.findById(userId)).willReturn(Optional.of(student));

        AssessmentResult newResult = AssessmentResult.builder()
            .id(UUID.randomUUID())
            .student(student)
            .assessment(assessment)
            .assessmentType(AssessmentType.APTITUDE)
            .startedAt(Instant.now())
            .status(ResultStatus.IN_PROGRESS)
            .build();
        given(resultRepository.save(any(AssessmentResult.class))).willReturn(newResult);
        given(questionRepository.findActiveQuestionsWithOptions(assessmentId)).willReturn(List.of());

        StartSessionResponse response = assessmentService.startAssessment(userId, request);

        assertThat(response.resumed()).isFalse();
        assertThat(response.assessmentType()).isEqualTo(AssessmentType.APTITUDE);
        verify(resultRepository).save(any(AssessmentResult.class));
    }

    @Test
    @DisplayName("startAssessment — resumes existing IN_PROGRESS session")
    void startAssessment_resumesExistingSession() {
        StartAssessmentRequest request = new StartAssessmentRequest(assessmentId);

        UUID existingResultId = UUID.randomUUID();
        AssessmentResult existingResult = AssessmentResult.builder()
            .id(existingResultId)
            .student(student)
            .assessment(assessment)
            .assessmentType(AssessmentType.APTITUDE)
            .startedAt(Instant.now())
            .status(ResultStatus.IN_PROGRESS)
            .build();

        given(assessmentRepository.findByIdAndStatus(assessmentId, AssessmentStatus.ACTIVE))
            .willReturn(Optional.of(assessment));
        given(resultRepository.findByStudentIdAndAssessmentIdAndStatus(userId, assessmentId, ResultStatus.IN_PROGRESS))
            .willReturn(Optional.of(existingResult));
        given(questionRepository.findActiveQuestionsWithOptions(assessmentId)).willReturn(List.of());

        StartSessionResponse response = assessmentService.startAssessment(userId, request);

        assertThat(response.resumed()).isTrue();
        assertThat(response.resultId()).isEqualTo(existingResultId);
        // Should NOT create a new result
        verify(resultRepository, never()).save(any(AssessmentResult.class));
    }

    @Test
    @DisplayName("startAssessment — throws ResourceNotFoundException for inactive assessment")
    void startAssessment_throwsForInactiveAssessment() {
        StartAssessmentRequest request = new StartAssessmentRequest(assessmentId);

        given(assessmentRepository.findByIdAndStatus(assessmentId, AssessmentStatus.ACTIVE))
            .willReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentService.startAssessment(userId, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── saveAnswer ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveAnswer — throws ForbiddenException when result belongs to different user")
    void saveAnswer_throwsForbiddenForWrongUser() {
        UUID otherUserId = UUID.randomUUID();
        UUID resultId    = UUID.randomUUID();
        UUID questionId  = UUID.randomUUID();

        AssessmentResult result = AssessmentResult.builder()
            .id(resultId)
            .student(student)  // belongs to userId, not otherUserId
            .assessment(assessment)
            .status(ResultStatus.IN_PROGRESS)
            .startedAt(Instant.now())
            .build();

        given(resultRepository.findById(resultId)).willReturn(Optional.of(result));

        SaveAnswerRequest request = new SaveAnswerRequest(resultId, questionId, null, false);

        assertThatThrownBy(() -> assessmentService.saveAnswer(otherUserId, request))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("saveAnswer — throws BadRequestException when session expired")
    void saveAnswer_throwsForExpiredSession() {
        UUID resultId   = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        AssessmentResult expiredResult = AssessmentResult.builder()
            .id(resultId)
            .student(student)
            .assessment(assessment)
            .status(ResultStatus.IN_PROGRESS)
            .startedAt(Instant.now().minus(120, ChronoUnit.MINUTES))  // 2h ago, limit is 60m
            .build();

        given(resultRepository.findById(resultId)).willReturn(Optional.of(expiredResult));
        given(resultRepository.save(any())).willReturn(expiredResult);

        SaveAnswerRequest request = new SaveAnswerRequest(resultId, questionId, null, false);

        assertThatThrownBy(() -> assessmentService.saveAnswer(userId, request))
            .isInstanceOf(AssessmentExpiredException.class);
    }

    // ── getHistory ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getHistory — returns empty list when no attempts")
    void getHistory_returnsEmptyListForNoAttempts() {
        given(resultRepository.findByStudentIdOrderByCreatedAtDesc(userId))
            .willReturn(List.of());

        List<AssessmentHistoryResponse> history = assessmentService.getHistory(userId);

        assertThat(history).isEmpty();
    }
}
