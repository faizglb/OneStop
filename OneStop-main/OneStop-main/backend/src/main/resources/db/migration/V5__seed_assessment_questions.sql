-- ============================================================
-- V5: Seed Assessment Questions
-- Sprint 3 | ACEA Platform
-- ============================================================

-- ============================================================
-- 1. APTITUDE ASSESSMENT
-- ============================================================
INSERT INTO assessment_schema.assessments (id, assessment_type, title, description, instructions, time_limit_minutes, total_questions, passing_score, status)
VALUES (
    '00000000-0000-0000-0001-000000000001',
    'APTITUDE',
    'Aptitude Assessment',
    'Evaluate your logical reasoning, numerical ability, verbal skills, and programming fundamentals.',
    'You have 60 minutes to complete 40 questions. Each question has 4 options. Read carefully before answering. You can navigate between questions freely. Mark questions for review if unsure. Your score is calculated section-wise.',
    60, 40, 60, 'ACTIVE'
);

-- Categories for Aptitude
INSERT INTO assessment_schema.question_categories (id, assessment_id, name, description, display_order, weight_pct, trait_key) VALUES
('00000000-0000-0000-0002-000000000001', '00000000-0000-0000-0001-000000000001', 'Logical Reasoning',          'Pattern recognition and deductive reasoning', 1, 20.0, 'LOGICAL_REASONING'),
('00000000-0000-0000-0002-000000000002', '00000000-0000-0000-0001-000000000001', 'Numerical Ability',          'Arithmetic, percentages, and data interpretation', 2, 18.0, 'NUMERICAL_ABILITY'),
('00000000-0000-0000-0002-000000000003', '00000000-0000-0000-0001-000000000001', 'Verbal Ability',             'Reading comprehension and vocabulary', 3, 15.0, 'VERBAL_ABILITY'),
('00000000-0000-0000-0002-000000000004', '00000000-0000-0000-0001-000000000001', 'Analytical Thinking',        'Data analysis and problem interpretation', 4, 18.0, 'ANALYTICAL_THINKING'),
('00000000-0000-0000-0002-000000000005', '00000000-0000-0000-0001-000000000001', 'Problem Solving',            'Algorithmic and logical problem solving', 5, 15.0, 'PROBLEM_SOLVING'),
('00000000-0000-0000-0002-000000000006', '00000000-0000-0000-0001-000000000001', 'Programming Fundamentals',   'Basic programming concepts and logic', 6, 14.0, 'PROGRAMMING');

-- ── Logical Reasoning (8 questions) ──
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('10000000-0000-0000-0000-000000000001','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000001','What comes next in the series: 2, 4, 8, 16, ___?','EASY',1,1),
('10000000-0000-0000-0000-000000000002','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000001','All roses are flowers. Some flowers fade quickly. Which conclusion follows?','EASY',2,1),
('10000000-0000-0000-0000-000000000003','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000001','Find the odd one out: Circle, Square, Triangle, Sphere','EASY',3,1),
('10000000-0000-0000-0000-000000000004','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000001','If A = 1, B = 2, C = 3... what is the value of CAB?','MEDIUM',4,2),
('10000000-0000-0000-0000-000000000005','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000001','A clock shows 3:15. What is the angle between the hour and minute hands?','MEDIUM',5,2),
('10000000-0000-0000-0000-000000000006','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000001','In a certain code, TIGER is written as UJHFS. How is LION written?','MEDIUM',6,2),
('10000000-0000-0000-0000-000000000007','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000001','What is the next term: 1, 1, 2, 3, 5, 8, ___?','HARD',7,3),
('10000000-0000-0000-0000-000000000008','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000001','A says B is lying. B says C is lying. C says A and B are both lying. Who is telling the truth?','HARD',8,3);

-- Options for Logical Reasoning
INSERT INTO assessment_schema.options (question_id, option_text, is_correct, weight, option_order) VALUES
('10000000-0000-0000-0000-000000000001','24',false,0,1),
('10000000-0000-0000-0000-000000000001','32',true,1,2),
('10000000-0000-0000-0000-000000000001','28',false,0,3),
('10000000-0000-0000-0000-000000000001','20',false,0,4),

('10000000-0000-0000-0000-000000000002','Some roses fade quickly',false,0,1),
('10000000-0000-0000-0000-000000000002','All roses fade quickly',false,0,2),
('10000000-0000-0000-0000-000000000002','No definite conclusion follows',true,1,3),
('10000000-0000-0000-0000-000000000002','No roses fade quickly',false,0,4),

('10000000-0000-0000-0000-000000000003','Circle',false,0,1),
('10000000-0000-0000-0000-000000000003','Square',false,0,2),
('10000000-0000-0000-0000-000000000003','Triangle',false,0,3),
('10000000-0000-0000-0000-000000000003','Sphere',true,1,4),

('10000000-0000-0000-0000-000000000004','312',true,1,1),
('10000000-0000-0000-0000-000000000004','321',false,0,2),
('10000000-0000-0000-0000-000000000004','123',false,0,3),
('10000000-0000-0000-0000-000000000004','132',false,0,4),

('10000000-0000-0000-0000-000000000005','7.5 degrees',true,1,1),
('10000000-0000-0000-0000-000000000005','90 degrees',false,0,2),
('10000000-0000-0000-0000-000000000005','45 degrees',false,0,3),
('10000000-0000-0000-0000-000000000005','0 degrees',false,0,4),

('10000000-0000-0000-0000-000000000006','MJON',true,1,1),
('10000000-0000-0000-0000-000000000006','MJPO',false,0,2),
('10000000-0000-0000-0000-000000000006','LIOM',false,0,3),
('10000000-0000-0000-0000-000000000006','NJPO',false,0,4),

('10000000-0000-0000-0000-000000000007','11',false,0,1),
('10000000-0000-0000-0000-000000000007','12',false,0,2),
('10000000-0000-0000-0000-000000000007','13',true,1,3),
('10000000-0000-0000-0000-000000000007','14',false,0,4),

('10000000-0000-0000-0000-000000000008','A only',false,0,1),
('10000000-0000-0000-0000-000000000008','B only',false,0,2),
('10000000-0000-0000-0000-000000000008','C only',true,1,3),
('10000000-0000-0000-0000-000000000008','None of them',false,0,4);

-- ── Numerical Ability (7 questions) ──
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('10000000-0000-0000-0000-000000000009','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000002','What is 15% of 240?','EASY',9,1),
('10000000-0000-0000-0000-000000000010','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000002','A train travels 300 km in 4 hours. What is its average speed?','EASY',10,1),
('10000000-0000-0000-0000-000000000011','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000002','If 3x + 7 = 22, what is x?','EASY',11,1),
('10000000-0000-0000-0000-000000000012','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000002','A shopkeeper bought an item for ₹800 and sold it for ₹1000. What is the profit percentage?','MEDIUM',12,2),
('10000000-0000-0000-0000-000000000013','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000002','Two pipes fill a tank in 4 hours and 6 hours respectively. How long together?','MEDIUM',13,2),
('10000000-0000-0000-0000-000000000014','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000002','The ratio of milk to water in 40 litres is 3:1. How much water to add for 2:3 ratio?','HARD',14,3),
('10000000-0000-0000-0000-000000000015','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000002','A sum doubles in 5 years at simple interest. What is the annual interest rate?','HARD',15,3);

INSERT INTO assessment_schema.options (question_id, option_text, is_correct, weight, option_order) VALUES
('10000000-0000-0000-0000-000000000009','36',true,1,1),('10000000-0000-0000-0000-000000000009','32',false,0,2),('10000000-0000-0000-0000-000000000009','34',false,0,3),('10000000-0000-0000-0000-000000000009','38',false,0,4),
('10000000-0000-0000-0000-000000000010','70 km/h',false,0,1),('10000000-0000-0000-0000-000000000010','75 km/h',true,1,2),('10000000-0000-0000-0000-000000000010','80 km/h',false,0,3),('10000000-0000-0000-0000-000000000010','60 km/h',false,0,4),
('10000000-0000-0000-0000-000000000011','4',false,0,1),('10000000-0000-0000-0000-000000000011','5',true,1,2),('10000000-0000-0000-0000-000000000011','6',false,0,3),('10000000-0000-0000-0000-000000000011','7',false,0,4),
('10000000-0000-0000-0000-000000000012','20%',false,0,1),('10000000-0000-0000-0000-000000000012','25%',true,1,2),('10000000-0000-0000-0000-000000000012','22%',false,0,3),('10000000-0000-0000-0000-000000000012','30%',false,0,4),
('10000000-0000-0000-0000-000000000013','2 hours 24 mins',true,1,1),('10000000-0000-0000-0000-000000000013','2 hours',false,0,2),('10000000-0000-0000-0000-000000000013','3 hours',false,0,3),('10000000-0000-0000-0000-000000000013','2 hours 30 mins',false,0,4),
('10000000-0000-0000-0000-000000000014','40 litres',true,1,1),('10000000-0000-0000-0000-000000000014','30 litres',false,0,2),('10000000-0000-0000-0000-000000000014','50 litres',false,0,3),('10000000-0000-0000-0000-000000000014','35 litres',false,0,4),
('10000000-0000-0000-0000-000000000015','20%',true,1,1),('10000000-0000-0000-0000-000000000015','15%',false,0,2),('10000000-0000-0000-0000-000000000015','25%',false,0,3),('10000000-0000-0000-0000-000000000015','10%',false,0,4);

-- ── Verbal Ability (6 questions) ──
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('10000000-0000-0000-0000-000000000016','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000003','Choose the synonym of ARDUOUS:','EASY',16,1),
('10000000-0000-0000-0000-000000000017','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000003','Choose the antonym of VERBOSE:','EASY',17,1),
('10000000-0000-0000-0000-000000000018','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000003','Fill in the blank: She was so tired that she could __ walk home.','EASY',18,1),
('10000000-0000-0000-0000-000000000019','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000003','Identify the error: "Neither of the students are present today."','MEDIUM',19,2),
('10000000-0000-0000-0000-000000000020','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000003','Rearrange the jumbled words to form a sentence: quickly / the / ran / fox','MEDIUM',20,2),
('10000000-0000-0000-0000-000000000021','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000003','Identify the figure of speech: "The sun smiled down on us."','HARD',21,3);

INSERT INTO assessment_schema.options (question_id, option_text, is_correct, weight, option_order) VALUES
('10000000-0000-0000-0000-000000000016','Easy',false,0,1),('10000000-0000-0000-0000-000000000016','Difficult',true,1,2),('10000000-0000-0000-0000-000000000016','Simple',false,0,3),('10000000-0000-0000-0000-000000000016','Joyful',false,0,4),
('10000000-0000-0000-0000-000000000017','Wordy',false,0,1),('10000000-0000-0000-0000-000000000017','Concise',true,1,2),('10000000-0000-0000-0000-000000000017','Loud',false,0,3),('10000000-0000-0000-0000-000000000017','Silent',false,0,4),
('10000000-0000-0000-0000-000000000018','barely',true,1,1),('10000000-0000-0000-0000-000000000018','easily',false,0,2),('10000000-0000-0000-0000-000000000018','happily',false,0,3),('10000000-0000-0000-0000-000000000018','quickly',false,0,4),
('10000000-0000-0000-0000-000000000019','Neither...are should be Neither...is',true,1,1),('10000000-0000-0000-0000-000000000019','No error',false,0,2),('10000000-0000-0000-0000-000000000019','students should be student',false,0,3),('10000000-0000-0000-0000-000000000019','today should be tomorrow',false,0,4),
('10000000-0000-0000-0000-000000000020','The fox ran quickly',true,1,1),('10000000-0000-0000-0000-000000000020','Quickly ran the fox',false,0,2),('10000000-0000-0000-0000-000000000020','The quickly fox ran',false,0,3),('10000000-0000-0000-0000-000000000020','Ran the fox quickly',false,0,4),
('10000000-0000-0000-0000-000000000021','Metaphor',true,1,1),('10000000-0000-0000-0000-000000000021','Simile',false,0,2),('10000000-0000-0000-0000-000000000021','Personification',false,0,3),('10000000-0000-0000-0000-000000000021','Alliteration',false,0,4);

-- ── Analytical Thinking (7 questions) ──
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('10000000-0000-0000-0000-000000000022','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000004','In a group of 5, A is taller than B. C is taller than A. D is shorter than B. E is between A and B. Who is shortest?','EASY',22,1),
('10000000-0000-0000-0000-000000000023','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000004','Data: Sales Q1=150, Q2=200, Q3=175, Q4=225. Which quarter had maximum growth?','EASY',23,1),
('10000000-0000-0000-0000-000000000024','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000004','There are 20 students. 12 play cricket, 8 play football, 4 play both. How many play neither?','MEDIUM',24,2),
('10000000-0000-0000-0000-000000000025','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000004','Statement: All graduates are employed. Ram is employed. Conclusion?','MEDIUM',25,2),
('10000000-0000-0000-0000-000000000026','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000004','In a certain language, 13 means "good morning", 14 means "good evening". What does 3 mean?','MEDIUM',26,2),
('10000000-0000-0000-0000-000000000027','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000004','A, B, C, D sit in a row. A is not adjacent to B. C is adjacent to both B and D. What is the arrangement?','HARD',27,3),
('10000000-0000-0000-0000-000000000028','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000004','A box has R red, B blue, G green balls. Minimum balls to pick to guarantee 2 of same colour?','HARD',28,3);

INSERT INTO assessment_schema.options (question_id, option_text, is_correct, weight, option_order) VALUES
('10000000-0000-0000-0000-000000000022','D',true,1,1),('10000000-0000-0000-0000-000000000022','B',false,0,2),('10000000-0000-0000-0000-000000000022','E',false,0,3),('10000000-0000-0000-0000-000000000022','A',false,0,4),
('10000000-0000-0000-0000-000000000023','Q4',false,0,1),('10000000-0000-0000-0000-000000000023','Q2',true,1,2),('10000000-0000-0000-0000-000000000023','Q3',false,0,3),('10000000-0000-0000-0000-000000000023','Q1',false,0,4),
('10000000-0000-0000-0000-000000000024','4',true,1,1),('10000000-0000-0000-0000-000000000024','8',false,0,2),('10000000-0000-0000-0000-000000000024','6',false,0,3),('10000000-0000-0000-0000-000000000024','2',false,0,4),
('10000000-0000-0000-0000-000000000025','Ram may or may not be a graduate',true,1,1),('10000000-0000-0000-0000-000000000025','Ram is definitely a graduate',false,0,2),('10000000-0000-0000-0000-000000000025','Ram is not a graduate',false,0,3),('10000000-0000-0000-0000-000000000025','No conclusion can be drawn',false,0,4),
('10000000-0000-0000-0000-000000000026','Good',true,1,1),('10000000-0000-0000-0000-000000000026','Morning',false,0,2),('10000000-0000-0000-0000-000000000026','Evening',false,0,3),('10000000-0000-0000-0000-000000000026','Night',false,0,4),
('10000000-0000-0000-0000-000000000027','A-B-C-D',false,0,1),('10000000-0000-0000-0000-000000000027','D-C-B-A',true,1,2),('10000000-0000-0000-0000-000000000027','B-C-D-A',false,0,3),('10000000-0000-0000-0000-000000000027','A-C-B-D',false,0,4),
('10000000-0000-0000-0000-000000000028','4',true,1,1),('10000000-0000-0000-0000-000000000028','3',false,0,2),('10000000-0000-0000-0000-000000000028','2',false,0,3),('10000000-0000-0000-0000-000000000028','5',false,0,4);

-- ── Problem Solving (6 questions) ──
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('10000000-0000-0000-0000-000000000029','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000005','What is the minimum number of moves to solve the 3-peg Tower of Hanoi with 3 disks?','EASY',29,1),
('10000000-0000-0000-0000-000000000030','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000005','What is the output of the algorithm: Start, input x, if x > 0 print "Positive" else print "Non-positive", Stop. Input x = -5.','EASY',30,1),
('10000000-0000-0000-0000-000000000031','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000005','Which sorting algorithm has average O(n log n) time complexity?','MEDIUM',31,2),
('10000000-0000-0000-0000-000000000032','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000005','In a binary search on 1024 elements, what is the maximum number of comparisons needed?','MEDIUM',32,2),
('10000000-0000-0000-0000-000000000033','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000005','What is the time complexity of searching in a balanced BST?','HARD',33,3),
('10000000-0000-0000-0000-000000000034','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000005','Which data structure uses LIFO (Last In First Out) ordering?','HARD',34,3);

INSERT INTO assessment_schema.options (question_id, option_text, is_correct, weight, option_order) VALUES
('10000000-0000-0000-0000-000000000029','7',true,1,1),('10000000-0000-0000-0000-000000000029','6',false,0,2),('10000000-0000-0000-0000-000000000029','8',false,0,3),('10000000-0000-0000-0000-000000000029','9',false,0,4),
('10000000-0000-0000-0000-000000000030','Non-positive',true,1,1),('10000000-0000-0000-0000-000000000030','Positive',false,0,2),('10000000-0000-0000-0000-000000000030','0',false,0,3),('10000000-0000-0000-0000-000000000030','Error',false,0,4),
('10000000-0000-0000-0000-000000000031','Bubble Sort',false,0,1),('10000000-0000-0000-0000-000000000031','Merge Sort',true,1,2),('10000000-0000-0000-0000-000000000031','Insertion Sort',false,0,3),('10000000-0000-0000-0000-000000000031','Selection Sort',false,0,4),
('10000000-0000-0000-0000-000000000032','10',true,1,1),('10000000-0000-0000-0000-000000000032','12',false,0,2),('10000000-0000-0000-0000-000000000032','8',false,0,3),('10000000-0000-0000-0000-000000000032','16',false,0,4),
('10000000-0000-0000-0000-000000000033','O(log n)',true,1,1),('10000000-0000-0000-0000-000000000033','O(n)',false,0,2),('10000000-0000-0000-0000-000000000033','O(1)',false,0,3),('10000000-0000-0000-0000-000000000033','O(n log n)',false,0,4),
('10000000-0000-0000-0000-000000000034','Stack',true,1,1),('10000000-0000-0000-0000-000000000034','Queue',false,0,2),('10000000-0000-0000-0000-000000000034','Array',false,0,3),('10000000-0000-0000-0000-000000000034','Tree',false,0,4);

-- ── Programming Fundamentals (6 questions) ──
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('10000000-0000-0000-0000-000000000035','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000006','What does OOP stand for?','EASY',35,1),
('10000000-0000-0000-0000-000000000036','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000006','Which of the following is a compiled language?','EASY',36,1),
('10000000-0000-0000-0000-000000000037','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000006','What is a variable in programming?','EASY',37,1),
('10000000-0000-0000-0000-000000000038','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000006','Which OOP concept allows a class to use methods from another class?','MEDIUM',38,2),
('10000000-0000-0000-0000-000000000039','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000006','What does the keyword "final" in Java signify?','MEDIUM',39,2),
('10000000-0000-0000-0000-000000000040','00000000-0000-0000-0001-000000000001','00000000-0000-0000-0002-000000000006','In Java, what is the output: System.out.println(5 + 3 + "2")?','HARD',40,3);

INSERT INTO assessment_schema.options (question_id, option_text, is_correct, weight, option_order) VALUES
('10000000-0000-0000-0000-000000000035','Object Oriented Programming',true,1,1),('10000000-0000-0000-0000-000000000035','Object Output Processing',false,0,2),('10000000-0000-0000-0000-000000000035','Only Object Programs',false,0,3),('10000000-0000-0000-0000-000000000035','Open Object Protocol',false,0,4),
('10000000-0000-0000-0000-000000000036','Java',true,1,1),('10000000-0000-0000-0000-000000000036','Python',false,0,2),('10000000-0000-0000-0000-000000000036','JavaScript',false,0,3),('10000000-0000-0000-0000-000000000036','Ruby',false,0,4),
('10000000-0000-0000-0000-000000000037','A named storage location',true,1,1),('10000000-0000-0000-0000-000000000037','A function',false,0,2),('10000000-0000-0000-0000-000000000037','A loop',false,0,3),('10000000-0000-0000-0000-000000000037','A class',false,0,4),
('10000000-0000-0000-0000-000000000038','Inheritance',true,1,1),('10000000-0000-0000-0000-000000000038','Encapsulation',false,0,2),('10000000-0000-0000-0000-000000000038','Abstraction',false,0,3),('10000000-0000-0000-0000-000000000038','Polymorphism',false,0,4),
('10000000-0000-0000-0000-000000000039','Cannot be overridden / changed',true,1,1),('10000000-0000-0000-0000-000000000039','Must be inherited',false,0,2),('10000000-0000-0000-0000-000000000039','Is public',false,0,3),('10000000-0000-0000-0000-000000000039','Is static',false,0,4),
('10000000-0000-0000-0000-000000000040','82',true,1,1),('10000000-0000-0000-0000-000000000040','532',false,0,2),('10000000-0000-0000-0000-000000000040','10',false,0,3),('10000000-0000-0000-0000-000000000040','Error',false,0,4);


-- ============================================================
-- 2. PERSONALITY ASSESSMENT (Big Five / OCEAN)
-- ============================================================
INSERT INTO assessment_schema.assessments (id, assessment_type, title, description, instructions, time_limit_minutes, total_questions, passing_score, status)
VALUES (
    '00000000-0000-0000-0001-000000000002',
    'PERSONALITY',
    'Personality Assessment',
    'Discover your Big Five personality traits (OCEAN) to better understand yourself and how you interact with the world.',
    'Rate each statement from 1 (Strongly Disagree) to 5 (Strongly Agree). Answer honestly — there are no right or wrong answers. Your responses reveal your unique personality profile.',
    40, 25, 0, 'ACTIVE'
);

-- Categories for Personality (Big Five traits)
INSERT INTO assessment_schema.question_categories (id, assessment_id, name, description, display_order, weight_pct, trait_key) VALUES
('00000000-0000-0000-0002-000000000011', '00000000-0000-0000-0001-000000000002', 'Openness',           'Creativity, curiosity, and openness to new experiences', 1, 20.0, 'OPENNESS'),
('00000000-0000-0000-0002-000000000012', '00000000-0000-0000-0001-000000000002', 'Conscientiousness',  'Organization, dependability, and self-discipline', 2, 20.0, 'CONSCIENTIOUSNESS'),
('00000000-0000-0000-0002-000000000013', '00000000-0000-0000-0001-000000000002', 'Extraversion',       'Sociability, assertiveness, and positive emotions', 3, 20.0, 'EXTRAVERSION'),
('00000000-0000-0000-0002-000000000014', '00000000-0000-0000-0001-000000000002', 'Agreeableness',      'Cooperation, trust, and empathy', 4, 20.0, 'AGREEABLENESS'),
('00000000-0000-0000-0002-000000000015', '00000000-0000-0000-0001-000000000002', 'Emotional Stability','Calmness, emotional regulation, and resilience', 5, 20.0, 'EMOTIONAL_STABILITY');

-- Personality Questions (5 per trait = 25 total)
-- Openness
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('20000000-0000-0000-0000-000000000001','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000011','I enjoy exploring new ideas and concepts.','EASY',1,1),
('20000000-0000-0000-0000-000000000002','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000011','I have a vivid imagination.','EASY',2,1),
('20000000-0000-0000-0000-000000000003','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000011','I enjoy art, music, and literature.','EASY',3,1),
('20000000-0000-0000-0000-000000000004','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000011','I am curious about many different things.','EASY',4,1),
('20000000-0000-0000-0000-000000000005','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000011','I find abstract theories interesting.','EASY',5,1);

-- Conscientiousness
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('20000000-0000-0000-0000-000000000006','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000012','I always complete tasks on time.','EASY',6,1),
('20000000-0000-0000-0000-000000000007','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000012','I pay attention to details.','EASY',7,1),
('20000000-0000-0000-0000-000000000008','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000012','I keep my belongings neat and organized.','EASY',8,1),
('20000000-0000-0000-0000-000000000009','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000012','I follow a schedule and stick to plans.','EASY',9,1),
('20000000-0000-0000-0000-000000000010','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000012','I work diligently and persistently.','EASY',10,1);

-- Extraversion
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('20000000-0000-0000-0000-000000000011','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000013','I feel energized after spending time with groups of people.','EASY',11,1),
('20000000-0000-0000-0000-000000000012','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000013','I enjoy being the center of attention.','EASY',12,1),
('20000000-0000-0000-0000-000000000013','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000013','I am talkative and assertive in social settings.','EASY',13,1),
('20000000-0000-0000-0000-000000000014','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000013','I like going to parties and social gatherings.','EASY',14,1),
('20000000-0000-0000-0000-000000000015','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000013','I make friends easily wherever I go.','EASY',15,1);

-- Agreeableness
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('20000000-0000-0000-0000-000000000016','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000014','I feel others'' emotions and empathize with them.','EASY',16,1),
('20000000-0000-0000-0000-000000000017','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000014','I am helpful and always willing to cooperate.','EASY',17,1),
('20000000-0000-0000-0000-000000000018','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000014','I trust people and believe in their good intentions.','EASY',18,1),
('20000000-0000-0000-0000-000000000019','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000014','I avoid conflicts and seek harmony.','EASY',19,1),
('20000000-0000-0000-0000-000000000020','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000014','I am kind and considerate towards others.','EASY',20,1);

-- Emotional Stability
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('20000000-0000-0000-0000-000000000021','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000015','I remain calm even under stressful situations.','EASY',21,1),
('20000000-0000-0000-0000-000000000022','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000015','I handle setbacks and failures without being overwhelmed.','EASY',22,1),
('20000000-0000-0000-0000-000000000023','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000015','I rarely feel anxious or worried.','EASY',23,1),
('20000000-0000-0000-0000-000000000024','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000015','My mood remains stable throughout the day.','EASY',24,1),
('20000000-0000-0000-0000-000000000025','00000000-0000-0000-0001-000000000002','00000000-0000-0000-0002-000000000015','I recover quickly from negative experiences.','EASY',25,1);

-- Likert Options for all personality questions (5 options per question — weight 1-5)
DO $$
DECLARE
    q_ids UUID[] := ARRAY[
        '20000000-0000-0000-0000-000000000001','20000000-0000-0000-0000-000000000002',
        '20000000-0000-0000-0000-000000000003','20000000-0000-0000-0000-000000000004',
        '20000000-0000-0000-0000-000000000005','20000000-0000-0000-0000-000000000006',
        '20000000-0000-0000-0000-000000000007','20000000-0000-0000-0000-000000000008',
        '20000000-0000-0000-0000-000000000009','20000000-0000-0000-0000-000000000010',
        '20000000-0000-0000-0000-000000000011','20000000-0000-0000-0000-000000000012',
        '20000000-0000-0000-0000-000000000013','20000000-0000-0000-0000-000000000014',
        '20000000-0000-0000-0000-000000000015','20000000-0000-0000-0000-000000000016',
        '20000000-0000-0000-0000-000000000017','20000000-0000-0000-0000-000000000018',
        '20000000-0000-0000-0000-000000000019','20000000-0000-0000-0000-000000000020',
        '20000000-0000-0000-0000-000000000021','20000000-0000-0000-0000-000000000022',
        '20000000-0000-0000-0000-000000000023','20000000-0000-0000-0000-000000000024',
        '20000000-0000-0000-0000-000000000025'
    ];
    q_id UUID;
BEGIN
    FOREACH q_id IN ARRAY q_ids LOOP
        INSERT INTO assessment_schema.options (question_id, option_text, is_correct, weight, option_order) VALUES
        (q_id, 'Strongly Disagree', false, 1.0, 1),
        (q_id, 'Disagree',          false, 2.0, 2),
        (q_id, 'Neutral',           false, 3.0, 3),
        (q_id, 'Agree',             false, 4.0, 4),
        (q_id, 'Strongly Agree',    false, 5.0, 5);
    END LOOP;
END $$;


-- ============================================================
-- 3. INTEREST ASSESSMENT (Holland's RIASEC)
-- ============================================================
INSERT INTO assessment_schema.assessments (id, assessment_type, title, description, instructions, time_limit_minutes, total_questions, passing_score, status)
VALUES (
    '00000000-0000-0000-0001-000000000003',
    'INTEREST',
    'Interest Assessment',
    'Discover your dominant interest areas based on Holland''s RIASEC model to guide your career exploration.',
    'Rate each activity or statement based on how much you enjoy or identify with it. Use the scale: 1 = Not at all, 5 = Very much. There are no right or wrong answers.',
    30, 24, 0, 'ACTIVE'
);

-- RIASEC Categories
INSERT INTO assessment_schema.question_categories (id, assessment_id, name, description, display_order, weight_pct, trait_key) VALUES
('00000000-0000-0000-0002-000000000021', '00000000-0000-0000-0001-000000000003', 'Realistic',      'Working with tools, machines, and nature', 1, 16.67, 'REALISTIC'),
('00000000-0000-0000-0002-000000000022', '00000000-0000-0000-0001-000000000003', 'Investigative',  'Research, analysis, and intellectual pursuits', 2, 16.67, 'INVESTIGATIVE'),
('00000000-0000-0000-0002-000000000023', '00000000-0000-0000-0001-000000000003', 'Artistic',       'Creative and expressive activities', 3, 16.67, 'ARTISTIC'),
('00000000-0000-0000-0002-000000000024', '00000000-0000-0000-0001-000000000003', 'Social',         'Helping, teaching, and working with people', 4, 16.67, 'SOCIAL'),
('00000000-0000-0000-0002-000000000025', '00000000-0000-0000-0001-000000000003', 'Enterprising',   'Leadership, persuasion, and business activities', 5, 16.67, 'ENTERPRISING'),
('00000000-0000-0000-0002-000000000026', '00000000-0000-0000-0001-000000000003', 'Conventional',   'Organization, data management, and clerical tasks', 6, 16.65, 'CONVENTIONAL');

-- RIASEC Questions (4 per category = 24 total)
INSERT INTO assessment_schema.questions (id, assessment_id, category_id, question_text, difficulty, question_order, marks) VALUES
('30000000-0000-0000-0000-000000000001','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000021','I enjoy working with my hands to build or repair things.','EASY',1,1),
('30000000-0000-0000-0000-000000000002','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000021','I like working outdoors and with physical environments.','EASY',2,1),
('30000000-0000-0000-0000-000000000003','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000021','I am interested in how machines and equipment work.','EASY',3,1),
('30000000-0000-0000-0000-000000000004','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000021','I enjoy activities that require physical strength or skill.','EASY',4,1),
('30000000-0000-0000-0000-000000000005','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000022','I enjoy solving complex problems through research and analysis.','EASY',5,1),
('30000000-0000-0000-0000-000000000006','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000022','I like understanding how and why things work scientifically.','EASY',6,1),
('30000000-0000-0000-0000-000000000007','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000022','I am curious about mathematics, science, and technology.','EASY',7,1),
('30000000-0000-0000-0000-000000000008','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000022','I enjoy reading research papers and academic journals.','EASY',8,1),
('30000000-0000-0000-0000-000000000009','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000023','I enjoy creative writing, drawing, or making music.','EASY',9,1),
('30000000-0000-0000-0000-000000000010','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000023','I prefer to express my ideas in an original and creative way.','EASY',10,1),
('30000000-0000-0000-0000-000000000011','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000023','I am drawn to visual arts, design, or performance.','EASY',11,1),
('30000000-0000-0000-0000-000000000012','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000023','I like work environments that allow for imagination and self-expression.','EASY',12,1),
('30000000-0000-0000-0000-000000000013','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000024','I enjoy helping others learn and grow.','EASY',13,1),
('30000000-0000-0000-0000-000000000014','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000024','I like working in teams and collaborating with diverse people.','EASY',14,1),
('30000000-0000-0000-0000-000000000015','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000024','I am interested in social issues and community service.','EASY',15,1),
('30000000-0000-0000-0000-000000000016','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000024','I feel fulfilled when I can make a difference in others'' lives.','EASY',16,1),
('30000000-0000-0000-0000-000000000017','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000025','I enjoy taking charge and leading projects.','EASY',17,1),
('30000000-0000-0000-0000-000000000018','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000025','I like persuading others and influencing decisions.','EASY',18,1),
('30000000-0000-0000-0000-000000000019','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000025','I am interested in business, marketing, and entrepreneurship.','EASY',19,1),
('30000000-0000-0000-0000-000000000020','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000025','I enjoy competitive environments and goal-setting.','EASY',20,1),
('30000000-0000-0000-0000-000000000021','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000026','I enjoy working with numbers, spreadsheets, and data.','EASY',21,1),
('30000000-0000-0000-0000-000000000022','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000026','I prefer structured tasks with clear rules and procedures.','EASY',22,1),
('30000000-0000-0000-0000-000000000023','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000026','I am good at organizing information and maintaining records.','EASY',23,1),
('30000000-0000-0000-0000-000000000024','00000000-0000-0000-0001-000000000003','00000000-0000-0000-0002-000000000026','I like clerical and administrative work environments.','EASY',24,1);

-- Likert Options for all RIASEC questions
DO $$
DECLARE
    q_ids UUID[] := ARRAY[
        '30000000-0000-0000-0000-000000000001','30000000-0000-0000-0000-000000000002',
        '30000000-0000-0000-0000-000000000003','30000000-0000-0000-0000-000000000004',
        '30000000-0000-0000-0000-000000000005','30000000-0000-0000-0000-000000000006',
        '30000000-0000-0000-0000-000000000007','30000000-0000-0000-0000-000000000008',
        '30000000-0000-0000-0000-000000000009','30000000-0000-0000-0000-000000000010',
        '30000000-0000-0000-0000-000000000011','30000000-0000-0000-0000-000000000012',
        '30000000-0000-0000-0000-000000000013','30000000-0000-0000-0000-000000000014',
        '30000000-0000-0000-0000-000000000015','30000000-0000-0000-0000-000000000016',
        '30000000-0000-0000-0000-000000000017','30000000-0000-0000-0000-000000000018',
        '30000000-0000-0000-0000-000000000019','30000000-0000-0000-0000-000000000020',
        '30000000-0000-0000-0000-000000000021','30000000-0000-0000-0000-000000000022',
        '30000000-0000-0000-0000-000000000023','30000000-0000-0000-0000-000000000024'
    ];
    q_id UUID;
BEGIN
    FOREACH q_id IN ARRAY q_ids LOOP
        INSERT INTO assessment_schema.options (question_id, option_text, is_correct, weight, option_order) VALUES
        (q_id, 'Not at all',   false, 1.0, 1),
        (q_id, 'Slightly',     false, 2.0, 2),
        (q_id, 'Moderately',   false, 3.0, 3),
        (q_id, 'Quite a bit',  false, 4.0, 4),
        (q_id, 'Very much',    false, 5.0, 5);
    END LOOP;
END $$;
