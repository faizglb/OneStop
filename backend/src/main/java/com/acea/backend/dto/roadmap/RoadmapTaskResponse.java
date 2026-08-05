package com.acea.backend.dto.roadmap;

import java.time.LocalDate;
import java.util.UUID;

public record RoadmapTaskResponse(
    UUID id,
    UUID phaseId,
    String phaseName,
    String title,
    String description,
    String taskType,
    Integer estimatedHours,
    LocalDate dueDate,
    String status,
    String priority,
    UUID linkedResourceId,
    String linkedResourceTitle,
    String linkedResourceUrl,
    UUID linkedCertificationId,
    UUID linkedProjectId
) {}
