package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "resume_sections", schema = "resume_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class ResumeSection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private ResumeAnalysis analysis;

    @Column(name = "section_name", nullable = false, length = 50)
    private String sectionName; // CONTACT | SUMMARY | EXPERIENCE | EDUCATION | SKILLS | PROJECTS | CERTIFICATIONS | ACHIEVEMENTS

    @Column(name = "detected", nullable = false)
    @Builder.Default
    private Boolean detected = false;

    @Column(name = "quality", nullable = false, length = 10)
    @Builder.Default
    private String quality = "LOW"; // LOW | MEDIUM | HIGH

    @Column(name = "content_snippet", columnDefinition = "TEXT")
    private String contentSnippet;
}
