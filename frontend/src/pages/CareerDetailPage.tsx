import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getRecommendations } from '../services/recommendationService';
import type { RecommendationResponse } from '../types/recommendation';

const CareerDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [career, setCareer] = useState<RecommendationResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const data = await getRecommendations();
        const found = data.find(item => item.id === id);
        setCareer(found || null);
      } catch {
        // error handled
      } finally {
        setLoading(false);
      }
    };
    fetchDetails();
  }, [id]);

  if (loading) {
    return (
      <div className="take-page-loading">
        <div className="loading-spinner-large" />
        <p>Loading career map details...</p>
      </div>
    );
  }

  if (!career) {
    return (
      <div className="page-container">
        <div className="error-state">
          <h2>Career Role Details Not Found</h2>
          <button className="btn-primary" onClick={() => navigate('/recommendations')}>Back to Dashboard</button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      {/* Back link */}
      <button className="back-btn" onClick={() => navigate('/recommendations')}>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
        </svg>
        Back to Dashboard
      </button>

      {/* Header Info */}
      <div className="result-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 16 }}>
        <div>
          <div className="result-type-badge">{career.jobDemand} Demand</div>
          <h1 className="result-title" style={{ fontSize: '2.25rem', fontWeight: 800 }}>{career.careerName}</h1>
          <p className="result-date">Prerequisite degree: {career.requiredDegree}</p>
        </div>
        <div style={{ background: 'rgba(255,255,255,0.04)', border: '1px solid rgba(255,255,255,0.08)', padding: '16px 24px', borderRadius: 16, display: 'flex', gap: 24, textAlign: 'center' }}>
          <div>
            <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', textTransform: 'uppercase', letterSpacing: 0.5 }}>Match Score</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#6C3BEB' }}>{Math.round(career.matchScore)}%</div>
          </div>
          <div style={{ width: 1, background: 'rgba(255,255,255,0.1)' }} />
          <div>
            <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', textTransform: 'uppercase', letterSpacing: 0.5 }}>Confidence</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#22c55e' }}>{Math.round(career.confidenceScore)}%</div>
          </div>
        </div>
      </div>

      {/* Grid body */}
      <div className="info-page-layout" style={{ display: 'grid', gridTemplateColumns: '1fr 360px', gap: 32, marginTop: 24 }}>
        
        {/* Left main info */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {/* Summary / Description */}
          <div className="info-instructions-card" style={{ background: 'rgba(255,255,255,0.02)', padding: 28, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'white', margin: '0 0 12px' }}>Role Overview</h3>
            <p style={{ fontSize: '0.95rem', color: 'var(--gray-300)', lineHeight: 1.7, margin: 0 }}>{career.careerDescription}</p>
          </div>

          {/* Why Recommended explanation */}
          <div className="info-instructions-card" style={{ borderLeft: '4px solid #6C3BEB', background: 'rgba(108,59,235,0.04)', padding: 20, borderRadius: 12 }}>
            <h4 style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '0.95rem', fontWeight: 700, color: '#a855f7', margin: '0 0 8px' }}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>
              </svg>
              Why We Recommend This
            </h4>
            <p style={{ fontSize: '0.9rem', color: 'var(--gray-300)', margin: 0, lineHeight: 1.6 }}>{career.reason}</p>
          </div>

          {/* Pros & Challenges list */}
          <div className="result-sw-grid">
            <div className="sw-card strengths-card" style={{ padding: 24, borderRadius: 16 }}>
              <h3 style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '1rem', fontWeight: 700, color: '#22c55e', margin: '0 0 16px' }}>Pros / Advantages</h3>
              <ul style={{ padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: 10 }}>
                {career.pros.map((pro, idx) => (
                  <li key={idx} style={{ display: 'flex', gap: 8, fontSize: '0.9rem', color: 'var(--gray-300)', listStyle: 'none' }}>
                    <span style={{ color: '#22c55e' }}>✓</span> {pro}
                  </li>
                ))}
              </ul>
            </div>
            <div className="sw-card weaknesses-card" style={{ padding: 24, borderRadius: 16 }}>
              <h3 style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: '1rem', fontWeight: 700, color: '#f59e0b', margin: '0 0 16px' }}>Challenges</h3>
              <ul style={{ padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: 10 }}>
                {career.challenges.map((challenge, idx) => (
                  <li key={idx} style={{ display: 'flex', gap: 8, fontSize: '0.9rem', color: 'var(--gray-300)', listStyle: 'none' }}>
                    <span style={{ color: '#f59e0b' }}>⚠</span> {challenge}
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* Recommended Skills Roadmap */}
          <div className="result-section-card" style={{ padding: 28, borderRadius: 20 }}>
            <h2 className="section-heading">Skills Gap & Target Skills</h2>
            <p style={{ fontSize: '0.875rem', color: 'var(--gray-500)', marginBottom: 20 }}>Skills you need to learn or strengthen to meet standard career roles expectations.</p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              {career.requiredSkills.map(skill => (
                <div key={skill.id} style={{ display: 'grid', gridTemplateColumns: '1fr 120px 80px', alignItems: 'center', gap: 16, borderBottom: '1px solid rgba(255,255,255,0.04)', paddingBottom: 12 }}>
                  <div>
                    <div style={{ fontWeight: 600, color: 'white', fontSize: '0.95rem' }}>{skill.skillName}</div>
                  </div>
                  <div>
                    <span 
                      style={{ 
                        fontSize: '0.72rem', 
                        fontWeight: 700, 
                        padding: '2px 8px', 
                        borderRadius: 6,
                        backgroundColor: skill.priority === 'HIGH' ? 'rgba(239,68,68,0.1)' : 'rgba(245,158,11,0.1)',
                        color: skill.priority === 'HIGH' ? '#fca5a5' : '#fde047',
                        border: '1px solid'
                      }}
                    >
                      {skill.priority} Priority
                    </span>
                  </div>
                  <div style={{ textAlign: 'right', fontSize: '0.9rem', fontWeight: 700, color: '#6C3BEB' }}>{skill.confidence}%</div>
                </div>
              ))}
            </div>
          </div>

          {/* Projects & Certs List */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: 24 }}>
            <div className="result-section-card" style={{ padding: 28, borderRadius: 20 }}>
              <h2 className="section-heading">Recommended Projects</h2>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                {career.suggestedProjects.map(proj => (
                  <div key={proj.id} style={{ background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.06)', padding: 18, borderRadius: 12 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 }}>
                      <h4 style={{ margin: 0, fontWeight: 700, color: 'white' }}>{proj.projectTitle}</h4>
                      <span style={{ fontSize: '0.7rem', padding: '2px 8px', borderRadius: 4, background: 'rgba(255,255,255,0.08)', color: 'var(--gray-300)' }}>{proj.difficulty}</span>
                    </div>
                    <p style={{ fontSize: '0.85rem', color: 'var(--gray-400)', margin: '0 0 12px' }}>{proj.projectDescription}</p>
                    <div style={{ fontSize: '0.8rem', color: 'var(--gray-500)', display: 'flex', alignItems: 'center', gap: 6 }}>
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                      </svg>
                      Est. Duration: {proj.estimatedDuration}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Right Sidebar Quick stats */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'white', margin: '0 0 20px' }}>Market Parameters</h3>
            
            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ color: '#22c55e' }}>
                <line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
              </svg>
              <div>
                <div className="summary-label">Expected Salary</div>
                <div className="summary-value" style={{ color: 'white' }}>{career.expectedSalary}</div>
              </div>
            </div>

            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ color: '#6C3BEB' }}>
                <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/>
              </svg>
              <div>
                <div className="summary-label">Market Growth</div>
                <div className="summary-value" style={{ color: 'white' }}>{career.industryGrowth}</div>
              </div>
            </div>

            <div className="summary-item" style={{ display: 'flex', gap: 12, marginBottom: 16 }}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ color: '#f59e0b' }}>
                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
              </svg>
              <div>
                <div className="summary-label">Est. Skill Preparation</div>
                <div className="summary-value" style={{ color: 'white' }}>{career.estimatedLearningTime}</div>
              </div>
            </div>

            <div style={{ borderTop: '1px solid rgba(255,255,255,0.06)', paddingTop: 16, marginTop: 16 }}>
              <div className="summary-label" style={{ marginBottom: 10 }}>Future Outlook</div>
              <p style={{ fontSize: '0.85rem', color: 'var(--gray-300)', margin: 0, lineHeight: 1.5 }}>{career.futureScope}</p>
            </div>
          </div>

          <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'white', margin: '0 0 16px' }}>Target Certifications</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {career.recommendedCertifications.map(cert => (
                <div key={cert.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.04)', paddingBottom: 10 }}>
                  <div style={{ fontSize: '0.85rem', fontWeight: 700, color: 'white', lineHeight: 1.4 }}>{cert.certificationName}</div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.78rem', color: 'var(--gray-500)', marginTop: 4 }}>
                    <span>By {cert.provider}</span>
                    <span style={{ color: '#22c55e', fontWeight: 700 }}>{Math.round(cert.confidence)}% match</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

      </div>
    </div>
  );
};

export default CareerDetailPage;
