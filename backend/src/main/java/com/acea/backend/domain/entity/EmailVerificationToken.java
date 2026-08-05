package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Email verification token entity.
 * Also used for email-change verification requests.
 *
 * <p>Stored in: identity_schema.email_verifications
 */
@Entity
@Table(
    name = "email_verifications",
    schema = "identity_schema",
    indexes = {
        @Index(name = "idx_ev_user_id",    columnList = "user_id"),
        @Index(name = "idx_ev_token_hash", columnList = "token_hash", unique = true)
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ev_user"))
    private User user;

    /** SHA-256 hash of the token sent in the email link. */
    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    /** For email-change flow: the new email awaiting verification. */
    @Column(name = "new_email", nullable = true, length = 320)
    private String newEmail;

    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private boolean used = false;

    @Column(name = "used_at", nullable = true)
    private Instant usedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // ── Helpers ───────────────────────────────────────────────────────────

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }

    public void markUsed() {
        this.used = true;
        this.usedAt = Instant.now();
    }
}
