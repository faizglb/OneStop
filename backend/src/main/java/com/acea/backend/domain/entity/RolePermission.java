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
    name = "role_permissions",
    schema = "admin_schema",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_permission", columnNames = {"role", "permission"})
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @Column(name = "permission", nullable = false, length = 100)
    private String permission; // e.g. "USERS_READ", "USERS_WRITE", "CONTENT_READ", "CONTENT_WRITE", "AI_CONFIG_WRITE", "SYSTEM_CONFIG"

    @Column(name = "description", length = 255)
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
