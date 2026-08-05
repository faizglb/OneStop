-- ============================================================
-- V8: Scholarship Finder Schema & Seed Data
-- Sprint 6 | ACEA Platform
-- ============================================================

CREATE SCHEMA IF NOT EXISTS scholarship_schema;

-- ============================================================
-- scholarships — Scholarship details
-- ============================================================
CREATE TABLE scholarship_schema.scholarships (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    scholarship_name    VARCHAR(250)  NOT NULL,
    provider            VARCHAR(200)  NOT NULL,
    scholarship_type    VARCHAR(50)   NOT NULL, -- Government | Private | NGO | Corporate | International | University
    description         TEXT          NOT NULL,
    official_website    VARCHAR(250),
    application_link    VARCHAR(250),
    application_mode    VARCHAR(50)   NOT NULL DEFAULT 'ONLINE', -- ONLINE | OFFLINE
    amount              DECIMAL(12,2) NOT NULL DEFAULT 0.0,
    currency            VARCHAR(10)   NOT NULL DEFAULT 'INR',
    last_date           DATE          NOT NULL,
    state               VARCHAR(100), -- null if central/national
    country             VARCHAR(100)  NOT NULL DEFAULT 'India',
    education_level     VARCHAR(50)   NOT NULL, -- UNDERGRADUATE | POSTGRADUATE | SCHOOL
    course              VARCHAR(100), -- null if open to all
    category            VARCHAR(50), -- null if open to all (General | OBC | SC | ST)
    gender              VARCHAR(20), -- null if open to all (Male | Female)
    minority_eligible   BOOLEAN       NOT NULL DEFAULT FALSE,
    disability_eligible BOOLEAN       NOT NULL DEFAULT FALSE,
    income_limit        DECIMAL(12,2), -- annual family income limit
    minimum_marks       DECIMAL(5,2), -- minimum percentage/CGPA requirements
    entrance_exam       VARCHAR(100),
    renewable           BOOLEAN       NOT NULL DEFAULT FALSE,
    required_documents  TEXT          NOT NULL, -- comma-separated documents
    status              VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE', -- ACTIVE | EXPIRED
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- scholarship_eligibilities — Rule rules for eligibility checks
-- ============================================================
CREATE TABLE scholarship_schema.scholarship_eligibilities (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    scholarship_id      UUID          NOT NULL REFERENCES scholarship_schema.scholarships(id) ON DELETE CASCADE,
    eligibility_rule    VARCHAR(250)  NOT NULL,
    rule_type           VARCHAR(50)   NOT NULL, -- INCOME | MARKS | CATEGORY | GENDER | STATE | MINORITY | DISABILITY
    rule_value          VARCHAR(200)  NOT NULL
);

-- ============================================================
-- scholarship_bookmarks — Bookmark tracker
-- ============================================================
CREATE TABLE scholarship_schema.scholarship_bookmarks (
    id            UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id    UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    scholarship_id UUID         NOT NULL REFERENCES scholarship_schema.scholarships(id) ON DELETE CASCADE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT    uq_student_scholarship UNIQUE(student_id, scholarship_id)
);

-- ============================================================
-- scholarship_applications — Application tracker
-- ============================================================
CREATE TABLE scholarship_schema.scholarship_applications (
    id            UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id    UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    scholarship_id UUID         NOT NULL REFERENCES scholarship_schema.scholarships(id) ON DELETE CASCADE,
    status        VARCHAR(50)   NOT NULL DEFAULT 'APPLIED', -- BOOKMARKED | APPLIED | SHORTLISTED | COMPLETED
    notes         TEXT,
    reminder_date TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT    uq_student_app UNIQUE(student_id, scholarship_id)
);

-- ============================================================
-- scholarship_recommendations — AI matched recommendations
-- ============================================================
CREATE TABLE scholarship_schema.scholarship_recommendations (
    id                    UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id            UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    scholarship_id        UUID          NOT NULL REFERENCES scholarship_schema.scholarships(id) ON DELETE CASCADE,
    eligibility_score     DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    confidence_score      DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    recommendation_reason TEXT          NOT NULL,
    application_status    VARCHAR(50)   NOT NULL DEFAULT 'NOT_ELIGIBLE', -- ELIGIBLE | PARTIALLY_ELIGIBLE | NOT_ELIGIBLE
    created_at            TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- Indexes
-- ============================================================
CREATE INDEX idx_schol_type      ON scholarship_schema.scholarships(scholarship_type);
CREATE INDEX idx_schol_lastdate  ON scholarship_schema.scholarships(last_date);
CREATE INDEX idx_schol_student   ON scholarship_schema.scholarship_recommendations(student_id);
CREATE INDEX idx_schol_app_stud  ON scholarship_schema.scholarship_applications(student_id);
CREATE INDEX idx_schol_book_stud ON scholarship_schema.scholarship_bookmarks(student_id);

-- ============================================================
-- Seed Data
-- ============================================================

-- 1. Central Sector Scheme of Scholarship (Government - National)
INSERT INTO scholarship_schema.scholarships (id, scholarship_name, provider, scholarship_type, description, official_website, application_link, application_mode, amount, currency, last_date, state, country, education_level, course, category, gender, minority_eligible, disability_eligible, income_limit, minimum_marks, renewable, required_documents, status)
VALUES (
    '11111111-2222-3333-4444-555555555551',
    'Central Sector Scheme of Scholarship for College and University Students',
    'Department of Higher Education, MHRD', 'Government',
    'Provides financial assistance to meritorious students from low-income families to meet a part of their day-to-day expenses while pursuing higher studies.',
    'https://scholarships.gov.in', 'https://scholarships.gov.in/login', 'ONLINE',
    20000.00, 'INR', '2026-10-31', NULL, 'India', 'UNDERGRADUATE', NULL, NULL, NULL, FALSE, FALSE, 450000.00, 80.00, TRUE,
    'Class 12 Marksheet,Income Certificate,Caste Certificate,Aadhaar Card,Bank Passbook', 'ACTIVE'
);

-- 2. Post Matric Scholarship for OBC Students (Government - State)
INSERT INTO scholarship_schema.scholarships (id, scholarship_name, provider, scholarship_type, description, official_website, application_link, application_mode, amount, currency, last_date, state, country, education_level, course, category, gender, minority_eligible, disability_eligible, income_limit, minimum_marks, renewable, required_documents, status)
VALUES (
    '11111111-2222-3333-4444-555555555552',
    'Post Matric Scholarship for OBC Students - Rajasthan',
    'Social Justice & Empowerment Department, Rajasthan', 'Government',
    'Financial aid for OBC students of Rajasthan pursuing post-matric courses to support their higher educational goals.',
    'https://sje.rajasthan.gov.in', 'https://sje.rajasthan.gov.in/scholarship', 'ONLINE',
    15000.00, 'INR', '2026-11-30', 'Rajasthan', 'India', 'UNDERGRADUATE', NULL, 'OBC', NULL, FALSE, FALSE, 250000.00, 60.00, TRUE,
    'Income Certificate,Caste Certificate,Domicile Certificate,Marksheet,Fee Receipt', 'ACTIVE'
);

-- 3. L’Oreal India For Young Women In Science Scholarship (Private)
INSERT INTO scholarship_schema.scholarships (id, scholarship_name, provider, scholarship_type, description, official_website, application_link, application_mode, amount, currency, last_date, state, country, education_level, course, category, gender, minority_eligible, disability_eligible, income_limit, minimum_marks, renewable, required_documents, status)
VALUES (
    '11111111-2222-3333-4444-555555555553',
    'L’Oreal India For Young Women In Science Scholarship',
    'L’Oreal India', 'Private',
    'A highly selective private scholarship aiming to encourage young women to pursue careers in science, engineering, or medical streams.',
    'https://www.loreal.com', 'https://www.loreal.com/women-in-science', 'ONLINE',
    250000.00, 'INR', '2026-09-15', NULL, 'India', 'UNDERGRADUATE', 'Science', NULL, 'Female', FALSE, FALSE, 600000.00, 85.00, FALSE,
    'Class 10 Marksheet,Class 12 Marksheet,Income Certificate,Parent PAN Card,Self Declaration', 'ACTIVE'
);

-- 4. HDFC Badhte Kadam Scholarship (Corporate)
INSERT INTO scholarship_schema.scholarships (id, scholarship_name, provider, scholarship_type, description, official_website, application_link, application_mode, amount, currency, last_date, state, country, education_level, course, category, gender, minority_eligible, disability_eligible, income_limit, minimum_marks, renewable, required_documents, status)
VALUES (
    '11111111-2222-3333-4444-555555555554',
    'HDFC Bank Badhte Kadam Scholarship',
    'HDFC Bank CSR Department', 'Corporate',
    'Supports high-performing students facing family crises or financial distress, helping them continue their graduation courses.',
    'https://www.hdfcbank.com', 'https://www.hdfcbank.com/scholarships', 'ONLINE',
    50000.00, 'INR', '2026-10-15', NULL, 'India', 'UNDERGRADUATE', NULL, NULL, NULL, FALSE, FALSE, 600000.00, 70.00, TRUE,
    'Previous Year Marksheet,Income Certificate,Crisis Proof Document,Aadhaar Card,Bank Passbook', 'ACTIVE'
);

-- 5. BITS Pilani Merit-cum-Need Fellowship (University)
INSERT INTO scholarship_schema.scholarships (id, scholarship_name, provider, scholarship_type, description, official_website, application_link, application_mode, amount, currency, last_date, state, country, education_level, course, category, gender, minority_eligible, disability_eligible, income_limit, minimum_marks, renewable, required_documents, status)
VALUES (
    '11111111-2222-3333-4444-555555555555',
    'BITS Pilani Merit-cum-Need (MCN) Fellowship',
    'Birla Institute of Technology and Science, Pilani', 'University',
    'Internal fellowship providing tuition fee waivers up to 80% for meritorious BITS students based on combined academic CGPA and family income checks.',
    'https://www.bits-pilani.ac.in', 'https://www.bits-pilani.ac.in/scholarship-portal', 'OFFLINE',
    180000.00, 'INR', '2026-08-31', 'Rajasthan', 'India', 'UNDERGRADUATE', 'B.Tech', NULL, NULL, FALSE, FALSE, 800000.00, 7.50, TRUE,
    'ITR of Parents,Student CGPA Slip,Hostel Mess Bill Receipt,Aadhaar Card', 'ACTIVE'
);


-- Seeding Eligibilities rules
-- Central Sector Scheme Rules
INSERT INTO scholarship_schema.scholarship_eligibilities (id, scholarship_id, eligibility_rule, rule_type, rule_value)
VALUES (gen_random_uuid(), '11111111-2222-3333-4444-555555555551', 'Family income must not exceed 4.5 Lakhs annually', 'INCOME', '450000');
INSERT INTO scholarship_schema.scholarship_eligibilities (id, scholarship_id, eligibility_rule, rule_type, rule_value)
VALUES (gen_random_uuid(), '11111111-2222-3333-4444-555555555551', 'Minimum 80% marks in Class 12 exams', 'MARKS', '80.0');

-- Post Matric OBC Rajasthan Rules
INSERT INTO scholarship_schema.scholarship_eligibilities (id, scholarship_id, eligibility_rule, rule_type, rule_value)
VALUES (gen_random_uuid(), '11111111-2222-3333-4444-555555555552', 'Must belong to OBC reservation category', 'CATEGORY', 'OBC');
INSERT INTO scholarship_schema.scholarship_eligibilities (id, scholarship_id, eligibility_rule, rule_type, rule_value)
VALUES (gen_random_uuid(), '11111111-2222-3333-4444-555555555552', 'Must be a permanent resident of Rajasthan state', 'STATE', 'Rajasthan');

-- L’Oreal Young Women In Science Rules
INSERT INTO scholarship_schema.scholarship_eligibilities (id, scholarship_id, eligibility_rule, rule_type, rule_value)
VALUES (gen_random_uuid(), '11111111-2222-3333-4444-555555555553', 'Open only for female students', 'GENDER', 'Female');
INSERT INTO scholarship_schema.scholarship_eligibilities (id, scholarship_id, eligibility_rule, rule_type, rule_value)
VALUES (gen_random_uuid(), '11111111-2222-3333-4444-555555555553', 'Minimum 85% marks in PCM/PCB streams in Class 12', 'MARKS', '85.0');
