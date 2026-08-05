package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Category / section within an assessment.
 * Personality uses trait categories (OPENNESS etc), RIASEC uses codes.
 * Aptitude uses subject sections (Logical Reasoning etc).
 */
@Entity
@Table(name = "question_categories", schema = "assessment_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"assessment", "questions"})
public class QuestionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "weight_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPct;

    /** Trait/category key for scoring (e.g. OPENNESS, REALISTIC). */
    @Column(name = "trait_key", length = 50)
    private String traitKey;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
