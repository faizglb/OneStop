package com.acea.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Size(max = 20, message = "Gender cannot exceed 20 characters")
    private String gender;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{7,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Size(max = 500, message = "Profile photo URL cannot exceed 500 characters")
    @URL(message = "Invalid profile photo URL")
    private String profilePhoto;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country name cannot exceed 100 characters")
    private String country;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State name cannot exceed 100 characters")
    private String state;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District name cannot exceed 100 characters")
    private String district;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name cannot exceed 100 characters")
    private String city;

    @NotBlank(message = "Address is required")
    @Size(max = 300, message = "Address cannot exceed 300 characters")
    private String address;

    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^[0-9a-zA-Z\\s\\-]{3,10}$", message = "Invalid PIN code format")
    private String pinCode;

    // Academics
    @NotBlank(message = "School name is required")
    @Size(max = 200, message = "School name cannot exceed 200 characters")
    private String schoolName;

    @NotBlank(message = "College name is required")
    @Size(max = 200, message = "College name cannot exceed 200 characters")
    private String collegeName;

    @NotBlank(message = "Current course is required")
    @Size(max = 200, message = "Current course name cannot exceed 200 characters")
    private String currentCourse;

    @NotBlank(message = "Branch is required")
    @Size(max = 200, message = "Branch name cannot exceed 200 characters")
    private String branch;

    @NotNull(message = "Current year is required")
    @Min(value = 1, message = "Current year must be at least 1")
    @Max(value = 6, message = "Current year cannot exceed 6")
    private Integer currentYear;

    @NotBlank(message = "Education level is required")
    @Size(max = 50, message = "Education level cannot exceed 50 characters")
    private String educationLevel;

    @NotNull(message = "10th percentage is required")
    @DecimalMin(value = "0.0", message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100.0")
    private BigDecimal tenthPercentage;

    @NotNull(message = "12th percentage is required")
    @DecimalMin(value = "0.0", message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100.0")
    private BigDecimal twelfthPercentage;

    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
    private BigDecimal graduationCgpa;

    @NotNull(message = "Current CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
    private BigDecimal currentCgpa;

    @NotBlank(message = "Preferred study mode is required")
    @Size(max = 30, message = "Preferred study mode cannot exceed 30 characters")
    private String preferredStudyMode;

    // Demographics
    @NotNull(message = "Family income is required")
    @DecimalMin(value = "0.0", message = "Family income cannot be negative")
    private BigDecimal familyIncome;

    @NotBlank(message = "Category is required")
    @Size(max = 30, message = "Category cannot exceed 30 characters")
    private String category;

    @NotBlank(message = "Caste is required")
    @Size(max = 50, message = "Caste cannot exceed 50 characters")
    private String caste;

    @Size(max = 50, message = "Religion cannot exceed 50 characters")
    private String religion;

    @NotBlank(message = "Disability status is required")
    @Size(max = 50, message = "Disability status cannot exceed 50 characters")
    private String disabilityStatus;

    @NotEmpty(message = "Languages known list cannot be empty")
    private List<String> languagesKnown;

    // Career & Bio
    @NotBlank(message = "Career goal is required")
    private String careerGoal;

    @NotBlank(message = "Bio is required")
    private String bio;

    @Size(max = 500, message = "LinkedIn URL cannot exceed 500 characters")
    @URL(message = "Invalid LinkedIn URL")
    private String linkedinUrl;

    @Size(max = 500, message = "GitHub URL cannot exceed 500 characters")
    @URL(message = "Invalid GitHub URL")
    private String githubUrl;

    @Size(max = 500, message = "Portfolio URL cannot exceed 500 characters")
    @URL(message = "Invalid Portfolio URL")
    private String portfolioUrl;

    @Size(max = 500, message = "Resume URL cannot exceed 500 characters")
    @URL(message = "Invalid Resume URL")
    private String resumeUrl;
}
