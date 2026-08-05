import React, { useState, useCallback, useEffect, useRef } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { saveAnswer, submitAssessment, startAssessment } from '../services/assessmentService';
import type { StartSessionResponse, AnswerMap, FlagMap } from '../types/assessment';

import TimerBar       from '../components/assessment/TimerBar';
import QuestionCard   from '../components/assessment/QuestionCard';
import QuestionPalette from '../components/assessment/QuestionPalette';
import ConfirmDialog  from '../components/assessment/ConfirmDialog';

const AUTOSAVE_DEBOUNCE_MS = 800;

const AssessmentTakePage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { id }   = useParams<{ id: string }>();

  const [session, setSession]         = useState<StartSessionResponse | null>(null);
  const [loading, setLoading]         = useState(true);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers]         = useState<AnswerMap>({});
  const [flags, setFlags]             = useState<FlagMap>({});
  const [showSubmit, setShowSubmit]   = useState(false);
  const [submitting, setSubmitting]   = useState(false);
  const [autoSaving, setAutoSaving]   = useState(false);
  const [error, setError]             = useState('');

  const autosaveTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Load session from location.state or fetch fresh
  useEffect(() => {
    const s = location.state?.session as StartSessionResponse | undefined;
    if (s) {
      setSession(s);
      setLoading(false);
      return;
    }
    // Fallback: start/resume directly
    if (id) {
      startAssessment(id)
        .then(newSession => {
          setSession(newSession);
          setLoading(false);
        })
        .catch(() => {
          setError('Failed to load assessment. Please go back and try again.');
          setLoading(false);
        });
    }
  }, [id, location.state]);

  const handleSelectOption = useCallback((questionId: string, optionId: string) => {
    if (!session) return;

    setAnswers(prev => ({ ...prev, [questionId]: optionId }));

    // Debounced autosave
    if (autosaveTimer.current) clearTimeout(autosaveTimer.current);
    autosaveTimer.current = setTimeout(async () => {
      setAutoSaving(true);
      try {
        await saveAnswer(session.resultId, questionId, optionId, flags[questionId] ?? false);
      } catch {
        // Silent failure — local state preserved
      } finally {
        setAutoSaving(false);
      }
    }, AUTOSAVE_DEBOUNCE_MS);
  }, [session, flags]);

  const handleFlag = useCallback((questionId: string) => {
    setFlags(prev => ({ ...prev, [questionId]: !prev[questionId] }));
  }, []);

  const handleNavigate = (index: number) => {
    setCurrentIndex(index);
  };

  const handlePrev = () => setCurrentIndex(i => Math.max(0, i - 1));
  const handleNext = () => {
    if (!session) return;
    setCurrentIndex(i => Math.min(session.questions.length - 1, i + 1));
  };

  const handleExpire = useCallback(async () => {
    if (!session || submitting) return;
    setSubmitting(true);
    try {
      const result = await submitAssessment(session.resultId, session.timeLimitMinutes * 60);
      navigate(`/assessments/result/${result.resultId}`, { state: { result } });
    } catch {
      navigate('/assessments');
    }
  }, [session, submitting, navigate]);

  const handleSubmitConfirm = async () => {
    if (!session) return;
    setSubmitting(true);
    setShowSubmit(false);
    const timeTaken = Math.floor((Date.now() - new Date(session.startedAt).getTime()) / 1000);
    try {
      const result = await submitAssessment(session.resultId, timeTaken);
      navigate(`/assessments/result/${result.resultId}`, { state: { result } });
    } catch (e: unknown) {
      const err = e as { response?: { data?: { message?: string } } };
      setError(err.response?.data?.message ?? 'Submission failed. Please try again.');
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="take-page-loading">
        <div className="loading-spinner-large" />
        <p>Loading your assessment...</p>
      </div>
    );
  }

  if (error || !session) {
    return (
      <div className="page-container">
        <div className="error-state">
          <h2>Something went wrong</h2>
          <p>{error}</p>
          <button className="btn-primary" onClick={() => navigate('/assessments')}>Back to Assessments</button>
        </div>
      </div>
    );
  }

  const questions  = session.questions;
  const currentQ   = questions[currentIndex];
  const questionIds = questions.map(q => q.id);
  const answeredCount = Object.values(answers).filter(v => v !== null).length;
  const totalCount    = questions.length;
  const progressPct   = Math.round((answeredCount / totalCount) * 100);

  return (
    <div className="take-page">
      {/* Top bar */}
      <div className="take-topbar">
        <div className="take-title-area">
          <h1 className="take-title">{session.title}</h1>
          <span className="take-type-badge">{session.assessmentType}</span>
        </div>

        {/* Progress */}
        <div className="take-progress">
          <div className="take-progress-bar">
            <div className="take-progress-fill" style={{ width: `${progressPct}%` }} />
          </div>
          <span className="take-progress-label">{answeredCount}/{totalCount} answered</span>
        </div>

        <TimerBar expiresAt={session.expiresAt} onExpire={handleExpire} />

        {autoSaving && (
          <div className="autosave-indicator">
            <span className="spinner-sm" />
            Saving...
          </div>
        )}
      </div>

      {/* Main layout */}
      <div className="take-body">
        {/* Question area */}
        <div className="take-question-area">
          {currentQ && (
            <QuestionCard
              index={currentIndex}
              total={totalCount}
              questionText={currentQ.questionText}
              categoryName={currentQ.categoryName}
              marks={currentQ.marks}
              options={currentQ.options}
              selectedOptionId={answers[currentQ.id] ?? null}
              isFlagged={flags[currentQ.id] ?? false}
              onSelect={(optId) => handleSelectOption(currentQ.id, optId)}
              onFlag={() => handleFlag(currentQ.id)}
            />
          )}

          {/* Navigation buttons */}
          <div className="take-nav-buttons">
            <button
              className="btn-ghost btn-nav"
              onClick={handlePrev}
              disabled={currentIndex === 0}
            >
              ← Previous
            </button>

            <button
              className="btn-ghost btn-nav btn-skip"
              onClick={handleNext}
              disabled={currentIndex === totalCount - 1}
            >
              Skip →
            </button>

            {currentIndex === totalCount - 1 ? (
              <button
                className="btn-primary btn-submit-final"
                onClick={() => setShowSubmit(true)}
                disabled={submitting}
              >
                {submitting ? 'Submitting...' : '✓ Submit Assessment'}
              </button>
            ) : (
              <button
                className="btn-primary btn-nav btn-next"
                onClick={handleNext}
              >
                Next →
              </button>
            )}
          </div>

          {error && (
            <div className="error-banner mt-2">{error}</div>
          )}
        </div>

        {/* Palette sidebar */}
        <div className="take-sidebar">
          <QuestionPalette
            totalQuestions={totalCount}
            questionIds={questionIds}
            currentIndex={currentIndex}
            answers={answers}
            flags={flags}
            onNavigate={handleNavigate}
          />
          <button
            className="btn-primary btn-full mt-2"
            onClick={() => setShowSubmit(true)}
            disabled={submitting}
          >
            Submit Assessment
          </button>
        </div>
      </div>

      {/* Submit confirmation */}
      <ConfirmDialog
        isOpen={showSubmit}
        title="Submit Assessment?"
        message={`You have answered ${answeredCount} out of ${totalCount} questions. ${
          answeredCount < totalCount
            ? `${totalCount - answeredCount} question(s) are unanswered. `
            : ''
        }Once submitted, you cannot change your answers.`}
        confirmLabel="Yes, Submit"
        cancelLabel="Continue Reviewing"
        onConfirm={handleSubmitConfirm}
        onCancel={() => setShowSubmit(false)}
        isDestructive={answeredCount < totalCount}
      />
    </div>
  );
};

export default AssessmentTakePage;
