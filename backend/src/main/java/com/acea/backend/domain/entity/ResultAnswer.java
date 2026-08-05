package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Records a student's answer to a single question within an assessment result. */
@Entity
@Table(
    name = "result_answers",
    schema = "assessment_schema",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_result_question",
        columnNames = {"result_id", "question_id"}
    )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"result", "question", "selectedOption"})
public class ResultAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id", nullable = false)
    private AssessmentResult result;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private Option selectedOption;

    @Column(name = "score_awarded", nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal scoreAwarded = BigDecimal.ZERO;

    @Column(name = "answered_at", nullable = false)
    @Builder.Default
    private Instant answeredAt = Instant.now();

    @Column(name = "is_flagged", nullable = false)
    @Builder.Default
    private Boolean isFlagged = false;
}
