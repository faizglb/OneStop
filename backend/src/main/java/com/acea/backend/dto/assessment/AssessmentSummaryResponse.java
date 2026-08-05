package com.acea.backend.dto.assessment;

import com.acea.backend.domain.enums.AssessmentStatus;
import com.acea.backend.domain.enums.AssessmentType;

import java.util.UUID;

/** Lightweight assessment card for the listing page. */
public record AssessmentSummaryResponse(
    UUID id,
    AssessmentType assessmentType,
    String title,
    String description,
    int timeLimitMinutes,
    int totalQuestions,
    AssessmentStatus status
) {}
