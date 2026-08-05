import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getRecommendations,
  generateRecommendations,
  toggleBookmark,
  clearRecommendations
} from '../services/recommendationService';
import type { RecommendationResponse } from '../types/recommendation';
import ScoreCircle from '../components/assessment/ScoreCircle';

const RecommendationDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [recommendations, setRecommendations] = useState<RecommendationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [domainFilter, setDomainFilter] = useState('ALL');
  const [showBookmarkedOnly, setShowBookmarkedOnly] = useState(false);
  
  // Comparison state
  const [comparedIds, setComparedIds] = useState<string[]>([]);
  const [showComparisonModal, setShowComparisonModal] = useState(false);

  const fetchRecs = async () => {
    try {
      const data = await getRecommendations();
      setRecommendations(data);
    } catch (e: any) {
      setError(e.response?.data?.message || 'Failed to fetch recommendations.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecs();
  }, []);

  const handleGenerate = async () => {
    setGenerating(true);
    setError('');
    try {
      const data = await generateRecommendations();
      setRecommendations(data);
    } catch (e: any) {
      setError(e.response?.data?.message || 'Assessment scores or student profile incomplete. Make sure you complete all 3 assessments first.');
    } finally {
      setGenerating(false);
    }
  };

  const handleClear = async () => {
    if (!window.confirm('Are you sure you want to clear your current career recommendations?')) return;
    setLoading(true);
    try {
      await clearRecommendations();
      setRecommendations([]);
      setComparedIds([]);
    } catch {
      setError('Failed to clear recommendations.');
    } finally {
      setLoading(false);
    }
  };

  const handleBookmark = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      const updated = await toggleBookmark(id);
      setRecommendations(prev => prev.map(r => r.id === id ? updated : r));
    } catch {
      setError('Failed to update bookmark.');
    }
  };

  const handleCompareCheckbox = (id: string, e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.checked) {
      if (comparedIds.length >= 3) {
        alert('You can compare a maximum of 3 career roles at a time.');
        e.target.checked = false;
        return;
      }
      setComparedIds(prev => [...prev, id]);
    } else {
      setComparedIds(prev => prev.filter(item => item !== id));
    }
  };

  // Filter recommendations
  const filtered = recommendations.filter(r => {
    const matchesSearch = r.careerName.toLowerCase().includes(search.toLowerCase()) ||
                          r.careerDescription.toLowerCase().includes(search.toLowerCase());
    const matchesDomain = domainFilter === 'ALL' || r.requiredDegree.toLowerCase().includes(domainFilter.toLowerCase()) || r.careerDescription.toLowerCase().includes(domainFilter.toLowerCase());
    const matchesBookmark = !showBookmarkedOnly || r.isBookmarked;
    return matchesSearch && matchesDomain && matchesBookmark;
  });

  const uniqueDomains = Array.from(new Set(recommendations.map(r => r.requiredDegree.split(' ')[0])));

  return (
    <div className="page-container">
      {/* Header Banner */}
      <div className="history-header">
        <div>
          <h1 className="history-title">AI Career Recommendations</h1>
          <p className="history-subtitle">Personalized career paths mapped to your assessments and skills</p>
        </div>
        {recommendations.length > 0 && (
          <div style={{ display: 'flex', gap: 12 }}>
            <button className="btn-ghost btn-sm" onClick={handleClear}>Clear Recommendations</button>
            <button className="btn-primary btn-sm" onClick={handleGenerate} disabled={generating}>
              {generating ? 'Regenerating...' : 'Regenerate'}
            </button>
          </div>
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
          <p>Analyzing your profiles...</p>
        </div>
      ) : recommendations.length === 0 ? (
        /* Empty State */
        <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="rgba(108,59,235,0.4)" strokeWidth="1.5">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
            <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
            <line x1="12" y1="22.08" x2="12" y2="12"/>
          </svg>
          <h2>Unlock Your AI-Powered Career Roadmap</h2>
          <p style={{ maxWidth: 500, margin: '8px auto 24px' }}>
            We'll evaluate your Aptitude tests, OCEAN personality profiles, RIASEC interest metrics, 
            academic CGPA, and current skills to find the absolute best professional roles for you.
          </p>
          <button className="btn-primary" onClick={handleGenerate} disabled={generating}>
            {generating ? (
              <>
                <span className="spinner" /> Analyzing profiles & generating paths...
              </>
            ) : (
              'Generate Career Recommendations'
            )}
          </button>
        </div>
      ) : (
        /* Full Dashboard Dashboard */
        <div className="recommendations-layout" style={{ display: 'grid', gridTemplateColumns: '1fr', gap: 24 }}>
          
          {/* Controls Bar */}
          <div className="take-topbar" style={{ display: 'flex', gap: 16, alignItems: 'center', justifyContent: 'space-between', padding: '16px 20px', background: 'rgba(255,255,255,0.03)', borderRadius: 16 }}>
            <div style={{ display: 'flex', gap: 12, flex: 1, flexWrap: 'wrap' }}>
              <input
                type="text"
                className="form-control"
                placeholder="Search careers..."
                value={search}
                onChange={e => setSearch(e.target.value)}
                style={{ maxWidth: 260, background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 12px', borderRadius: 8 }}
              />
              <select
                className="form-control"
                value={domainFilter}
                onChange={e => setDomainFilter(e.target.value)}
                style={{ maxWidth: 180, background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 12px', borderRadius: 8 }}
              >
                <option value="ALL">All Domains</option>
                <option value="Bachelor">Engineering/CS</option>
                <option value="Commerce">Finance/Commerce</option>
                <option value="Design">Design/HCI</option>
              </select>
              <button 
                className={`filter-tab ${showBookmarkedOnly ? 'active' : ''}`}
                onClick={() => setShowBookmarkedOnly(!showBookmarkedOnly)}
              >
                Bookmarks
              </button>
            </div>
            
            {comparedIds.length > 0 && (
              <button 
                className="btn-primary btn-sm"
                onClick={() => setShowComparisonModal(true)}
              >
                Compare Selected ({comparedIds.length})
              </button>
            )}
          </div>

          {/* Cards List */}
          <div className="assessment-cards-grid">
            {filtered.map(r => (
              <div 
                key={r.id} 
                className="assessment-card" 
                style={{ cursor: 'pointer' }}
                onClick={() => navigate(`/recommendations/career/${r.id}`)}
              >
                {/* Visual Header */}
                <div 
                  className="assessment-card-top" 
                  style={{ 
                    background: 'linear-gradient(135deg, rgba(108,59,235,0.15) 0%, rgba(168,85,247,0.15) 100%)',
                    borderBottom: '1px solid rgba(255,255,255,0.06)',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'flex-start'
                  }}
                >
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                    <span className="take-type-badge">{r.jobDemand} Demand</span>
                    <h2 style={{ fontSize: '1.25rem', fontWeight: 800, margin: 0, color: 'white' }}>{r.careerName}</h2>
                  </div>
                  <button 
                    className="back-btn" 
                    onClick={(e) => handleBookmark(r.id, e)}
                    style={{ margin: 0, padding: 4 }}
                  >
                    <svg width="20" height="20" viewBox="0 0 24 24" fill={r.isBookmarked ? '#6C3BEB' : 'none'} stroke={r.isBookmarked ? '#6C3BEB' : 'currentColor'} strokeWidth="2">
                      <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                    </svg>
                  </button>
                </div>

                {/* Body details */}
                <div className="assessment-card-body">
                  <p style={{ minHeight: 64, overflow: 'hidden', textOverflow: 'ellipsis', display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical' }}>
                    {r.careerDescription}
                  </p>
                  
                  {/* Scores Progress Row */}
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 10, margin: '16px 0 20px' }}>
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', color: 'var(--gray-400)', marginBottom: 4 }}>
                        <span>Match Compatibility</span>
                        <span style={{ fontWeight: 700, color: '#6C3BEB' }}>{Math.round(r.matchScore)}%</span>
                      </div>
                      <div className="take-progress-bar" style={{ width: '100%' }}>
                        <div className="take-progress-fill" style={{ width: `${r.matchScore}%` }} />
                      </div>
                    </div>
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', color: 'var(--gray-400)', marginBottom: 4 }}>
                        <span>AI Confidence Index</span>
                        <span style={{ fontWeight: 700, color: '#22c55e' }}>{Math.round(r.confidenceScore)}%</span>
                      </div>
                      <div className="take-progress-bar" style={{ width: '100%', background: 'rgba(255,255,255,0.1)' }}>
                        <div className="take-progress-fill" style={{ width: `${r.confidenceScore}%`, backgroundColor: '#22c55e' }} />
                      </div>
                    </div>
                  </div>

                  <div className="assessment-meta" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 20 }}>
                    <div className="meta-item">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                      </svg>
                      {r.expectedSalary}
                    </div>
                    <div className="meta-item">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/>
                      </svg>
                      {r.industryGrowth.split(' ')[0]} Growth
                    </div>
                  </div>

                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid rgba(255,255,255,0.06)', paddingTop: 16 }}>
                    <label style={{ display: 'inline-flex', alignItems: 'center', gap: 6, fontSize: '0.8rem', color: 'var(--gray-400)', cursor: 'pointer' }} onClick={e => e.stopPropagation()}>
                      <input 
                        type="checkbox" 
                        checked={comparedIds.includes(r.id)}
                        onChange={(e) => handleCompareCheckbox(r.id, e)}
                        style={{ accentColor: '#6C3BEB' }}
                      />
                      Compare
                    </label>
                    <span style={{ fontSize: '0.85rem', color: '#6C3BEB', fontWeight: 600 }}>Explore Roadmap →</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Comparison Modal */}
      {showComparisonModal && (
        <div className="dialog-overlay" onClick={() => setShowComparisonModal(false)}>
          <div className="dialog-box" style={{ maxWidth: 840, textAlign: 'left' }} onClick={e => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
              <h2 className="dialog-title" style={{ margin: 0 }}>Career Comparison Matrix</h2>
              <button className="back-btn" onClick={() => setShowComparisonModal(false)} style={{ margin: 0 }}>✕ Close</button>
            </div>
            
            <div className="history-table-wrapper" style={{ maxHeight: 500, overflowY: 'auto' }}>
              <table className="history-table">
                <thead>
                  <tr>
                    <th>Metric</th>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return <th key={id}>{career?.careerName}</th>;
                    })}
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Compatibility Score</td>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return <td key={id} style={{ color: '#6C3BEB', fontWeight: 700 }}>{Math.round(career?.matchScore || 0)}%</td>;
                    })}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>AI Confidence Score</td>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return <td key={id} style={{ color: '#22c55e', fontWeight: 700 }}>{Math.round(career?.confidenceScore || 0)}%</td>;
                    })}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Salary Range</td>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return <td key={id}>{career?.expectedSalary}</td>;
                    })}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Growth Trend</td>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return <td key={id}>{career?.industryGrowth}</td>;
                    })}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Estimated Learning Time</td>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return <td key={id}>{career?.estimatedLearningTime}</td>;
                    })}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Prerequisite Degree</td>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return <td key={id} style={{ fontSize: '0.8rem' }}>{career?.requiredDegree}</td>;
                    })}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Core Recommended Certifications</td>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return (
                        <td key={id} style={{ fontSize: '0.8rem' }}>
                          <ul style={{ paddingLeft: 16, margin: 0 }}>
                            {career?.recommendedCertifications.slice(0, 2).map(c => (
                              <li key={c.id}>{c.certificationName}</li>
                            ))}
                          </ul>
                        </td>
                      );
                    })}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Suggested Project Ideas</td>
                    {comparedIds.map(id => {
                      const career = recommendations.find(r => r.id === id);
                      return (
                        <td key={id} style={{ fontSize: '0.8rem' }}>
                          <ul style={{ paddingLeft: 16, margin: 0 }}>
                            {career?.suggestedProjects.slice(0, 2).map(p => (
                              <li key={p.id}>{p.projectTitle}</li>
                            ))}
                          </ul>
                        </td>
                      );
                    })}
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div style={{ marginTop: 20, display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
              <button className="btn-ghost" onClick={() => setComparedIds([])}>Reset Selection</button>
              <button className="btn-primary" onClick={() => setShowComparisonModal(false)}>Done</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RecommendationDashboard;
