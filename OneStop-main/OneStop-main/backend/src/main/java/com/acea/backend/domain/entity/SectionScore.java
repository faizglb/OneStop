package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/** Per-section/category score calculated when an assessment is submitted. */
@Entity
@Table(name = "section_scores", schema = "assessment_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"result", "category"})
public class SectionScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id", nullable = false)
    private AssessmentResult result;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private QuestionCategory category;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "raw_score", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal rawScore = BigDecimal.ZERO;

    @Column(name = "max_score", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal maxScore = BigDecimal.ZERO;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal percentage = BigDecimal.ZERO;

    @Column(name = "is_strength", nullable = false)
    @Builder.Default
    private Boolean isStrength = false;
}
