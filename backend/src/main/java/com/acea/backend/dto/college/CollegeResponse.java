package com.acea.backend.dto.college;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CollegeResponse(
    UUID id,
    String collegeName,
    String collegeType,
    String ownership,
    String state,
    String district,
    String city,
    String address,
    BigDecimal latitude,
    BigDecimal longitude,
    Integer nirfRanking,
    String naacGrade,
    Boolean aicteApproved,
    Boolean ugcApproved,
    Integer establishedYear,
    String website,
    String email,
    String phone,
    BigDecimal averagePackage,
    BigDecimal highestPackage,
    BigDecimal medianPackage,
    BigDecimal placementPercentage,
    Boolean hostelAvailable,
    Boolean girlsHostel,
    Boolean boysHostel,
    BigDecimal totalFees,
    BigDecimal annualFees,
    BigDecimal campusArea,
    String description,
    Boolean isBookmarked,
    List<CourseDto> courses,
    List<FacilityDto> facilities
) {
    public record CourseDto(
        UUID id,
        String courseName,
        String stream,
        Integer duration,
        String degree,
        String eligibility,
        String entranceExam,
        Integer seatIntake
    ) {}

    public record FacilityDto(
        UUID id,
        String facilityName,
        String description
    ) {}
}
