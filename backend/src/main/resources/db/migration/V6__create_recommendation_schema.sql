-- ============================================================
-- V6: Recommendation Schema — AI Recommendation Module
-- Sprint 4 | ACEA Platform
-- ============================================================

CREATE SCHEMA IF NOT EXISTS recommendation_schema;

-- ============================================================
-- career_recommendations — Career roles matched by AI Engine
-- ============================================================
CREATE TABLE recommendation_schema.career_recommendations (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id          UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    career_name         VARCHAR(200)  NOT NULL,
    career_description  TEXT          NOT NULL,
    confidence_score    DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    match_score         DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    reason              TEXT          NOT NULL,
    expected_salary     VARCHAR(100)  NOT NULL,
    industry_growth     VARCHAR(100)  NOT NULL,
    required_degree     VARCHAR(200)  NOT NULL,
    future_scope        TEXT          NOT NULL,
    job_demand          VARCHAR(50)   NOT NULL,
    estimated_learning_time VARCHAR(100) NOT NULL,
    pros                TEXT          NOT NULL, -- comma separated or JSON
    challenges          TEXT          NOT NULL, -- comma separated or JSON
    is_bookmarked       BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- recommended_skills — Prioritized skills recommended
-- ============================================================
CREATE TABLE recommendation_schema.recommended_skills (
    id              UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id      UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    career_rec_id   UUID          REFERENCES recommendation_schema.career_recommendations(id) ON DELETE CASCADE,
    skill_name      VARCHAR(100)  NOT NULL,
    priority        VARCHAR(20)   NOT NULL DEFAULT 'MEDIUM', -- HIGH | MEDIUM | LOW
    confidence      DECIMAL(5,2)  NOT NULL DEFAULT 0.0
);

-- ============================================================
-- recommended_certifications — Suggested professional certs
-- ============================================================
CREATE TABLE recommendation_schema.recommended_certifications (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id          UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    career_rec_id       UUID          REFERENCES recommendation_schema.career_recommendations(id) ON DELETE CASCADE,
    certification_name  VARCHAR(200)  NOT NULL,
    provider            VARCHAR(100)  NOT NULL,
    confidence          DECIMAL(5,2)  NOT NULL DEFAULT 0.0
);

-- ============================================================
-- recommended_projects — Project ideas to build profile strength
-- ============================================================
CREATE TABLE recommendation_schema.recommended_projects (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id          UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    career_rec_id       UUID          REFERENCES recommendation_schema.career_recommendations(id) ON DELETE CASCADE,
    project_title       VARCHAR(200)  NOT NULL,
    project_description TEXT,
    difficulty          VARCHAR(20)   NOT NULL DEFAULT 'MEDIUM', -- BEGINNER | INTERMEDIATE | ADVANCED
    estimated_duration  VARCHAR(100)  NOT NULL
);

-- ============================================================
-- Indexes
-- ============================================================
CREATE INDEX idx_recs_student   ON recommendation_schema.career_recommendations(student_id);
CREATE INDEX idx_recs_bookmark  ON recommendation_schema.career_recommendations(is_bookmarked);
CREATE INDEX idx_rec_skills_rec ON recommendation_schema.recommended_skills(career_rec_id);
CREATE INDEX idx_rec_certs_rec  ON recommendation_schema.recommended_certifications(career_rec_id);
CREATE INDEX idx_rec_projs_rec  ON recommendation_schema.recommended_projects(career_rec_id);
