package com.acea.backend.dto.resume;

public record ResumeKeywordResponse(
        String keyword,
        String category,
        Boolean found,
        Integer frequency,
        String importance
) {}
