package com.acea.backend.dto.resume;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResumeAnalysisResponse(
        UUID id,
        String fileName,
        String fileUrl,
        String fileType,
        Integer wordCount,
        Integer pageCount,
        String status,
        BigDecimal atsScore,
        BigDecimal overallScore,
        String targetCareer,
        Instant createdAt,
        Instant updatedAt,
        ResumeScoreBreakdownResponse scoreBreakdown,
        List<ResumeSectionResponse> sections,
        List<ResumeKeywordResponse> keywords,
        List<ResumeSuggestionResponse> suggestions
) {}
