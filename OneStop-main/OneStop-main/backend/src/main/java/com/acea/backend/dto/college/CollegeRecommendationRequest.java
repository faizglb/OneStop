package com.acea.backend.dto.college;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CollegeRecommendationRequest(
    @NotBlank String entranceExam,
    @NotNull @Min(1) Integer examRank,
    @NotBlank String reservationCategory, // General | OBC | SC | ST
    @NotNull BigDecimal maxBudget,
    @NotNull Boolean hostelRequired,
    String locationPreference, // Optional state preferred
    String preferredCourse      // Optional course name preferred
) {}
