package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Personality trait or RIASEC interest category score.
 * Computed by the ScoringEngine after submission.
 */
@Entity
@Table(name = "trait_scores", schema = "assessment_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "result")
public class TraitScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id", nullable = false)
    private AssessmentResult result;

    /** e.g. OPENNESS, CONSCIENTIOUSNESS, REALISTIC, INVESTIGATIVE */
    @Column(name = "trait_key", nullable = false, length = 50)
    private String traitKey;

    /** Human-readable label, e.g. "Openness to Experience" */
    @Column(name = "trait_label", nullable = false, length = 100)
    private String traitLabel;

    @Column(name = "raw_score", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal rawScore = BigDecimal.ZERO;

    @Column(name = "max_score", nullable = false, precision = 8, scale = 2)
    @Builder.Default
    private BigDecimal maxScore = BigDecimal.ZERO;

    /** Normalized to 0–100 scale. */
    @Column(name = "normalized_score", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal normalizedScore = BigDecimal.ZERO;

    /** LOW | MODERATE | HIGH */
    @Column(name = "level", nullable = false, length = 20)
    @Builder.Default
    private String level = "MODERATE";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
