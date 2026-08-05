import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getResourceById, bookmarkResource, removeBookmark, updateLearningProgress } from '../services/learningService';
import type { LearningResourceResponse } from '../types/learning';

const LearningResourceDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [resource, setResource] = useState<LearningResourceResponse | null>(null);
  const [loading, setLoading] = useState(true);

  // Tracker State
  const [progressVal, setProgressVal] = useState(0);
  const [statusVal, setStatusVal] = useState('STARTED');
  const [saving, setSaving] = useState(false);

  const fetchResource = async () => {
    if (!id) return;
    try {
      const data = await getResourceById(id);
      setResource(data);
      setProgressVal(data.progressPercentage || 0);
      setStatusVal(data.progressStatus || 'STARTED');
    } catch {
      // error handled
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchResource();
  }, [id]);

  const handleBookmarkToggle = async () => {
    if (!resource) return;
    try {
      if (resource.isBookmarked) {
        await removeBookmark(resource.id);
        setResource({ ...resource, isBookmarked: false });
      } else {
        await bookmarkResource(resource.id);
        setResource({ ...resource, isBookmarked: true });
      }
    } catch {
      alert('Error updating bookmark.');
    }
  };

  const handleUpdateProgress = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!resource) return;
    setSaving(true);
    try {
      const updated = await updateLearningProgress({
        resourceId: resource.id,
        status: statusVal,
        progressPercentage: progressVal
      });
      setResource(prev => prev ? {
        ...prev,
        progressStatus: updated.status,
        progressPercentage: updated.progressPercentage
      } : null);
      setProgressVal(updated.progressPercentage);
      setStatusVal(updated.status);
      alert('Learning progress updated successfully!');
    } catch {
      alert('Failed to save progress update.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="take-page-loading">
        <div className="loading-spinner-large" />
        <p>Loading course profile...</p>
      </div>
    );
  }

  if (!resource) {
    return (
      <div className="page-container">
        <div className="error-state">
          <h2>Resource Not Found</h2>
          <button className="btn-primary" onClick={() => navigate('/resources')}>Back to Dashboard</button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      {/* Back Link */}
      <button className="back-btn" onClick={() => navigate('/resources')}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
        </svg>
        All Resources
      </button>

      {/* Header */}
      <div className="result-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 16 }}>
        <div>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 12 }}>
            <span className="take-type-badge">{resource.platform}</span>
            <span className="take-type-badge" style={{ backgroundColor: 'rgba(255,255,255,0.08)', color: 'white' }}>{resource.resourceType}</span>
          </div>
          <h1 className="result-title" style={{ fontSize: '2rem', fontWeight: 800, margin: 0 }}>{resource.title}</h1>
          <p className="result-date" style={{ marginTop: 4 }}>Created by: <strong>{resource.author || resource.provider}</strong> — Language: {resource.language}</p>
        </div>

        <div style={{ display: 'flex', gap: 12 }}>
          <button 
            className={`filter-tab ${resource.isBookmarked ? 'active' : ''}`}
            onClick={handleBookmarkToggle}
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill={resource.isBookmarked ? '#6C3BEB' : 'none'} stroke={resource.isBookmarked ? '#6C3BEB' : 'currentColor'} strokeWidth="2">
              <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
            </svg>
            {resource.isBookmarked ? 'Bookmarked' : 'Save'}
          </button>
          
          <a 
            href={resource.resourceUrl} 
            target="_blank" 
            rel="noreferrer" 
            className="btn-primary"
            style={{ display: 'inline-flex', alignItems: 'center', gap: 6, textDecoration: 'none', padding: '10px 20px', borderRadius: 8, fontSize: '0.9rem' }}
          >
            Launch Course Website ↗
          </a>
        </div>
      </div>

      {/* Main Grid Layout */}
      <div className="info-page-layout" style={{ gridTemplateColumns: '1fr 340px', marginTop: 32 }}>
        
        {/* Left Column Content */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {/* Overview */}
          <div className="info-instructions-card" style={{ background: 'rgba(255,255,255,0.02)', padding: 28, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white', marginBottom: 12 }}>Course Description</h3>
            <p style={{ fontSize: '0.95rem', color: 'var(--gray-300)', lineHeight: 1.7, margin: 0 }}>{resource.description}</p>
          </div>

          {/* Skill Tag items */}
          <div className="result-section-card" style={{ padding: 28, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white', marginBottom: 16 }}>Skills You Will Master</h3>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
              <span className="take-type-badge" style={{ backgroundColor: 'rgba(108,59,235,0.12)', color: '#a855f7', padding: '6px 12px', fontSize: '0.85rem' }}>
                {resource.skill}
              </span>
              <span className="take-type-badge" style={{ backgroundColor: 'rgba(255,255,255,0.05)', color: 'white', padding: '6px 12px', fontSize: '0.85rem' }}>
                {resource.category}
              </span>
            </div>
          </div>

          {/* Progress updating box if started learning */}
          {resource.progressStatus && (
            <form onSubmit={handleUpdateProgress} className="result-section-card" style={{ padding: 28, borderRadius: 20 }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white', marginBottom: 16 }}>Log Learning Progress</h3>
              
              <div className="form-group" style={{ marginBottom: 20 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                  <label className="form-label" style={{ margin: 0 }}>Completion Percentage</label>
                  <span style={{ fontWeight: 700, color: '#6C3BEB' }}>{progressVal}%</span>
                </div>
                <input 
                  type="range" 
                  min={0} 
                  max={100} 
                  step={5}
                  value={progressVal} 
                  onChange={e => {
                    const pct = Number(e.target.value);
                    setProgressVal(pct);
                    if (pct === 100) setStatusVal('COMPLETED');
                    else setStatusVal('IN_PROGRESS');
                  }}
                  style={{ width: '100%', accentColor: '#6C3BEB' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div className="form-group" style={{ margin: 0 }}>
                  <label className="form-label" style={{ display: 'inline-flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
                    <input 
                      type="checkbox"
                      checked={statusVal === 'COMPLETED'}
                      onChange={e => {
                        const comp = e.target.checked;
                        setStatusVal(comp ? 'COMPLETED' : 'IN_PROGRESS');
                        setProgressVal(comp ? 100 : 50);
                      }}
                      style={{ accentColor: '#6C3BEB' }}
                    />
                    Mark Course as Fully Completed
                  </label>
                </div>
                <button type="submit" className="btn-primary btn-sm" disabled={saving}>
                  {saving ? 'Saving...' : 'Save Progress'}
                </button>
              </div>
            </form>
          )}
        </div>

        {/* Right Column details */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {/* Rating / Pricing card */}
          <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'white', margin: '0 0 20px' }}>Course Parameters</h3>
            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <div>
                <div className="summary-label">Syllabus Level</div>
                <div className="summary-value" style={{ color: 'white' }}>{resource.difficulty}</div>
              </div>
            </div>
            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <div>
                <div className="summary-label">Pacing / Duration</div>
                <div className="summary-value" style={{ color: 'white' }}>{resource.duration}</div>
              </div>
            </div>
            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <div>
                <div className="summary-label">Cost</div>
                <div className="summary-value" style={{ color: 'white' }}>{resource.isFree ? 'Free (No Paywall)' : `₹${resource.price}`}</div>
              </div>
            </div>
            <div className="summary-item" style={{ display: 'flex', gap: 12 }}>
              <div>
                <div className="summary-label">Certificate Availability</div>
                <div className="summary-value" style={{ color: 'white' }}>{resource.certificateAvailable ? 'Yes (Verified Certificate)' : 'No'}</div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default LearningResourceDetailPage;
