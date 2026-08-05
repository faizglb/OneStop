import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  getStudentRoadmaps,
  generateRoadmap,
  updateTaskStatus,
  getRoadmapProgress,
  regenerateRoadmap
} from '../services/roadmapService';
import type { RoadmapResponse, RoadmapTaskResponse, RoadmapProgressResponse } from '../types/roadmap';

const RoadmapDashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const [roadmaps, setRoadmaps] = useState<RoadmapResponse[]>([]);
  const [activeRoadmap, setActiveRoadmap] = useState<RoadmapResponse | null>(null);
  const [progress, setProgress] = useState<RoadmapProgressResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);

  // Form State for Roadmap Generation
  const [targetCareer, setTargetCareer] = useState('Software Engineer');
  const [targetCompany, setTargetCompany] = useState('');
  const [duration, setDuration] = useState('60-Day Plan');
  const [weeklyHours, setWeeklyHours] = useState(12);
  const [generating, setGenerating] = useState(false);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const all = await getStudentRoadmaps();
      setRoadmaps(all);
      const active = all.find(r => r.status === 'ACTIVE') || all[0] || null;
      setActiveRoadmap(active);

      if (active) {
        const prog = await getRoadmapProgress();
        setProgress(prog);
      }
    } catch {
      // Handled
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const handleCreateRoadmap = async (e: React.FormEvent) => {
    e.preventDefault();
    setGenerating(true);
    try {
      const created = await generateRoadmap({
        targetCareer,
        targetCompany: targetCompany || undefined,
        duration,
        weeklyHours
      });
      setShowModal(false);
      fetchDashboardData();
      alert('AI Personalized Roadmap generated successfully!');
    } catch {
      alert('Failed to generate roadmap. Please try again.');
    } finally {
      setGenerating(false);
    }
  };

  const handleTaskCheck = async (task: RoadmapTaskResponse) => {
    const nextStatus = task.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED';
    try {
      await updateTaskStatus(task.id, { status: nextStatus });
      fetchDashboardData();
    } catch {
      alert('Failed to update task status.');
    }
  };

  const handleRegenerateRemaining = async () => {
    if (!activeRoadmap) return;
    try {
      await regenerateRoadmap(activeRoadmap.id);
      fetchDashboardData();
      alert('Rescheduled remaining uncompleted tasks starting from today!');
    } catch {
      alert('Failed to regenerate remaining schedule.');
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header">
        <div>
          <h1 className="history-title">Career Roadmap Hub</h1>
          <p className="history-subtitle">AI-guided multi-phase execution plan for your target tech careers</p>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          {activeRoadmap && (
            <button className="btn-ghost btn-sm" onClick={() => navigate(`/roadmaps/${activeRoadmap.id}/timeline`)}>
              Timeline & Kanban Board
            </button>
          )}
          <button className="btn-primary btn-sm" onClick={() => setShowModal(true)}>
            + Generate Roadmap
          </button>
        </div>
      </div>

      {loading ? (
        <div className="take-page-loading" style={{ height: '50vh' }}>
          <div className="loading-spinner-large" />
          <p>Structuring career milestones...</p>
        </div>
      ) : !activeRoadmap ? (
        /* Empty State */
        <div className="history-empty" style={{ background: 'rgba(255,255,255,0.02)', padding: '64px 32px', borderRadius: 20 }}>
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="rgba(108,59,235,0.4)" strokeWidth="1.5">
            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
          </svg>
          <h2>No Active Roadmap</h2>
          <p style={{ maxWidth: 480, margin: '8px auto 24px' }}>
            Build an AI-powered 8-phase step-by-step career path tailored to your study hours and target companies.
          </p>
          <button className="btn-primary" onClick={() => setShowModal(true)}>
            Build My Personalized Roadmap
          </button>
        </div>
      ) : (
        /* Dashboard Active Overview */
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          
          {/* Active Roadmap Banner */}
          <div className="info-instructions-card" style={{ background: 'rgba(108,59,235,0.04)', border: '1px solid rgba(108,59,235,0.15)', borderRadius: 20, padding: 28 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 16 }}>
              <div>
                <span className="take-type-badge">{activeRoadmap.duration}</span>
                {activeRoadmap.targetCompany && (
                  <span className="take-type-badge" style={{ backgroundColor: 'rgba(255,255,255,0.08)', color: 'white', marginLeft: 8 }}>
                    Target: {activeRoadmap.targetCompany}
                  </span>
                )}
                <h2 style={{ fontSize: '1.6rem', fontWeight: 800, color: 'white', margin: '8px 0 4px' }}>{activeRoadmap.title}</h2>
                <p style={{ fontSize: '0.85rem', color: 'var(--gray-400)', margin: 0 }}>
                  Start: {new Date(activeRoadmap.startDate).toLocaleDateString('en-IN')} — Target Completion: {new Date(activeRoadmap.endDate).toLocaleDateString('en-IN')}
                </p>
              </div>

              {/* Streak & Completion Badge */}
              <div style={{ display: 'flex', gap: 24, alignItems: 'center' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '1.8rem', fontWeight: 800, color: '#f59e0b', lineHeight: 1 }}>🔥 {progress?.streakDays || 0}</div>
                  <div style={{ fontSize: '0.72rem', color: 'var(--gray-500)', marginTop: 4 }}>Day Streak</div>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '2rem', fontWeight: 800, color: '#6C3BEB', lineHeight: 1 }}>{activeRoadmap.completionPercentage}%</div>
                  <div style={{ fontSize: '0.72rem', color: 'var(--gray-500)', marginTop: 4 }}>Progress</div>
                </div>
              </div>
            </div>

            {/* Progress Bar */}
            <div className="progress-bar-container" style={{ height: 10, background: 'rgba(255,255,255,0.06)', marginTop: 24 }}>
              <div className="progress-bar-fill" style={{ width: `${activeRoadmap.completionPercentage}%`, background: 'linear-gradient(90deg, #6C3BEB, #a855f7)' }} />
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 16 }}>
              <span style={{ fontSize: '0.85rem', color: 'var(--gray-400)' }}>
                {progress?.completedTasks || 0} of {progress?.totalTasks || 0} tasks completed
              </span>
              <button className="btn-ghost btn-xs" onClick={handleRegenerateRemaining} style={{ fontSize: '0.78rem' }}>
                🔄 Reschedule Remaining Tasks
              </button>
            </div>
          </div>

          {/* Main Dashboard Grid */}
          <div className="info-page-layout" style={{ gridTemplateColumns: '1fr 340px' }}>
            
            {/* Left Column: Today's Tasks & Phases breakdown */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
              <div className="result-section-card" style={{ padding: 24, borderRadius: 20 }}>
                <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'white', marginBottom: 16 }}>Phases Roadmap Overview</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                  {activeRoadmap.phases.map(phase => (
                    <div 
                      key={phase.id} 
                      style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.05)', padding: 16, borderRadius: 14 }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                        <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'white', margin: 0 }}>{phase.phaseName}</h4>
                        <span style={{ fontSize: '0.78rem', color: 'var(--gray-500)' }}>{phase.tasks.length} Tasks</span>
                      </div>
                      <p style={{ fontSize: '0.82rem', color: 'var(--gray-400)', margin: '0 0 12px' }}>{phase.description}</p>
                      
                      {/* Tasks List */}
                      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                        {phase.tasks.map(t => (
                          <div 
                            key={t.id} 
                            style={{ 
                              display: 'flex', 
                              alignItems: 'center', 
                              justifyContent: 'space-between',
                              background: 'rgba(0,0,0,0.15)', 
                              padding: '8px 12px', 
                              borderRadius: 8 
                            }}
                          >
                            <label style={{ display: 'inline-flex', alignItems: 'center', gap: 10, cursor: 'pointer', fontSize: '0.85rem', color: t.status === 'COMPLETED' ? 'var(--gray-500)' : 'white', textDecoration: t.status === 'COMPLETED' ? 'line-through' : 'none' }}>
                              <input 
                                type="checkbox" 
                                checked={t.status === 'COMPLETED'}
                                onChange={() => handleTaskCheck(t)}
                                style={{ accentColor: '#6C3BEB' }}
                              />
                              {t.title}
                            </label>
                            {t.linkedResourceUrl && (
                              <a href={t.linkedResourceUrl} target="_blank" rel="noreferrer" style={{ fontSize: '0.75rem', color: '#6C3BEB', textDecoration: 'none' }}>
                                View Resource ↗
                              </a>
                            )}
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Right Column: Milestones */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
              <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
                <h3 style={{ fontSize: '1.05rem', fontWeight: 800, color: 'white', margin: '0 0 16px' }}>Milestone Checkpoints</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                  {activeRoadmap.milestones.map((m, idx) => (
                    <div key={m.id} style={{ borderLeft: '3px solid #6C3BEB', paddingLeft: 12 }}>
                      <div style={{ fontSize: '0.78rem', color: '#6C3BEB', fontWeight: 700 }}>Milestone {idx + 1}</div>
                      <h4 style={{ fontSize: '0.9rem', color: 'white', margin: '2px 0 4px', fontWeight: 700 }}>{m.title}</h4>
                      <p style={{ fontSize: '0.8rem', color: 'var(--gray-400)', margin: 0, lineHeight: 1.4 }}>{m.description}</p>
                    </div>
                  ))}
                </div>
              </div>
            </div>

          </div>
        </div>
      )}

      {/* Generate Roadmap Modal */}
      {showModal && (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(4px)', zIndex: 9999, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 20 }}>
          <div style={{ background: '#12131A', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 20, padding: 32, width: '100%', maxWidth: 500, color: 'white' }}>
            <h2 style={{ fontSize: '1.3rem', fontWeight: 800, margin: '0 0 16px' }}>Configure Career Roadmap</h2>
            
            <form onSubmit={handleCreateRoadmap}>
              <div className="form-group" style={{ marginBottom: 16 }}>
                <label className="form-label">Target Career Goal</label>
                <input 
                  type="text" 
                  className="form-control" 
                  value={targetCareer} 
                  onChange={e => setTargetCareer(e.target.value)} 
                  required
                  style={{ width: '100%', background: 'rgba(0,0,0,0.3)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '10px 12px', borderRadius: 8 }}
                />
              </div>

              <div className="form-group" style={{ marginBottom: 16 }}>
                <label className="form-label">Target Company (Optional)</label>
                <input 
                  type="text" 
                  className="form-control" 
                  placeholder="e.g. Google, Microsoft, Amazon" 
                  value={targetCompany} 
                  onChange={e => setTargetCompany(e.target.value)}
                  style={{ width: '100%', background: 'rgba(0,0,0,0.3)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '10px 12px', borderRadius: 8 }}
                />
              </div>

              <div className="form-group" style={{ marginBottom: 16 }}>
                <label className="form-label">Plan Duration</label>
                <select 
                  className="form-control" 
                  value={duration} 
                  onChange={e => setDuration(e.target.value)}
                  style={{ width: '100%', background: 'rgba(0,0,0,0.3)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '10px 12px', borderRadius: 8 }}
                >
                  <option value="30-Day Plan">30-Day Plan</option>
                  <option value="60-Day Plan">60-Day Plan</option>
                  <option value="90-Day Plan">90-Day Plan</option>
                  <option value="6-Month Plan">6-Month Plan</option>
                  <option value="1-Year Plan">1-Year Plan</option>
                </select>
              </div>

              <div className="form-group" style={{ marginBottom: 24 }}>
                <label className="form-label">Available Weekly Study Hours ({weeklyHours} hrs/week)</label>
                <input 
                  type="range" 
                  min={5} 
                  max={40} 
                  value={weeklyHours} 
                  onChange={e => setWeeklyHours(Number(e.target.value))}
                  style={{ width: '100%', accentColor: '#6C3BEB' }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
                <button type="button" className="btn-ghost btn-sm" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn-primary btn-sm" disabled={generating}>
                  {generating ? 'Generating...' : 'Build Roadmap'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
};

export default RoadmapDashboardPage;
