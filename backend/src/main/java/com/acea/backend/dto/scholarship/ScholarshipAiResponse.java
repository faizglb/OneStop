package com.acea.backend.dto.scholarship;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ScholarshipAiResponse(
    List<ScholarshipRecDto> recommendations
) {
    public record ScholarshipRecDto(
        UUID scholarshipId,
        BigDecimal eligibilityScore,
        BigDecimal confidenceScore,
        String recommendationReason,
        String applicationStatus // ELIGIBLE | PARTIALLY_ELIGIBLE | NOT_ELIGIBLE
    ) {}
}
