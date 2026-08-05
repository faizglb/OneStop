-- Sprint 10: Create Skill Gap Schema and Tables

CREATE SCHEMA IF NOT EXISTS skill_gap_schema;

-- 1. Skill Gap Reports Table
CREATE TABLE IF NOT EXISTS skill_gap_schema.skill_gap_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    career_id VARCHAR(255) NOT NULL,
    target_career VARCHAR(255) NOT NULL,
    overall_readiness_score DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    overall_gap_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    estimated_job_readiness_months INTEGER NOT NULL DEFAULT 6,
    technical_readiness DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    soft_skill_readiness DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    project_readiness DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    interview_readiness DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    resume_readiness DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    coding_readiness DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sgr_student FOREIGN KEY (student_id) REFERENCES identity_schema.users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sgr_student_id ON skill_gap_schema.skill_gap_reports(student_id);
CREATE INDEX IF NOT EXISTS idx_sgr_career_id ON skill_gap_schema.skill_gap_reports(career_id);

-- 2. Skill Gaps Table
CREATE TABLE IF NOT EXISTS skill_gap_schema.skill_gaps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id UUID NOT NULL,
    skill_name VARCHAR(255) NOT NULL,
    required_level VARCHAR(50) NOT NULL, -- BEGINNER | INTERMEDIATE | ADVANCED | EXPERT
    current_level VARCHAR(50) NOT NULL,  -- NONE | BEGINNER | INTERMEDIATE | ADVANCED | EXPERT
    gap_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM', -- HIGH | MEDIUM | LOW
    estimated_learning_hours INTEGER NOT NULL DEFAULT 10,
    status VARCHAR(50) NOT NULL DEFAULT 'MISSING', -- MISSING | WEAK | STRONG | EMERGING
    category VARCHAR(50) NOT NULL DEFAULT 'TECHNICAL', -- TECHNICAL | SOFT_SKILL | CODING | PROJECT
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sg_report FOREIGN KEY (report_id) REFERENCES skill_gap_schema.skill_gap_reports(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sg_report_id ON skill_gap_schema.skill_gaps(report_id);
CREATE INDEX IF NOT EXISTS idx_sg_status ON skill_gap_schema.skill_gaps(status);

-- 3. Improvement Recommendations Table
CREATE TABLE IF NOT EXISTS skill_gap_schema.improvement_recommendations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gap_id UUID NOT NULL,
    resource_id VARCHAR(255),
    project_id VARCHAR(255),
    certificate_id VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    recommendation_type VARCHAR(50) NOT NULL, -- RESOURCE | PROJECT | COURSE | CERTIFICATION | PRACTICE
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    estimated_duration VARCHAR(100),
    url VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ir_gap FOREIGN KEY (gap_id) REFERENCES skill_gap_schema.improvement_recommendations(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ir_gap_id ON skill_gap_schema.improvement_recommendations(gap_id);
