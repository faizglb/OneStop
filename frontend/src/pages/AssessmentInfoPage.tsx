import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getAssessmentById, startAssessment } from '../services/assessmentService';
import type { AssessmentSummary } from '../types/assessment';

const AssessmentInfoPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [assessment, setAssessment] = useState<AssessmentSummary | null>(null);
  const [loading, setLoading]       = useState(true);
  const [starting, setStarting]     = useState(false);
  const [error, setError]           = useState('');

  useEffect(() => {
    if (!id) return;
    getAssessmentById(id)
      .then(setAssessment)
      .catch(() => setError('Assessment not found.'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleStart = async () => {
    if (!id) return;
    setStarting(true);
    try {
      const session = await startAssessment(id);
      navigate(`/assessments/${id}/take`, { state: { session } });
    } catch (e: unknown) {
      const err = e as { response?: { data?: { message?: string } } };
      setError(err.response?.data?.message ?? 'Failed to start assessment. Please try again.');
      setStarting(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="info-page-skeleton">
          <div className="skeleton skeleton-title" style={{ width: 300, height: 36 }} />
          <div className="skeleton skeleton-text" style={{ width: '100%', height: 120 }} />
        </div>
      </div>
    );
  }

  if (!assessment) {
    return (
      <div className="page-container">
        <div className="error-state">
          <h2>Assessment Not Found</h2>
          <button className="btn-primary" onClick={() => navigate('/assessments')}>Back to Assessments</button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <button className="back-btn" onClick={() => navigate('/assessments')}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
        </svg>
        Back to Assessments
      </button>

      <div className="info-page-layout">
        {/* Main info */}
        <div className="info-main">
          <div className="info-type-badge">{assessment.assessmentType}</div>
          <h1 className="info-title">{assessment.title}</h1>
          <p className="info-description">{assessment.description}</p>

          {/* Instructions */}
          <div className="info-instructions-card">
            <h3>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              Instructions
            </h3>
            <ul className="instructions-list">
              <li>Read each question carefully before answering.</li>
              <li>You can navigate freely between questions using the palette.</li>
              <li>Flag questions you want to revisit using the flag button.</li>
              <li>Your answers are auto-saved every time you select an option.</li>
              {assessment.assessmentType === 'APTITUDE' && (
                <li>Each correct answer earns marks based on difficulty (1–3 marks).</li>
              )}
              {(assessment.assessmentType === 'PERSONALITY' || assessment.assessmentType === 'INTEREST') && (
                <li>There are no right or wrong answers — respond honestly.</li>
              )}
              <li>Do NOT close or refresh the browser during the assessment.</li>
              <li>The timer will auto-submit if you run out of time.</li>
            </ul>
          </div>

          {error && (
            <div className="error-banner">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              {error}
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div className="info-sidebar">
          <div className="info-summary-card">
            <h3>Assessment Details</h3>
            <div className="summary-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
              <div>
                <div className="summary-label">Time Limit</div>
                <div className="summary-value">{assessment.timeLimitMinutes} minutes</div>
              </div>
            </div>
            <div className="summary-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
              </svg>
              <div>
                <div className="summary-label">Total Questions</div>
                <div className="summary-value">{assessment.totalQuestions}</div>
              </div>
            </div>
            <div className="summary-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
              </svg>
              <div>
                <div className="summary-label">Type</div>
                <div className="summary-value">{assessment.assessmentType}</div>
              </div>
            </div>

            <div className="info-checklist">
              <div className="check-item">✓ Auto-save enabled</div>
              <div className="check-item">✓ Resume if disconnected</div>
              <div className="check-item">✓ Instant results after submission</div>
            </div>

            <button
              className="btn-primary btn-full"
              onClick={handleStart}
              disabled={starting}
              id="start-assessment-btn"
            >
              {starting ? (
                <>
                  <span className="spinner" /> Starting...
                </>
              ) : (
                <>
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polygon points="5 3 19 12 5 21 5 3"/>
                  </svg>
                  Begin Assessment
                </>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssessmentInfoPage;
