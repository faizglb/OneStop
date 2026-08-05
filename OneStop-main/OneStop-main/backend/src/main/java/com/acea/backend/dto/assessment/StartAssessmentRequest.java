package com.acea.backend.dto.assessment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Request to start a new assessment attempt. */
public record StartAssessmentRequest(
    @NotNull(message = "Assessment ID is required")
    UUID assessmentId
) {}
