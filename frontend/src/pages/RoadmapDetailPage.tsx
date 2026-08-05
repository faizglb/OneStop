import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getRoadmapById, deleteRoadmap, updateRoadmapStatus } from '../services/roadmapService';
import type { RoadmapResponse } from '../types/roadmap';

const RoadmapDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [roadmap, setRoadmap] = useState<RoadmapResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchRoadmap = async () => {
    if (!id) return;
    try {
      const data = await getRoadmapById(id);
      setRoadmap(data);
    } catch {
      // Handled
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoadmap();
  }, [id]);

  const handleDelete = async () => {
    if (!roadmap || !window.confirm('Are you sure you want to delete this roadmap?')) return;
    try {
      await deleteRoadmap(roadmap.id);
      navigate('/roadmaps');
    } catch {
      alert('Failed to delete roadmap.');
    }
  };

  const handleTogglePause = async () => {
    if (!roadmap) return;
    const nextStatus = roadmap.status === 'PAUSED' ? 'ACTIVE' : 'PAUSED';
    try {
      const updated = await updateRoadmapStatus(roadmap.id, nextStatus);
      setRoadmap(updated);
    } catch {
      alert('Failed to update roadmap status.');
    }
  };

  if (loading) {
    return (
      <div className="take-page-loading">
        <div className="loading-spinner-large" />
        <p>Loading roadmap profile...</p>
      </div>
    );
  }

  if (!roadmap) {
    return (
      <div className="page-container">
        <div className="error-state">
          <h2>Roadmap Not Found</h2>
          <button className="btn-primary" onClick={() => navigate('/roadmaps')}>Back to Dashboard</button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      {/* Header */}
      <div className="result-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 16 }}>
        <div>
          <button className="back-btn" onClick={() => navigate('/roadmaps')} style={{ marginBottom: 12 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            Roadmaps Dashboard
          </button>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginBottom: 8 }}>
            <span className="take-type-badge">{roadmap.status}</span>
            <span className="take-type-badge" style={{ backgroundColor: 'rgba(255,255,255,0.08)', color: 'white' }}>{roadmap.duration}</span>
          </div>
          <h1 className="result-title" style={{ fontSize: '2rem', fontWeight: 800, margin: 0 }}>{roadmap.title}</h1>
        </div>

        <div style={{ display: 'flex', gap: 12 }}>
          <button className="btn-ghost btn-sm" onClick={handleTogglePause}>
            {roadmap.status === 'PAUSED' ? 'Resume Roadmap' : 'Pause Plan'}
          </button>
          <button className="btn-ghost btn-sm" onClick={handleDelete} style={{ color: '#ef4444', borderColor: 'rgba(239,68,68,0.3)' }}>
            Delete
          </button>
        </div>
      </div>

      {/* Details layout */}
      <div className="info-page-layout" style={{ gridTemplateColumns: '1fr 340px', marginTop: 32 }}>
        
        {/* Left Column Phases */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {roadmap.phases.map(phase => (
            <div key={phase.id} className="info-instructions-card" style={{ background: 'rgba(255,255,255,0.02)', padding: 24, borderRadius: 20 }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'white', margin: '0 0 4px' }}>{phase.phaseName}</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--gray-400)', margin: '0 0 16px' }}>{phase.description}</p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {phase.tasks.map(t => (
                  <div key={t.id} style={{ background: 'rgba(0,0,0,0.2)', padding: 14, borderRadius: 10 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4 }}>
                      <h4 style={{ fontSize: '0.9rem', color: 'white', margin: 0 }}>{t.title}</h4>
                      <span style={{ fontSize: '0.75rem', color: 'var(--gray-500)' }}>Due: {t.dueDate}</span>
                    </div>
                    <p style={{ fontSize: '0.8rem', color: 'var(--gray-400)', margin: 0 }}>{t.description}</p>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        {/* Right Column Summary */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'white', margin: '0 0 20px' }}>Roadmap Metrics</h3>
            <div className="summary-item" style={{ marginBottom: 16 }}>
              <div className="summary-label">Target Career Goal</div>
              <div className="summary-value" style={{ color: 'white' }}>{roadmap.targetCareer}</div>
            </div>
            <div className="summary-item" style={{ marginBottom: 16 }}>
              <div className="summary-label">Weekly Commitment</div>
              <div className="summary-value" style={{ color: 'white' }}>{roadmap.weeklyHours} Hours / Week</div>
            </div>
            <div className="summary-item" style={{ marginBottom: 16 }}>
              <div className="summary-label">Start Date</div>
              <div className="summary-value" style={{ color: 'white' }}>{roadmap.startDate}</div>
            </div>
            <div className="summary-item">
              <div className="summary-label">Estimated Completion Date</div>
              <div className="summary-value" style={{ color: 'white' }}>{roadmap.endDate}</div>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default RoadmapDetailPage;
