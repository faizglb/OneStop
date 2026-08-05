package com.acea.backend.dto.college;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CollegeCompareResponse(
    List<CompareItem> colleges
) {
    public record CompareItem(
        UUID id,
        String collegeName,
        String collegeType,
        String ownership,
        String state,
        String city,
        Integer nirfRanking,
        String naacGrade,
        BigDecimal averagePackage,
        BigDecimal highestPackage,
        BigDecimal placementPercentage,
        BigDecimal annualFees,
        BigDecimal totalFees,
        BigDecimal campusArea,
        Boolean hostelAvailable,
        Boolean girlsHostel,
        Boolean boysHostel,
        List<String> facilities,
        List<String> courseNames
    ) {}
}
