import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getLearningRecommendations,
  generateLearningRecommendations,
  bookmarkResource,
  removeBookmark
} from '../services/learningService';
import type { LearningRecommendationResponse } from '../types/learning';

const RecommendedResourcesPage: React.FC = () => {
  const navigate = useNavigate();
  const [recommendations, setRecommendations] = useState<LearningRecommendationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState('');

  const fetchRecommendations = async () => {
    try {
      const data = await getLearningRecommendations();
      setRecommendations(data);
    } catch {
      setError('Failed to fetch recommendations.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecommendations();
  }, []);

  const handleGenerate = async () => {
    setGenerating(true);
    setError('');
    try {
      const data = await generateLearningRecommendations();
      setRecommendations(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error generating AI course matches.');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <button className="back-btn" onClick={() => navigate('/resources')} style={{ marginBottom: 12 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            Learning Hub
          </button>
          <h1 className="history-title">AI Course Recommendations</h1>
          <p className="history-subtitle">Personalized courses and books suggestions matched against target careers and coding diagnostics deficiencies</p>
        </div>
        <button className="btn-primary btn-sm" onClick={handleGenerate} disabled={generating}>
          {generating ? 'Regenerating...' : 'Regenerate Matches'}
        </button>
      </div>

      {error && (
        <div className="error-banner" style={{ marginBottom: 24 }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          {error}
        </div>
      )}

      {loading ? (
        <div className="take-page-loading">
          <div className="loading-spinner-large" />
          <p>Running course alignment algorithms...</p>
        </div>
      ) : recommendations.length === 0 ? (
        /* Empty State */
        <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="rgba(108,59,235,0.4)" strokeWidth="1.5">
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 19.5A2.5 2.5 0 0 0 6.5 22H20M4 19.5V3A2.5 2.5 0 0 1 6.5 0.5H20v20H6.5a2.5 2.5 0 0 1-2.5-1M14 6h6v4h-6z"/>
          </svg>
          <h2>Recommended Syllabus Maps</h2>
          <p style={{ maxWidth: 500, margin: '8px auto 24px' }}>
            We'll evaluate skills gaps between your current level and target careers to suggest high-impact certifications.
          </p>
          <button className="btn-primary" onClick={handleGenerate} disabled={generating}>
            {generating ? 'Processing...' : 'Run Skill Gaps Check'}
          </button>
        </div>
      ) : (
        /* Recommendations Dashboard list */
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {recommendations.map(r => (
            <div 
              key={r.id} 
              className="info-instructions-card" 
              style={{ 
                background: 'rgba(255,255,255,0.02)', 
                borderRadius: 20, 
                padding: '24px 28px',
                display: 'grid',
                gridTemplateColumns: '1fr 200px',
                gap: 24,
                alignItems: 'center'
              }}
            >
              <div>
                <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 8 }}>
                  <span className="take-type-badge" style={{ 
                    backgroundColor: r.priority === 'HIGH' ? 'rgba(239,68,68,0.1)' : 'rgba(245,158,11,0.1)', 
                    color: r.priority === 'HIGH' ? '#ef4444' : '#fbbf24' 
                  }}>
                    {r.priority} Priority
                  </span>
                  <span className="take-type-badge" style={{ backgroundColor: 'rgba(255,255,255,0.08)', color: 'white' }}>{r.platform}</span>
                </div>
                
                <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: 'white', margin: '0 0 4px' }}>{r.title}</h3>
                <p style={{ fontSize: '0.8rem', color: 'var(--gray-500)', margin: '0 0 14px' }}>Level: {r.difficulty} | Est. time: {r.estimatedCompletionTime}</p>

                {/* Reasons / Explanation Box */}
                <div style={{ borderLeft: '3px solid #6C3BEB', background: 'rgba(108,59,235,0.03)', padding: '10px 16px', borderRadius: 8, fontSize: '0.875rem', color: 'var(--gray-300)', lineHeight: 1.5 }}>
                  {r.reason}
                </div>
              </div>

              {/* Confidence Circle Match & view */}
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 14, borderLeft: '1px solid rgba(255,255,255,0.06)', paddingLeft: 24 }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--gray-500)', textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 4 }}>Confidence Score</div>
                  <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#6C3BEB', lineHeight: 1 }}>{Math.round(r.confidenceScore)}%</div>
                </div>
                
                <button 
                  className="btn-primary btn-sm btn-full"
                  onClick={() => navigate(`/resources/${r.resourceId}`)}
                  style={{ width: '100%', fontSize: '0.8rem' }}
                >
                  View Details
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RecommendedResourcesPage;
