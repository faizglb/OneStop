package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "career_recommendations", schema = "recommendation_schema")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"student", "skills", "certifications", "projects"})
public class CareerRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "career_name", nullable = false, length = 200)
    private String careerName;

    @Column(name = "career_description", nullable = false, columnDefinition = "TEXT")
    private String careerDescription;

    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal confidenceScore = BigDecimal.ZERO;

    @Column(name = "match_score", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal matchScore = BigDecimal.ZERO;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "expected_salary", nullable = false, length = 100)
    private String expectedSalary;

    @Column(name = "industry_growth", nullable = false, length = 100)
    private String industryGrowth;

    @Column(name = "required_degree", nullable = false, length = 200)
    private String requiredDegree;

    @Column(name = "future_scope", nullable = false, columnDefinition = "TEXT")
    private String futureScope;

    @Column(name = "job_demand", nullable = false, length = 50)
    private String jobDemand;

    @Column(name = "estimated_learning_time", nullable = false, length = 100)
    private String estimatedLearningTime;

    /** Comma-separated or JSON list of pros. */
    @Column(name = "pros", nullable = false, columnDefinition = "TEXT")
    private String pros;

    /** Comma-separated or JSON list of challenges. */
    @Column(name = "challenges", nullable = false, columnDefinition = "TEXT")
    private String challenges;

    @Column(name = "is_bookmarked", nullable = false)
    @Builder.Default
    private Boolean isBookmarked = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "careerRecommendation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecommendedSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "careerRecommendation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecommendedCertification> certifications = new ArrayList<>();

    @OneToMany(mappedBy = "careerRecommendation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RecommendedProject> projects = new ArrayList<>();
}
