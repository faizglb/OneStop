package com.acea.backend.dto.roadmap;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RoadmapGenerateRequest(
    @NotBlank String targetCareer,
    String targetCompany,
    @NotBlank String duration, // 30-Day Plan | 60-Day Plan | 90-Day Plan | 6-Month Plan | 1-Year Plan | Custom Plan
    @NotNull @Min(1) Integer weeklyHours,
    LocalDate startDate
) {}
