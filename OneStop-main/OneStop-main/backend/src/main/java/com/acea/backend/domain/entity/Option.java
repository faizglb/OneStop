package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Answer option for a question.
 * For aptitude: is_correct=true marks the correct answer.
 * For personality/interest: weight (1-5) represents the Likert scale value.
 * NOTE: is_correct and weight are NEVER exposed via the REST API.
 */
@Entity
@Table(name = "options", schema = "assessment_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "question")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    /** Server-side only — never exposed to clients. */
    @Column(name = "is_correct", nullable = false)
    @Builder.Default
    private Boolean isCorrect = false;

    /** Likert weight for personality/interest; 0 for aptitude. Server-side only. */
    @Column(name = "weight", nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal weight = BigDecimal.ZERO;

    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
