-- ============================================================
-- SCHEMA: profile_schema
-- ============================================================
CREATE SCHEMA IF NOT EXISTS profile_schema;

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ────────────────────────────────────────────────────────────
-- TABLE: student_profiles
-- ────────────────────────────────────────────────────────────
CREATE TABLE profile_schema.student_profiles (
    id                     UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                UUID          NOT NULL UNIQUE REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    first_name             VARCHAR(100)  NOT NULL,
    last_name              VARCHAR(100)  NOT NULL,
    date_of_birth          DATE          NOT NULL,
    gender                 VARCHAR(20)   NOT NULL,
    phone_number           VARCHAR(20)   NOT NULL,
    profile_photo          VARCHAR(500)  NULL,
    country                VARCHAR(100)  NOT NULL,
    state                  VARCHAR(100)  NOT NULL,
    district               VARCHAR(100)  NOT NULL,
    city                   VARCHAR(100)  NOT NULL,
    address                VARCHAR(300)  NOT NULL,
    pin_code               VARCHAR(10)   NOT NULL,
    
    -- Academics
    school_name            VARCHAR(200)  NOT NULL,
    college_name           VARCHAR(200)  NOT NULL,
    current_course         VARCHAR(200)  NOT NULL,
    branch                 VARCHAR(200)  NOT NULL,
    current_year           INTEGER       NOT NULL,
    education_level        VARCHAR(50)   NOT NULL,
    tenth_percentage       DECIMAL(5,2)  NOT NULL,
    twelfth_percentage     DECIMAL(5,2)  NOT NULL,
    graduation_cgpa        DECIMAL(4,2)  NULL,
    current_cgpa           DECIMAL(4,2)  NOT NULL,
    preferred_study_mode   VARCHAR(30)   NOT NULL,
    
    -- Demographics
    family_income          DECIMAL(12,2) NOT NULL,
    category               VARCHAR(30)   NOT NULL,
    caste                  VARCHAR(50)   NOT NULL,
    religion               VARCHAR(50)   NULL, -- optional
    disability_status      VARCHAR(50)   NOT NULL,
    
    -- Career & Bio
    career_goal            TEXT          NOT NULL,
    bio                    TEXT          NOT NULL,
    linkedin_url           VARCHAR(500)  NULL,
    github_url             VARCHAR(500)  NULL,
    portfolio_url          VARCHAR(500)  NULL,
    resume_url             VARCHAR(500)  NULL,
    
    -- Metadata
    profile_completion_pct INTEGER       NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ────────────────────────────────────────────────────────────
-- TABLE: skills
-- ────────────────────────────────────────────────────────────
CREATE TABLE profile_schema.skills (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    student_profile_id UUID         NOT NULL REFERENCES profile_schema.student_profiles(id) ON DELETE CASCADE,
    skill_name         VARCHAR(100) NOT NULL,
    skill_level        VARCHAR(20)  NOT NULL,
    experience_months  INTEGER      NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ────────────────────────────────────────────────────────────
-- TABLE: interests
-- ────────────────────────────────────────────────────────────
CREATE TABLE profile_schema.interests (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    student_profile_id UUID         NOT NULL REFERENCES profile_schema.student_profiles(id) ON DELETE CASCADE,
    interest_name      VARCHAR(100) NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ────────────────────────────────────────────────────────────
-- TABLE: goals
-- ────────────────────────────────────────────────────────────
CREATE TABLE profile_schema.goals (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    student_profile_id UUID         NOT NULL REFERENCES profile_schema.student_profiles(id) ON DELETE CASCADE,
    goal_title         VARCHAR(200) NOT NULL,
    goal_description   TEXT         NOT NULL,
    target_year        INTEGER      NOT NULL,
    priority           VARCHAR(20)  NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ────────────────────────────────────────────────────────────
-- TABLE: certifications
-- ────────────────────────────────────────────────────────────
CREATE TABLE profile_schema.certifications (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    student_profile_id UUID         NOT NULL REFERENCES profile_schema.student_profiles(id) ON DELETE CASCADE,
    certificate_name   VARCHAR(200) NOT NULL,
    platform           VARCHAR(100) NOT NULL,
    issue_date         DATE         NOT NULL,
    certificate_url    VARCHAR(500) NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ────────────────────────────────────────────────────────────
-- TABLE: projects
-- ────────────────────────────────────────────────────────────
CREATE TABLE profile_schema.projects (
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    student_profile_id UUID         NOT NULL REFERENCES profile_schema.student_profiles(id) ON DELETE CASCADE,
    project_title      VARCHAR(200) NOT NULL,
    description        TEXT         NOT NULL,
    github_link        VARCHAR(500) NULL,
    live_link          VARCHAR(500) NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_sp_user_id ON profile_schema.student_profiles(user_id);
CREATE INDEX idx_skills_sp_id ON profile_schema.skills(student_profile_id);
CREATE INDEX idx_interests_sp_id ON profile_schema.interests(student_profile_id);
CREATE INDEX idx_goals_sp_id ON profile_schema.goals(student_profile_id);
CREATE INDEX idx_certifications_sp_id ON profile_schema.certifications(student_profile_id);
CREATE INDEX idx_projects_sp_id ON profile_schema.projects(student_profile_id);

-- Triggers for updated_at auto-updates on every modification
CREATE TRIGGER trg_student_profiles_updated_at
    BEFORE UPDATE ON profile_schema.student_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_skills_updated_at
    BEFORE UPDATE ON profile_schema.skills
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_interests_updated_at
    BEFORE UPDATE ON profile_schema.interests
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_goals_updated_at
    BEFORE UPDATE ON profile_schema.goals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_certifications_updated_at
    BEFORE UPDATE ON profile_schema.certifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_projects_updated_at
    BEFORE UPDATE ON profile_schema.projects
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Collection tables for element collections
CREATE TABLE profile_schema.student_languages (
    student_profile_id UUID NOT NULL REFERENCES profile_schema.student_profiles(id) ON DELETE CASCADE,
    language           VARCHAR(50) NOT NULL,
    PRIMARY KEY (student_profile_id, language)
);

CREATE TABLE profile_schema.project_tech_stacks (
    project_id         UUID NOT NULL REFERENCES profile_schema.projects(id) ON DELETE CASCADE,
    tech               VARCHAR(50) NOT NULL,
    PRIMARY KEY (project_id, tech)
);
