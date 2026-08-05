package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "scholarship_recommendations", schema = "scholarship_schema")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"student", "scholarship"})
public class ScholarshipRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scholarship_id", nullable = false)
    private Scholarship scholarship;

    @Column(name = "eligibility_score", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal eligibilityScore = BigDecimal.ZERO;

    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal confidenceScore = BigDecimal.ZERO;

    @Column(name = "recommendation_reason", nullable = false, columnDefinition = "TEXT")
    private String recommendationReason;

    @Column(name = "application_status", nullable = false, length = 50)
    @Builder.Default
    private String applicationStatus = "NOT_ELIGIBLE"; // ELIGIBLE | PARTIALLY_ELIGIBLE | NOT_ELIGIBLE

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
