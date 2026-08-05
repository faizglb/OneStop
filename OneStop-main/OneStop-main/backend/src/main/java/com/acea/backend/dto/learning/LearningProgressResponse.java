package com.acea.backend.dto.learning;

import java.time.Instant;
import java.util.UUID;

public record LearningProgressResponse(
    UUID id,
    UUID resourceId,
    String title,
    String platform,
    String category,
    String status,
    Integer progressPercentage,
    Instant startedAt,
    Instant completedAt,
    Instant lastAccessed
) {}
