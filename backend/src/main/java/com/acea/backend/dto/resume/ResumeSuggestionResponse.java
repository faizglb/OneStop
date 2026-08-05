package com.acea.backend.dto.resume;

public record ResumeSuggestionResponse(
        String category,
        String priority,
        String title,
        String description,
        String example
) {}
