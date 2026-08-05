package com.acea.backend.dto.learning;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LearningRecommendationResponse(
    UUID id,
    UUID resourceId,
    String title,
    String platform,
    String resourceType,
    String difficulty,
    String duration,
    Boolean isFree,
    BigDecimal rating,
    String thumbnail,
    String resourceUrl,
    String recommendedSkill,
    String priority,
    BigDecimal confidenceScore,
    String reason,
    String estimatedCompletionTime,
    Instant createdAt
) {}
