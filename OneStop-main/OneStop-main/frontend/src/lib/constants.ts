export const API_BASE_URL = import.meta.env.VITE_API_URL || '/api/v1';

export const STORAGE_KEYS = {
  ACCESS_TOKEN:  'acea_access_token',
  REFRESH_TOKEN: 'acea_refresh_token',
  USER:          'acea_user',
} as const;

export const ROUTES = {
  HOME:             '/',
  LOGIN:            '/login',
  REGISTER:         '/register',
  FORGOT_PASSWORD:  '/forgot-password',
  RESET_PASSWORD:   '/reset-password',
  VERIFY_EMAIL:     '/verify-email',
  DASHBOARD:        '/dashboard',
  NOT_FOUND:        '/404',
  // Assessment Module (Sprint 3)
  ASSESSMENTS:           '/assessments',
  ASSESSMENT_INFO:       '/assessments/:id/info',
  ASSESSMENT_TAKE:       '/assessments/:id/take',
  ASSESSMENT_RESULT:     '/assessments/result/:resultId',
  ASSESSMENT_HISTORY:    '/assessments/history',
  // Recommendation Engine (Sprint 4)
  RECOMMENDATIONS:       '/recommendations',
  CAREER_DETAILS:        '/recommendations/career/:id',
  // College Finder Module (Sprint 5)
  COLLEGE_FINDER:          '/colleges',
  COLLEGE_DETAILS:         '/colleges/:id',
  COLLEGE_RECOMMENDATIONS: '/college-recommendations',
  BOOKMARKS:               '/bookmarks',
  // Scholarship Finder Module (Sprint 6)
  SCHOLARSHIP_FINDER:          '/scholarships',
  SCHOLARSHIP_DETAILS:         '/scholarships/:id',
  SCHOLARSHIP_RECOMMENDATIONS: '/scholarship-recommendations',
  SCHOLARSHIP_BOOKMARKS:       '/scholarships/bookmarks',
  SCHOLARSHIP_APPLICATIONS:    '/scholarships/applications',
  SCHOLARSHIP_CALENDAR:        '/scholarships/calendar',
  // Learning Resources Finder Module (Sprint 7)
  LEARNING_DASHBOARD:          '/resources',
  RESOURCE_DETAILS:            '/resources/:id',
  LEARNING_RECOMMENDATIONS:    '/learning-recommendations',
  LEARNING_BOOKMARKS:          '/resources/bookmarks',
  // AI Roadmap Generator Module (Sprint 8)
  ROADMAP_DASHBOARD:           '/roadmaps',
  ROADMAP_TIMELINE:            '/roadmaps/:id/timeline',
  ROADMAP_DETAILS:             '/roadmaps/:id',
  // AI Resume Analyzer Module (Sprint 9)
  RESUME_ANALYZER:             '/resume-analyzer',
  RESUME_ANALYSIS:             '/resume-analyzer/:id',
  // AI Skill Gap Analysis Module (Sprint 10)
  SKILL_GAP_DASHBOARD:         '/skill-gap',
  // AI Career Chatbot Module (Sprint 11)
  CHAT:                        '/chat',
  CHAT_SESSION:                '/chat/:sessionId',
} as const;

export const TOKEN_REFRESH_THRESHOLD_MS = 2 * 60 * 1000; // Refresh when < 2 min remaining

export const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&_#])[A-Za-z\d@$!%*?&_#]{8,}$/;
