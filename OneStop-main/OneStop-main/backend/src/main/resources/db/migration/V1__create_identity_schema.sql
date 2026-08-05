-- ============================================================
-- V1__create_identity_schema.sql
-- Flyway Migration — Sprint 1, Auth Module
-- Creates identity_schema and all authentication-related tables
-- ============================================================

-- Create schema
CREATE SCHEMA IF NOT EXISTS identity_schema;

-- Extensions (idempotent)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- ────────────────────────────────────────────────────────────
-- TABLE: users
-- ────────────────────────────────────────────────────────────
CREATE TABLE identity_schema.users (
    id                   UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email                VARCHAR(320) NOT NULL,
    password_hash        VARCHAR(255) NULL,
    first_name           VARCHAR(100) NOT NULL,
    last_name            VARCHAR(100) NOT NULL,
    phone                VARCHAR(20)  NULL,
    avatar_url           VARCHAR(500) NULL,

    -- Role & Status
    role                 VARCHAR(20)  NOT NULL DEFAULT 'STUDENT'
                             CONSTRAINT chk_users_role CHECK (
                                 role IN ('STUDENT','PREMIUM_STUDENT','MENTOR','HR_MANAGER','ADMIN','SUPER_ADMIN')
                             ),
    account_tier         VARCHAR(20)  NOT NULL DEFAULT 'FREE'
                             CONSTRAINT chk_users_tier CHECK (
                                 account_tier IN ('FREE','PREMIUM','ENTERPRISE')
                             ),
    is_email_verified    BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active            BOOLEAN      NOT NULL DEFAULT TRUE,
    is_suspended         BOOLEAN      NOT NULL DEFAULT FALSE,
    suspension_reason    TEXT         NULL,

    -- Security
    failed_login_count   INTEGER      NOT NULL DEFAULT 0,
    locked_until         TIMESTAMPTZ  NULL,
    last_login_at        TIMESTAMPTZ  NULL,
    login_count          INTEGER      NOT NULL DEFAULT 0,

    -- Localization
    locale               VARCHAR(10)  NOT NULL DEFAULT 'en-IN',
    timezone             VARCHAR(50)  NOT NULL DEFAULT 'Asia/Kolkata',

    -- Soft Delete & Audit
    deleted_at           TIMESTAMPTZ  NULL,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_email_format CHECK (
        email ~* '^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$'
    ),
    CONSTRAINT chk_users_name_not_empty CHECK (
        LENGTH(TRIM(first_name)) > 0 AND LENGTH(TRIM(last_name)) > 0
    )
);

COMMENT ON TABLE  identity_schema.users              IS 'Core user accounts — canonical identity record';
COMMENT ON COLUMN identity_schema.users.password_hash IS 'BCrypt hash (cost=12). NULL for OAuth-only accounts.';
COMMENT ON COLUMN identity_schema.users.role         IS 'RBAC role assigned to user';
COMMENT ON COLUMN identity_schema.users.deleted_at   IS 'Soft delete: NULL=active, timestamp=deleted';

-- ────────────────────────────────────────────────────────────
-- TABLE: refresh_tokens
-- ────────────────────────────────────────────────────────────
CREATE TABLE identity_schema.refresh_tokens (
    id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID         NOT NULL
                       REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    session_id     VARCHAR(100) NOT NULL,
    token_hash     VARCHAR(255) NOT NULL,
    device_info    VARCHAR(500) NULL,
    ip_address     VARCHAR(45)  NULL,
    is_revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    revoked_at     TIMESTAMPTZ  NULL,
    revoke_reason  VARCHAR(50)  NULL,
    expires_at     TIMESTAMPTZ  NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_rt_token_hash UNIQUE (token_hash),
    CONSTRAINT chk_rt_expiry    CHECK  (expires_at > created_at)
);

COMMENT ON TABLE  identity_schema.refresh_tokens            IS 'JWT refresh tokens — only SHA-256 hash stored';
COMMENT ON COLUMN identity_schema.refresh_tokens.token_hash IS 'SHA-256 hex digest of the opaque refresh token string';

-- ────────────────────────────────────────────────────────────
-- TABLE: email_verifications
-- ────────────────────────────────────────────────────────────
CREATE TABLE identity_schema.email_verifications (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL
                    REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(255) NOT NULL,
    new_email   VARCHAR(320) NULL,
    is_used     BOOLEAN      NOT NULL DEFAULT FALSE,
    used_at     TIMESTAMPTZ  NULL,
    expires_at  TIMESTAMPTZ  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_ev_token_hash UNIQUE (token_hash)
);

COMMENT ON TABLE identity_schema.email_verifications IS 'Email verification tokens — also used for email-change flow';

-- ────────────────────────────────────────────────────────────
-- TABLE: password_resets
-- ────────────────────────────────────────────────────────────
CREATE TABLE identity_schema.password_resets (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID         NOT NULL
                            REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    token_hash          VARCHAR(255) NOT NULL,
    is_used             BOOLEAN      NOT NULL DEFAULT FALSE,
    used_at             TIMESTAMPTZ  NULL,
    requested_from_ip   VARCHAR(45)  NULL,
    expires_at          TIMESTAMPTZ  NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_pr_token_hash UNIQUE (token_hash)
);

COMMENT ON TABLE identity_schema.password_resets IS 'Password reset tokens — single-use, 2h TTL';

-- ────────────────────────────────────────────────────────────
-- INDEXES
-- ────────────────────────────────────────────────────────────
CREATE INDEX idx_users_email         ON identity_schema.users(email);
CREATE INDEX idx_users_role          ON identity_schema.users(role);
CREATE INDEX idx_users_active        ON identity_schema.users(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_users_not_deleted   ON identity_schema.users(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_name_trgm     ON identity_schema.users USING gin((first_name || ' ' || last_name) gin_trgm_ops);

CREATE INDEX idx_rt_user_id          ON identity_schema.refresh_tokens(user_id);
CREATE INDEX idx_rt_active           ON identity_schema.refresh_tokens(expires_at, is_revoked)
                                      WHERE is_revoked = FALSE;
CREATE INDEX idx_rt_session_id       ON identity_schema.refresh_tokens(session_id);

CREATE INDEX idx_ev_user_id          ON identity_schema.email_verifications(user_id);
CREATE INDEX idx_ev_active           ON identity_schema.email_verifications(expires_at, is_used)
                                      WHERE is_used = FALSE;

CREATE INDEX idx_pr_user_id          ON identity_schema.password_resets(user_id);
CREATE INDEX idx_pr_active           ON identity_schema.password_resets(expires_at, is_used)
                                      WHERE is_used = FALSE;

-- ────────────────────────────────────────────────────────────
-- TRIGGER: auto-update updated_at on users
-- ────────────────────────────────────────────────────────────
CREATE OR REPLACE FUNCTION identity_schema.update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON identity_schema.users
    FOR EACH ROW EXECUTE FUNCTION identity_schema.update_updated_at();
