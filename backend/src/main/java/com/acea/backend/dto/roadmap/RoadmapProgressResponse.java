package com.acea.backend.dto.roadmap;

import java.util.UUID;

public record RoadmapProgressResponse(
    UUID roadmapId,
    String title,
    String targetCareer,
    Integer completionPercentage,
    long totalTasks,
    long completedTasks,
    long pendingTasks,
    long skippedTasks,
    int streakDays
) {}
