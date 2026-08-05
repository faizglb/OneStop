import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { getResult } from '../services/assessmentService';
import type { AssessmentResult, TraitLevel } from '../types/assessment';
import ScoreCircle from '../components/assessment/ScoreCircle';
import BarChart    from '../components/assessment/BarChart';
import RadarChart  from '../components/assessment/RadarChart';

const levelColor: Record<TraitLevel, string> = {
  HIGH:     '#22c55e',
  MODERATE: '#6C3BEB',
  LOW:      '#f59e0b',
};

const levelBg: Record<TraitLevel, string> = {
  HIGH:     'rgba(34,197,94,0.1)',
  MODERATE: 'rgba(108,59,235,0.1)',
  LOW:      'rgba(245,158,11,0.1)',
};

function formatTime(secs: number): string {
  const m = Math.floor(secs / 60);
  const s = secs % 60;
  return m > 0 ? `${m}m ${s}s` : `${s}s`;
}

const AssessmentResultPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { resultId } = useParams<{ resultId: string }>();

  const [result, setResult] = useState<AssessmentResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const r = location.state?.result as AssessmentResult | undefined;
    if (r) {
      setResult(r);
      setLoading(false);
      return;
    }
    if (resultId) {
      getResult(resultId)
        .then(setResult)
        .catch(() => setResult(null))
        .finally(() => setLoading(false));
    }
  }, [resultId, location.state]);

  if (loading) {
    return (
      <div className="page-container">
        <div className="result-loading">
          <div className="loading-spinner-large" />
          <p>Computing your results...</p>
        </div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="page-container">
        <div className="error-state">
          <h2>Result Not Found</h2>
          <button className="btn-primary" onClick={() => navigate('/assessments')}>Back to Assessments</button>
        </div>
      </div>
    );
  }

  const isAptitude    = result.assessmentType === 'APTITUDE';
  const isPersonality = result.assessmentType === 'PERSONALITY';
  const isInterest    = result.assessmentType === 'INTEREST';

  return (
    <div className="page-container result-page">
      {/* Header */}
      <div className="result-header">
        <div className="result-header-left">
          <button className="back-btn" onClick={() => navigate('/assessments')}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            All Assessments
          </button>
          <div className="result-type-badge">{result.assessmentType}</div>
          <h1 className="result-title">{result.assessmentTitle} — Results</h1>
          {result.completedAt && (
            <p className="result-date">
              Completed on {new Date(result.completedAt).toLocaleDateString('en-IN', {
                day: 'numeric', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit'
              })}
            </p>
          )}
        </div>
      </div>

      {/* Score overview */}
      <div className="result-overview-grid">
        <div className="result-score-card">
          <ScoreCircle
            percentage={Math.round(Number(result.percentage))}
            size={160}
            label="Overall Score"
            sublabel={isAptitude ? `${Math.round(Number(result.totalScore))}/${Math.round(Number(result.maxScore))}` : undefined}
          />
        </div>

        <div className="result-stats-grid">
          {isAptitude && (
            <div className="result-stat-card">
              <div className="stat-label">Score</div>
              <div className="stat-value">{Math.round(Number(result.totalScore))}/{Math.round(Number(result.maxScore))}</div>
            </div>
          )}
          <div className="result-stat-card">
            <div className="stat-label">Percentile</div>
            <div className="stat-value" style={{ color: '#6C3BEB' }}>
              ~{Math.round(Number(result.percentile))}th
            </div>
          </div>
          <div className="result-stat-card">
            <div className="stat-label">Time Taken</div>
            <div className="stat-value">{formatTime(result.timeTakenSecs)}</div>
          </div>
          <div className="result-stat-card">
            <div className="stat-label">Status</div>
            <div className="stat-value" style={{ color: '#22c55e' }}>{result.status}</div>
          </div>
        </div>
      </div>

      {/* Aptitude — Section bar chart */}
      {isAptitude && result.sectionScores.length > 0 && (
        <div className="result-section-card">
          <h2 className="section-heading">Section-wise Performance</h2>
          <BarChart
            labels={result.chartLabels}
            values={result.chartValues}
          />
        </div>
      )}

      {/* Personality / Interest — Radar + Trait cards */}
      {(isPersonality || isInterest) && result.traitScores.length > 0 && (
        <div className="result-traits-section">
          <div className="result-radar-card">
            <h2 className="section-heading">
              {isPersonality ? 'Personality Profile' : 'Interest Profile'}
            </h2>
            <div className="radar-wrapper">
              <RadarChart
                labels={result.traitScores.map(t => t.traitLabel)}
                values={result.traitScores.map(t => Number(t.normalizedScore))}
                size={280}
              />
            </div>
          </div>

          <div className="result-traits-cards">
            <h2 className="section-heading">Trait Breakdown</h2>
            <div className="traits-grid">
              {result.traitScores.map(ts => (
                <div
                  key={ts.traitKey}
                  className="trait-card"
                  style={{ borderColor: levelColor[ts.level], background: levelBg[ts.level] }}
                >
                  <div className="trait-card-header">
                    <span className="trait-label">{ts.traitLabel}</span>
                    <span
                      className="trait-level-badge"
                      style={{ color: levelColor[ts.level], borderColor: levelColor[ts.level] }}
                    >
                      {ts.level}
                    </span>
                  </div>
                  <div className="trait-score-row">
                    <div className="trait-bar-track">
                      <div
                        className="trait-bar-fill"
                        style={{
                          width: `${Math.round(Number(ts.normalizedScore))}%`,
                          background: levelColor[ts.level],
                        }}
                      />
                    </div>
                    <span className="trait-score-pct">{Math.round(Number(ts.normalizedScore))}%</span>
                  </div>
                  <p className="trait-description">{ts.description}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Strengths & Weaknesses */}
      {(result.strengths.length > 0 || result.weaknesses.length > 0) && (
        <div className="result-sw-grid">
          {result.strengths.length > 0 && (
            <div className="sw-card strengths-card">
              <h3>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#22c55e" strokeWidth="2">
                  <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
                </svg>
                Strengths
              </h3>
              <ul>
                {result.strengths.map(s => (
                  <li key={s}>
                    <span className="sw-dot strength-dot" />
                    {s}
                  </li>
                ))}
              </ul>
            </div>
          )}
          {result.weaknesses.length > 0 && (
            <div className="sw-card weaknesses-card">
              <h3>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#f59e0b" strokeWidth="2">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="12" y1="8" x2="12" y2="12"/>
                  <line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                Areas to Improve
              </h3>
              <ul>
                {result.weaknesses.map(w => (
                  <li key={w}>
                    <span className="sw-dot weakness-dot" />
                    {w}
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}

      {/* Actions */}
      <div className="result-actions">
        <button className="btn-primary" onClick={() => navigate(`/assessments/${result.assessmentId}/info`)}>
          Retake Assessment
        </button>
        <button className="btn-ghost" onClick={() => navigate('/assessments/history')}>
          View History
        </button>
        <button className="btn-ghost" onClick={() => navigate('/assessments')}>
          All Assessments
        </button>
      </div>
    </div>
  );
};

export default AssessmentResultPage;
