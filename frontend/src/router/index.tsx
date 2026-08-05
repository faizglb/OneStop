import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ROUTES } from '@/lib/constants';
import ProtectedRoute from '@/components/auth/ProtectedRoute';
import LandingPage          from '@/pages/LandingPage';
import LoginPage            from '@/pages/LoginPage';
import SignupPage           from '@/pages/SignupPage';
import ForgotPasswordPage   from '@/pages/ForgotPasswordPage';
import ResetPasswordPage    from '@/pages/ResetPasswordPage';
import VerifyEmailPage      from '@/pages/VerifyEmailPage';
import DashboardPage        from '@/pages/DashboardPage';
import NotFoundPage         from '@/pages/NotFoundPage';
// Sprint 3 — Assessment Module
import AssessmentLandingPage from '@/pages/AssessmentLandingPage';
import AssessmentInfoPage    from '@/pages/AssessmentInfoPage';
import AssessmentTakePage    from '@/pages/AssessmentTakePage';
import AssessmentResultPage  from '@/pages/AssessmentResultPage';
import AssessmentHistoryPage from '@/pages/AssessmentHistoryPage';
// Sprint 4 — Recommendation Engine
import RecommendationDashboard from '@/pages/RecommendationDashboard';
import CareerDetailPage        from '@/pages/CareerDetailPage';
// Sprint 5 — College Finder
import CollegeFinderPage          from '@/pages/CollegeFinderPage';
import CollegeDetailPage         from '@/pages/CollegeDetailPage';
import RecommendedCollegesPage    from '@/pages/RecommendedCollegesPage';
import BookmarksPage               from '@/pages/BookmarksPage';
// Sprint 6 — Scholarship Finder
import ScholarshipFinderPage          from '@/pages/ScholarshipFinderPage';
import ScholarshipDetailPage         from '@/pages/ScholarshipDetailPage';
import RecommendedScholarshipsPage    from '@/pages/RecommendedScholarshipsPage';
import ScholarshipsBookmarksPage       from '@/pages/ScholarshipsBookmarksPage';
import ApplicationTrackerPage         from '@/pages/ApplicationTrackerPage';
import DeadlineCalendar                from '@/pages/DeadlineCalendar';
// Sprint 7 — Learning Resources Finder
import LearningDashboardPage          from '@/pages/LearningDashboardPage';
import LearningResourceDetailPage     from '@/pages/LearningResourceDetailPage';
import RecommendedResourcesPage       from '@/pages/RecommendedResourcesPage';
import LearningBookmarksPage          from '@/pages/LearningBookmarksPage';
// Sprint 8 — AI Roadmap Generator
import RoadmapDashboardPage           from '@/pages/RoadmapDashboardPage';
import RoadmapTimelinePage            from '@/pages/RoadmapTimelinePage';
import RoadmapDetailPage              from '@/pages/RoadmapDetailPage';
// Sprint 9 — AI Resume Analyzer
import ResumeAnalyzerPage             from '@/pages/ResumeAnalyzerPage';
import ResumeAnalysisDetailPage       from '@/pages/ResumeAnalysisDetailPage';
// Sprint 10 — AI Skill Gap Analysis Engine
import SkillGapDashboardPage          from '@/pages/SkillGapDashboardPage';
// Sprint 11 — AI Career Chatbot Module
import ChatPage                       from '@/pages/ChatPage';

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path={ROUTES.HOME}            element={<LandingPage />} />
        <Route path={ROUTES.LOGIN}           element={<LoginPage />} />
        <Route path={ROUTES.REGISTER}        element={<SignupPage />} />
        <Route path={ROUTES.FORGOT_PASSWORD} element={<ForgotPasswordPage />} />
        <Route path={ROUTES.RESET_PASSWORD}  element={<ResetPasswordPage />} />
        <Route path={ROUTES.VERIFY_EMAIL}    element={<VerifyEmailPage />} />

        {/* Protected Routes — Sprint 2+ */}
        <Route path={ROUTES.DASHBOARD} element={
          <ProtectedRoute requireEmailVerification>
            <DashboardPage />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 3: Assessment Module */}
        <Route path={ROUTES.ASSESSMENTS} element={
          <ProtectedRoute requireEmailVerification>
            <AssessmentLandingPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.ASSESSMENT_INFO} element={
          <ProtectedRoute requireEmailVerification>
            <AssessmentInfoPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.ASSESSMENT_TAKE} element={
          <ProtectedRoute requireEmailVerification>
            <AssessmentTakePage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.ASSESSMENT_RESULT} element={
          <ProtectedRoute requireEmailVerification>
            <AssessmentResultPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.ASSESSMENT_HISTORY} element={
          <ProtectedRoute requireEmailVerification>
            <AssessmentHistoryPage />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 4: Recommendation Engine */}
        <Route path={ROUTES.RECOMMENDATIONS} element={
          <ProtectedRoute requireEmailVerification>
            <RecommendationDashboard />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.CAREER_DETAILS} element={
          <ProtectedRoute requireEmailVerification>
            <CareerDetailPage />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 5: College Finder Module */}
        <Route path={ROUTES.COLLEGE_FINDER} element={
          <ProtectedRoute requireEmailVerification>
            <CollegeFinderPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.COLLEGE_DETAILS} element={
          <ProtectedRoute requireEmailVerification>
            <CollegeDetailPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.COLLEGE_RECOMMENDATIONS} element={
          <ProtectedRoute requireEmailVerification>
            <RecommendedCollegesPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.BOOKMARKS} element={
          <ProtectedRoute requireEmailVerification>
            <BookmarksPage />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 6: Scholarship Finder Module */}
        <Route path={ROUTES.SCHOLARSHIP_FINDER} element={
          <ProtectedRoute requireEmailVerification>
            <ScholarshipFinderPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.SCHOLARSHIP_DETAILS} element={
          <ProtectedRoute requireEmailVerification>
            <ScholarshipDetailPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.SCHOLARSHIP_RECOMMENDATIONS} element={
          <ProtectedRoute requireEmailVerification>
            <RecommendedScholarshipsPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.SCHOLARSHIP_BOOKMARKS} element={
          <ProtectedRoute requireEmailVerification>
            <ScholarshipsBookmarksPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.SCHOLARSHIP_APPLICATIONS} element={
          <ProtectedRoute requireEmailVerification>
            <ApplicationTrackerPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.SCHOLARSHIP_CALENDAR} element={
          <ProtectedRoute requireEmailVerification>
            <DeadlineCalendar />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 7: Learning Finder Module */}
        <Route path={ROUTES.LEARNING_DASHBOARD} element={
          <ProtectedRoute requireEmailVerification>
            <LearningDashboardPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.RESOURCE_DETAILS} element={
          <ProtectedRoute requireEmailVerification>
            <LearningResourceDetailPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.LEARNING_RECOMMENDATIONS} element={
          <ProtectedRoute requireEmailVerification>
            <RecommendedResourcesPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.LEARNING_BOOKMARKS} element={
          <ProtectedRoute requireEmailVerification>
            <LearningBookmarksPage />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 8: AI Roadmap Generator Module */}
        <Route path={ROUTES.ROADMAP_DASHBOARD} element={
          <ProtectedRoute requireEmailVerification>
            <RoadmapDashboardPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.ROADMAP_TIMELINE} element={
          <ProtectedRoute requireEmailVerification>
            <RoadmapTimelinePage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.ROADMAP_DETAILS} element={
          <ProtectedRoute requireEmailVerification>
            <RoadmapDetailPage />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 9: AI Resume Analyzer Module */}
        <Route path={ROUTES.RESUME_ANALYZER} element={
          <ProtectedRoute requireEmailVerification>
            <ResumeAnalyzerPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.RESUME_ANALYSIS} element={
          <ProtectedRoute requireEmailVerification>
            <ResumeAnalysisDetailPage />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 10: AI Skill Gap Analysis Module */}
        <Route path={ROUTES.SKILL_GAP_DASHBOARD} element={
          <ProtectedRoute requireEmailVerification>
            <SkillGapDashboardPage />
          </ProtectedRoute>
        } />

        {/* Protected Routes — Sprint 11: AI Career Chatbot Module */}
        <Route path={ROUTES.CHAT} element={
          <ProtectedRoute requireEmailVerification>
            <ChatPage />
          </ProtectedRoute>
        } />
        <Route path={ROUTES.CHAT_SESSION} element={
          <ProtectedRoute requireEmailVerification>
            <ChatPage />
          </ProtectedRoute>
        } />

        {/* Fallbacks */}
        <Route path={ROUTES.NOT_FOUND} element={<NotFoundPage />} />
        <Route path="*"                element={<Navigate to={ROUTES.NOT_FOUND} replace />} />
      </Routes>
    </BrowserRouter>
  );
}
