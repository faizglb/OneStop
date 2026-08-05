package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "improvement_recommendations", schema = "skill_gap_schema")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"skillGap"})
public class ImprovementRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gap_id", nullable = false)
    private SkillGap skillGap;

    @Column(name = "resource_id", length = 255)
    private String resourceId;

    @Column(name = "project_id", length = 255)
    private String projectId;

    @Column(name = "certificate_id", length = 255)
    private String certificateId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "recommendation_type", nullable = false, length = 50)
    private String recommendationType; // RESOURCE | PROJECT | COURSE | CERTIFICATION | PRACTICE

    @Column(name = "priority", nullable = false, length = 50)
    @Builder.Default
    private String priority = "MEDIUM";

    @Column(name = "estimated_duration", length = 100)
    private String estimatedDuration;

    @Column(name = "url", length = 512)
    private String url;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
