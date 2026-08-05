package com.acea.backend.dto.assessment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Autosave a single answer during an in-progress session. */
public record SaveAnswerRequest(
    @NotNull(message = "Result ID is required")
    UUID resultId,

    @NotNull(message = "Question ID is required")
    UUID questionId,

    /** Nullable: student may clear/skip an answer. */
    UUID selectedOptionId,

    Boolean isFlagged
) {}
