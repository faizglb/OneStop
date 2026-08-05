package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "recommended_certifications", schema = "recommendation_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"student", "careerRecommendation"})
public class RecommendedCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_rec_id")
    private CareerRecommendation careerRecommendation;

    @Column(name = "certification_name", nullable = false, length = 200)
    private String certificationName;

    @Column(name = "provider", nullable = false, length = 100)
    private String provider;

    @Column(name = "confidence", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal confidence = BigDecimal.ZERO;
}
