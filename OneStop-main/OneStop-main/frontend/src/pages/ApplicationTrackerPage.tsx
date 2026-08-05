import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getApplicationHistory, updateApplicationStatus } from '../services/scholarshipService';
import type { ScholarshipApplicationHistoryResponse } from '../types/scholarship';

const ApplicationTrackerPage: React.FC = () => {
  const navigate = useNavigate();
  const [applications, setApplications] = useState<ScholarshipApplicationHistoryResponse[]>([]);
  const [loading, setLoading] = useState(true);

  // Edit states
  const [selectedApp, setSelectedApp] = useState<ScholarshipApplicationHistoryResponse | null>(null);
  const [newStatus, setNewStatus] = useState('');
  const [notes, setNotes] = useState('');
  const [reminderDate, setReminderDate] = useState('');
  const [saving, setSaving] = useState(false);

  const fetchApplications = async () => {
    try {
      const data = await getApplicationHistory();
      setApplications(data);
    } catch {
      // error handled
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchApplications();
  }, []);

  const handleEditClick = (app: ScholarshipApplicationHistoryResponse) => {
    setSelectedApp(app);
    setNewStatus(app.status);
    setNotes(app.notes || '');
    setReminderDate(app.reminderDate ? new Date(app.reminderDate).toISOString().substring(0, 16) : '');
  };

  const handleSaveChanges = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedApp) return;
    setSaving(true);
    try {
      const updated = await updateApplicationStatus({
        scholarshipId: selectedApp.scholarshipId,
        status: newStatus,
        notes: notes || undefined,
        reminderDate: reminderDate ? new Date(reminderDate).toISOString() : undefined
      });

      setApplications(prev => prev.map(a => a.scholarshipId === selectedApp.scholarshipId ? updated : a));
      setSelectedApp(null);
    } catch {
      alert('Failed to update application tracker.');
    } finally {
      setSaving(false);
    }
  };

  // Group columns
  const columns = [
    { key: 'BOOKMARKED', title: 'Shortlisted' },
    { key: 'APPLIED', title: 'Applied' },
    { key: 'SHORTLISTED', title: 'Under Review' },
    { key: 'COMPLETED', title: 'Awarded' }
  ];

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header" style={{ marginBottom: 32 }}>
        <div>
          <button className="back-btn" onClick={() => navigate('/scholarships')} style={{ marginBottom: 12 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            Scholarship Finder
          </button>
          <h1 className="history-title">Application Tracker</h1>
          <p className="history-subtitle">Track your application phases, document uploads, and set alerts for deadlines</p>
        </div>
      </div>

      {loading ? (
        <div className="take-page-loading">
          <div className="loading-spinner-large" />
          <p>Loading tracking board...</p>
        </div>
      ) : (
        /* Columns layout */
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 20, alignItems: 'flex-start' }}>
          {columns.map(col => {
            const colApps = applications.filter(a => a.status === col.key);
            return (
              <div 
                key={col.key} 
                style={{ 
                  background: 'rgba(255,255,255,0.02)', 
                  border: '1px solid rgba(255,255,255,0.06)', 
                  borderRadius: 16, 
                  padding: 16,
                  minHeight: 400 
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                  <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'white', margin: 0 }}>{col.title}</h3>
                  <span style={{ fontSize: '0.8rem', background: 'rgba(255,255,255,0.05)', color: 'var(--gray-400)', padding: '2px 8px', borderRadius: 10 }}>
                    {colApps.length}
                  </span>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                  {colApps.map(a => (
                    <div 
                      key={a.id} 
                      onClick={() => handleEditClick(a)}
                      style={{ 
                        background: 'rgba(255,255,255,0.03)', 
                        border: '1px solid rgba(255,255,255,0.06)', 
                        padding: 14, 
                        borderRadius: 12,
                        cursor: 'pointer',
                        transition: 'transform 0.15s ease'
                      }}
                      onMouseEnter={e => e.currentTarget.style.transform = 'translateY(-2px)'}
                      onMouseLeave={e => e.currentTarget.style.transform = 'none'}
                    >
                      <h4 style={{ fontSize: '0.88rem', fontWeight: 700, color: 'white', margin: '0 0 4px' }}>{a.scholarshipName}</h4>
                      <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)' }}>Provider: {a.provider}</div>
                      
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: 12, fontSize: '0.78rem' }}>
                        <span style={{ color: '#22c55e', fontWeight: 600 }}>₹{(a.amount/1000).toFixed(0)}k</span>
                        <span style={{ color: 'var(--gray-400)' }}>📅 {new Date(a.lastDate).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' })}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Edit Tracker Dialog Modal */}
      {selectedApp && (
        <div className="dialog-overlay" onClick={() => setSelectedApp(null)}>
          <form 
            onSubmit={handleSaveChanges}
            className="dialog-box" 
            style={{ maxWidth: 450, textAlign: 'left' }}
            onClick={e => e.stopPropagation()}
          >
            <h2 className="dialog-title" style={{ marginBottom: 4 }}>Manage Application Status</h2>
            <p style={{ fontSize: '0.85rem', color: 'var(--gray-500)', marginBottom: 20 }}>{selectedApp.scholarshipName}</p>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Tracking Stage</label>
              <select 
                className="form-control" 
                value={newStatus} 
                onChange={e => setNewStatus(e.target.value)}
                style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
              >
                <option value="BOOKMARKED">Shortlisted</option>
                <option value="APPLIED">Applied</option>
                <option value="SHORTLISTED">Under Review</option>
                <option value="COMPLETED">Awarded</option>
              </select>
            </div>

            <div className="form-group" style={{ marginBottom: 14 }}>
              <label className="form-label">Set Deadline Alert Reminder</label>
              <input 
                type="datetime-local" 
                className="form-control" 
                value={reminderDate} 
                onChange={e => setReminderDate(e.target.value)}
                style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8 }}
              />
            </div>

            <div className="form-group" style={{ marginBottom: 20 }}>
              <label className="form-label">Notes & Status Remarks</label>
              <textarea 
                className="form-control" 
                rows={3}
                value={notes}
                onChange={e => setNotes(e.target.value)}
                placeholder="E.g. Submitted income certificate, awaiting verification..."
                style={{ width: '100%', background: 'rgba(0,0,0,0.2)', border: '1px solid rgba(255,255,255,0.1)', color: 'white', padding: '8px 10px', borderRadius: 8, resize: 'none' }}
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 12 }}>
              <button type="button" className="btn-ghost" onClick={() => setSelectedApp(null)}>Cancel</button>
              <button type="submit" className="btn-primary" disabled={saving}>
                {saving ? 'Saving...' : 'Update Status'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};

export default ApplicationTrackerPage;
