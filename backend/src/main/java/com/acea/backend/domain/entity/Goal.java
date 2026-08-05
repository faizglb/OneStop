package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "goals",
    schema = "profile_schema",
    indexes = {
        @Index(name = "idx_goals_sp_id", columnList = "student_profile_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "studentProfile")
@EqualsAndHashCode(of = "id")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @Column(name = "goal_title", nullable = false, length = 200)
    private String goalTitle;

    @Column(name = "goal_description", nullable = false, columnDefinition = "TEXT")
    private String goalDescription;

    @Column(name = "target_year", nullable = false)
    private Integer targetYear;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority; // e.g. HIGH, MEDIUM, LOW

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
