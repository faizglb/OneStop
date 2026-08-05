package com.acea.backend.dto.resume;

import java.math.BigDecimal;

public record ResumeScoreBreakdownResponse(
        BigDecimal keywordScore,
        BigDecimal sectionScore,
        BigDecimal formattingScore,
        BigDecimal experienceScore,
        BigDecimal educationScore,
        BigDecimal grammarScore
) {}
