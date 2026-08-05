import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getRoadmapById, updateTaskStatus } from '../services/roadmapService';
import type { RoadmapResponse, RoadmapTaskResponse } from '../types/roadmap';

const RoadmapTimelinePage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [roadmap, setRoadmap] = useState<RoadmapResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [viewMode, setViewMode] = useState<'kanban' | 'timeline'>('kanban');

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

  const handleStatusChange = async (taskId: string, newStatus: string) => {
    try {
      await updateTaskStatus(taskId, { status: newStatus });
      fetchRoadmap();
    } catch {
      alert('Failed to update task status.');
    }
  };

  if (loading) {
    return (
      <div className="take-page-loading">
        <div className="loading-spinner-large" />
        <p>Loading timeline & kanban board...</p>
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

  // Flatten tasks for Kanban
  const allTasks: RoadmapTaskResponse[] = [];
  roadmap.phases.forEach(p => p.tasks.forEach(t => allTasks.push(t)));

  const pendingTasks = allTasks.filter(t => t.status === 'PENDING');
  const inProgressTasks = allTasks.filter(t => t.status === 'IN_PROGRESS');
  const completedTasks = allTasks.filter(t => t.status === 'COMPLETED');
  const skippedTasks = allTasks.filter(t => t.status === 'SKIPPED');

  const renderKanbanColumn = (title: string, taskList: RoadmapTaskResponse[], badgeColor: string) => (
    <div style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.05)', borderRadius: 16, padding: 16, display: 'flex', flexDirection: 'column', gap: 12 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4 }}>
        <h3 style={{ fontSize: '0.95rem', fontWeight: 800, color: 'white', margin: 0 }}>{title}</h3>
        <span className="take-type-badge" style={{ backgroundColor: badgeColor, color: 'white' }}>{taskList.length}</span>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 12, minHeight: 200 }}>
        {taskList.map(t => (
          <div key={t.id} style={{ background: 'rgba(0,0,0,0.25)', border: '1px solid rgba(255,255,255,0.06)', borderRadius: 10, padding: 14 }}>
            <span style={{ fontSize: '0.72rem', color: '#6C3BEB', fontWeight: 700 }}>{t.phaseName}</span>
            <h4 style={{ fontSize: '0.9rem', color: 'white', margin: '4px 0 6px', fontWeight: 700 }}>{t.title}</h4>
            <p style={{ fontSize: '0.78rem', color: 'var(--gray-400)', margin: '0 0 10px', lineHeight: 1.4 }}>{t.description}</p>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.75rem', color: 'var(--gray-500)' }}>
              <span>⏱ {t.estimatedHours} hrs</span>
              <span>📅 {t.dueDate}</span>
            </div>

            {t.linkedResourceUrl && (
              <div style={{ marginTop: 10, borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: 8 }}>
                <a href={t.linkedResourceUrl} target="_blank" rel="noreferrer" style={{ fontSize: '0.75rem', color: '#6C3BEB', textDecoration: 'none' }}>
                  📖 {t.linkedResourceTitle || 'Linked Learning Resource'} ↗
                </a>
              </div>
            )}

            {/* Quick Actions */}
            <div style={{ display: 'flex', gap: 6, marginTop: 12 }}>
              {t.status !== 'IN_PROGRESS' && t.status !== 'COMPLETED' && (
                <button className="btn-ghost btn-xs" onClick={() => handleStatusChange(t.id, 'IN_PROGRESS')} style={{ fontSize: '0.7rem' }}>
                  Start Task
                </button>
              )}
              {t.status !== 'COMPLETED' && (
                <button className="btn-primary btn-xs" onClick={() => handleStatusChange(t.id, 'COMPLETED')} style={{ fontSize: '0.7rem' }}>
                  Mark Done ✓
                </button>
              )}
              {t.status !== 'SKIPPED' && t.status !== 'COMPLETED' && (
                <button className="btn-ghost btn-xs" onClick={() => handleStatusChange(t.id, 'SKIPPED')} style={{ fontSize: '0.7rem', color: 'var(--gray-500)' }}>
                  Skip
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <button className="back-btn" onClick={() => navigate('/roadmaps')} style={{ marginBottom: 12 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            Roadmap Dashboard
          </button>
          <h1 className="history-title">{roadmap.title}</h1>
          <p className="history-subtitle">Visual timeline schedule & Kanban task tracking board</p>
        </div>

        <div style={{ display: 'flex', gap: 8 }}>
          <button 
            className={`filter-tab ${viewMode === 'kanban' ? 'active' : ''}`} 
            onClick={() => setViewMode('kanban')}
          >
            Kanban Board
          </button>
          <button 
            className={`filter-tab ${viewMode === 'timeline' ? 'active' : ''}`} 
            onClick={() => setViewMode('timeline')}
          >
            Timeline View
          </button>
        </div>
      </div>

      {viewMode === 'kanban' ? (
        /* Kanban Grid Board */
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: 20 }}>
          {renderKanbanColumn('Pending Tasks', pendingTasks, 'rgba(255,255,255,0.1)')}
          {renderKanbanColumn('In Progress', inProgressTasks, '#6C3BEB')}
          {renderKanbanColumn('Completed', completedTasks, '#22c55e')}
          {renderKanbanColumn('Skipped', skippedTasks, 'rgba(239,68,68,0.5)')}
        </div>
      ) : (
        /* Timeline View */
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {roadmap.phases.map(phase => (
            <div key={phase.id} className="info-instructions-card" style={{ background: 'rgba(255,255,255,0.02)', padding: 24, borderRadius: 20 }}>
              <div style={{ fontSize: '0.8rem', color: '#6C3BEB', fontWeight: 800 }}>Phase {phase.sequence}</div>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: 'white', margin: '2px 0 8px' }}>{phase.phaseName}</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--gray-400)', margin: '0 0 16px' }}>{phase.description}</p>

              <div style={{ borderLeft: '2px solid rgba(255,255,255,0.1)', paddingLeft: 16, display: 'flex', flexDirection: 'column', gap: 16 }}>
                {phase.tasks.map(t => (
                  <div key={t.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <h4 style={{ fontSize: '0.9rem', color: 'white', margin: '0 0 2px' }}>{t.title}</h4>
                      <div style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>Due: {t.dueDate} — {t.estimatedHours} Hours</div>
                    </div>
                    <span className="take-type-badge" style={{ backgroundColor: t.status === 'COMPLETED' ? 'rgba(34,197,94,0.15)' : 'rgba(255,255,255,0.05)', color: t.status === 'COMPLETED' ? '#22c55e' : 'white' }}>
                      {t.status}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default RoadmapTimelinePage;
