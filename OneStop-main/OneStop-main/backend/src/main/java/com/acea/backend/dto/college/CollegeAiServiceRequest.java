package com.acea.backend.dto.college;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CollegeAiServiceRequest(
    // Student profile
    BigDecimal currentCgpa,
    List<String> careerGoals,
    Map<String, BigDecimal> aptitudeScores,
    Map<String, BigDecimal> personalityTraits,
    Map<String, BigDecimal> riasecProfile,

    // Exam & Constraints request
    String entranceExam,
    Integer examRank,
    String reservationCategory,
    BigDecimal maxBudget,
    Boolean hostelRequired,
    String locationPreference,
    String preferredCourse,

    // Available Colleges
    List<CollegeProfileDto> colleges
) {
    public record CollegeProfileDto(
        UUID collegeId,
        String collegeName,
        String collegeType,
        String state,
        String city,
        Integer nirfRanking,
        BigDecimal averagePackage,
        BigDecimal annualFees,
        Boolean hostelAvailable,
        List<CourseDto> courses,
        List<CutoffDto> cutoffs
    ) {}

    public record CourseDto(
        UUID courseId,
        String courseName,
        String stream,
        String degree
    ) {}

    public record CutoffDto(
        String examName,
        String category,
        Integer closingRank,
        Integer year
    ) {}
}
