package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "skill_gap_reports", schema = "skill_gap_schema")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"student", "skillGaps"})
public class SkillGapReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "career_id", nullable = false, length = 255)
    private String careerId;

    @Column(name = "target_career", nullable = false, length = 255)
    private String targetCareer;

    @Column(name = "overall_readiness_score", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal overallReadinessScore = BigDecimal.ZERO;

    @Column(name = "overall_gap_percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal overallGapPercentage = BigDecimal.ZERO;

    @Column(name = "estimated_job_readiness_months", nullable = false)
    @Builder.Default
    private Integer estimatedJobReadinessMonths = 6;

    @Column(name = "technical_readiness", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal technicalReadiness = BigDecimal.ZERO;

    @Column(name = "soft_skill_readiness", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal softSkillReadiness = BigDecimal.ZERO;

    @Column(name = "project_readiness", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal projectReadiness = BigDecimal.ZERO;

    @Column(name = "interview_readiness", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal interviewReadiness = BigDecimal.ZERO;

    @Column(name = "resume_readiness", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal resumeReadiness = BigDecimal.ZERO;

    @Column(name = "coding_readiness", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal codingReadiness = BigDecimal.ZERO;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SkillGap> skillGaps = new ArrayList<>();
}
