package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "roadmap_tasks", schema = "roadmap_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"phase", "linkedResource"})
public class RoadmapTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "phase_id", nullable = false)
    private RoadmapPhase phase;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType; // FOUNDATION | CORE_SKILL | PROJECT | CERTIFICATION | REVISION | INTERVIEW_PREP

    @Column(name = "estimated_hours", nullable = false)
    @Builder.Default
    private Integer estimatedHours = 1;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "PENDING"; // PENDING | IN_PROGRESS | COMPLETED | SKIPPED

    @Column(name = "priority", nullable = false, length = 30)
    @Builder.Default
    private String priority = "MEDIUM"; // HIGH | MEDIUM | LOW

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_resource_id")
    private LearningResource linkedResource;

    @Column(name = "linked_certification_id")
    private UUID linkedCertificationId;

    @Column(name = "linked_project_id")
    private UUID linkedProjectId;
}
