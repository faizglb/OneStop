package com.acea.backend.domain.entity;

import com.acea.backend.domain.enums.AssessmentType;
import com.acea.backend.domain.enums.ResultStatus;
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

/**
 * Records a student's attempt at an assessment.
 * Created when student starts, updated on save-answer and submit.
 */
@Entity
@Table(name = "assessment_results", schema = "assessment_schema")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"student", "assessment", "answers", "sectionScores", "traitScores"})
public class AssessmentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", nullable = false, length = 20)
    private AssessmentType assessmentType;

    @Column(name = "total_score", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal totalScore = BigDecimal.ZERO;

    @Column(name = "max_score", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal maxScore = BigDecimal.ZERO;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal percentage = BigDecimal.ZERO;

    @Column(name = "percentile", precision = 5, scale = 2)
    private BigDecimal percentile;

    @Column(name = "time_taken_secs", nullable = false)
    @Builder.Default
    private Integer timeTakenSecs = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ResultStatus status = ResultStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @OneToMany(mappedBy = "result", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResultAnswer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "result", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SectionScore> sectionScores = new ArrayList<>();

    @OneToMany(mappedBy = "result", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TraitScore> traitScores = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
