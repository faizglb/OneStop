import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getScholarshipById, bookmarkScholarship, removeBookmark } from '../services/scholarshipService';
import type { ScholarshipResponse } from '../types/scholarship';

const ScholarshipDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [scholarship, setScholarship] = useState<ScholarshipResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchScholarship = async () => {
    if (!id) return;
    try {
      const data = await getScholarshipById(id);
      setScholarship(data);
    } catch {
      // error handled
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchScholarship();
  }, [id]);

  const handleBookmarkToggle = async () => {
    if (!scholarship) return;
    try {
      if (scholarship.isBookmarked) {
        await removeBookmark(scholarship.id);
        setScholarship({ ...scholarship, isBookmarked: false });
      } else {
        await bookmarkScholarship(scholarship.id);
        setScholarship({ ...scholarship, isBookmarked: true });
      }
    } catch {
      alert('Error updating bookmark status.');
    }
  };

  if (loading) {
    return (
      <div className="take-page-loading">
        <div className="loading-spinner-large" />
        <p>Loading scholarship details...</p>
      </div>
    );
  }

  if (!scholarship) {
    return (
      <div className="page-container">
        <div className="error-state">
          <h2>Scholarship Not Found</h2>
          <button className="btn-primary" onClick={() => navigate('/scholarships')}>Back to Finder</button>
        </div>
      </div>
    );
  }

  const daysLeft = Math.ceil((new Date(scholarship.lastDate).getTime() - new Date().getTime()) / (1000 * 3600 * 24));

  return (
    <div className="page-container">
      {/* Back Link */}
      <button className="back-btn" onClick={() => navigate('/scholarships')}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
        </svg>
        All Scholarships
      </button>

      {/* Header */}
      <div className="result-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 16 }}>
        <div>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 12 }}>
            <span className="take-type-badge">{scholarship.scholarshipType}</span>
            <span className="take-type-badge" style={{ backgroundColor: daysLeft <= 15 ? 'rgba(239,68,68,0.1)' : 'rgba(245,158,11,0.1)', color: daysLeft <= 15 ? '#ef4444' : '#fbbf24' }}>
              {daysLeft > 0 ? `${daysLeft} days left` : 'Application Closed'}
            </span>
          </div>
          <h1 className="result-title" style={{ fontSize: '2rem', fontWeight: 800, margin: 0 }}>{scholarship.scholarshipName}</h1>
          <p className="result-date" style={{ marginTop: 4 }}>Offered by: <strong>{scholarship.provider}</strong> — Domicile State: {scholarship.state || 'National'}</p>
        </div>

        <div style={{ display: 'flex', gap: 12 }}>
          <button 
            className={`filter-tab ${scholarship.isBookmarked ? 'active' : ''}`}
            onClick={handleBookmarkToggle}
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill={scholarship.isBookmarked ? '#6C3BEB' : 'none'} stroke={scholarship.isBookmarked ? '#6C3BEB' : 'currentColor'} strokeWidth="2">
              <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
            </svg>
            {scholarship.isBookmarked ? 'Bookmarked' : 'Shortlist'}
          </button>
          
          {scholarship.applicationLink && (
            <a 
              href={scholarship.applicationLink} 
              target="_blank" 
              rel="noreferrer" 
              className="btn-primary"
              style={{ display: 'inline-flex', alignItems: 'center', gap: 6, textDecoration: 'none', padding: '10px 20px', borderRadius: 8, fontSize: '0.9rem' }}
            >
              Apply Online →
            </a>
          )}
        </div>
      </div>

      {/* Grid Layout details */}
      <div className="info-page-layout" style={{ gridTemplateColumns: '1fr 340px', marginTop: 32 }}>
        
        {/* Left Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {/* Overview */}
          <div className="info-instructions-card" style={{ background: 'rgba(255,255,255,0.02)', padding: 28, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white', marginBottom: 12 }}>Scholarship Overview</h3>
            <p style={{ fontSize: '0.95rem', color: 'var(--gray-300)', lineHeight: 1.7, margin: '0 0 20px' }}>{scholarship.description}</p>
            
            <div style={{ display: 'flex', gap: 24, borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: 16 }}>
              <div>
                <div style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Renewable Status</div>
                <div style={{ fontWeight: 700, color: 'white' }}>{scholarship.renewable ? 'Renewable Annually' : 'One-time Grant'}</div>
              </div>
              <div>
                <div style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Application Mode</div>
                <div style={{ fontWeight: 700, color: 'white' }}>{scholarship.applicationMode}</div>
              </div>
            </div>
          </div>

          {/* Eligibility Rules Checklist */}
          <div className="result-section-card" style={{ padding: 28, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white', marginBottom: 16 }}>Eligibility Criteria</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              {scholarship.eligibilities.map(e => (
                <div key={e.id} style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
                  <span style={{ color: '#22c55e', fontSize: '1.2rem' }}>✓</span>
                  <span style={{ fontSize: '0.92rem', color: 'var(--gray-300)' }}>{e.eligibilityRule}</span>
                </div>
              ))}
              {scholarship.educationLevel && (
                <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
                  <span style={{ color: '#22c55e', fontSize: '1.2rem' }}>✓</span>
                  <span style={{ fontSize: '0.92rem', color: 'var(--gray-300)' }}>Education level limit: {scholarship.educationLevel} courses</span>
                </div>
              )}
            </div>
          </div>

          {/* Required Documents */}
          <div className="result-section-card" style={{ padding: 28, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white', marginBottom: 16 }}>Required Documents Checklist</h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 12 }}>
              {scholarship.requiredDocuments.split(',').map((doc, idx) => (
                <div key={idx} style={{ background: 'rgba(255,255,255,0.02)', padding: '12px 16px', borderRadius: 8, border: '1px solid rgba(255,255,255,0.05)', fontSize: '0.88rem', color: 'white', display: 'flex', alignItems: 'center', gap: 8 }}>
                  <span style={{ color: '#a855f7' }}>📄</span> {doc.trim()}
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right Column quick specs */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {/* Amount Card */}
          <div className="info-summary-card" style={{ padding: 24, borderRadius: 20, textAlign: 'center' }}>
            <div style={{ fontSize: '0.78rem', color: 'var(--gray-500)', textTransform: 'uppercase', letterSpacing: 0.5 }}>Scholarship Amount</div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: '#22c55e', margin: '6px 0 16px' }}>₹{Number(scholarship.amount).toLocaleString('en-IN')}</div>
            <div style={{ fontSize: '0.82rem', color: 'var(--gray-400)' }}>Estimated total annual grant allocation.</div>
          </div>

          {/* Deadlines Details */}
          <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
            <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'white', margin: '0 0 16px' }}>Important Deadlines</h3>
            <div className="summary-item" style={{ display: 'flex', gap: 12 }}>
              <div>
                <div className="summary-label">Last Date of Application</div>
                <div className="summary-value" style={{ color: 'white' }}>
                  {new Date(scholarship.lastDate).toLocaleDateString('en-IN', { day: 'numeric', month: 'long', year: 'numeric' })}
                </div>
              </div>
            </div>
          </div>

          {/* Official Website */}
          {scholarship.officialWebsite && (
            <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
              <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'white', margin: '0 0 16px' }}>Verify Information</h3>
              <a 
                href={scholarship.officialWebsite} 
                target="_blank" 
                rel="noreferrer" 
                style={{ color: '#6C3BEB', textDecoration: 'none', fontSize: '0.88rem', fontWeight: 600 }}
              >
                Launch Official Web Portal ↗
              </a>
            </div>
          )}
        </div>

      </div>
    </div>
  );
};

export default ScholarshipDetailPage;
