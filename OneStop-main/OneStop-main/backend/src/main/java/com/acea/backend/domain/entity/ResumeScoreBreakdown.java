package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "resume_score_breakdowns", schema = "resume_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class ResumeScoreBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false, unique = true)
    private ResumeAnalysis analysis;

    // ATS Score Weights: Keywords 35% | Sections 25% | Formatting 15% | Experience 15% | Education 10%
    @Column(name = "keyword_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal keywordScore = BigDecimal.ZERO;

    @Column(name = "section_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal sectionScore = BigDecimal.ZERO;

    @Column(name = "formatting_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal formattingScore = BigDecimal.ZERO;

    @Column(name = "experience_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal experienceScore = BigDecimal.ZERO;

    @Column(name = "education_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal educationScore = BigDecimal.ZERO;

    @Column(name = "grammar_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal grammarScore = BigDecimal.ZERO;
}
