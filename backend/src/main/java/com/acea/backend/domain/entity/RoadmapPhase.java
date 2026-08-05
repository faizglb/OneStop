package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "roadmap_phases", schema = "roadmap_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"roadmap", "tasks"})
public class RoadmapPhase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @Column(name = "phase_name", nullable = false, length = 150)
    private String phaseName;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "phase", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dueDate ASC")
    @Builder.Default
    private List<RoadmapTask> tasks = new ArrayList<>();
}
