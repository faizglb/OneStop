package com.acea.backend.dto.assessment;

import java.math.BigDecimal;
import java.util.UUID;

/** Per-category score in the result breakdown. */
public record SectionScoreResponse(
    UUID categoryId,
    String categoryName,
    BigDecimal rawScore,
    BigDecimal maxScore,
    BigDecimal percentage,
    boolean isStrength
) {}
