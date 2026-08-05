-- ─────────────────────────────────────────────────────────────────────────────
-- V11: Resume Analyzer Schema
-- Sprint 9 — AI Resume Analyzer
-- ─────────────────────────────────────────────────────────────────────────────

CREATE SCHEMA IF NOT EXISTS resume_schema;

-- ── Resume Analyses (core record per upload) ──────────────────────────────────
CREATE TABLE resume_schema.resume_analyses (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL,
    file_name       VARCHAR(512) NOT NULL,
    file_url        TEXT        NOT NULL,
    file_type       VARCHAR(20) NOT NULL CHECK (file_type IN ('PDF', 'DOCX')),
    word_count      INT         DEFAULT 0,
    page_count      INT         DEFAULT 1,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                                CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    ats_score       NUMERIC(5,2) DEFAULT 0.00,
    overall_score   NUMERIC(5,2) DEFAULT 0.00,
    target_career   VARCHAR(255),
    error_message   TEXT,
    created_at      TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT fk_resume_user FOREIGN KEY (user_id)
        REFERENCES identity_schema.users(id) ON DELETE CASCADE
);

CREATE INDEX idx_resume_analyses_user_id   ON resume_schema.resume_analyses(user_id);
CREATE INDEX idx_resume_analyses_status    ON resume_schema.resume_analyses(status);
CREATE INDEX idx_resume_analyses_created   ON resume_schema.resume_analyses(created_at DESC);

-- ── Resume Score Breakdowns ──────────────────────────────────────────────────
CREATE TABLE resume_schema.resume_score_breakdowns (
    id                UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    analysis_id       UUID    NOT NULL UNIQUE,
    keyword_score     NUMERIC(5,2) DEFAULT 0.00,
    section_score     NUMERIC(5,2) DEFAULT 0.00,
    formatting_score  NUMERIC(5,2) DEFAULT 0.00,
    experience_score  NUMERIC(5,2) DEFAULT 0.00,
    education_score   NUMERIC(5,2) DEFAULT 0.00,
    grammar_score     NUMERIC(5,2) DEFAULT 0.00,
    CONSTRAINT fk_score_analysis FOREIGN KEY (analysis_id)
        REFERENCES resume_schema.resume_analyses(id) ON DELETE CASCADE
);

-- ── Resume Sections ───────────────────────────────────────────────────────────
CREATE TABLE resume_schema.resume_sections (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    analysis_id     UUID        NOT NULL,
    section_name    VARCHAR(50) NOT NULL
                    CHECK (section_name IN (
                        'CONTACT', 'SUMMARY', 'OBJECTIVE', 'EXPERIENCE',
                        'EDUCATION', 'SKILLS', 'PROJECTS', 'CERTIFICATIONS',
                        'ACHIEVEMENTS', 'LANGUAGES', 'HOBBIES', 'REFERENCES'
                    )),
    detected        BOOLEAN     NOT NULL DEFAULT FALSE,
    quality         VARCHAR(10) NOT NULL DEFAULT 'LOW'
                    CHECK (quality IN ('LOW', 'MEDIUM', 'HIGH')),
    content_snippet TEXT,
    CONSTRAINT fk_section_analysis FOREIGN KEY (analysis_id)
        REFERENCES resume_schema.resume_analyses(id) ON DELETE CASCADE
);

CREATE INDEX idx_resume_sections_analysis ON resume_schema.resume_sections(analysis_id);

-- ── Resume Keywords ───────────────────────────────────────────────────────────
CREATE TABLE resume_schema.resume_keywords (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    analysis_id UUID        NOT NULL,
    keyword     VARCHAR(255) NOT NULL,
    category    VARCHAR(100),
    found       BOOLEAN     NOT NULL DEFAULT FALSE,
    frequency   INT         DEFAULT 0,
    importance  VARCHAR(10) NOT NULL DEFAULT 'MEDIUM'
                CHECK (importance IN ('HIGH', 'MEDIUM', 'LOW')),
    CONSTRAINT fk_keyword_analysis FOREIGN KEY (analysis_id)
        REFERENCES resume_schema.resume_analyses(id) ON DELETE CASCADE
);

CREATE INDEX idx_resume_keywords_analysis ON resume_schema.resume_keywords(analysis_id);
CREATE INDEX idx_resume_keywords_found    ON resume_schema.resume_keywords(found);

-- ── Resume Suggestions ────────────────────────────────────────────────────────
CREATE TABLE resume_schema.resume_suggestions (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    analysis_id UUID        NOT NULL,
    category    VARCHAR(50) NOT NULL
                CHECK (category IN (
                    'KEYWORDS', 'FORMATTING', 'SECTIONS', 'EXPERIENCE',
                    'EDUCATION', 'PROJECTS', 'GRAMMAR', 'CONTACT', 'GENERAL'
                )),
    priority    VARCHAR(10) NOT NULL DEFAULT 'MEDIUM'
                CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW')),
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    example     TEXT,
    CONSTRAINT fk_suggestion_analysis FOREIGN KEY (analysis_id)
        REFERENCES resume_schema.resume_analyses(id) ON DELETE CASCADE
);

CREATE INDEX idx_resume_suggestions_analysis  ON resume_schema.resume_suggestions(analysis_id);
CREATE INDEX idx_resume_suggestions_priority  ON resume_schema.resume_suggestions(priority);
