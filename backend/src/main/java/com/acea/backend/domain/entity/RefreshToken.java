package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Persisted refresh token for JWT rotation strategy.
 *
 * <p>Security model:
 * - Only the SHA-256 hash of the token is stored (never plaintext)
 * - One token per session; revoked on logout or rotation
 * - Expired tokens are cleaned up by a scheduled job
 *
 * <p>Stored in: identity_schema.refresh_tokens
 */
@Entity
@Table(
    name = "refresh_tokens",
    schema = "identity_schema",
    indexes = {
        @Index(name = "idx_rt_user_id",    columnList = "user_id"),
        @Index(name = "idx_rt_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_rt_session_id", columnList = "session_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rt_user"))
    private User user;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    /** SHA-256 hash of the actual opaque refresh token string. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "device_info", nullable = true, length = 500)
    private String deviceInfo;

    @Column(name = "ip_address", nullable = true, length = 45)
    private String ipAddress;

    @Column(name = "is_revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(name = "revoked_at", nullable = true)
    private Instant revokedAt;

    @Column(name = "revoke_reason", nullable = true, length = 50)
    private String revokeReason;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // ── Helpers ───────────────────────────────────────────────────────────

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }

    public void revoke(String reason) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.revokeReason = reason;
    }
}
