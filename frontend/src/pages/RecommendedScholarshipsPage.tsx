import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getScholarshipRecommendations,
  generateScholarshipRecommendations,
  bookmarkScholarship,
  removeBookmark
} from '../services/scholarshipService';
import type { ScholarshipRecommendationResponse } from '../types/scholarship';

const RecommendedScholarshipsPage: React.FC = () => {
  const navigate = useNavigate();
  const [recommendations, setRecommendations] = useState<ScholarshipRecommendationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState('');

  const fetchRecommendations = async () => {
    try {
      const data = await getScholarshipRecommendations();
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
      const data = await generateScholarshipRecommendations();
      setRecommendations(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error generating AI scholarship matches.');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <button className="back-btn" onClick={() => navigate('/scholarships')} style={{ marginBottom: 12 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            Scholarship Finder
          </button>
          <h1 className="history-title">AI Scholarship Matches</h1>
          <p className="history-subtitle">Tailored grants matched against your profile category, family income limits, and academic percentage</p>
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
          <p>Running profile diagnostic checkers...</p>
        </div>
      ) : recommendations.length === 0 ? (
        /* Empty State */
        <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="rgba(108,59,235,0.4)" strokeWidth="1.5">
            <path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c0 2 2 3 6 3s6-1 6-3v-5"/>
          </svg>
          <h2>Find Grants Matching Your Profile</h2>
          <p style={{ maxWidth: 500, margin: '8px auto 24px' }}>
            We evaluate active scholarships against your family income, reservation categories, and course stream to map eligible grants.
          </p>
          <button className="btn-primary" onClick={handleGenerate} disabled={generating}>
            {generating ? 'Processing...' : 'Run Eligibility Check'}
          </button>
        </div>
      ) : (
        /* Matches Grid */
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
                    backgroundColor: r.applicationStatus === 'ELIGIBLE' ? 'rgba(34,197,94,0.1)' : 'rgba(245,158,11,0.1)', 
                    color: r.applicationStatus === 'ELIGIBLE' ? '#22c55e' : '#fbbf24' 
                  }}>
                    {r.applicationStatus.replace('_', ' ')}
                  </span>
                  <span className="take-type-badge" style={{ backgroundColor: 'rgba(255,255,255,0.08)', color: 'white' }}>{r.scholarshipType}</span>
                </div>
                
                <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: 'white', margin: '0 0 4px' }}>{r.scholarshipName}</h3>
                <p style={{ fontSize: '0.8rem', color: 'var(--gray-500)', margin: '0 0 14px' }}>Provided by: {r.provider}</p>

                {/* Reasons / Explanation List */}
                <div style={{ borderLeft: '3px solid #22c55e', background: 'rgba(34,197,94,0.03)', padding: '10px 16px', borderRadius: 8, fontSize: '0.875rem', color: 'var(--gray-300)', lineHeight: 1.5 }}>
                  {r.recommendationReason}
                </div>
              </div>

              {/* Match values & action */}
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 14, borderLeft: '1px solid rgba(255,255,255,0.06)', paddingLeft: 24 }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--gray-500)', textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 4 }}>Eligibility Score</div>
                  <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#22c55e', lineHeight: 1 }}>{Math.round(r.eligibilityScore)}%</div>
                </div>
                
                <button 
                  className="btn-primary btn-sm btn-full"
                  onClick={() => navigate(`/scholarships/${r.scholarshipId}`)}
                  style={{ width: '100%', fontSize: '0.8rem' }}
                >
                  Explore Details
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RecommendedScholarshipsPage;
