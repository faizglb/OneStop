import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAvailableAssessments } from '../services/assessmentService';
import { getHistory } from '../services/assessmentService';
import type { AssessmentSummary, AssessmentHistory, AssessmentType } from '../types/assessment';

const typeConfig: Record<AssessmentType, {
  icon: React.ReactNode;
  gradient: string;
  tag: string;
  duration: string;
}> = {
  APTITUDE: {
    icon: (
      <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <polygon points="12 2 2 7 12 12 22 7 12 2"/>
        <polyline points="2 17 12 22 22 17"/>
        <polyline points="2 12 12 17 22 12"/>
      </svg>
    ),
    gradient: 'linear-gradient(135deg, #6C3BEB 0%, #9B59E8 100%)',
    tag: 'Logic & Skills',
    duration: '60 mins',
  },
  PERSONALITY: {
    icon: (
      <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
      </svg>
    ),
    gradient: 'linear-gradient(135deg, #ec4899 0%, #a855f7 100%)',
    tag: 'Big Five / OCEAN',
    duration: '40 mins',
  },
  INTEREST: {
    icon: (
      <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
        <circle cx="12" cy="12" r="10"/>
        <path d="M12 8v4l3 3"/>
        <path d="M3.05 11a9 9 0 1 0 .5-2.48"/>
      </svg>
    ),
    gradient: 'linear-gradient(135deg, #f59e0b 0%, #ef4444 100%)',
    tag: "Holland's RIASEC",
    duration: '30 mins',
  },
};

const AssessmentLandingPage: React.FC = () => {
  const navigate = useNavigate();
  const [assessments, setAssessments]   = useState<AssessmentSummary[]>([]);
  const [history, setHistory]           = useState<AssessmentHistory[]>([]);
  const [loading, setLoading]           = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [aList, hList] = await Promise.all([
          getAvailableAssessments(),
          getHistory(),
        ]);
        setAssessments(aList);
        setHistory(hList);
      } catch {
        // fallback
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const getLastResult = (assessmentId: string) =>
    history.find(h => h.assessmentId === assessmentId && h.status === 'COMPLETED');

  const hasActiveSession = (assessmentId: string) =>
    history.some(h => h.assessmentId === assessmentId && h.status === 'IN_PROGRESS');

  return (
    <div className="page-container">
      {/* Hero */}
      <div className="assessment-landing-hero">
        <div className="hero-badge">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
          </svg>
          AI-Powered Assessment Engine
        </div>
        <h1 className="hero-title">Discover Your <span className="gradient-text">True Potential</span></h1>
        <p className="hero-subtitle">
          Take scientifically designed assessments to understand your aptitude, personality, and interests.
          Your results will power AI-driven career recommendations.
        </p>
        <div className="hero-stats">
          <div className="stat-pill">
            <strong>89+</strong> Questions
          </div>
          <div className="stat-pill">
            <strong>3</strong> Assessment Types
          </div>
          <div className="stat-pill">
            <strong>130</strong> min Total
          </div>
        </div>
      </div>

      {/* Cards */}
      {loading ? (
        <div className="assessment-cards-grid">
          {[1, 2, 3].map(i => (
            <div key={i} className="assessment-card skeleton-card">
              <div className="skeleton skeleton-icon" />
              <div className="skeleton skeleton-title" />
              <div className="skeleton skeleton-text" />
              <div className="skeleton skeleton-btn" />
            </div>
          ))}
        </div>
      ) : (
        <div className="assessment-cards-grid">
          {assessments.map(a => {
            const config     = typeConfig[a.assessmentType];
            const lastResult = getLastResult(a.id);
            const inProgress = hasActiveSession(a.id);
            return (
              <div key={a.id} className="assessment-card">
                <div className="assessment-card-top" style={{ background: config.gradient }}>
                  <div className="assessment-card-icon">{config.icon}</div>
                  <div className="assessment-card-tags">
                    <span className="tag">{config.tag}</span>
                    <span className="tag">{config.duration}</span>
                  </div>
                  {lastResult && (
                    <div className="previous-score">
                      Previous: <strong>{Math.round(Number(lastResult.percentage))}%</strong>
                    </div>
                  )}
                </div>

                <div className="assessment-card-body">
                  <h2>{a.title}</h2>
                  <p>{a.description}</p>

                  <div className="assessment-meta">
                    <div className="meta-item">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10"/>
                        <polyline points="12 6 12 12 16 14"/>
                      </svg>
                      {a.timeLimitMinutes} minutes
                    </div>
                    <div className="meta-item">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                        <polyline points="14 2 14 8 20 8"/>
                      </svg>
                      {a.totalQuestions} questions
                    </div>
                  </div>

                  <div className="assessment-card-actions">
                    {inProgress ? (
                      <button
                        className="btn-primary btn-resume"
                        onClick={() => navigate(`/assessments/${a.id}/info`)}
                      >
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <polygon points="5 3 19 12 5 21 5 3"/>
                        </svg>
                        Resume
                      </button>
                    ) : (
                      <button
                        className="btn-primary"
                        onClick={() => navigate(`/assessments/${a.id}/info`)}
                      >
                        {lastResult ? 'Retake Assessment' : 'Start Assessment'}
                      </button>
                    )}
                    {lastResult && (
                      <button
                        className="btn-ghost btn-sm"
                        onClick={() => navigate(`/assessments/result/${lastResult.resultId}`)}
                      >
                        View Results
                      </button>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* History shortcut */}
      {history.length > 0 && (
        <div className="assessment-history-cta">
          <p>You have <strong>{history.length}</strong> assessment attempt{history.length !== 1 ? 's' : ''}</p>
          <button className="btn-ghost" onClick={() => navigate('/assessments/history')}>
            View Full History →
          </button>
        </div>
      )}
    </div>
  );
};

export default AssessmentLandingPage;
