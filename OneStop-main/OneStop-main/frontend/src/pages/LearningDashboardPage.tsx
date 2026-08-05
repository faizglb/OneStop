import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  searchResources,
  bookmarkResource,
  removeBookmark,
  getLearningProgressHistory,
  updateLearningProgress
} from '../services/learningService';
import type { LearningResourceResponse, LearningProgressResponse } from '../types/learning';

const LearningDashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const [resources, setResources] = useState<LearningResourceResponse[]>([]);
  const [inProgress, setInProgress] = useState<LearningProgressResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Filters
  const [search, setSearch] = useState('');
  const [platform, setPlatform] = useState('');
  const [difficulty, setDifficulty] = useState('');
  const [category, setCategory] = useState('');
  const [isFree, setIsFree] = useState<boolean | null>(null);
  const [certRequired, setCertRequired] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const fetchResources = async () => {
    setLoading(true);
    try {
      const data = await searchResources({
        search: search || undefined,
        platform: platform || undefined,
        difficulty: difficulty || undefined,
        category: category || undefined,
        isFree: isFree !== null ? isFree : undefined,
        certificateRequired: certRequired,
        page,
        size: 6
      });
      setResources(data.content);
      setTotalPages(data.totalPages);
    } catch {
      setError('Failed to fetch learning resources.');
    } finally {
      setLoading(false);
    }
  };

  const fetchProgress = async () => {
    try {
      const history = await getLearningProgressHistory();
      setInProgress(history.filter(h => h.status !== 'COMPLETED'));
    } catch {
      // ignore
    }
  };

  useEffect(() => {
    fetchResources();
  }, [search, platform, difficulty, category, isFree, certRequired, page]);

  useEffect(() => {
    fetchProgress();
  }, []);

  const handleBookmarkToggle = async (res: LearningResourceResponse) => {
    try {
      if (res.isBookmarked) {
        await removeBookmark(res.id);
        setResources(prev => prev.map(r => r.id === res.id ? { ...r, isBookmarked: false } : r));
      } else {
        await bookmarkResource(res.id);
        setResources(prev => prev.map(r => r.id === res.id ? { ...r, isBookmarked: true } : r));
      }
    } catch {
      alert('Error updating bookmark.');
    }
  };

  const handleStartLearning = async (res: LearningResourceResponse) => {
    try {
      await updateLearningProgress({
        resourceId: res.id,
        status: 'STARTED',
        progressPercentage: 0
      });
      setResources(prev => prev.map(r => r.id === res.id ? { ...r, progressStatus: 'STARTED', progressPercentage: 0 } : r));
      fetchProgress();
      alert('Added to Continue Learning tracker! View details to update progress.');
    } catch {
      alert('Error updating progress.');
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <h1 className="history-title">Learning Hub</h1>
          <p className="history-subtitle">Explore curated video courses, certifications, and books to bridge your career skill gaps</p>
        </div>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <button className="btn-ghost btn-sm" onClick={() => navigate('/learning-recommendations')}>
            AI Skill Recommendations
          </button>
          <button className="btn-ghost btn-sm" onClick={() => navigate('/resources/bookmarks')}>
            Saved Bookmarks
          </button>
        </div>
      </div>

      {/* Continue Learning Widget */}
      {inProgress.length > 0 && (
        <div className="info-instructions-card" style={{ background: 'rgba(108,59,235,0.03)', padding: 20, borderRadius: 16, marginBottom: 24 }}>
          <h3 style={{ fontSize: '0.95rem', fontWeight: 800, color: 'white', margin: '0 0 14px' }}>Continue Learning</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 16 }}>
            {inProgress.map(p => (
              <div 
                key={p.id} 
                onClick={() => navigate(`/resources/${p.resourceId}`)}
                style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.05)', padding: 14, borderRadius: 12, cursor: 'pointer' }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.78rem', color: 'var(--gray-500)', marginBottom: 6 }}>
                  <span>{p.platform}</span>
                  <span>{p.progressPercentage}% Complete</span>
                </div>
                <h4 style={{ fontSize: '0.88rem', fontWeight: 700, color: 'white', margin: '0 0 10px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{p.title}</h4>
                <div className="progress-bar-container" style={{ height: 6, background: 'rgba(255,255,255,0.05)' }}>
                  <div className="progress-bar-fill" style={{ width: `${p.progressPercentage}%`, background: '#6C3BEB' }} />
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Main Layout */}
      <div className="info-page-layout" style={{ gridTemplateColumns: '300px 1fr' }}>
        {/* Filters Sidebar */}
        <div className="info-summary-card" style={{ padding: 20 }}>
          <h3 style={{ fontSize: '1rem', color: 'white', margin: '0 0 16px' }}>Filter Options</h3>
          
          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Keywords</label>
            <input 
              type="text" 
              className="form-control" 
              placeholder="Title or skill..." 
              value={search} 
              onChange={e => { setSearch(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            />
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Platform</label>
            <select 
              className="form-control"
              value={platform}
              onChange={e => { setPlatform(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">All Platforms</option>
              <option value="edX">edX</option>
              <option value="Coursera">Coursera</option>
              <option value="Udemy">Udemy</option>
              <option value="NPTEL">NPTEL</option>
              <option value="books">Books</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Difficulty</label>
            <select 
              className="form-control"
              value={difficulty}
              onChange={e => { setDifficulty(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">All Levels</option>
              <option value="BEGINNER">Beginner</option>
              <option value="INTERMEDIATE">Intermediate</option>
              <option value="ADVANCED">Advanced</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Category</label>
            <select 
              className="form-control"
              value={category}
              onChange={e => { setCategory(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">All Categories</option>
              <option value="Programming Languages">Programming Languages</option>
              <option value="Frameworks">Frameworks</option>
              <option value="DSA">Data Structures & Algorithms</option>
              <option value="Soft Skills">Soft Skills</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Pricing</label>
            <select 
              className="form-control"
              value={isFree === null ? '' : String(isFree)}
              onChange={e => {
                const val = e.target.value;
                setIsFree(val === '' ? null : val === 'true');
                setPage(0);
              }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">All Pricing</option>
              <option value="true">Free Only</option>
              <option value="false">Paid Only</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ display: 'inline-flex', alignItems: 'center', gap: 8, fontSize: '0.85rem', color: 'var(--gray-300)', cursor: 'pointer' }}>
              <input 
                type="checkbox" 
                checked={certRequired}
                onChange={e => { setCertRequired(e.target.checked); setPage(0); }}
                style={{ accentColor: '#6C3BEB' }}
              />
              Certificate Available
            </label>
          </div>
        </div>

        {/* Resources Grid */}
        <div>
          {loading ? (
            <div className="take-page-loading" style={{ height: '50vh' }}>
              <div className="loading-spinner-large" />
              <p>Scanning educational libraries...</p>
            </div>
          ) : resources.length === 0 ? (
            <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.3)" strokeWidth="1.5">
                <circle cx="12" cy="12" r="10"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
              <h3>No Resources Found</h3>
              <p>Adjust filters or try typing other keywords.</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
              <div className="assessment-cards-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', margin: 0 }}>
                {resources.map(r => (
                  <div 
                    key={r.id} 
                    className="assessment-card" 
                    onClick={() => navigate(`/resources/${r.id}`)}
                    style={{ cursor: 'pointer' }}
                  >
                    <div className="assessment-card-top" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                      <div>
                        <span className="take-type-badge">{r.platform}</span>
                        <h3 style={{ fontSize: '1.05rem', fontWeight: 800, margin: '8px 0 0', color: 'white' }}>{r.title}</h3>
                        <p style={{ fontSize: '0.78rem', color: 'var(--gray-500)', margin: '2px 0 0' }}>By {r.author || r.provider}</p>
                      </div>
                      <button 
                        className="back-btn" 
                        onClick={e => { e.stopPropagation(); handleBookmarkToggle(r); }}
                        style={{ margin: 0, padding: 4 }}
                      >
                        <svg width="18" height="18" viewBox="0 0 24 24" fill={r.isBookmarked ? '#6C3BEB' : 'none'} stroke={r.isBookmarked ? '#6C3BEB' : 'currentColor'} strokeWidth="2">
                          <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                        </svg>
                      </button>
                    </div>

                    <div className="assessment-card-body">
                      <p style={{ minHeight: 48, fontSize: '0.85rem', color: 'var(--gray-400)', WebkitLineClamp: 2, display: '-webkit-box', WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{r.description}</p>
                      
                      <div className="assessment-meta" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, margin: '14px 0 16px' }}>
                        <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                          <strong>Level:</strong> {r.difficulty}
                        </div>
                        <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                          <strong>Duration:</strong> {r.duration}
                        </div>
                        <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                          <strong>Pricing:</strong> {r.isFree ? 'Free' : `₹${r.price}`}
                        </div>
                        <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                          <strong>Rating:</strong> ⭐ {r.rating.toFixed(1)}
                        </div>
                      </div>

                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid rgba(255,255,255,0.06)', paddingTop: 12 }}>
                        {r.progressStatus ? (
                          <span style={{ fontSize: '0.8rem', color: '#6C3BEB', fontWeight: 600 }}>
                            {r.progressStatus === 'COMPLETED' ? '✓ Completed' : `Learning: ${r.progressPercentage}%`}
                          </span>
                        ) : (
                          <button 
                            className="btn-ghost btn-xs" 
                            onClick={e => { e.stopPropagation(); handleStartLearning(r); }}
                            style={{ padding: '4px 8px', fontSize: '0.75rem' }}
                          >
                            Start Learning
                          </button>
                        )}
                        <span style={{ fontSize: '0.8rem', color: '#6C3BEB', fontWeight: 600 }}>Explore →</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 16 }}>
                  <button 
                    className="btn-ghost btn-sm" 
                    onClick={() => setPage(prev => Math.max(0, prev - 1))}
                    disabled={page === 0}
                  >
                    Previous
                  </button>
                  <span style={{ display: 'inline-flex', alignItems: 'center', color: 'var(--gray-400)', fontSize: '0.9rem' }}>
                    Page {page + 1} of {totalPages}
                  </span>
                  <button 
                    className="btn-ghost btn-sm" 
                    onClick={() => setPage(prev => Math.min(totalPages - 1, prev + 1))}
                    disabled={page === totalPages - 1}
                  >
                    Next
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default LearningDashboardPage;
