package com.acea.backend.dto.assessment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Final submission payload. */
public record SubmitAssessmentRequest(
    @NotNull(message = "Result ID is required")
    UUID resultId,

    @Min(value = 0, message = "Time taken cannot be negative")
    int timeTakenSecs
) {}
