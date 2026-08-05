package com.acea.backend.dto.learning;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record LearningProgressRequest(
    @NotNull UUID resourceId,
    @NotBlank String status, // STARTED | IN_PROGRESS | COMPLETED
    @NotNull @Min(0) @Max(100) Integer progressPercentage
) {}
