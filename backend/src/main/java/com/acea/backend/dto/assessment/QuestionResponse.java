package com.acea.backend.dto.assessment;

import com.acea.backend.domain.enums.Difficulty;

import java.util.List;
import java.util.UUID;

/** Question DTO for active assessment sessions — no correct answer data. */
public record QuestionResponse(
    UUID id,
    UUID categoryId,
    String categoryName,
    String questionText,
    Difficulty difficulty,
    int marks,
    int questionOrder,
    List<OptionResponse> options
) {}
