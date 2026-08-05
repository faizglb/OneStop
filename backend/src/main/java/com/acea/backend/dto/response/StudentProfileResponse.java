package com.acea.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record StudentProfileResponse(
    UUID id,
    UUID userId,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String gender,
    String phoneNumber,
    String profilePhoto,
    String country,
    String state,
    String district,
    String city,
    String address,
    String pinCode,
    
    // Academics
    String schoolName,
    String collegeName,
    String currentCourse,
    String branch,
    Integer currentYear,
    String educationLevel,
    BigDecimal tenthPercentage,
    BigDecimal twelfthPercentage,
    BigDecimal graduationCgpa,
    BigDecimal currentCgpa,
    String preferredStudyMode,
    
    // Demographics
    BigDecimal familyIncome,
    String category,
    String caste,
    String religion,
    String disabilityStatus,
    List<String> languagesKnown,
    
    // Career & Bio
    String careerGoal,
    String bio,
    String linkedinUrl,
    String githubUrl,
    String portfolioUrl,
    String resumeUrl,
    
    // Metadata
    Integer profileCompletionPct,
    
    // Nested Collections
    List<SkillResponse> skills,
    List<InterestResponse> interests,
    List<GoalResponse> goals,
    List<CertificationResponse> certifications,
    List<ProjectResponse> projects
) {}
