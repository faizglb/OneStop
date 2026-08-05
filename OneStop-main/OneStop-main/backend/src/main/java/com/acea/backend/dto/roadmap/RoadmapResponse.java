package com.acea.backend.dto.roadmap;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RoadmapResponse(
    UUID id,
    String title,
    String targetCareer,
    String targetCompany,
    String duration,
    String status,
    Integer completionPercentage,
    LocalDate startDate,
    LocalDate endDate,
    Integer weeklyHours,
    Instant createdAt,
    Instant updatedAt,
    List<RoadmapPhaseResponse> phases,
    List<RoadmapMilestoneResponse> milestones
) {}
