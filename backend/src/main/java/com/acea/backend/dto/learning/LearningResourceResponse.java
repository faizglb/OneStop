package com.acea.backend.dto.learning;

import java.math.BigDecimal;
import java.util.UUID;

public record LearningResourceResponse(
    UUID id,
    String title,
    String description,
    String platform,
    String resourceType,
    String category,
    String skill,
    String difficulty,
    String duration,
    String language,
    Boolean isFree,
    BigDecimal price,
    BigDecimal rating,
    String thumbnail,
    String resourceUrl,
    String author,
    String provider,
    Boolean certificateAvailable,
    Boolean isBookmarked,
    String progressStatus, // STARTED, IN_PROGRESS, COMPLETED or null
    Integer progressPercentage // or null
) {}
