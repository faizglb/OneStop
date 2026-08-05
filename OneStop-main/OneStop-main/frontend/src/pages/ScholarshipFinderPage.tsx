import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  searchScholarships,
  bookmarkScholarship,
  removeBookmark,
  updateApplicationStatus
} from '../services/scholarshipService';
import type { ScholarshipResponse } from '../types/scholarship';

const ScholarshipFinderPage: React.FC = () => {
  const navigate = useNavigate();
  const [scholarships, setScholarships] = useState<ScholarshipResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Filters
  const [search, setSearch] = useState('');
  const [state, setState] = useState('');
  const [type, setType] = useState('');
  const [educationLevel, setEducationLevel] = useState('');
  const [category, setCategory] = useState('');
  const [minAmount, setMinAmount] = useState(0);
  const [maxIncome, setMaxIncome] = useState(800000);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const fetchScholarships = async () => {
    setLoading(true);
    try {
      const data = await searchScholarships({
        search: search || undefined,
        state: state || undefined,
        type: type || undefined,
        educationLevel: educationLevel || undefined,
        category: category || undefined,
        minAmount: minAmount || undefined,
        maxIncome: maxIncome || undefined,
        page,
        size: 6
      });
      setScholarships(data.content);
      setTotalPages(data.totalPages);
    } catch {
      setError('Failed to fetch scholarships list.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchScholarships();
  }, [search, state, type, educationLevel, category, minAmount, maxIncome, page]);

  const handleBookmarkToggle = async (schol: ScholarshipResponse) => {
    try {
      if (schol.isBookmarked) {
        await removeBookmark(schol.id);
        setScholarships(prev => prev.map(s => s.id === schol.id ? { ...s, isBookmarked: false } : s));
      } else {
        await bookmarkScholarship(schol.id);
        setScholarships(prev => prev.map(s => s.id === schol.id ? { ...s, isBookmarked: true } : s));
      }
    } catch {
      alert('Error updating bookmark status.');
    }
  };

  const handleApplyState = async (schol: ScholarshipResponse) => {
    try {
      await updateApplicationStatus({
        scholarshipId: schol.id,
        status: 'APPLIED',
        notes: 'Applied through official website'
      });
      setScholarships(prev => prev.map(s => s.id === schol.id ? { ...s, applicationStatus: 'APPLIED' } : s));
      alert('Application status updated to APPLIED!');
    } catch {
      alert('Failed to update status.');
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <h1 className="history-title">Scholarship Finder</h1>
          <p className="history-subtitle">Search, filter, and apply for government and private scholarships matching your profile</p>
        </div>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          <button className="btn-ghost btn-sm" onClick={() => navigate('/scholarship-recommendations')}>
            AI Match Recommendations
          </button>
          <button className="btn-ghost btn-sm" onClick={() => navigate('/scholarships/bookmarks')}>
            Shortlist Bookmarks
          </button>
          <button className="btn-ghost btn-sm" onClick={() => navigate('/scholarships/applications')}>
            Tracker Board
          </button>
          <button className="btn-ghost btn-sm" onClick={() => navigate('/scholarships/calendar')}>
            Deadline Calendar
          </button>
        </div>
      </div>

      <div className="info-page-layout" style={{ gridTemplateColumns: '300px 1fr' }}>
        {/* Filters Sidebar */}
        <div className="info-summary-card" style={{ padding: 20 }}>
          <h3 style={{ fontSize: '1rem', color: 'white', margin: '0 0 16px' }}>Filter Options</h3>
          
          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Keywords</label>
            <input 
              type="text" 
              className="form-control" 
              placeholder="Name or provider..." 
              value={search} 
              onChange={e => { setSearch(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            />
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Scholarship Type</label>
            <select 
              className="form-control"
              value={type}
              onChange={e => { setType(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">All Types</option>
              <option value="Government">Government</option>
              <option value="Private">Private</option>
              <option value="Corporate">Corporate</option>
              <option value="University">University</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Category Eligibility</label>
            <select 
              className="form-control"
              value={category}
              onChange={e => { setCategory(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">Open to All</option>
              <option value="General">General (UR)</option>
              <option value="OBC">OBC</option>
              <option value="SC">SC</option>
              <option value="ST">ST</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Education Level</label>
            <select 
              className="form-control"
              value={educationLevel}
              onChange={e => { setEducationLevel(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">All Levels</option>
              <option value="UNDERGRADUATE">Undergraduate</option>
              <option value="POSTGRADUATE">Postgraduate</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
              <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)', margin: 0 }}>Max Family Income</label>
              <span style={{ fontSize: '0.8rem', color: 'white', fontWeight: 600 }}>₹{(maxIncome/100000).toFixed(1)}L</span>
            </div>
            <input 
              type="range" 
              min={100000} 
              max={1000000} 
              step={50000}
              value={maxIncome} 
              onChange={e => { setMaxIncome(Number(e.target.value)); setPage(0); }}
              style={{ width: '100%', accentColor: '#6C3BEB' }}
            />
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
              <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)', margin: 0 }}>Min Grant Amount</label>
              <span style={{ fontSize: '0.8rem', color: 'white', fontWeight: 600 }}>₹{(minAmount/1000).toFixed(0)}k</span>
            </div>
            <input 
              type="range" 
              min={0} 
              max={200000} 
              step={5000}
              value={minAmount} 
              onChange={e => { setMinAmount(Number(e.target.value)); setPage(0); }}
              style={{ width: '100%', accentColor: '#6C3BEB' }}
            />
          </div>
        </div>

        {/* Scholarships List */}
        <div>
          {loading ? (
            <div className="take-page-loading" style={{ height: '50vh' }}>
              <div className="loading-spinner-large" />
              <p>Scanning scholarships database...</p>
            </div>
          ) : scholarships.length === 0 ? (
            <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.3)" strokeWidth="1.5">
                <path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c0 2 2 3 6 3s6-1 6-3v-5"/>
              </svg>
              <h3>No Scholarships Found</h3>
              <p>Adjust your search query or filter sliders.</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
              <div className="assessment-cards-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', margin: 0 }}>
                {scholarships.map(s => {
                  const daysLeft = Math.ceil((new Date(s.lastDate).getTime() - new Date().getTime()) / (1000 * 3600 * 24));
                  return (
                    <div 
                      key={s.id} 
                      className="assessment-card" 
                      onClick={() => navigate(`/scholarships/${s.id}`)}
                      style={{ cursor: 'pointer' }}
                    >
                      <div className="assessment-card-top" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                          <span className="take-type-badge" style={{ backgroundColor: 'rgba(108,59,235,0.1)', color: '#a855f7' }}>
                            {s.scholarshipType}
                          </span>
                          <h3 style={{ fontSize: '1.05rem', fontWeight: 800, margin: '8px 0 0', color: 'white' }}>{s.scholarshipName}</h3>
                          <p style={{ fontSize: '0.78rem', color: 'var(--gray-500)', margin: '2px 0 0' }}>By {s.provider}</p>
                        </div>
                        <button 
                          className="back-btn" 
                          onClick={e => { e.stopPropagation(); handleBookmarkToggle(s); }}
                          style={{ margin: 0, padding: 4 }}
                        >
                          <svg width="18" height="18" viewBox="0 0 24 24" fill={s.isBookmarked ? '#6C3BEB' : 'none'} stroke={s.isBookmarked ? '#6C3BEB' : 'currentColor'} strokeWidth="2">
                            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                          </svg>
                        </button>
                      </div>

                      <div className="assessment-card-body">
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', margin: '8px 0 12px' }}>
                          <span style={{ fontSize: '1.4rem', fontWeight: 800, color: '#22c55e' }}>₹{Number(s.amount).toLocaleString('en-IN')}</span>
                          <span style={{ fontSize: '0.78rem', color: daysLeft <= 15 ? '#ef4444' : '#fbbf24', fontWeight: 600 }}>
                            {daysLeft > 0 ? `${daysLeft} days left` : 'Expired'}
                          </span>
                        </div>

                        <div className="assessment-meta" style={{ display: 'grid', gridTemplateColumns: '1fr', gap: 6, margin: '0 0 16px', fontSize: '0.8rem' }}>
                          <div><strong>Education Level:</strong> {s.educationLevel}</div>
                          {s.incomeLimit && <div><strong>Income Cap:</strong> Under ₹{(s.incomeLimit/100000).toFixed(1)}L/yr</div>}
                          {s.minimumMarks && <div><strong>Min Marks:</strong> {s.minimumMarks}%</div>}
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid rgba(255,255,255,0.06)', paddingTop: 12 }}>
                          {s.applicationStatus === 'APPLIED' ? (
                            <span style={{ fontSize: '0.8rem', color: '#22c55e', fontWeight: 600 }}>✓ Applied</span>
                          ) : (
                            <button 
                              className="btn-ghost btn-xs" 
                              onClick={e => { e.stopPropagation(); handleApplyState(s); }}
                              style={{ padding: '4px 8px', fontSize: '0.75rem' }}
                            >
                              Mark Applied
                            </button>
                          )}
                          <span style={{ fontSize: '0.8rem', color: '#6C3BEB', fontWeight: 600 }}>Details →</span>
                        </div>
                      </div>
                    </div>
                  );
                })}
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

export default ScholarshipFinderPage;
