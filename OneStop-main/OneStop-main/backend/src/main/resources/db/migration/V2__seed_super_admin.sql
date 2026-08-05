-- ============================================================
-- V2__seed_super_admin.sql
-- Seeds the initial SUPER_ADMIN account for platform access
-- Password: Admin@123456 (BCrypt cost=12 — CHANGE IN PRODUCTION)
-- ============================================================

INSERT INTO identity_schema.users (
    id, email, password_hash, first_name, last_name,
    role, account_tier, is_email_verified, is_active,
    locale, timezone, created_at, updated_at
) VALUES (
    gen_random_uuid(),
    'admin@acea.io',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj.dQMYR3FKC', -- Admin@123456
    'Platform',
    'Admin',
    'SUPER_ADMIN',
    'ENTERPRISE',
    TRUE,
    TRUE,
    'en-IN',
    'Asia/Kolkata',
    NOW(),
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Add a comment for auditors
COMMENT ON TABLE identity_schema.users IS
    'Core user accounts. Super admin seeded by V2 migration.';
