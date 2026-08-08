package com.acea.backend.domain.entity;

import com.acea.backend.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "admin_logs",
    schema = "admin_schema",
    indexes = {
        @Index(name = "idx_admin_logs_actor", columnList = "actor_id"),
        @Index(name = "idx_admin_logs_module", columnList = "module"),
        @Index(name = "idx_admin_logs_created_at", columnList = "created_at")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Column(name = "actor_email", nullable = false, length = 320)
    private String actorEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_role", nullable = false, length = 30)
    private Role actorRole;

    @Column(name = "action", nullable = false, length = 100)
    private String action; // e.g. "CREATE_USER", "UPDATE_CAREER", "SUSPEND_USER", "UPDATE_AI_CONFIG"

    @Column(name = "module", nullable = false, length = 50)
    private String module; // e.g. "USER_MANAGEMENT", "CAREER_MANAGEMENT", "AI_CONFIG"

    @Column(name = "target_id", length = 255)
    private String targetId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
