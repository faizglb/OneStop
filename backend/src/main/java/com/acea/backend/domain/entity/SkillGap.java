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
@Table(name = "skill_gaps", schema = "skill_gap_schema")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"report", "improvementRecommendations"})
public class SkillGap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private SkillGapReport report;

    @Column(name = "skill_name", nullable = false, length = 255)
    private String skillName;

    @Column(name = "required_level", nullable = false, length = 50)
    private String requiredLevel; // BEGINNER | INTERMEDIATE | ADVANCED | EXPERT

    @Column(name = "current_level", nullable = false, length = 50)
    private String currentLevel;  // NONE | BEGINNER | INTERMEDIATE | ADVANCED | EXPERT

    @Column(name = "gap_percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal gapPercentage = BigDecimal.ZERO;

    @Column(name = "priority", nullable = false, length = 50)
    @Builder.Default
    private String priority = "MEDIUM"; // HIGH | MEDIUM | LOW

    @Column(name = "estimated_learning_hours", nullable = false)
    @Builder.Default
    private Integer estimatedLearningHours = 10;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "MISSING"; // MISSING | WEAK | STRONG | EMERGING

    @Column(name = "category", nullable = false, length = 50)
    @Builder.Default
    private String category = "TECHNICAL"; // TECHNICAL | SOFT_SKILL | CODING

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "skillGap", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ImprovementRecommendation> improvementRecommendations = new ArrayList<>();
}
