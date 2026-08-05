package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "courses", schema = "college_schema")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "college")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @Column(name = "course_name", nullable = false, length = 200)
    private String courseName;

    @Column(name = "stream", nullable = false, length = 100)
    private String stream;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "degree", nullable = false, length = 100)
    private String degree;

    @Column(name = "eligibility", columnDefinition = "TEXT")
    private String eligibility;

    @Column(name = "entrance_exam", length = 100)
    private String entranceExam;

    @Column(name = "seat_intake", nullable = false)
    @Builder.Default
    private Integer seatIntake = 0;
}
