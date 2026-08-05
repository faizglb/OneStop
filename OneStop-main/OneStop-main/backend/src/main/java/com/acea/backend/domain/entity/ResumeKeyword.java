package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "resume_keywords", schema = "resume_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class ResumeKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private ResumeAnalysis analysis;

    @Column(name = "keyword", nullable = false, length = 255)
    private String keyword;

    @Column(name = "category", length = 100)
    private String category; // e.g., Technical Skill, Soft Skill, Tool, Framework

    @Column(name = "found", nullable = false)
    @Builder.Default
    private Boolean found = false;

    @Column(name = "frequency")
    @Builder.Default
    private Integer frequency = 0;

    @Column(name = "importance", nullable = false, length = 10)
    @Builder.Default
    private String importance = "MEDIUM"; // HIGH | MEDIUM | LOW
}
