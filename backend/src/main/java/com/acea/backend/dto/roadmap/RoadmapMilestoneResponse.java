package com.acea.backend.dto.roadmap;

import java.util.UUID;

public record RoadmapMilestoneResponse(
    UUID id,
    String title,
    String description,
    String completionCriteria,
    String status
) {}
