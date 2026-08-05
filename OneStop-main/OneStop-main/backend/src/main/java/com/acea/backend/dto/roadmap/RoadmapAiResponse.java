package com.acea.backend.dto.roadmap;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RoadmapAiResponse(
    String title,
    LocalDate endDate,
    List<PhaseAiDto> phases,
    List<MilestoneAiDto> milestones
) {
    public record PhaseAiDto(
        String phaseName,
        Integer sequence,
        String description,
        List<TaskAiDto> tasks
    ) {}

    public record TaskAiDto(
        String title,
        String description,
        String taskType,
        Integer estimatedHours,
        LocalDate dueDate,
        String priority,
        UUID linkedResourceId
    ) {}

    public record MilestoneAiDto(
        String title,
        String description,
        String completionCriteria
    ) {}
}
