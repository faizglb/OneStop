import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  searchColleges,
  bookmarkCollege,
  removeBookmark,
  compareColleges
} from '../services/collegeService';
import type { CollegeResponse, CompareItem } from '../types/college';

const CollegeFinderPage: React.FC = () => {
  const navigate = useNavigate();
  const [colleges, setColleges] = useState<CollegeResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Search parameters
  const [search, setSearch] = useState('');
  const [state, setState] = useState('');
  const [type, setType] = useState('');
  const [maxFees, setMaxFees] = useState(600000);
  const [minPlacement, setMinPlacement] = useState(0);
  const [hostelRequired, setHostelRequired] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // Comparison State
  const [compareIds, setCompareIds] = useState<string[]>([]);
  const [compareData, setCompareData] = useState<CompareItem[]>([]);
  const [showCompareModal, setShowCompareModal] = useState(false);

  const fetchColleges = async () => {
    setLoading(true);
    try {
      const data = await searchColleges({
        search: search || undefined,
        state: state || undefined,
        type: type || undefined,
        maxFees: maxFees || undefined,
        minPlacementPct: minPlacement || undefined,
        hostelRequired,
        page,
        size: 6
      });
      setColleges(data.content);
      setTotalPages(data.totalPages);
    } catch {
      setError('Failed to fetch colleges list.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchColleges();
  }, [search, state, type, maxFees, minPlacement, hostelRequired, page]);

  const handleBookmarkToggle = async (college: CollegeResponse) => {
    try {
      if (college.isBookmarked) {
        await removeBookmark(college.id);
        setColleges(prev => prev.map(c => c.id === college.id ? { ...c, isBookmarked: false } : c));
      } else {
        await bookmarkCollege(college.id);
        setColleges(prev => prev.map(c => c.id === college.id ? { ...c, isBookmarked: true } : c));
      }
    } catch {
      alert('Error updating bookmark.');
    }
  };

  const handleCompareSelect = (id: string, checked: boolean) => {
    if (checked) {
      if (compareIds.length >= 3) {
        alert('You can compare a maximum of 3 colleges at a time.');
        return;
      }
      setCompareIds(prev => [...prev, id]);
    } else {
      setCompareIds(prev => prev.filter(i => i !== id));
    }
  };

  const triggerComparison = async () => {
    if (compareIds.length === 0) return;
    try {
      const res = await compareColleges(compareIds);
      setCompareData(res.colleges);
      setShowCompareModal(true);
    } catch {
      alert('Failed to load comparison data.');
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <h1 className="history-title">College Finder</h1>
          <p className="history-subtitle">Explore and filter top universities based on cutoffs, fees, and placements</p>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <button className="btn-ghost btn-sm" onClick={() => navigate('/college-recommendations')}>
            AI Recommendations
          </button>
          <button className="btn-ghost btn-sm" onClick={() => navigate('/bookmarks')}>
            Bookmarked Colleges
          </button>
          {compareIds.length > 0 && (
            <button className="btn-primary btn-sm" onClick={triggerComparison}>
              Compare Colleges ({compareIds.length})
            </button>
          )}
        </div>
      </div>

      <div className="info-page-layout" style={{ gridTemplateColumns: '300px 1fr' }}>
        {/* Filters Sidebar */}
        <div className="info-summary-card" style={{ padding: 20 }}>
          <h3 style={{ fontSize: '1rem', color: 'white', margin: '0 0 16px' }}>Filter Options</h3>
          
          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Search Keyword</label>
            <input 
              type="text" 
              className="form-control" 
              placeholder="E.g. IIT, Mumbai..." 
              value={search} 
              onChange={e => { setSearch(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            />
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>State</label>
            <select 
              className="form-control"
              value={state}
              onChange={e => { setState(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">All States</option>
              <option value="Delhi">Delhi</option>
              <option value="Rajasthan">Rajasthan</option>
              <option value="Tamil Nadu">Tamil Nadu</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>College Type</label>
            <select 
              className="form-control"
              value={type}
              onChange={e => { setType(e.target.value); setPage(0); }}
              style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
            >
              <option value="">All Types</option>
              <option value="Government">Government</option>
              <option value="Private">Private</option>
            </select>
          </div>

          <div className="form-group" style={{ marginBottom: 16 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
              <label className="form-label" style={{ fontSize: '0.78rem', color: 'var(--gray-500)', margin: 0 }}>Max Annual Fees</label>
              <span style={{ fontSize: '0.8rem', color: 'white', fontWeight: 600 }}>₹{(maxFees/100000).toFixed(1)}L</span>
            </div>
            <input 
              type="range" 
              min={10000} 
              max={600000} 
              step={10000}
              value={maxFees} 
              onChange={e => { setMaxFees(Number(e.target.value)); setPage(0); }}
              style={{ width: '100%', accentColor: '#6C3BEB' }}
            />
          </div>

          <div className="form-group" style={{ marginBottom: 20 }}>
            <label className="form-label" style={{ display: 'inline-flex', alignItems: 'center', gap: 8, fontSize: '0.85rem', color: 'var(--gray-300)', cursor: 'pointer' }}>
              <input 
                type="checkbox" 
                checked={hostelRequired}
                onChange={e => { setHostelRequired(e.target.checked); setPage(0); }}
                style={{ accentColor: '#6C3BEB' }}
              />
              Hostel Facility Required
            </label>
          </div>
        </div>

        {/* Colleges List */}
        <div>
          {loading ? (
            <div className="take-page-loading" style={{ height: '50vh' }}>
              <div className="loading-spinner-large" />
              <p>Scanning universities database...</p>
            </div>
          ) : colleges.length === 0 ? (
            <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="rgba(255,255,255,0.3)" strokeWidth="1.5">
                <circle cx="12" cy="12" r="10"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
              <h3>No Colleges Found</h3>
              <p>Try resetting filters or adjusting search keywords.</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
              <div className="assessment-cards-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', margin: 0 }}>
                {colleges.map(c => (
                  <div 
                    key={c.id} 
                    className="assessment-card" 
                    onClick={() => navigate(`/colleges/${c.id}`)}
                    style={{ cursor: 'pointer' }}
                  >
                    <div className="assessment-card-top" style={{ background: 'linear-gradient(135deg, rgba(255,255,255,0.02) 0%, rgba(255,255,255,0.05) 100%)', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                      <div>
                        <span className="take-type-badge" style={{ backgroundColor: c.collegeType === 'Government' ? 'rgba(34,197,94,0.1)' : 'rgba(245,158,11,0.1)', color: c.collegeType === 'Government' ? '#22c55e' : '#fbbf24' }}>
                          {c.collegeType}
                        </span>
                        <h3 style={{ fontSize: '1.1rem', fontWeight: 800, margin: '8px 0 0', color: 'white' }}>{c.collegeName}</h3>
                      </div>
                      <button 
                        className="back-btn" 
                        onClick={e => { e.stopPropagation(); handleBookmarkToggle(c); }}
                        style={{ margin: 0, padding: 4 }}
                      >
                        <svg width="18" height="18" viewBox="0 0 24 24" fill={c.isBookmarked ? '#6C3BEB' : 'none'} stroke={c.isBookmarked ? '#6C3BEB' : 'currentColor'} strokeWidth="2">
                          <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
                        </svg>
                      </button>
                    </div>

                    <div className="assessment-card-body">
                      <p style={{ minHeight: 48, fontSize: '0.85rem', color: 'var(--gray-400)', WebkitLineClamp: 2, display: '-webkit-box', WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{c.description}</p>
                      
                      <div className="assessment-meta" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, margin: '14px 0 16px' }}>
                        <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                          <strong>NIRF Rank:</strong> #{c.nirfRanking || 'N/A'}
                        </div>
                        <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                          <strong>Avg Package:</strong> ₹{(c.averagePackage / 100000).toFixed(1)} LPA
                        </div>
                        <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                          <strong>Fees:</strong> ₹{(c.annualFees / 100000).toFixed(2)}L / Yr
                        </div>
                        <div className="meta-item" style={{ fontSize: '0.78rem' }}>
                          <strong>Hostel:</strong> {c.hostelAvailable ? 'Available' : 'No'}
                        </div>
                      </div>

                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid rgba(255,255,255,0.06)', paddingTop: 12 }}>
                        <label style={{ display: 'inline-flex', alignItems: 'center', gap: 6, fontSize: '0.78rem', color: 'var(--gray-500)', cursor: 'pointer' }} onClick={e => e.stopPropagation()}>
                          <input 
                            type="checkbox"
                            checked={compareIds.includes(c.id)}
                            onChange={e => handleCompareSelect(c.id, e.target.checked)}
                            style={{ accentColor: '#6C3BEB' }}
                          />
                          Add to Compare
                        </label>
                        <span style={{ fontSize: '0.8rem', color: '#6C3BEB', fontWeight: 600 }}>Profile Details →</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {/* Pagination controls */}
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

      {/* Comparison Modal */}
      {showCompareModal && (
        <div className="dialog-overlay" onClick={() => setShowCompareModal(false)}>
          <div className="dialog-box" style={{ maxWidth: 840, textAlign: 'left' }} onClick={e => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
              <h2 className="dialog-title" style={{ margin: 0 }}>Colleges Comparison Grid</h2>
              <button className="back-btn" onClick={() => setShowCompareModal(false)} style={{ margin: 0 }}>✕ Close</button>
            </div>
            
            <div className="history-table-wrapper" style={{ maxHeight: 500, overflowY: 'auto' }}>
              <table className="history-table">
                <thead>
                  <tr>
                    <th>Attributes</th>
                    {compareData.map(c => (
                      <th key={c.id}>{c.collegeName}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Type / Ownership</td>
                    {compareData.map(c => (
                      <td key={c.id}>{c.collegeType} ({c.ownership})</td>
                    ))}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>NIRF Ranking</td>
                    {compareData.map(c => (
                      <td key={c.id} style={{ color: '#22c55e', fontWeight: 700 }}>#{c.nirfRanking || 'N/A'}</td>
                    ))}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>NAAC Grade</td>
                    {compareData.map(c => (
                      <td key={c.id} style={{ fontWeight: 700 }}>{c.naacGrade || 'N/A'}</td>
                    ))}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Average Package</td>
                    {compareData.map(c => (
                      <td key={c.id} style={{ color: '#6C3BEB', fontWeight: 700 }}>₹{(c.averagePackage/100000).toFixed(1)} LPA</td>
                    ))}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Highest Package</td>
                    {compareData.map(c => (
                      <td key={c.id}>₹{(c.highestPackage/100000).toFixed(1)} LPA</td>
                    ))}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Placement %</td>
                    {compareData.map(c => (
                      <td key={c.id}>{c.placementPercentage}%</td>
                    ))}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Annual Fees</td>
                    {compareData.map(c => (
                      <td key={c.id}>₹{(c.annualFees/100000).toFixed(2)}L</td>
                    ))}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Campus Size</td>
                    {compareData.map(c => (
                      <td key={c.id}>{c.campusArea} Acres</td>
                    ))}
                  </tr>
                  <tr>
                    <td style={{ fontWeight: 600 }}>Facilities</td>
                    {compareData.map(c => (
                      <td key={c.id} style={{ fontSize: '0.8rem' }}>
                        {c.facilities.join(', ')}
                      </td>
                    ))}
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div style={{ marginTop: 20, display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
              <button className="btn-ghost" onClick={() => { setCompareIds([]); setShowCompareModal(false); }}>Clear Selection</button>
              <button className="btn-primary" onClick={() => setShowCompareModal(false)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CollegeFinderPage;
