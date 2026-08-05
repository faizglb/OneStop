package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "roadmaps", schema = "roadmap_schema")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"student", "phases", "milestones"})
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "target_career", nullable = false, length = 150)
    private String targetCareer;

    @Column(name = "target_company", length = 150)
    private String targetCompany;

    @Column(name = "duration", nullable = false, length = 50)
    private String duration; // 30-Day Plan | 60-Day Plan | 90-Day Plan | 6-Month Plan | 1-Year Plan | Custom Plan

    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE | PAUSED | COMPLETED | ARCHIVED

    @Column(name = "completion_percentage", nullable = false)
    @Builder.Default
    private Integer completionPercentage = 0;

    @Column(name = "start_date", nullable = false)
    @Builder.Default
    private LocalDate startDate = LocalDate.now();

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "weekly_hours", nullable = false)
    @Builder.Default
    private Integer weeklyHours = 10;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequence ASC")
    @Builder.Default
    private List<RoadmapPhase> phases = new ArrayList<>();

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoadmapMilestone> milestones = new ArrayList<>();
}
