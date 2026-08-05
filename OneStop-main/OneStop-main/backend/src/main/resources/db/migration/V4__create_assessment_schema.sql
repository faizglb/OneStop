-- ============================================================
-- V4: Assessment Schema — AI Assessment Module
-- Sprint 3 | ACEA Platform
-- ============================================================

-- ── Prerequisite: updated_at auto-update function (already defined in V1) ──

CREATE SCHEMA IF NOT EXISTS assessment_schema;

-- ============================================================
-- assessments — Assessment Templates
-- ============================================================
CREATE TABLE assessment_schema.assessments (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    assessment_type     VARCHAR(20)   NOT NULL,   -- APTITUDE | PERSONALITY | INTEREST
    title               VARCHAR(200)  NOT NULL,
    description         TEXT          NOT NULL,
    instructions        TEXT          NOT NULL,
    time_limit_minutes  INTEGER       NOT NULL DEFAULT 60,
    total_questions     INTEGER       NOT NULL,
    passing_score       INTEGER       NOT NULL DEFAULT 60,
    status              VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_assessment_type CHECK (assessment_type IN ('APTITUDE', 'PERSONALITY', 'INTEREST')),
    CONSTRAINT chk_assessment_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT'))
);

-- ============================================================
-- question_categories — Groupings within an assessment
-- ============================================================
CREATE TABLE assessment_schema.question_categories (
    id              UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    assessment_id   UUID          NOT NULL REFERENCES assessment_schema.assessments(id) ON DELETE CASCADE,
    name            VARCHAR(100)  NOT NULL,
    description     TEXT,
    display_order   INTEGER       NOT NULL DEFAULT 0,
    weight_pct      DECIMAL(5,2)  NOT NULL DEFAULT 100.0,
    trait_key       VARCHAR(50),  -- For personality (OPENNESS etc) / RIASEC (REALISTIC etc)
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- questions — Individual test questions
-- ============================================================
CREATE TABLE assessment_schema.questions (
    id              UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    assessment_id   UUID         NOT NULL REFERENCES assessment_schema.assessments(id) ON DELETE CASCADE,
    category_id     UUID         NOT NULL REFERENCES assessment_schema.question_categories(id),
    question_text   TEXT         NOT NULL,
    difficulty      VARCHAR(10)  NOT NULL DEFAULT 'MEDIUM',
    question_order  INTEGER      NOT NULL DEFAULT 0,
    marks           INTEGER      NOT NULL DEFAULT 1,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_difficulty CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'))
);

-- ============================================================
-- options — Answer options per question
-- ============================================================
CREATE TABLE assessment_schema.options (
    id           UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    question_id  UUID         NOT NULL REFERENCES assessment_schema.questions(id) ON DELETE CASCADE,
    option_text  TEXT         NOT NULL,
    is_correct   BOOLEAN      NOT NULL DEFAULT FALSE,
    weight       DECIMAL(4,2) NOT NULL DEFAULT 0.0,  -- Used for Likert scale (personality/interest)
    option_order INTEGER      NOT NULL DEFAULT 0,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- assessment_results — Student attempt records
-- ============================================================
CREATE TABLE assessment_schema.assessment_results (
    id               UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id       UUID         NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    assessment_id    UUID         NOT NULL REFERENCES assessment_schema.assessments(id),
    assessment_type  VARCHAR(20)  NOT NULL,
    total_score      DECIMAL(8,2) NOT NULL DEFAULT 0.0,
    max_score        DECIMAL(8,2) NOT NULL DEFAULT 0.0,
    percentage       DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    percentile       DECIMAL(5,2),
    time_taken_secs  INTEGER      NOT NULL DEFAULT 0,
    status           VARCHAR(20)  NOT NULL DEFAULT 'IN_PROGRESS',
    started_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    completed_at     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_result_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED'))
);

-- ============================================================
-- result_answers — Individual question answers in a result
-- ============================================================
CREATE TABLE assessment_schema.result_answers (
    id                UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    result_id         UUID         NOT NULL REFERENCES assessment_schema.assessment_results(id) ON DELETE CASCADE,
    question_id       UUID         NOT NULL REFERENCES assessment_schema.questions(id),
    selected_option_id UUID        REFERENCES assessment_schema.options(id),
    score_awarded     DECIMAL(4,2) NOT NULL DEFAULT 0.0,
    answered_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    is_flagged        BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_result_question UNIQUE (result_id, question_id)
);

-- ============================================================
-- section_scores — Per-category scores calculated on submission
-- ============================================================
CREATE TABLE assessment_schema.section_scores (
    id           UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    result_id    UUID          NOT NULL REFERENCES assessment_schema.assessment_results(id) ON DELETE CASCADE,
    category_id  UUID          NOT NULL REFERENCES assessment_schema.question_categories(id),
    category_name VARCHAR(100) NOT NULL,
    raw_score    DECIMAL(8,2)  NOT NULL DEFAULT 0.0,
    max_score    DECIMAL(8,2)  NOT NULL DEFAULT 0.0,
    percentage   DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    is_strength  BOOLEAN       NOT NULL DEFAULT FALSE
);

-- ============================================================
-- trait_scores — Personality / RIASEC trait-level scores
-- ============================================================
CREATE TABLE assessment_schema.trait_scores (
    id               UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    result_id        UUID          NOT NULL REFERENCES assessment_schema.assessment_results(id) ON DELETE CASCADE,
    trait_key        VARCHAR(50)   NOT NULL,  -- e.g. OPENNESS, REALISTIC
    trait_label      VARCHAR(100)  NOT NULL,
    raw_score        DECIMAL(8,2)  NOT NULL DEFAULT 0.0,
    max_score        DECIMAL(8,2)  NOT NULL DEFAULT 0.0,
    normalized_score DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    level            VARCHAR(20)   NOT NULL DEFAULT 'MODERATE',  -- LOW | MODERATE | HIGH
    description      TEXT
);

-- ============================================================
-- Indexes
-- ============================================================
CREATE INDEX idx_assessments_type   ON assessment_schema.assessments(assessment_type);
CREATE INDEX idx_assessments_status ON assessment_schema.assessments(status);
CREATE INDEX idx_questions_assessment ON assessment_schema.questions(assessment_id);
CREATE INDEX idx_questions_category   ON assessment_schema.questions(category_id);
CREATE INDEX idx_options_question      ON assessment_schema.options(question_id);
CREATE INDEX idx_results_student       ON assessment_schema.assessment_results(student_id);
CREATE INDEX idx_results_assessment    ON assessment_schema.assessment_results(assessment_id);
CREATE INDEX idx_results_status        ON assessment_schema.assessment_results(status);
CREATE INDEX idx_answers_result        ON assessment_schema.result_answers(result_id);
CREATE INDEX idx_section_scores_result ON assessment_schema.section_scores(result_id);
CREATE INDEX idx_trait_scores_result   ON assessment_schema.trait_scores(result_id);

-- ============================================================
-- Triggers for updated_at
-- ============================================================
CREATE TRIGGER trg_assessments_updated_at
    BEFORE UPDATE ON assessment_schema.assessments
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_results_updated_at
    BEFORE UPDATE ON assessment_schema.assessment_results
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
