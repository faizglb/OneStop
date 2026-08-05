-- ============================================================
-- V9: AI Learning Resources Recommendation Schema & Seed Data
-- Sprint 7 | ACEA Platform
-- ============================================================

CREATE SCHEMA IF NOT EXISTS learning_schema;

-- ============================================================
-- learning_resources — Available learning resources
-- ============================================================
CREATE TABLE learning_schema.learning_resources (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title               VARCHAR(250)  NOT NULL,
    description         TEXT          NOT NULL,
    platform            VARCHAR(100)  NOT NULL, -- YouTube | edX | Coursera | Udemy | NPTEL | freeCodeCamp | books
    resource_type       VARCHAR(50)   NOT NULL, -- Video Course | Playlist | Book | Article | Documentation | GitHub Project | Certification Course
    category            VARCHAR(100)  NOT NULL, -- Programming Languages | Frameworks | DSA | Soft Skills | System Design | Cloud | DevOps | AI/ML | Database
    skill               VARCHAR(100)  NOT NULL, -- Java | Python | React | SQL | etc.
    difficulty          VARCHAR(30)   NOT NULL, -- BEGINNER | INTERMEDIATE | ADVANCED
    duration            VARCHAR(50)   NOT NULL, -- e.g. "12 hours", "4 weeks"
    language            VARCHAR(50)   NOT NULL DEFAULT 'English',
    is_free             BOOLEAN       NOT NULL DEFAULT TRUE,
    price               DECIMAL(12,2) DEFAULT 0.0,
    rating              DECIMAL(3,2)  NOT NULL DEFAULT 0.0,
    thumbnail           VARCHAR(500),
    resource_url        VARCHAR(250)  NOT NULL,
    author              VARCHAR(100),
    provider            VARCHAR(100),
    certificate_available BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- learning_bookmarks — Student shortlists
-- ============================================================
CREATE TABLE learning_schema.learning_bookmarks (
    id            UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id    UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    resource_id   UUID          NOT NULL REFERENCES learning_schema.learning_resources(id) ON DELETE CASCADE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT    uq_student_resource UNIQUE(student_id, resource_id)
);

-- ============================================================
-- learning_progress — Continuing studies tracker
-- ============================================================
CREATE TABLE learning_schema.learning_progress (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id          UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    resource_id         UUID          NOT NULL REFERENCES learning_schema.learning_resources(id) ON DELETE CASCADE,
    status              VARCHAR(50)   NOT NULL DEFAULT 'STARTED', -- STARTED | IN_PROGRESS | COMPLETED
    progress_percentage  INTEGER       NOT NULL DEFAULT 0 CHECK (progress_percentage >= 0 AND progress_percentage <= 100),
    started_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    completed_at        TIMESTAMPTZ,
    last_accessed       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT          uq_student_progress UNIQUE(student_id, resource_id)
);

-- ============================================================
-- learning_recommendations — AI matched recommendations
-- ============================================================
CREATE TABLE learning_schema.learning_recommendations (
    id                      UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id              UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    resource_id             UUID          NOT NULL REFERENCES learning_schema.learning_resources(id) ON DELETE CASCADE,
    recommended_skill       VARCHAR(100)  NOT NULL,
    priority                VARCHAR(30)   NOT NULL DEFAULT 'MEDIUM', -- HIGH | MEDIUM | LOW
    confidence_score        DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    reason                  TEXT          NOT NULL,
    estimated_completion_time VARCHAR(50),
    created_at              TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- Indexes
-- ============================================================
CREATE INDEX idx_learning_platform ON learning_schema.learning_resources(platform);
CREATE INDEX idx_learning_category ON learning_schema.learning_resources(category);
CREATE INDEX idx_learning_skill    ON learning_schema.learning_resources(skill);
CREATE INDEX idx_progress_student  ON learning_schema.learning_progress(student_id);
CREATE INDEX idx_recs_student      ON learning_schema.learning_recommendations(student_id);

-- ============================================================
-- Seed Curated Data
-- ============================================================

-- 1. Harvard CS50 (edX - Video Course)
INSERT INTO learning_schema.learning_resources (id, title, description, platform, resource_type, category, skill, difficulty, duration, language, is_free, price, rating, thumbnail, resource_url, author, provider, certificate_available)
VALUES (
    '22222222-3333-4444-5555-666666666661',
    'CS50: Introduction to Computer Science',
    'An introduction to the intellectual enterprises of computer science and the art of programming for majors and non-majors alike, with or without prior programming experience.',
    'edX', 'Video Course', 'Programming Languages', 'Computer Science', 'BEGINNER',
    '12 weeks (6-18 hours/week)', 'English', TRUE, 0.0, 4.80,
    'https://cs50.harvard.edu/x/2026/assets/images/cs50x.png',
    'https://www.edx.org/course/cs50s-introduction-to-computer-science',
    'David J. Malan', 'Harvard University', TRUE
);

-- 2. Java Programming Masterclass (Udemy - Video Course)
INSERT INTO learning_schema.learning_resources (id, title, description, platform, resource_type, category, skill, difficulty, duration, language, is_free, price, rating, thumbnail, resource_url, author, provider, certificate_available)
VALUES (
    '22222222-3333-4444-5555-666666666662',
    'Java Programming Masterclass for Software Developers',
    'Learn Java In This Course And Become A Computer Programmer. Obtain valuable Core Java Skills And Java Certification.',
    'Udemy', 'Video Course', 'Programming Languages', 'Java', 'BEGINNER',
    '80 hours', 'English', FALSE, 499.00, 4.70,
    'https://img-c.udemycdn.com/course/480x270/533682_c10c_4.jpg',
    'https://www.udemy.com/course/java-the-complete-java-developer-course/',
    'Tim Buchalka', 'Udemy Academy', TRUE
);

-- 3. Complete React Developer (Coursera - Video Course)
INSERT INTO learning_schema.learning_resources (id, title, description, platform, resource_type, category, skill, difficulty, duration, language, is_free, price, rating, thumbnail, resource_url, author, provider, certificate_available)
VALUES (
    '22222222-3333-4444-5555-666666666663',
    'Front-End Web Development with React',
    'This course explores React-based front-end web development, Webpack and Babel, and Reactstrap for bootstrap-based responsive UI design.',
    'Coursera', 'Video Course', 'Frameworks', 'React', 'INTERMEDIATE',
    '36 hours', 'English', TRUE, 0.0, 4.60,
    'https://d3njjcbhgcj5a0.cloudfront.net/course-photos/react-development.png',
    'https://www.coursera.org/learn/front-end-react',
    'Jogesh K. Muppala', 'The Hong Kong University of Science and Technology', TRUE
);

-- 4. Data Structures and Algorithms (NPTEL - Video Course)
INSERT INTO learning_schema.learning_resources (id, title, description, platform, resource_type, category, skill, difficulty, duration, language, is_free, price, rating, thumbnail, resource_url, author, provider, certificate_available)
VALUES (
    '22222222-3333-4444-5555-666666666664',
    'Programming, Data Structures and Algorithms in Python',
    'A formal introduction to programming, data structures and algorithms using Python, covering searching, sorting, lists, stacks, queues, and tree structures.',
    'NPTEL', 'Video Course', 'DSA', 'Python', 'INTERMEDIATE',
    '8 weeks', 'English', TRUE, 0.0, 4.50,
    'https://nptel.ac.in/assets/images/nptel-logo.png',
    'https://onlinecourses.nptel.ac.in/noc26_cs10/preview',
    'Prof. Madhavan Mukund', 'IIT Madras', TRUE
);

-- 5. Clean Code book (books - Book)
INSERT INTO learning_schema.learning_resources (id, title, description, platform, resource_type, category, skill, difficulty, duration, language, is_free, price, rating, thumbnail, resource_url, author, provider, certificate_available)
VALUES (
    '22222222-3333-4444-5555-666666666665',
    'Clean Code: A Handbook of Agile Software Craftsmanship',
    'Even bad code can function. But if code isn’t clean, it can bring a development organization to its knees. Clean Code helps developers write better code.',
    'books', 'Book', 'Soft Skills', 'Software Engineering', 'ADVANCED',
    '464 pages', 'English', FALSE, 899.00, 4.90,
    'https://images-na.ssl-images-amazon.com/images/I/41xShOLq9LL._SX218_BO1,204,203,200_QL40_FMwebp_.jpg',
    'https://www.amazon.in/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882',
    'Robert C. Martin', 'Prentice Hall', FALSE
);
