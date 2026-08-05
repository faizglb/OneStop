package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "learning_recommendations", schema = "learning_schema")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"student", "resource"})
public class LearningRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private LearningResource resource;

    @Column(name = "recommended_skill", nullable = false, length = 100)
    private String recommendedSkill;

    @Column(name = "priority", nullable = false, length = 30)
    @Builder.Default
    private String priority = "MEDIUM"; // HIGH | MEDIUM | LOW

    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal confidenceScore = BigDecimal.ZERO;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "estimated_completion_time", length = 50)
    private String estimatedCompletionTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
