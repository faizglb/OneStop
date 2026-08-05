-- ============================================================
-- V10: AI Personalized Roadmap Generator Schema
-- Sprint 8 | ACEA Platform
-- ============================================================

CREATE SCHEMA IF NOT EXISTS roadmap_schema;

-- ============================================================
-- roadmaps — Student learning roadmaps
-- ============================================================
CREATE TABLE roadmap_schema.roadmaps (
    id                     UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id             UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    title                  VARCHAR(250)  NOT NULL,
    target_career          VARCHAR(150)  NOT NULL,
    target_company         VARCHAR(150),
    duration               VARCHAR(50)   NOT NULL, -- 30-Day Plan | 60-Day Plan | 90-Day Plan | 6-Month Plan | 1-Year Plan | Custom Plan
    status                 VARCHAR(50)   NOT NULL DEFAULT 'ACTIVE', -- ACTIVE | PAUSED | COMPLETED | ARCHIVED
    completion_percentage  INTEGER       NOT NULL DEFAULT 0 CHECK (completion_percentage >= 0 AND completion_percentage <= 100),
    start_date             DATE          NOT NULL DEFAULT CURRENT_DATE,
    end_date               DATE          NOT NULL,
    weekly_hours           INTEGER       NOT NULL DEFAULT 10,
    created_at             TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- roadmap_phases — Phases of a roadmap
-- ============================================================
CREATE TABLE roadmap_schema.roadmap_phases (
    id           UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    roadmap_id   UUID          NOT NULL REFERENCES roadmap_schema.roadmaps(id) ON DELETE CASCADE,
    phase_name   VARCHAR(150)  NOT NULL,
    sequence     INTEGER       NOT NULL,
    description  TEXT
);

-- ============================================================
-- roadmap_tasks — Daily/Weekly tasks inside phases
-- ============================================================
CREATE TABLE roadmap_schema.roadmap_tasks (
    id                       UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    phase_id                 UUID          NOT NULL REFERENCES roadmap_schema.roadmap_phases(id) ON DELETE CASCADE,
    title                    VARCHAR(250)  NOT NULL,
    description              TEXT,
    task_type                VARCHAR(50)   NOT NULL, -- FOUNDATION | CORE_SKILL | PROJECT | CERTIFICATION | REVISION | INTERVIEW_PREP
    estimated_hours          INTEGER       NOT NULL DEFAULT 1 CHECK (estimated_hours >= 0),
    due_date                 DATE,
    status                   VARCHAR(50)   NOT NULL DEFAULT 'PENDING', -- PENDING | IN_PROGRESS | COMPLETED | SKIPPED
    priority                 VARCHAR(30)   NOT NULL DEFAULT 'MEDIUM', -- HIGH | MEDIUM | LOW
    linked_resource_id       UUID          REFERENCES learning_schema.learning_resources(id) ON DELETE SET NULL,
    linked_certification_id  UUID,
    linked_project_id        UUID
);

-- ============================================================
-- roadmap_milestones — Major goal milestones
-- ============================================================
CREATE TABLE roadmap_schema.roadmap_milestones (
    id                   UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    roadmap_id           UUID          NOT NULL REFERENCES roadmap_schema.roadmaps(id) ON DELETE CASCADE,
    title                VARCHAR(250)  NOT NULL,
    description          TEXT,
    completion_criteria  TEXT,
    status               VARCHAR(50)   NOT NULL DEFAULT 'PENDING' -- PENDING | COMPLETED
);

-- ============================================================
-- Indexes
-- ============================================================
CREATE INDEX idx_roadmaps_student ON roadmap_schema.roadmaps(student_id);
CREATE INDEX idx_phases_roadmap   ON roadmap_schema.roadmap_phases(roadmap_id);
CREATE INDEX idx_tasks_phase       ON roadmap_schema.roadmap_tasks(phase_id);
CREATE INDEX idx_tasks_status      ON roadmap_schema.roadmap_tasks(status);
CREATE INDEX idx_tasks_due_date    ON roadmap_schema.roadmap_tasks(due_date);
CREATE INDEX idx_milestones_rm    ON roadmap_schema.roadmap_milestones(roadmap_id);
