package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "recommended_projects", schema = "recommendation_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"student", "careerRecommendation"})
public class RecommendedProject {

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

    @Column(name = "project_title", nullable = false, length = 200)
    private String projectTitle;

    @Column(name = "project_description", columnDefinition = "TEXT")
    private String projectDescription;

    @Column(name = "difficulty", nullable = false, length = 20)
    @Builder.Default
    private String difficulty = "MEDIUM"; // BEGINNER | INTERMEDIATE | ADVANCED

    @Column(name = "estimated_duration", nullable = false, length = 100)
    private String estimatedDuration;
}
