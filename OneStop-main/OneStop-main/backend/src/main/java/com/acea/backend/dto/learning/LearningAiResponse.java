package com.acea.backend.dto.learning;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record LearningAiResponse(
    List<ResourceRecDto> recommendations
) {
    public record ResourceRecDto(
        UUID resourceId,
        String recommendedSkill,
        String priority, // HIGH | MEDIUM | LOW
        BigDecimal confidenceScore,
        String reason,
        String estimatedCompletionTime
    ) {}
}
