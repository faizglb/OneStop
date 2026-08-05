import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getBookmarkedColleges, removeBookmark } from '../services/collegeService';
import type { CollegeResponse } from '../types/college';

const BookmarksPage: React.FC = () => {
  const navigate = useNavigate();
  const [bookmarks, setBookmarks] = useState<CollegeResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchBookmarks = async () => {
    try {
      const data = await getBookmarkedColleges();
      setBookmarks(data);
    } catch {
      // error handled
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookmarks();
  }, []);

  const handleRemoveBookmark = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await removeBookmark(id);
      setBookmarks(prev => prev.filter(c => c.id !== id));
    } catch {
      alert('Failed to remove bookmark.');
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <button className="back-btn" onClick={() => navigate('/colleges')} style={{ marginBottom: 12 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            College Finder
          </button>
          <h1 className="history-title">Bookmarked Colleges</h1>
          <p className="history-subtitle">Your shortlisted universities and engineering centers</p>
        </div>
      </div>

      {loading ? (
        <div className="take-page-loading">
          <div className="loading-spinner-large" />
          <p>Loading bookmarks list...</p>
        </div>
      ) : bookmarks.length === 0 ? (
        <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.3)" strokeWidth="1.5">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
          <h3>No Bookmarks Yet</h3>
          <p>Shortlist colleges from the finder page to see them listed here.</p>
          <button className="btn-primary" onClick={() => navigate('/colleges')}>Find Colleges</button>
        </div>
      ) : (
        <div className="assessment-cards-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))' }}>
          {bookmarks.map(c => (
            <div 
              key={c.id} 
              className="assessment-card"
              onClick={() => navigate(`/colleges/${c.id}`)}
              style={{ cursor: 'pointer' }}
            >
              <div className="assessment-card-top" style={{ background: 'linear-gradient(135deg, rgba(255,255,255,0.02) 0%, rgba(255,255,255,0.05) 100%)', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <div>
                  <span className="take-type-badge">{c.collegeType}</span>
                  <h3 style={{ fontSize: '1.1rem', fontWeight: 800, margin: '8px 0 0', color: 'white' }}>{c.collegeName}</h3>
                </div>
                <button 
                  className="back-btn" 
                  onClick={e => handleRemoveBookmark(c.id, e)}
                  style={{ margin: 0, padding: 4 }}
                >
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="#6C3BEB" stroke="#6C3BEB" strokeWidth="2">
                    <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                  </svg>
                </button>
              </div>

              <div className="assessment-card-body">
                <p style={{ minHeight: 48, fontSize: '0.85rem', color: 'var(--gray-400)', WebkitLineClamp: 2, display: '-webkit-box', WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{c.description}</p>
                
                <div className="assessment-meta" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, margin: '14px 0 0' }}>
                  <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                    <strong>State:</strong> {c.state}
                  </div>
                  <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                    <strong>NIRF Rank:</strong> #{c.nirfRanking || 'N/A'}
                  </div>
                  <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                    <strong>Avg Salary:</strong> ₹{(c.averagePackage / 100000).toFixed(1)} LPA
                  </div>
                  <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                    <strong>Annual Fees:</strong> ₹{(c.annualFees / 100000).toFixed(2)}L
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default BookmarksPage;
