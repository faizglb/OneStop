package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "certifications",
    schema = "profile_schema",
    indexes = {
        @Index(name = "idx_certifications_sp_id", columnList = "student_profile_id")
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
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @Column(name = "certificate_name", nullable = false, length = 200)
    private String certificateName;

    @Column(name = "platform", nullable = false, length = 100)
    private String platform;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "certificate_url", nullable = false, length = 500)
    private String certificateUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
