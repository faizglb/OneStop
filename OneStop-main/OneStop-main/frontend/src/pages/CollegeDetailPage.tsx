import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getCollegeById, bookmarkCollege, removeBookmark } from '../services/collegeService';
import type { CollegeResponse } from '../types/college';

const CollegeDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [college, setCollege] = useState<CollegeResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'courses' | 'placements' | 'facilities'>('overview');

  const fetchCollege = async () => {
    if (!id) return;
    try {
      const data = await getCollegeById(id);
      setCollege(data);
    } catch {
      // error handled
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCollege();
  }, [id]);

  const handleBookmarkToggle = async () => {
    if (!college) return;
    try {
      if (college.isBookmarked) {
        await removeBookmark(college.id);
        setCollege({ ...college, isBookmarked: false });
      } else {
        await bookmarkCollege(college.id);
        setCollege({ ...college, isBookmarked: true });
      }
    } catch {
      alert('Error updating bookmark status.');
    }
  };

  if (loading) {
    return (
      <div className="take-page-loading">
        <div className="loading-spinner-large" />
        <p>Loading college profile...</p>
      </div>
    );
  }

  if (!college) {
    return (
      <div className="page-container">
        <div className="error-state">
          <h2>College Profile Not Found</h2>
          <button className="btn-primary" onClick={() => navigate('/colleges')}>Back to Search</button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      {/* Back Link */}
      <button className="back-btn" onClick={() => navigate('/colleges')}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
        </svg>
        All Colleges
      </button>

      {/* Profile Header */}
      <div className="result-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 16 }}>
        <div>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 12 }}>
            <span className="take-type-badge">{college.collegeType}</span>
            <span className="take-type-badge" style={{ backgroundColor: 'rgba(255,255,255,0.08)', color: 'white' }}>Ranked #{college.nirfRanking || 'N/A'} NIRF</span>
          </div>
          <h1 className="result-title" style={{ fontSize: '2rem', fontWeight: 800, margin: 0 }}>{college.collegeName}</h1>
          <p className="result-date" style={{ marginTop: 4 }}>{college.city}, {college.state} — Established in {college.establishedYear}</p>
        </div>

        <button 
          className={`filter-tab ${college.isBookmarked ? 'active' : ''}`}
          onClick={handleBookmarkToggle}
          style={{ display: 'flex', alignItems: 'center', gap: 6 }}
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill={college.isBookmarked ? '#6C3BEB' : 'none'} stroke={college.isBookmarked ? '#6C3BEB' : 'currentColor'} strokeWidth="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
          {college.isBookmarked ? 'Bookmarked' : 'Bookmark'}
        </button>
      </div>

      {/* Tabs list */}
      <div className="history-filters" style={{ margin: '24px 0' }}>
        {(['overview', 'courses', 'placements', 'facilities'] as const).map(tab => (
          <button
            key={tab}
            className={`filter-tab ${activeTab === tab ? 'active' : ''}`}
            onClick={() => setActiveTab(tab)}
            style={{ textTransform: 'capitalize' }}
          >
            {tab}
          </button>
        ))}
      </div>

      {/* Profile Grid layout */}
      <div className="info-page-layout" style={{ gridTemplateColumns: '1fr 340px' }}>
        
        {/* Left Column Contents */}
        <div>
          {/* TAB: OVERVIEW */}
          {activeTab === 'overview' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
              <div className="info-instructions-card" style={{ background: 'rgba(255,255,255,0.02)', padding: 28, borderRadius: 20 }}>
                <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white', marginBottom: 12 }}>About the University</h3>
                <p style={{ fontSize: '0.95rem', color: 'var(--gray-300)', lineHeight: 1.7, margin: 0 }}>{college.description}</p>
              </div>

              {/* Accreditations details */}
              <div className="result-stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))' }}>
                <div className="result-stat-card">
                  <div className="stat-label">NIRF Rank</div>
                  <div className="stat-value" style={{ color: '#6C3BEB' }}>#{college.nirfRanking || 'N/A'}</div>
                </div>
                <div className="result-stat-card">
                  <div className="stat-label">NAAC Rating</div>
                  <div className="stat-value" style={{ color: '#22c55e' }}>{college.naacGrade || 'N/A'}</div>
                </div>
                <div className="result-stat-card">
                  <div className="stat-label">AICTE Approved</div>
                  <div className="stat-value">{college.aicteApproved ? 'Yes' : 'No'}</div>
                </div>
                <div className="result-stat-card">
                  <div className="stat-label">UGC Recognized</div>
                  <div className="stat-value">{college.ugcApproved ? 'Yes' : 'No'}</div>
                </div>
              </div>
            </div>
          )}

          {/* TAB: COURSES */}
          {activeTab === 'courses' && (
            <div className="result-section-card" style={{ padding: 28, borderRadius: 20 }}>
              <h2 className="section-heading">Offered Courses & Admissions</h2>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                {college.courses.map(co => (
                  <div key={co.id} style={{ paddingBottom: 16, borderBottom: '1px solid rgba(255,255,255,0.04)', display: 'grid', gridTemplateColumns: '1fr 140px 100px', gap: 16, alignItems: 'center' }}>
                    <div>
                      <h4 style={{ margin: 0, fontWeight: 700, color: 'white' }}>{co.courseName}</h4>
                      <p style={{ fontSize: '0.8rem', color: 'var(--gray-500)', margin: '4px 0 0' }}>Degree: {co.degree} | Stream: {co.stream}</p>
                    </div>
                    <div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--gray-300)' }}><strong>Exam:</strong> {co.entranceExam || 'Direct'}</div>
                      <div style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Duration: {co.duration} Years</div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontSize: '0.9rem', fontWeight: 700, color: '#6C3BEB' }}>{co.seatIntake} Seats</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* TAB: PLACEMENTS */}
          {activeTab === 'placements' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
              <div className="result-stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))' }}>
                <div className="result-stat-card" style={{ background: 'rgba(108,59,235,0.04)', borderColor: 'rgba(108,59,235,0.2)' }}>
                  <div className="stat-label">Average Package</div>
                  <div className="stat-value" style={{ color: '#a855f7' }}>₹{(college.averagePackage / 100000).toFixed(1)} LPA</div>
                </div>
                <div className="result-stat-card">
                  <div className="stat-label">Highest Package</div>
                  <div className="stat-value" style={{ color: '#22c55e' }}>₹{(college.highestPackage / 100000).toFixed(1)} LPA</div>
                </div>
                <div className="result-stat-card">
                  <div className="stat-label">Placement Rate</div>
                  <div className="stat-value">{college.placementPercentage}%</div>
                </div>
              </div>

              <div className="info-instructions-card" style={{ background: 'rgba(255,255,255,0.02)', padding: 24, borderRadius: 16 }}>
                <h4 style={{ margin: '0 0 10px', color: 'white' }}>Recruitment details</h4>
                <p style={{ fontSize: '0.88rem', color: 'var(--gray-400)', lineHeight: 1.6, margin: 0 }}>
                  The university hosts annual placement drives matching graduates with top tech companies, finance houses, and corporate leaders. 
                  Median package stands at ₹{(college.medianPackage/100000).toFixed(1)} LPA.
                </p>
              </div>
            </div>
          )}

          {/* TAB: FACILITIES */}
          {activeTab === 'facilities' && (
            <div className="result-section-card" style={{ padding: 28, borderRadius: 20 }}>
              <h2 className="section-heading">Campus Facilities & Services</h2>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16 }}>
                {college.facilities.map(f => (
                  <div key={f.id} style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)', padding: 16, borderRadius: 12 }}>
                    <h4 style={{ margin: '0 0 6px', color: 'white', fontWeight: 700 }}>{f.facilityName}</h4>
                    <p style={{ fontSize: '0.85rem', color: 'var(--gray-400)', margin: 0, lineHeight: 1.5 }}>{f.description}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Right Column quick specs */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'white', margin: '0 0 20px' }}>Fee Structures</h3>
            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ color: '#22c55e' }}>
                <line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
              </svg>
              <div>
                <div className="summary-label">Annual Tuition Fees</div>
                <div className="summary-value" style={{ color: 'white' }}>₹{(college.annualFees / 100000).toFixed(2)} Lakhs</div>
              </div>
            </div>
            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ color: '#6C3BEB' }}>
                <line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
              </svg>
              <div>
                <div className="summary-label">Total Course Fees (Est.)</div>
                <div className="summary-value" style={{ color: 'white' }}>₹{(college.totalFees / 100000).toFixed(2)} Lakhs</div>
              </div>
            </div>
          </div>

          <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'white', margin: '0 0 20px' }}>Contact & Address</h3>
            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <div>
                <div className="summary-label">Website</div>
                <div className="summary-value" style={{ color: '#6C3BEB', fontSize: '0.85rem' }}>
                  <a href={college.website || '#'} target="_blank" rel="noreferrer" style={{ color: 'inherit', textDecoration: 'none' }}>{college.website || 'N/A'}</a>
                </div>
              </div>
            </div>
            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <div>
                <div className="summary-label">Email</div>
                <div className="summary-value" style={{ color: 'white', fontSize: '0.85rem' }}>{college.email || 'N/A'}</div>
              </div>
            </div>
            <div className="summary-item" style={{ display: 'flex', gap: 12 }}>
              <div>
                <div className="summary-label">Address</div>
                <div className="summary-value" style={{ color: 'white', fontSize: '0.82rem', lineHeight: 1.4 }}>{college.address}</div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default CollegeDetailPage;
