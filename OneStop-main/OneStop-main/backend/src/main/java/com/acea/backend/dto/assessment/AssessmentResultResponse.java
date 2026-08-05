package com.acea.backend.dto.assessment;

import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Full result response after assessment completion. */
public record AssessmentResultResponse(
    UUID resultId,
    UUID assessmentId,
    AssessmentType assessmentType,
    String assessmentTitle,

    BigDecimal totalScore,
    BigDecimal maxScore,
    BigDecimal percentage,
    BigDecimal percentile,
    ResultStatus status,

    int timeTakenSecs,
    Instant completedAt,

    // Breakdown
    List<SectionScoreResponse> sectionScores,
    List<TraitScoreResponse> traitScores,

    // Derived
    List<String> strengths,
    List<String> weaknesses,

    // For charts (frontend uses these directly)
    List<String> chartLabels,
    List<Double> chartValues
) {}
