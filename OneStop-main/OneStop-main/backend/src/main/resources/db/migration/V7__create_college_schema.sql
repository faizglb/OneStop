-- ============================================================
-- V7: College Finder Schema & Benchmark Seed Data
-- Sprint 5 | ACEA Platform
-- ============================================================

CREATE SCHEMA IF NOT EXISTS college_schema;

-- ============================================================
-- colleges — College details
-- ============================================================
CREATE TABLE college_schema.colleges (
    id                  UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    college_name        VARCHAR(250)  NOT NULL,
    college_type        VARCHAR(50)   NOT NULL, -- Government | Private
    ownership           VARCHAR(50)   NOT NULL, -- Public | Private | Semi
    state               VARCHAR(100)  NOT NULL,
    district            VARCHAR(100)  NOT NULL,
    city                VARCHAR(100)  NOT NULL,
    address             TEXT          NOT NULL,
    latitude            DECIMAL(9,6),
    longitude           DECIMAL(9,6),
    nirf_ranking        INTEGER,
    naac_grade          VARCHAR(10),
    aicte_approved      BOOLEAN       NOT NULL DEFAULT TRUE,
    ugc_approved        BOOLEAN       NOT NULL DEFAULT TRUE,
    established_year    INTEGER       NOT NULL,
    website             VARCHAR(250),
    email               VARCHAR(150),
    phone               VARCHAR(50),
    average_package     DECIMAL(12,2) NOT NULL DEFAULT 0.0,
    highest_package     DECIMAL(12,2) NOT NULL DEFAULT 0.0,
    median_package      DECIMAL(12,2) NOT NULL DEFAULT 0.0,
    placement_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    hostel_available    BOOLEAN       NOT NULL DEFAULT FALSE,
    girls_hostel        BOOLEAN       NOT NULL DEFAULT FALSE,
    boys_hostel         BOOLEAN       NOT NULL DEFAULT FALSE,
    total_fees          DECIMAL(12,2) NOT NULL DEFAULT 0.0,
    annual_fees         DECIMAL(12,2) NOT NULL DEFAULT 0.0,
    campus_area         DECIMAL(10,2) NOT NULL DEFAULT 0.0, -- in acres
    description         TEXT,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- courses — Academic courses offered
-- ============================================================
CREATE TABLE college_schema.courses (
    id            UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    college_id    UUID          NOT NULL REFERENCES college_schema.colleges(id) ON DELETE CASCADE,
    course_name   VARCHAR(200)  NOT NULL,
    stream        VARCHAR(100)  NOT NULL, -- Engineering | Management | Science | Arts
    duration      INTEGER       NOT NULL, -- in years
    degree        VARCHAR(100)  NOT NULL, -- B.Tech | M.Tech | MBA | MCA | B.Sc | B.A
    eligibility   TEXT,
    entrance_exam VARCHAR(100), -- JEE Main | GATE | CAT | None
    seat_intake   INTEGER       NOT NULL DEFAULT 0
);

-- ============================================================
-- cutoffs — Selection cutoffs by entrance exams
-- ============================================================
CREATE TABLE college_schema.cutoffs (
    id            UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    college_id    UUID          NOT NULL REFERENCES college_schema.colleges(id) ON DELETE CASCADE,
    course_id     UUID          NOT NULL REFERENCES college_schema.courses(id) ON DELETE CASCADE,
    exam_name     VARCHAR(100)  NOT NULL, -- JEE Main | GATE | CAT
    category      VARCHAR(50)   NOT NULL, -- General | OBC | SC | ST
    opening_rank  INTEGER       NOT NULL,
    closing_rank  INTEGER       NOT NULL,
    year          INTEGER       NOT NULL
);

-- ============================================================
-- facilities — Campus facilities
-- ============================================================
CREATE TABLE college_schema.facilities (
    id            UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    college_id    UUID          NOT NULL REFERENCES college_schema.colleges(id) ON DELETE CASCADE,
    facility_name VARCHAR(100)  NOT NULL,
    description   TEXT
);

-- ============================================================
-- college_bookmarks — Student bookmarks tracker
-- ============================================================
CREATE TABLE college_schema.college_bookmarks (
    id            UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id    UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    college_id    UUID          NOT NULL REFERENCES college_schema.colleges(id) ON DELETE CASCADE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT    uq_student_college UNIQUE(student_id, college_id)
);

-- ============================================================
-- college_recommendations — Saved AI college recommendations
-- ============================================================
CREATE TABLE college_schema.college_recommendations (
    id                    UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    student_id            UUID          NOT NULL REFERENCES identity_schema.users(id) ON DELETE CASCADE,
    college_id            UUID          NOT NULL REFERENCES college_schema.colleges(id) ON DELETE CASCADE,
    match_score           DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    confidence_score      DECIMAL(5,2)  NOT NULL DEFAULT 0.0,
    recommendation_reason TEXT          NOT NULL,
    recommended_course    VARCHAR(200)  NOT NULL,
    created_at            TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
-- Indexes
-- ============================================================
CREATE INDEX idx_colleges_location ON college_schema.colleges(state, city);
CREATE INDEX idx_colleges_type     ON college_schema.colleges(college_type);
CREATE INDEX idx_courses_college   ON college_schema.courses(college_id);
CREATE INDEX idx_cutoffs_course    ON college_schema.cutoffs(course_id);
CREATE INDEX idx_bookmarks_student ON college_schema.college_bookmarks(student_id);
CREATE INDEX idx_col_recs_student  ON college_schema.college_recommendations(student_id);

-- ============================================================
-- Seed Benchmark Data
-- ============================================================

-- 1. IIT Delhi
INSERT INTO college_schema.colleges (id, college_name, college_type, ownership, state, district, city, address, nirf_ranking, naac_grade, established_year, website, email, phone, average_package, highest_package, median_package, placement_percentage, hostel_available, girls_hostel, boys_hostel, total_fees, annual_fees, campus_area, description)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Indian Institute of Technology Delhi (IITD)',
    'Government', 'Public', 'Delhi', 'South Delhi', 'New Delhi',
    'Hauz Khas, New Delhi, Delhi 110016',
    2, 'A++', 1961, 'https://home.iitd.ac.in', 'director@iitd.ac.in', '+91-11-26591000',
    2050000.00, 40000000.00, 1850000.00, 94.50, TRUE, TRUE, TRUE, 850000.00, 220000.00, 325.00,
    'One of the premier public engineering and research institutes in India, consistently ranked among the top technical universities globally.'
);

-- 2. BITS Pilani
INSERT INTO college_schema.colleges (id, college_name, college_type, ownership, state, district, city, address, nirf_ranking, naac_grade, established_year, website, email, phone, average_package, highest_package, median_package, placement_percentage, hostel_available, girls_hostel, boys_hostel, total_fees, annual_fees, campus_area, description)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'Birla Institute of Technology and Science, Pilani (BITS)',
    'Private', 'Private', 'Rajasthan', 'Jhunjhunu', 'Pilani',
    'Vidya Vihar, Pilani, Rajasthan 333031',
    20, 'A', 1964, 'https://www.bits-pilani.ac.in', 'admissions@pilani.bits-pilani.ac.in', '+91-1596-245073',
    1580000.00, 6000000.00, 1400000.00, 92.00, TRUE, TRUE, TRUE, 2200000.00, 550000.00, 328.00,
    'A premier private deemed university known for its flexible academic curriculum, strong alumni network, and zero-reservation merit-only admission model.'
);

-- 3. NIT Trichy
INSERT INTO college_schema.colleges (id, college_name, college_type, ownership, state, district, city, address, nirf_ranking, naac_grade, established_year, website, email, phone, average_package, highest_package, median_package, placement_percentage, hostel_available, girls_hostel, boys_hostel, total_fees, annual_fees, campus_area, description)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'National Institute of Technology, Tiruchirappalli (NITT)',
    'Government', 'Public', 'Tamil Nadu', 'Tiruchirappalli', 'Trichy',
    'Tanjore Main Road, National Highway 67, Tiruchirappalli, Tamil Nadu 620015',
    9, 'A+', 1964, 'https://www.nitt.edu', 'director@nitt.edu', '+91-431-2503000',
    1200000.00, 5000000.00, 1050000.00, 90.00, TRUE, TRUE, TRUE, 600000.00, 150000.00, 800.00,
    'Consistently ranked as the #1 NIT in India, recognized for engineering research excellence and its sprawling residential campus.'
);

-- 4. VIT Vellore
INSERT INTO college_schema.colleges (id, college_name, college_type, ownership, state, district, city, address, nirf_ranking, naac_grade, established_year, website, email, phone, average_package, highest_package, median_package, placement_percentage, hostel_available, girls_hostel, boys_hostel, total_fees, annual_fees, campus_area, description)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    'Vellore Institute of Technology (VIT)',
    'Private', 'Private', 'Tamil Nadu', 'Vellore', 'Vellore',
    'Katpadi, Vellore, Tamil Nadu 632014',
    11, 'A++', 1984, 'https://vit.ac.in', 'info@vit.ac.in', '+91-416-2243091',
    820000.00, 10200000.00, 750000.00, 88.00, TRUE, TRUE, TRUE, 980000.00, 245000.00, 372.00,
    'A leading private university in South India offering diverse engineering branches with a highly systemized international credit system.'
);

-- 5. Delhi University
INSERT INTO college_schema.colleges (id, college_name, college_type, ownership, state, district, city, address, nirf_ranking, naac_grade, established_year, website, email, phone, average_package, highest_package, median_package, placement_percentage, hostel_available, girls_hostel, boys_hostel, total_fees, annual_fees, campus_area, description)
VALUES (
    '55555555-5555-5555-5555-555555555555',
    'University of Delhi (DU)',
    'Government', 'Public', 'Delhi', 'North Delhi', 'New Delhi',
    'Benito Juarez Marg, South Campus, New Delhi, Delhi 110021',
    13, 'A+', 1922, 'https://www.du.ac.in', 'registrar@du.ac.in', '+91-11-27006900',
    650000.00, 3000000.00, 580000.00, 80.00, TRUE, TRUE, TRUE, 60000.00, 15000.00, 69.00,
    'A historic public central university known for its premium humanities, science, and commerce colleges like SRCC, St. Stephens, and Hindu.'
);


-- Courses Seeding
-- IIT Delhi B.Tech CSE & M.Tech CSE
INSERT INTO college_schema.courses (id, college_id, course_name, stream, duration, degree, eligibility, entrance_exam, seat_intake)
VALUES (
    '11111111-1111-1111-1111-222222222221', '11111111-1111-1111-1111-111111111111',
    'Computer Science and Engineering', 'Engineering', 4, 'B.Tech',
    'Class 12 with PCM, JEE Advanced qualified', 'JEE Advanced', 120
);
INSERT INTO college_schema.courses (id, college_id, course_name, stream, duration, degree, eligibility, entrance_exam, seat_intake)
VALUES (
    '11111111-1111-1111-1111-222222222222', '11111111-1111-1111-1111-111111111111',
    'Data Science and Artificial Intelligence', 'Engineering', 4, 'B.Tech',
    'Class 12 with PCM, JEE Advanced qualified', 'JEE Advanced', 50
);

-- BITS Pilani B.Tech CSE & MBA
INSERT INTO college_schema.courses (id, college_id, course_name, stream, duration, degree, eligibility, entrance_exam, seat_intake)
VALUES (
    '22222222-2222-2222-2222-222222222221', '22222222-2222-2222-2222-222222222222',
    'Computer Science', 'Engineering', 4, 'B.Tech',
    'Class 12 with PCM (75% minimum), BITSAT qualified', 'BITSAT', 150
);
INSERT INTO college_schema.courses (id, college_id, course_name, stream, duration, degree, eligibility, entrance_exam, seat_intake)
VALUES (
    '22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222',
    'Master of Business Administration', 'Management', 2, 'MBA',
    'Graduation with 60% minimum, BAAT/CAT qualified', 'CAT', 60
);

-- NIT Trichy B.Tech CSE
INSERT INTO college_schema.courses (id, college_id, course_name, stream, duration, degree, eligibility, entrance_exam, seat_intake)
VALUES (
    '33333333-3333-3333-3333-222222222221', '33333333-3333-3333-3333-333333333333',
    'Computer Science and Engineering', 'Engineering', 4, 'B.Tech',
    'Class 12 with PCM, JEE Main qualified', 'JEE Main', 110
);

-- VIT Vellore B.Tech CSE
INSERT INTO college_schema.courses (id, college_id, course_name, stream, duration, degree, eligibility, entrance_exam, seat_intake)
VALUES (
    '44444444-4444-4444-4444-222222222221', '44444444-4444-4444-4444-444444444444',
    'Computer Science and Engineering', 'Engineering', 4, 'B.Tech',
    'Class 12 with PCM, VITEEE qualified', 'VITEEE', 400
);

-- DU SRCC B.Com (Hons)
INSERT INTO college_schema.courses (id, college_id, course_name, stream, duration, degree, eligibility, entrance_exam, seat_intake)
VALUES (
    '55555555-5555-5555-5555-222222222221', '55555555-5555-5555-5555-555555555555',
    'Bachelor of Commerce (Hons)', 'Commerce', 3, 'B.Com',
    'Class 12 with Mathematics/Accountancy, CUET qualified', 'CUET', 626
);


-- Cutoffs Seeding (JEE Main / Advanced ranks for B.Tech)
-- IIT Delhi B.Tech CSE General cutoffs
INSERT INTO college_schema.cutoffs (id, college_id, course_id, exam_name, category, opening_rank, closing_rank, year)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-222222222221', 'JEE Advanced', 'General', 1, 115, 2024);
INSERT INTO college_schema.cutoffs (id, college_id, course_id, exam_name, category, opening_rank, closing_rank, year)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-222222222221', 'JEE Advanced', 'OBC', 20, 85, 2024);

-- NIT Trichy CSE JEE Main cutoffs
INSERT INTO college_schema.cutoffs (id, college_id, course_id, exam_name, category, opening_rank, closing_rank, year)
VALUES (gen_random_uuid(), '33333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-222222222221', 'JEE Main', 'General', 200, 1500, 2024);
INSERT INTO college_schema.cutoffs (id, college_id, course_id, exam_name, category, opening_rank, closing_rank, year)
VALUES (gen_random_uuid(), '33333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-222222222221', 'JEE Main', 'OBC', 400, 2200, 2024);

-- BITS Pilani B.Tech CSE BITSAT cutoffs (Score out of 390)
INSERT INTO college_schema.cutoffs (id, college_id, course_id, exam_name, category, opening_rank, closing_rank, year)
VALUES (gen_random_uuid(), '22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222221', 'BITSAT', 'General', 320, 340, 2024);


-- Facilities Seeding
-- IIT Delhi Facilities
INSERT INTO college_schema.facilities (id, college_id, facility_name, description)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Central Library', 'Huge 3-story library holding over 3,00,000 reference logs and online e-journals subscriptions.');
INSERT INTO college_schema.facilities (id, college_id, facility_name, description)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Supercomputing Lab', 'Houses the SC3 supercomputing cluster for high performance numerical modeling projects.');
INSERT INTO college_schema.facilities (id, college_id, facility_name, description)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'Hostels & Mess', '18 residential student hostels equipped with modern dining mess halls, Wi-Fi, and basketball courts.');

-- BITS Pilani Facilities
INSERT INTO college_schema.facilities (id, college_id, facility_name, description)
VALUES (gen_random_uuid(), '22222222-2222-2222-2222-222222222222', 'Student Activity Centre (SAC)', 'Sports complex with wooden badminton courts, gym, swimming pool, and music rooms.');
INSERT INTO college_schema.facilities (id, college_id, facility_name, description)
VALUES (gen_random_uuid(), '22222222-2222-2222-2222-222222222222', 'Central Library BITS', 'Air-conditioned digital library modeled on university styles containing over 2,00,000 volumes.');
