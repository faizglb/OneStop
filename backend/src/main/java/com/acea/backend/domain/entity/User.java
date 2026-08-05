package com.acea.backend.domain.entity;

import com.acea.backend.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

/**
 * Core User entity representing a platform account.
 *
 * <p>Implements {@link UserDetails} to integrate directly with Spring Security
 * without a separate UserDetails wrapper class.
 *
 * <p>Stored in: identity_schema.users (Flyway V1)
 * Designed for extension: future modules (Profile, AI, Enterprise)
 * add their own schemas with foreign keys to this entity.
 */
@Entity
@Table(
    name = "users",
    schema = "identity_schema",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_role",  columnList = "role"),
        @Index(name = "idx_users_deleted_at", columnList = "deleted_at")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"password", "refreshTokens", "emailVerificationTokens", "passwordResetTokens"})
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    // ── Identity ──────────────────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", unique = true, nullable = false, length = 320)
    private String email;

    @Column(name = "password_hash", nullable = true, length = 255)
    private String password; // Nullable for OAuth-only accounts

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", nullable = true, length = 20)
    private String phone;

    @Column(name = "avatar_url", nullable = true, length = 500)
    private String avatarUrl;

    // ── Role & Status ─────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.STUDENT;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "is_suspended", nullable = false)
    @Builder.Default
    private boolean suspended = false;

    @Column(name = "suspension_reason", nullable = true)
    private String suspensionReason;

    // ── Account Tier ──────────────────────────────────────────────────────

    @Column(name = "account_tier", nullable = false, length = 20)
    @Builder.Default
    private String accountTier = "FREE"; // FREE | PREMIUM | ENTERPRISE

    // ── Security Tracking ─────────────────────────────────────────────────

    @Column(name = "failed_login_count", nullable = false)
    @Builder.Default
    private int failedLoginCount = 0;

    @Column(name = "locked_until", nullable = true)
    private Instant lockedUntil;

    @Column(name = "last_login_at", nullable = true)
    private Instant lastLoginAt;

    @Column(name = "login_count", nullable = false)
    @Builder.Default
    private int loginCount = 0;

    // ── Localization ──────────────────────────────────────────────────────

    @Column(name = "locale", nullable = false, length = 10)
    @Builder.Default
    private String locale = "en-IN";

    @Column(name = "timezone", nullable = false, length = 50)
    @Builder.Default
    private String timezone = "Asia/Kolkata";

    // ── Soft Delete ───────────────────────────────────────────────────────

    @Column(name = "deleted_at", nullable = true)
    private Instant deletedAt;

    // ── Audit ─────────────────────────────────────────────────────────────

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ── Relationships ─────────────────────────────────────────────────────

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EmailVerificationToken> emailVerificationTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    // ── UserDetails Implementation ────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getSpringRole()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return deletedAt == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return lockedUntil == null || Instant.now().isAfter(lockedUntil);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active && !suspended && deletedAt == null;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
        this.active = false;
    }

    public void incrementFailedLogin() {
        this.failedLoginCount++;
    }

    public void resetFailedLogin() {
        this.failedLoginCount = 0;
        this.lockedUntil = null;
    }

    public void recordSuccessfulLogin() {
        this.lastLoginAt = Instant.now();
        this.loginCount++;
        resetFailedLogin();
    }
}
