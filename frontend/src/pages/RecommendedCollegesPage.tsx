import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getCollegeRecommendations,
  generateCollegeRecommendations,
  bookmarkCollege,
  removeBookmark
} from '../services/collegeService';
import type { CollegeRecommendationResponse } from '../types/college';

const RecommendedCollegesPage: React.FC = () => {
  const navigate = useNavigate();
  const [recommendations, setRecommendations] = useState<CollegeRecommendationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [showFormModal, setShowFormModal] = useState(false);
  const [error, setError] = useState('');

  // Form states
  const [entranceExam, setEntranceExam] = useState('JEE Main');
  const [examRank, setExamRank] = useState(5000);
  const [reservationCategory, setReservationCategory] = useState('General');
  const [maxBudget, setMaxBudget] = useState(250000);
  const [hostelRequired, setHostelRequired] = useState(true);
  const [locationPreference, setLocationPreference] = useState('');
  const [preferredCourse, setPreferredCourse] = useState('');

  const fetchRecommendations = async () => {
    try {
      const data = await getCollegeRecommendations();
      setRecommendations(data);
      if (data.length === 0) {
        setShowFormModal(true);
      }
    } catch {
      setError('Failed to fetch recommendations.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecommendations();
  }, []);

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    setGenerating(true);
    setError('');
    try {
      const data = await generateCollegeRecommendations({
        entranceExam,
        examRank,
        reservationCategory,
        maxBudget,
        hostelRequired,
        locationPreference: locationPreference || undefined,
        preferredCourse: preferredCourse || undefined
      });
      setRecommendations(data);
      setShowFormModal(false);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error generating recommendations. Make sure you complete student profile first.');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <h1 className="history-title">AI College Recommendations</h1>
          <p className="history-subtitle">Best-fit universities generated based on your scores, budget, and career alignments</p>
        </div>
        {recommendations.length > 0 && (
          <button className="btn-primary btn-sm" onClick={() => setShowFormModal(true)}>
            Regenerate AI Match
          </button>
        )}
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
          <p>Running predictive algorithms...</p>
        </div>
      ) : recommendations.length === 0 ? (
        /* Empty State */
        <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="rgba(108,59,235,0.4)" strokeWidth="1.5">
            <path d="M22 10v6M2 10l10-5 10 5-10 5z"/>
            <path d="M6 12v5c0 2 2 3 6 3s6-1 6-3v-5"/>
          </svg>
          <h2>Find Your Ideal University</h2>
          <p style={{ maxWidth: 500, margin: '8px auto 24px' }}>
            Enter your entrance exam rank, category, and tuition fees budget constraints. 
            We'll evaluate colleges cutoffs and match you with top options.
          </p>
          <button className="btn-primary" onClick={() => setShowFormModal(true)}>
            Start Recommendation Survey
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
                  <span className="take-type-badge" style={{ backgroundColor: 'rgba(108,59,235,0.12)', color: '#a855f7' }}>
                    Ranked #{r.nirfRanking || 'N/A'} NIRF
                  </span>
                  <span style={{ fontSize: '0.8rem', color: 'var(--gray-500)' }}>{r.city}, {r.state}</span>
                </div>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: 'white', margin: '0 0 6px' }}>{r.collegeName}</h3>
                
                {/* Course stream alignment */}
                <div style={{ fontSize: '0.88rem', color: '#6C3BEB', fontWeight: 600, marginBottom: 14 }}>
                  ★ Recommended Pathway: {r.recommendedCourse}
                </div>

                {/* Reasons / Explanation Box */}
                <div style={{ borderLeft: '3px solid #22c55e', background: 'rgba(34,197,94,0.03)', padding: '10px 16px', borderRadius: 8, fontSize: '0.875rem', color: 'var(--gray-300)', lineHeight: 1.5 }}>
                  {r.recommendationReason}
                </div>
              </div>

              {/* Match Score circles & actions */}
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 14, borderLeft: '1px solid rgba(255,255,255,0.06)', paddingLeft: 24 }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '0.7rem', color: 'var(--gray-500)', textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 4 }}>Compatibility</div>
                  <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#6C3BEB', lineHeight: 1 }}>{Math.round(r.matchScore)}%</div>
                </div>
                
                <button 
                  className="btn-primary btn-sm btn-full"
                  onClick={() => navigate(`/colleges/${r.collegeId}`)}
                  style={{ width: '100%', fontSize: '0.8rem' }}
                >
                  Explore Campus
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Inputs Form Modal */}
      {showFormModal && (
        <div className="dialog-overlay" onClick={() => setShowFormModal(false)}>
          <form 
            onSubmit={handleGenerate}
            className="dialog-box" 
            style={{ maxWidth: 500, textAlign: 'left' }} 
            onClick={e => e.stopPropagation()}
          >
            <h2 className="dialog-title" style={{ marginBottom: 20 }}>College Finder Questionnaire</h2>
            
            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Entrance Exam</label>
              <select 
                className="form-control" 
                value={entranceExam} 
                onChange={e => setEntranceExam(e.target.value)}
                style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
              >
                <option value="JEE Main">JEE Main</option>
                <option value="JEE Advanced">JEE Advanced</option>
                <option value="BITSAT">BITSAT</option>
                <option value="CAT">CAT</option>
                <option value="GATE">GATE</option>
                <option value="CUET">CUET</option>
              </select>
            </div>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Exam Rank / Percentile Score</label>
              <input 
                type="number" 
                className="form-control" 
                required 
                min={1} 
                value={examRank} 
                onChange={e => setExamRank(Number(e.target.value))}
                style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
              />
            </div>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Reservation Category</label>
              <select 
                className="form-control" 
                value={reservationCategory} 
                onChange={e => setReservationCategory(e.target.value)}
                style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
              >
                <option value="General">General (UR)</option>
                <option value="OBC">OBC</option>
                <option value="SC">SC</option>
                <option value="ST">ST</option>
              </select>
            </div>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                <label className="form-label" style={{ margin: 0 }}>Max Annual Tuition Fee Budget</label>
                <span style={{ fontWeight: 700, color: '#6C3BEB' }}>₹{(maxBudget/100000).toFixed(1)}L</span>
              </div>
              <input 
                type="range" 
                min={20000} 
                max={600000} 
                step={10000}
                value={maxBudget} 
                onChange={e => setMaxBudget(Number(e.target.value))}
                style={{ width: '100%', accentColor: '#6C3BEB' }}
              />
            </div>

            <div className="form-group" style={{ marginBottom: 20 }}>
              <label className="form-label" style={{ display: 'inline-flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
                <input 
                  type="checkbox" 
                  checked={hostelRequired}
                  onChange={e => setHostelRequired(e.target.checked)}
                  style={{ accentColor: '#6C3BEB' }}
                />
                Require On-Campus Hostel Services
              </label>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
              <button type="button" className="btn-ghost" onClick={() => setShowFormModal(false)}>Cancel</button>
              <button type="submit" className="btn-primary" disabled={generating}>
                {generating ? 'Processing...' : 'Match My Colleges'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};

export default RecommendedCollegesPage;
