package com.acea.backend.dto.scholarship;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScholarshipRecommendationResponse(
    UUID id,
    UUID scholarshipId,
    String scholarshipName,
    String provider,
    String scholarshipType,
    BigDecimal amount,
    LocalDate lastDate,
    String requiredDocuments,
    String officialWebsite,
    Boolean renewable,
    BigDecimal eligibilityScore,
    BigDecimal confidenceScore,
    String recommendationReason,
    String applicationStatus, // ELIGIBLE | PARTIALLY_ELIGIBLE | NOT_ELIGIBLE
    Instant createdAt
) {}
