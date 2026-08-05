package com.acea.backend.dto.roadmap;

import java.util.List;
import java.util.UUID;

public record RoadmapPhaseResponse(
    UUID id,
    String phaseName,
    Integer sequence,
    String description,
    List<RoadmapTaskResponse> tasks
) {}
