import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getHistory } from '../services/assessmentService';
import type { AssessmentHistory, AssessmentType, ResultStatus } from '../types/assessment';

const typeColors: Record<AssessmentType, string> = {
  APTITUDE:    '#6C3BEB',
  PERSONALITY: '#ec4899',
  INTEREST:    '#f59e0b',
};

const statusBadge: Record<ResultStatus, { label: string; color: string }> = {
  COMPLETED:   { label: 'Completed', color: '#22c55e' },
  IN_PROGRESS: { label: 'In Progress', color: '#6C3BEB' },
  ABANDONED:   { label: 'Abandoned', color: '#ef4444' },
};

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString('en-IN', {
    day: 'numeric', month: 'short', year: 'numeric',
  });
}

function formatTime(secs: number): string {
  const m = Math.floor(secs / 60);
  const s = secs % 60;
  return m > 0 ? `${m}m ${s}s` : `${s}s`;
}

const AssessmentHistoryPage: React.FC = () => {
  const navigate = useNavigate();
  const [history, setHistory] = useState<AssessmentHistory[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter]   = useState<AssessmentType | 'ALL'>('ALL');

  useEffect(() => {
    getHistory()
      .then(setHistory)
      .catch(() => setHistory([]))
      .finally(() => setLoading(false));
  }, []);

  const filtered = filter === 'ALL' ? history : history.filter(h => h.assessmentType === filter);

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <h1 className="history-title">Assessment History</h1>
          <p className="history-subtitle">All your past attempts across all assessment types</p>
        </div>
        <button className="btn-primary btn-sm" onClick={() => navigate('/assessments')}>
          New Assessment
        </button>
      </div>

      {/* Filter tabs */}
      <div className="history-filters">
        {(['ALL', 'APTITUDE', 'PERSONALITY', 'INTEREST'] as const).map(f => (
          <button
            key={f}
            className={`filter-tab ${filter === f ? 'active' : ''}`}
            onClick={() => setFilter(f)}
          >
            {f === 'ALL' ? 'All Types' : f.charAt(0) + f.slice(1).toLowerCase()}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="history-skeleton">
          {[1, 2, 3].map(i => (
            <div key={i} className="history-row skeleton-row">
              <div className="skeleton" style={{ width: 80, height: 20 }} />
              <div className="skeleton" style={{ width: 200, height: 20 }} />
              <div className="skeleton" style={{ width: 80, height: 20 }} />
              <div className="skeleton" style={{ width: 60, height: 20 }} />
            </div>
          ))}
        </div>
      ) : filtered.length === 0 ? (
        <div className="history-empty">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.3)" strokeWidth="1">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/>
          </svg>
          <h3>No attempts yet</h3>
          <p>Take your first assessment to see results here.</p>
          <button className="btn-primary" onClick={() => navigate('/assessments')}>Start Now</button>
        </div>
      ) : (
        <div className="history-table-wrapper">
          <table className="history-table">
            <thead>
              <tr>
                <th>Assessment</th>
                <th>Type</th>
                <th>Score</th>
                <th>Time Taken</th>
                <th>Status</th>
                <th>Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(h => {
                const badge  = statusBadge[h.status];
                const tColor = typeColors[h.assessmentType];
                return (
                  <tr key={h.resultId} className="history-row">
                    <td className="history-title-cell">
                      <div className="history-dot" style={{ background: tColor }} />
                      {h.assessmentTitle}
                    </td>
                    <td>
                      <span className="type-pill" style={{ borderColor: tColor, color: tColor }}>
                        {h.assessmentType}
                      </span>
                    </td>
                    <td>
                      {h.status === 'COMPLETED' ? (
                        <span className="score-pill">
                          {Math.round(Number(h.percentage))}%
                        </span>
                      ) : '—'}
                    </td>
                    <td>{h.timeTakenSecs > 0 ? formatTime(h.timeTakenSecs) : '—'}</td>
                    <td>
                      <span
                        className="status-badge"
                        style={{ color: badge.color, borderColor: badge.color }}
                      >
                        {badge.label}
                      </span>
                    </td>
                    <td>{formatDate(h.startedAt)}</td>
                    <td>
                      <div className="history-actions">
                        {h.status === 'COMPLETED' && (
                          <button
                            className="btn-ghost btn-xs"
                            onClick={() => navigate(`/assessments/result/${h.resultId}`)}
                          >
                            View
                          </button>
                        )}
                        {h.status === 'IN_PROGRESS' && (
                          <button
                            className="btn-primary btn-xs"
                            onClick={() => navigate(`/assessments/${h.assessmentId}/info`)}
                          >
                            Resume
                          </button>
                        )}
                        <button
                          className="btn-ghost btn-xs"
                          onClick={() => navigate(`/assessments/${h.assessmentId}/info`)}
                        >
                          Retake
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default AssessmentHistoryPage;
