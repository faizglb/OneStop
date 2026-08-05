package com.acea.backend.dto.college;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CollegeRecommendationResponse(
    UUID id,
    UUID collegeId,
    String collegeName,
    String state,
    String city,
    Integer nirfRanking,
    BigDecimal averagePackage,
    BigDecimal annualFees,
    BigDecimal matchScore,
    BigDecimal confidenceScore,
    String recommendationReason,
    String recommendedCourse,
    Instant createdAt
) {}
