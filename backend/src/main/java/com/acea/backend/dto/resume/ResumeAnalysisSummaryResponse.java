package com.acea.backend.dto.resume;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight summary for the analysis history list view.
 */
public record ResumeAnalysisSummaryResponse(
        UUID id,
        String fileName,
        String fileType,
        String status,
        BigDecimal atsScore,
        BigDecimal overallScore,
        String targetCareer,
        Integer wordCount,
        Integer pageCount,
        Instant createdAt
) {}
