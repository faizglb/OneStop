package com.acea.backend.dto.roadmap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record RoadmapAiRequest(
    String targetCareer,
    String targetCompany,
    String duration,
    Integer weeklyHours,
    LocalDate startDate,
    String educationLevel,
    String currentCourse,
    BigDecimal currentCgpa,
    Map<String, BigDecimal> aptitudeScores,
    Map<String, BigDecimal> personalityTraits,
    Map<String, BigDecimal> riasecProfile,
    List<ResourceItemDto> availableResources
) {
    public record ResourceItemDto(
        UUID id,
        String title,
        String platform,
        String category,
        String skill,
        String difficulty,
        String url
    ) {}
}
