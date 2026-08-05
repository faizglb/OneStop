package com.acea.backend.dto.college;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CollegeAiServiceResponse(
    List<CollegeRecDto> recommendations
) {
    public record CollegeRecDto(
        UUID collegeId,
        BigDecimal matchScore,
        BigDecimal confidenceScore,
        String recommendationReason,
        String recommendedCourse
    ) {}
}
