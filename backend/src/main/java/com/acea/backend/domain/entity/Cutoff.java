package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "cutoffs", schema = "college_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"college", "course"})
public class Cutoff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "exam_name", nullable = false, length = 100)
    private String examName;

    @Column(name = "category", nullable = false, length = 50)
    private String category; // General | OBC | SC | ST

    @Column(name = "opening_rank", nullable = false)
    private Integer openingRank;

    @Column(name = "closing_rank", nullable = false)
    private Integer closingRank;

    @Column(name = "year", nullable = false)
    private Integer year;
}
