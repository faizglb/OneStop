package com.acea.backend.dto.assessment;

import java.math.BigDecimal;

/** Trait or RIASEC category score. */
public record TraitScoreResponse(
    String traitKey,
    String traitLabel,
    BigDecimal normalizedScore,
    String level,
    String description
) {}
