package com.acea.backend.service;

import com.acea.backend.dto.assessment.*;

import java.util.List;
import java.util.UUID;

/**
 * Assessment module business logic contract.
 *
 * <p>Rules enforced by implementations:
 * <ul>
 *   <li>Students cannot have two IN_PROGRESS sessions for the same assessment.</li>
 *   <li>Resumption is transparent — start returns existing session if IN_PROGRESS.</li>
 *   <li>Scoring is delegated to {@link ScoringEngine}.</li>
 *   <li>No recommendation or career matching logic may be added here.</li>
 * </ul>
 */
public interface AssessmentService {

    /** Returns all ACTIVE assessments for the listing page. */
    List<AssessmentSummaryResponse> getAvailableAssessments();

    /** Returns assessment metadata + instructions for the info page (no answers). */
    AssessmentSummaryResponse getAssessmentById(UUID assessmentId);

    /**
     * Starts a new session OR resumes an existing IN_PROGRESS session.
     * Returns questions in randomized order.
     */
    StartSessionResponse startAssessment(UUID userId, StartAssessmentRequest request);

    /** Autosaves a single answer. Idempotent — upserts if answer already exists. */
    void saveAnswer(UUID userId, SaveAnswerRequest request);

    /** Finalizes the attempt, triggers scoring, returns the full result. */
    AssessmentResultResponse submitAssessment(UUID userId, SubmitAssessmentRequest request);

    /** Returns the scored result for a completed attempt. */
    AssessmentResultResponse getResult(UUID userId, UUID resultId);

    /** Returns all attempts (any status) for the current user. */
    List<AssessmentHistoryResponse> getHistory(UUID userId);
}
