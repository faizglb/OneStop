package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "student_profiles",
    schema = "profile_schema",
    indexes = {
        @Index(name = "idx_sp_user_id", columnList = "user_id", unique = true)
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "skills", "interests", "goals", "certifications", "projects"})
@EqualsAndHashCode(of = "id")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "gender", nullable = false, length = 20)
    private String gender;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "profile_photo", length = 500)
    private String profilePhoto;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "address", nullable = false, length = 300)
    private String address;

    @Column(name = "pin_code", nullable = false, length = 10)
    private String pinCode;

    // Academics
    @Column(name = "school_name", nullable = false, length = 200)
    private String schoolName;

    @Column(name = "college_name", nullable = false, length = 200)
    private String collegeName;

    @Column(name = "current_course", nullable = false, length = 200)
    private String currentCourse;

    @Column(name = "branch", nullable = false, length = 200)
    private String branch;

    @Column(name = "current_year", nullable = false)
    private Integer currentYear;

    @Column(name = "education_level", nullable = false, length = 50)
    private String educationLevel;

    @Column(name = "tenth_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal tenthPercentage;

    @Column(name = "twelfth_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal twelfthPercentage;

    @Column(name = "graduation_cgpa", precision = 4, scale = 2)
    private BigDecimal graduationCgpa;

    @Column(name = "current_cgpa", nullable = false, precision = 4, scale = 2)
    private BigDecimal currentCgpa;

    @Column(name = "preferred_study_mode", nullable = false, length = 30)
    private String preferredStudyMode;

    // Demographics
    @Column(name = "family_income", nullable = false, precision = 12, scale = 2)
    private BigDecimal familyIncome;

    @Column(name = "category", nullable = false, length = 30)
    private String category;

    @Column(name = "caste", nullable = false, length = 50)
    private String caste;

    @Column(name = "religion", length = 50)
    private String religion; // Optional

    @Column(name = "disability_status", nullable = false, length = 50)
    private String disabilityStatus;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "student_languages", schema = "profile_schema", joinColumns = @JoinColumn(name = "student_profile_id"))
    @Column(name = "language", nullable = false)
    @Builder.Default
    private List<String> languagesKnown = new ArrayList<>();

    // Career & Bio
    @Column(name = "career_goal", nullable = false, columnDefinition = "TEXT")
    private String careerGoal;

    @Column(name = "bio", nullable = false, columnDefinition = "TEXT")
    private String bio;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Column(name = "profile_completion_pct", nullable = false)
    @Builder.Default
    private Integer profileCompletionPct = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Relationships
    @OneToMany(mappedBy = "studentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "studentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Interest> interests = new ArrayList<>();

    @OneToMany(mappedBy = "studentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "studentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Certification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "studentProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Project> projects = new ArrayList<>();
}
