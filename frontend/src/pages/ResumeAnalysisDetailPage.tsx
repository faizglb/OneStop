import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { resumeService } from '../services/resumeService';
import type { ResumeAnalysisResponse } from '../services/resumeService';

const ResumeAnalysisDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [analysis, setAnalysis] = useState<ResumeAnalysisResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'suggestions' | 'keywords' | 'sections'>('suggestions');

  useEffect(() => {
    const fetchDetails = async () => {
      if (!id) return;
      setLoading(true);
      try {
        const data = await resumeService.getAnalysisById(id);
        setAnalysis(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Failed to load resume analysis details.');
      } finally {
        setLoading(false);
      }
    };
    fetchDetails();
  }, [id]);

  if (loading) {
    return (
      <div className="page-container" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '60vh' }}>
        <div className="loading-spinner-large" />
        <p style={{ marginTop: 16, color: 'var(--gray-400)' }}>Compiling analysis reports...</p>
      </div>
    );
  }

  if (error || !analysis) {
    return (
      <div className="page-container" style={{ textAlign: 'center', padding: '48px 16px' }}>
        <h2 style={{ color: '#EF4444' }}>Error Loading Report</h2>
        <p style={{ color: 'var(--gray-400)', margin: '8px 0 24px' }}>{error || 'Unable to retrieve analysis details.'}</p>
        <button className="btn-primary" onClick={() => navigate('/resume-analyzer')}>
          Back to Analyzer
        </button>
      </div>
    );
  }

  // Color code based on scores
  const getScoreColor = (score: number) => {
    if (score >= 70) return '#10B981'; // Green
    if (score >= 40) return '#F59E0B'; // Orange
    return '#EF4444'; // Red
  };

  const getPriorityColor = (priority: 'HIGH' | 'MEDIUM' | 'LOW') => {
    switch (priority) {
      case 'HIGH': return '#EF4444';
      case 'MEDIUM': return '#F59E0B';
      case 'LOW': return '#3B82F6';
    }
  };

  const atsColor = getScoreColor(analysis.atsScore);

  return (
    <div className="page-container">
      {/* Detail Header */}
      <div className="history-header">
        <div>
          <button 
            onClick={() => navigate('/resume-analyzer')}
            style={{ 
              background: 'none', 
              border: 'none', 
              color: '#6C3BEB', 
              fontWeight: 700, 
              cursor: 'pointer', 
              fontSize: '0.88rem', 
              marginBottom: 10,
              padding: 0
            }}
          >
            ← Back to Analyzer
          </button>
          <h1 className="history-title">Resume Analysis Scorecard</h1>
          <p className="history-subtitle">
            File: <strong>{analysis.fileName}</strong> • Role Profile: <strong>{analysis.targetCareer}</strong>
          </p>
        </div>
        <div>
          <a 
            href={analysis.fileUrl} 
            target="_blank" 
            rel="noreferrer" 
            className="btn-ghost btn-sm"
            style={{ textDecoration: 'none' }}
          >
            Download Resume
          </a>
        </div>
      </div>

      {/* Main Grid */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 28 }}>
        
        {/* Score Summary Banner */}
        <div 
          className="info-instructions-card" 
          style={{ 
            background: 'rgba(255,255,255,0.01)', 
            border: '1px solid rgba(255,255,255,0.05)', 
            borderRadius: 24, 
            padding: 32,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: 24
          }}
        >
          {/* Circular Gauge */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
            <div style={{ position: 'relative', width: 100, height: 100 }}>
              <svg width="100" height="100" viewBox="0 0 100 100">
                <circle 
                  cx="50" cy="50" r="40" 
                  stroke="rgba(255,255,255,0.05)" 
                  strokeWidth="8" fill="transparent" 
                />
                <circle 
                  cx="50" cy="50" r="40" 
                  stroke={atsColor} 
                  strokeWidth="8" fill="transparent" 
                  strokeDasharray={`${2 * Math.PI * 40}`}
                  strokeDashoffset={`${2 * Math.PI * 40 * (1 - analysis.atsScore / 100)}`}
                  strokeLinecap="round"
                  transform="rotate(-90 50 50)"
                />
              </svg>
              <div 
                style={{ 
                  position: 'absolute', 
                  top: 0, left: 0, right: 0, bottom: 0, 
                  display: 'flex', flexDirection: 'column', 
                  alignItems: 'center', justifyContent: 'center' 
                }}
              >
                <span style={{ fontSize: '1.6rem', fontWeight: 800, color: 'white', lineHeight: 1 }}>
                  {analysis.atsScore.toFixed(0)}
                </span>
                <span style={{ fontSize: '0.62rem', color: 'var(--gray-500)', fontWeight: 700 }}>ATS</span>
              </div>
            </div>
            
            <div>
              <span className="take-type-badge" style={{ backgroundColor: 'rgba(108,59,235,0.1)', color: '#6C3BEB' }}>
                Overall Rating: {analysis.overallScore}%
              </span>
              <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: 'white', margin: '6px 0 2px' }}>
                {analysis.atsScore >= 70 ? 'Excellent Match!' : analysis.atsScore >= 40 ? 'Needs Improvement' : 'Critical Fixes Required'}
              </h2>
              <p style={{ fontSize: '0.85rem', color: 'var(--gray-400)', margin: 0 }}>
                Word Count: <strong>{analysis.wordCount}</strong> words • Pages: <strong>{analysis.pageCount}</strong>
              </p>
            </div>
          </div>

          {/* Metadata */}
          <div style={{ display: 'flex', gap: 32 }}>
            <div style={{ textAlign: 'center' }}>
              <span style={{ fontSize: '0.78rem', color: 'var(--gray-500)', display: 'block', marginBottom: 4 }}>Date Analyzed</span>
              <strong style={{ fontSize: '1rem', color: 'white' }}>
                {new Date(analysis.createdAt).toLocaleDateString('en-IN')}
              </strong>
            </div>
            <div style={{ textAlign: 'center' }}>
              <span style={{ fontSize: '0.78rem', color: 'var(--gray-500)', display: 'block', marginBottom: 4 }}>Document Format</span>
              <strong style={{ fontSize: '1rem', color: 'white' }}>{analysis.fileType}</strong>
            </div>
          </div>
        </div>

        {/* Score Breakdown Section */}
        <div className="result-section-card" style={{ padding: 24, borderRadius: 20 }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'white', marginBottom: 20 }}>Score Breakdown</h3>
          
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: 16 }}>
            {[
              { label: 'Keywords Matching', score: analysis.scoreBreakdown.keywordScore, desc: 'Relevance of skills to career profile' },
              { label: 'Section Completeness', score: analysis.scoreBreakdown.sectionScore, desc: 'Key sections identified by ATS' },
              { label: 'Document Formatting', score: analysis.scoreBreakdown.formattingScore, desc: 'Contact details and page structures' },
              { label: 'Experience Quality', score: analysis.scoreBreakdown.experienceScore, desc: 'Action verbs and impact metrics' },
              { label: 'Education & Credentials', score: analysis.scoreBreakdown.educationScore, desc: 'GPA and degree configurations' },
              { label: 'Grammar & Clarity', score: analysis.scoreBreakdown.grammarScore, desc: 'Sentence length and caps style checks' }
            ].map((item, index) => (
              <div 
                key={index} 
                style={{ 
                  background: 'rgba(255,255,255,0.01)', 
                  border: '1px solid rgba(255,255,255,0.03)', 
                  padding: 16, 
                  borderRadius: 14,
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between'
                }}
              >
                <div>
                  <h4 style={{ fontSize: '0.88rem', fontWeight: 700, color: 'var(--gray-300)', margin: '0 0 4px' }}>{item.label}</h4>
                  <p style={{ fontSize: '0.72rem', color: 'var(--gray-500)', margin: '0 0 16px' }}>{item.desc}</p>
                </div>
                <div>
                  <div style={{ fontSize: '1.5rem', fontWeight: 800, color: getScoreColor(item.score), marginBottom: 8 }}>
                    {item.score.toFixed(0)}<span style={{ fontSize: '0.9rem', color: 'var(--gray-500)' }}>/100</span>
                  </div>
                  <div style={{ height: 4, background: 'rgba(255,255,255,0.05)', borderRadius: 2 }}>
                    <div style={{ height: '100%', width: `${item.score}%`, background: getScoreColor(item.score), borderRadius: 2 }} />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Tabs for Details */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          
          <div style={{ display: 'flex', borderBottom: '1px solid rgba(255,255,255,0.08)', gap: 24 }}>
            {[
              { id: 'suggestions', label: `Suggestions (${analysis.suggestions.length})` },
              { id: 'keywords', label: `Keyword Match (${analysis.keywords.length})` },
              { id: 'sections', label: `Section Coverage (${analysis.sections.length})` }
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as any)}
                style={{
                  background: 'none',
                  border: 'none',
                  padding: '12px 4px',
                  color: activeTab === tab.id ? 'white' : 'var(--gray-500)',
                  fontWeight: 700,
                  fontSize: '0.95rem',
                  borderBottom: activeTab === tab.id ? '2px solid #6C3BEB' : '2px solid transparent',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease'
                }}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* Suggestions Tab */}
          {activeTab === 'suggestions' && (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              {analysis.suggestions.length === 0 ? (
                <div style={{ padding: 32, textAlign: 'center', background: 'rgba(255,255,255,0.01)', borderRadius: 16, color: 'var(--gray-500)' }}>
                  🎉 No improvements found. Your resume formatting and structure are optimal!
                </div>
              ) : (
                analysis.suggestions.map((sug, idx) => (
                  <div 
                    key={idx}
                    style={{
                      background: 'rgba(255,255,255,0.01)',
                      borderLeft: `4px solid ${getPriorityColor(sug.priority)}`,
                      padding: 20,
                      borderRadius: '0 14px 14px 0',
                      borderTop: '1px solid rgba(255,255,255,0.03)',
                      borderRight: '1px solid rgba(255,255,255,0.03)',
                      borderBottom: '1px solid rgba(255,255,255,0.03)'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 8, marginBottom: 8 }}>
                      <h4 style={{ fontSize: '1rem', fontWeight: 700, color: 'white', margin: 0 }}>{sug.title}</h4>
                      <span 
                        style={{ 
                          fontSize: '0.7rem', 
                          fontWeight: 800, 
                          color: getPriorityColor(sug.priority),
                          border: `1px solid ${getPriorityColor(sug.priority)}`,
                          padding: '2px 8px',
                          borderRadius: 4
                        }}
                      >
                        {sug.priority} PRIORITY
                      </span>
                    </div>
                    
                    <p style={{ fontSize: '0.85rem', color: 'var(--gray-400)', margin: '0 0 12px' }}>{sug.description}</p>
                    
                    {sug.example && (
                      <div style={{ background: 'rgba(255,255,255,0.02)', padding: 12, borderRadius: 8, border: '1px dashed rgba(255,255,255,0.06)' }}>
                        <span style={{ fontSize: '0.72rem', color: 'var(--gray-500)', display: 'block', marginBottom: 4, fontWeight: 700 }}>EXAMPLE / TIP</span>
                        <code style={{ fontSize: '0.82rem', color: '#a855f7', wordBreak: 'break-all' }}>{sug.example}</code>
                      </div>
                    )}
                  </div>
                ))
              )}
            </div>
          )}

          {/* Keywords Tab */}
          {activeTab === 'keywords' && (
            <div className="result-section-card" style={{ padding: 20, borderRadius: 16 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                <h4 style={{ fontSize: '0.95rem', fontWeight: 800, color: 'white', margin: 0 }}>ATS Keyword Analysis</h4>
                <span style={{ fontSize: '0.8rem', color: 'var(--gray-400)' }}>
                  Matches identified: <strong>{analysis.keywords.filter(k => k.found).length}</strong> of <strong>{analysis.keywords.length}</strong> checked
                </span>
              </div>

              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', minWidth: 500 }}>
                  <thead>
                    <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
                      <th style={{ padding: '12px 16px', fontSize: '0.82rem', color: 'var(--gray-500)', fontWeight: 800 }}>Keyword</th>
                      <th style={{ padding: '12px 16px', fontSize: '0.82rem', color: 'var(--gray-500)', fontWeight: 800 }}>Category</th>
                      <th style={{ padding: '12px 16px', fontSize: '0.82rem', color: 'var(--gray-500)', fontWeight: 800 }}>Importance</th>
                      <th style={{ padding: '12px 16px', fontSize: '0.82rem', color: 'var(--gray-500)', fontWeight: 800 }}>Frequency</th>
                      <th style={{ padding: '12px 16px', fontSize: '0.82rem', color: 'var(--gray-500)', fontWeight: 800 }}>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {analysis.keywords.map((kw, idx) => (
                      <tr 
                        key={idx} 
                        style={{ 
                          borderBottom: '1px solid rgba(255,255,255,0.03)',
                          background: kw.found ? 'rgba(16, 185, 129, 0.01)' : 'rgba(239, 68, 68, 0.01)'
                        }}
                      >
                        <td style={{ padding: '12px 16px', fontSize: '0.88rem', fontWeight: 700, color: 'white' }}>{kw.keyword}</td>
                        <td style={{ padding: '12px 16px', fontSize: '0.82rem', color: 'var(--gray-400)' }}>{kw.category}</td>
                        <td style={{ padding: '12px 16px', fontSize: '0.82rem', fontWeight: 700, color: getPriorityColor(kw.importance) }}>{kw.importance}</td>
                        <td style={{ padding: '12px 16px', fontSize: '0.88rem', color: 'white' }}>{kw.frequency}</td>
                        <td style={{ padding: '12px 16px' }}>
                          <span 
                            style={{ 
                              fontSize: '0.75rem', 
                              fontWeight: 800,
                              color: kw.found ? '#10B981' : '#EF4444',
                              background: kw.found ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)',
                              padding: '2px 8px',
                              borderRadius: 4
                            }}
                          >
                            {kw.found ? 'FOUND' : 'MISSING'}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Sections Tab */}
          {activeTab === 'sections' && (
            <div className="result-section-card" style={{ padding: 24, borderRadius: 16 }}>
              <h4 style={{ fontSize: '0.95rem', fontWeight: 800, color: 'white', marginBottom: 20 }}>Section Detection Overview</h4>
              
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {analysis.sections.map((sec, idx) => (
                  <div 
                    key={idx}
                    style={{
                      background: 'rgba(255,255,255,0.01)',
                      border: '1px solid rgba(255,255,255,0.03)',
                      padding: 16,
                      borderRadius: 12,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      flexWrap: 'wrap',
                      gap: 12
                    }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                        <span style={{ color: sec.detected ? '#10B981' : '#EF4444', fontSize: '1.2rem', fontWeight: 800 }}>
                          {sec.detected ? '✓' : '✗'}
                        </span>
                        <strong style={{ color: 'white', fontSize: '0.92rem' }}>{sec.sectionName}</strong>
                      </div>
                      {sec.contentSnippet && (
                        <p style={{ fontSize: '0.78rem', color: 'var(--gray-500)', margin: '4px 0 0 24px', maxWidth: '75vw', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                          Snippet: &quot;{sec.contentSnippet}&quot;
                        </p>
                      )}
                    </div>

                    <div style={{ display: 'flex', gap: 10 }}>
                      <span 
                        style={{ 
                          fontSize: '0.72rem', 
                          fontWeight: 700, 
                          color: sec.detected ? 'white' : 'var(--gray-500)',
                          background: sec.detected ? 'rgba(255,255,255,0.05)' : 'transparent',
                          padding: '2px 8px',
                          borderRadius: 4
                        }}
                      >
                        Quality: {sec.quality}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

        </div>

      </div>
    </div>
  );
};

export default ResumeAnalysisDetailPage;
