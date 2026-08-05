package com.acea.backend.dto.resume;

public record ResumeSectionResponse(
        String sectionName,
        Boolean detected,
        String quality,
        String contentSnippet
) {}
