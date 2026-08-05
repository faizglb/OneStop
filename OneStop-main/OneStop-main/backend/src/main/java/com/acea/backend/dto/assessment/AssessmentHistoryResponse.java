package com.acea.backend.dto.assessment;

import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** One row in the assessment history list. */
public record AssessmentHistoryResponse(
    UUID resultId,
    UUID assessmentId,
    AssessmentType assessmentType,
    String assessmentTitle,
    BigDecimal percentage,
    ResultStatus status,
    int timeTakenSecs,
    Instant completedAt,
    Instant startedAt
) {}
