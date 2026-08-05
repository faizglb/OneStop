package com.acea.backend.dto.assessment;

import com.acea.backend.domain.enums.AssessmentType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Returned when a student starts or resumes an assessment. */
public record StartSessionResponse(
    UUID resultId,
    UUID assessmentId,
    AssessmentType assessmentType,
    String title,
    int timeLimitMinutes,
    int totalQuestions,
    Instant startedAt,
    Instant expiresAt,
    boolean resumed,
    List<QuestionResponse> questions
) {}
