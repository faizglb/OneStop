package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Password reset token entity.
 *
 * <p>Security: Only SHA-256 hash stored; single-use; short TTL (2h default).
 * Stored in: identity_schema.password_resets
 */
@Entity
@Table(
    name = "password_resets",
    schema = "identity_schema",
    indexes = {
        @Index(name = "idx_pr_user_id",    columnList = "user_id"),
        @Index(name = "idx_pr_token_hash", columnList = "token_hash", unique = true)
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pr_user"))
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private boolean used = false;

    @Column(name = "used_at", nullable = true)
    private Instant usedAt;

    @Column(name = "requested_from_ip", nullable = true, length = 45)
    private String requestedFromIp;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    public boolean isValid()   { return !used && !isExpired(); }
    public void markUsed()     { this.used = true; this.usedAt = Instant.now(); }
}
