import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { resumeService } from '../services/resumeService';
import { profileService } from '../services/profileService';
import type { ResumeAnalysisSummaryResponse, ResumeAnalysisResponse } from '../services/resumeService';

const ResumeAnalyzerPage: React.FC = () => {
  const navigate = useNavigate();
  const [history, setHistory] = useState<ResumeAnalysisSummaryResponse[]>([]);
  const [loadingHistory, setLoadingHistory] = useState(true);
  
  // File Upload State
  const [file, setFile] = useState<File | null>(null);
  const [targetCareer, setTargetCareer] = useState('Software Engineer');
  const [analyzing, setAnalyzing] = useState(false);
  const [analysisStep, setAnalysisStep] = useState(0); // 0: Idle, 1: Uploading, 2: Parsing, 3: Scoring, 4: Suggesting
  const [uploadError, setUploadError] = useState<string | null>(null);

  // Available Careers for Selection
  const careersList = [
    'Software Engineer',
    'Data Scientist',
    'UX Designer',
    'Product Manager',
    'DevOps Engineer',
    'Cybersecurity Analyst',
    'Cloud Architect',
    'Systems Analyst'
  ];

  const fetchHistory = async () => {
    setLoadingHistory(true);
    try {
      const data = await resumeService.getAnalysisHistory(0, 15);
      setHistory(data.content || []);
    } catch (err) {
      console.error('Failed to load analysis history', err);
    } finally {
      setLoadingHistory(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setUploadError(null);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      validateAndSetFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUploadError(null);
    if (e.target.files && e.target.files[0]) {
      validateAndSetFile(e.target.files[0]);
    }
  };

  const validateAndSetFile = (selectedFile: File) => {
    const validExtensions = ['application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
    const maxSize = 10 * 1024 * 1024; // 10MB

    if (!validExtensions.includes(selectedFile.type)) {
      setUploadError('Unsupported file type. Please upload a PDF or DOCX file.');
      return;
    }
    if (selectedFile.size > maxSize) {
      setUploadError('File is too large. Maximum supported size is 10MB.');
      return;
    }
    setFile(selectedFile);
  };

  const handleAnalyze = async () => {
    if (!file) return;

    setAnalyzing(true);
    setUploadError(null);
    
    // Step 1: Uploading to Cloudinary
    setAnalysisStep(1);
    try {
      let fileUrl = '';
      if (file.type === 'application/pdf') {
        const uploadRes = await profileService.uploadResume(file);
        fileUrl = uploadRes.url;
      } else {
        // DOCX is uploaded via same Cloudinary resume endpoint
        const uploadRes = await profileService.uploadResume(file);
        fileUrl = uploadRes.url;
      }

      if (!fileUrl) {
        throw new Error('Failed to obtain file URL from storage provider.');
      }

      // Step 2: Extracting Text
      setAnalysisStep(2);
      await new Promise(resolve => setTimeout(resolve, 1500)); // Dynamic feel

      // Step 3: Running AI Scorer
      setAnalysisStep(3);
      await new Promise(resolve => setTimeout(resolve, 1000));

      const fileType = file.name.toLowerCase().endsWith('.docx') ? 'DOCX' : 'PDF';
      
      const analysisResult = await resumeService.analyzeResume({
        fileUrl,
        fileName: file.name,
        fileType,
        targetCareer
      });

      // Step 4: Generating Report
      setAnalysisStep(4);
      await new Promise(resolve => setTimeout(resolve, 800));

      // Navigate to detail view
      navigate(`/resume-analyzer/${analysisResult.id}`);

    } catch (err: any) {
      console.error(err);
      setUploadError(err.response?.data?.message || err.message || 'An error occurred during resume analysis.');
      setAnalyzing(false);
      setAnalysisStep(0);
    }
  };

  const handleDelete = async (e: React.MouseEvent, id: string) => {
    e.stopPropagation();
    if (!window.confirm('Are you sure you want to delete this analysis record?')) return;
    try {
      await resumeService.deleteAnalysis(id);
      fetchHistory();
    } catch (err) {
      alert('Failed to delete history record.');
    }
  };

  return (
    <div className="page-container">
      {/* Page Header */}
      <div className="history-header">
        <div>
          <h1 className="history-title">AI Resume Analyzer</h1>
          <p className="history-subtitle">Benchmark your resume against job profiles, optimize keywords, and boost your ATS score</p>
        </div>
      </div>

      <div className="info-page-layout" style={{ gridTemplateColumns: '1fr 360px', gap: 32 }}>
        
        {/* Left Side: Drag and Drop & Progress Area */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 28 }}>
          
          {!analyzing ? (
            <div className="result-section-card" style={{ padding: 36, borderRadius: 24 }}>
              <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: 'white', marginBottom: 24 }}>Upload Resume</h2>
              
              {/* Target Career Dropdown */}
              <div style={{ marginBottom: 28 }}>
                <label style={{ display: 'block', fontSize: '0.88rem', fontWeight: 600, color: 'var(--gray-300)', marginBottom: 8 }}>
                  Target Career Role
                </label>
                <select 
                  value={targetCareer} 
                  onChange={(e) => setTargetCareer(e.target.value)}
                  className="form-input"
                  style={{ width: '100%', height: 46, borderRadius: 10, background: 'rgba(255,255,255,0.03)', color: 'white', border: '1px solid rgba(255,255,255,0.1)' }}
                >
                  {careersList.map(career => (
                    <option key={career} value={career} style={{ background: '#11131c' }}>{career}</option>
                  ))}
                </select>
                <p style={{ fontSize: '0.78rem', color: 'var(--gray-500)', marginTop: 6 }}>
                  Our AI compares your resume text with skills required for this specific career path.
                </p>
              </div>

              {/* Drag Drop Box */}
              <div
                onDragOver={handleDragOver}
                onDrop={handleDrop}
                style={{
                  border: file ? '2px solid #6C3BEB' : '2px dashed rgba(255,255,255,0.1)',
                  borderRadius: 18,
                  padding: '48px 24px',
                  textAlign: 'center',
                  background: 'rgba(255,255,255,0.01)',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                  position: 'relative'
                }}
                onClick={() => document.getElementById('resume-file-input')?.click()}
              >
                <input
                  type="file"
                  id="resume-file-input"
                  style={{ display: 'none' }}
                  accept=".pdf,.docx"
                  onChange={handleFileChange}
                />
                
                <div style={{ fontSize: '3rem', marginBottom: 16 }}>📄</div>
                {file ? (
                  <div>
                    <h4 style={{ color: 'white', fontWeight: 700, margin: '0 0 4px' }}>{file.name}</h4>
                    <p style={{ color: 'var(--gray-400)', fontSize: '0.85rem', margin: 0 }}>
                      {(file.size / (1024 * 1024)).toFixed(2)} MB • {file.name.split('.').pop()?.toUpperCase()}
                    </p>
                    <span 
                      style={{ 
                        display: 'inline-block', 
                        marginTop: 14, 
                        fontSize: '0.78rem', 
                        color: '#a855f7', 
                        fontWeight: 600,
                        textDecoration: 'underline'
                      }}
                    >
                      Change file
                    </span>
                  </div>
                ) : (
                  <div>
                    <h4 style={{ color: 'white', fontWeight: 700, margin: '0 0 6px' }}>Drag & Drop your resume here</h4>
                    <p style={{ color: 'var(--gray-400)', fontSize: '0.85rem', margin: '0 0 16px' }}>
                      Supports PDF and DOCX files up to 10MB
                    </p>
                    <button className="btn-ghost btn-sm" type="button" style={{ pointerEvents: 'none' }}>
                      Browse Files
                    </button>
                  </div>
                )}
              </div>

              {uploadError && (
                <div 
                  className="alert alert-error" 
                  style={{ 
                    marginTop: 20, 
                    padding: 16, 
                    borderRadius: 12, 
                    background: 'rgba(239, 68, 68, 0.1)', 
                    border: '1px solid rgba(239, 68, 68, 0.2)', 
                    color: '#f87171',
                    fontSize: '0.85rem'
                  }}
                >
                  ⚠️ {uploadError}
                </div>
              )}

              {/* Action Buttons */}
              <div style={{ marginTop: 32, display: 'flex', justifyContent: 'flex-end' }}>
                <button
                  onClick={handleAnalyze}
                  disabled={!file}
                  className="btn-primary"
                  style={{ padding: '12px 36px', borderRadius: 10, fontWeight: 700 }}
                >
                  Analyze Resume
                </button>
              </div>

            </div>
          ) : (
            /* Analyzing/Progress Screen */
            <div className="result-section-card" style={{ padding: '48px 36px', borderRadius: 24, textAlign: 'center' }}>
              <div className="loading-spinner-large" style={{ margin: '0 auto 28px' }} />
              
              <h2 style={{ fontSize: '1.4rem', color: 'white', fontWeight: 800, margin: '0 0 8px' }}>
                Analyzing Your Resume
              </h2>
              <p style={{ color: 'var(--gray-400)', fontSize: '0.88rem', maxWidth: 440, margin: '0 auto 36px' }}>
                Please wait while our AI engine scans text, evaluates sections, and matches keywords for <strong>{targetCareer}</strong>.
              </p>

              {/* Step Checklist Indicator */}
              <div 
                style={{ 
                  display: 'flex', 
                  flexDirection: 'column', 
                  gap: 16, 
                  maxWidth: 320, 
                  margin: '0 auto', 
                  textAlign: 'left',
                  background: 'rgba(255,255,255,0.02)',
                  border: '1px solid rgba(255,255,255,0.05)',
                  padding: 24,
                  borderRadius: 16
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 12, opacity: analysisStep >= 1 ? 1 : 0.4 }}>
                  <span style={{ color: analysisStep > 1 ? '#10B981' : '#6C3BEB', fontWeight: 800 }}>
                    {analysisStep > 1 ? '✓' : '●'}
                  </span>
                  <span style={{ fontSize: '0.9rem', color: 'white', fontWeight: analysisStep === 1 ? 700 : 500 }}>
                    Uploading document to Cloud Storage
                  </span>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: 12, opacity: analysisStep >= 2 ? 1 : 0.4 }}>
                  <span style={{ color: analysisStep > 2 ? '#10B981' : '#6C3BEB', fontWeight: 800 }}>
                    {analysisStep > 2 ? '✓' : '●'}
                  </span>
                  <span style={{ fontSize: '0.9rem', color: 'white', fontWeight: analysisStep === 2 ? 700 : 500 }}>
                    Parsing text and layout structures
                  </span>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: 12, opacity: analysisStep >= 3 ? 1 : 0.4 }}>
                  <span style={{ color: analysisStep > 3 ? '#10B981' : '#6C3BEB', fontWeight: 800 }}>
                    {analysisStep > 3 ? '✓' : '●'}
                  </span>
                  <span style={{ fontSize: '0.9rem', color: 'white', fontWeight: analysisStep === 3 ? 700 : 500 }}>
                    Matching keywords & role alignment
                  </span>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: 12, opacity: analysisStep >= 4 ? 1 : 0.4 }}>
                  <span style={{ color: analysisStep > 4 ? '#10B981' : '#6C3BEB', fontWeight: 800 }}>
                    {analysisStep > 4 ? '✓' : '●'}
                  </span>
                  <span style={{ fontSize: '0.9rem', color: 'white', fontWeight: analysisStep === 4 ? 700 : 500 }}>
                    Compiling scorecard scorecard
                  </span>
                </div>
              </div>
            </div>
          )}

        </div>

        {/* Right Side: Analysis History Sidebar */}
        <div className="result-section-card" style={{ padding: 24, borderRadius: 20, height: 'fit-content' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'white', marginBottom: 16 }}>Scan History</h3>
          
          {loadingHistory ? (
            <div style={{ padding: '24px 0', textAlign: 'center' }}>
              <div className="loading-spinner-large" style={{ width: 30, height: 30 }} />
            </div>
          ) : history.length === 0 ? (
            <div style={{ padding: '32px 16px', textAlign: 'center', color: 'var(--gray-500)' }}>
              <p style={{ fontSize: '0.85rem', margin: 0 }}>No past analyses found.</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {history.map((item) => (
                <div
                  key={item.id}
                  onClick={() => navigate(`/resume-analyzer/${item.id}`)}
                  style={{
                    background: 'rgba(255,255,255,0.02)',
                    border: '1px solid rgba(255,255,255,0.05)',
                    padding: 14,
                    borderRadius: 12,
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                    position: 'relative'
                  }}
                  className="history-item-hover"
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 6 }}>
                    <span 
                      style={{ 
                        fontSize: '0.82rem', 
                        fontWeight: 700, 
                        color: 'white', 
                        whiteSpace: 'nowrap', 
                        overflow: 'hidden', 
                        textOverflow: 'ellipsis',
                        maxWidth: '75%'
                      }}
                    >
                      {item.fileName}
                    </span>
                    <button 
                      onClick={(e) => handleDelete(e, item.id)}
                      style={{ 
                        background: 'none', 
                        border: 'none', 
                        color: 'var(--gray-500)', 
                        cursor: 'pointer', 
                        fontSize: '0.8rem',
                        padding: 0
                      }}
                      className="trash-btn"
                    >
                      🗑️
                    </button>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '0.72rem', color: 'var(--gray-500)' }}>
                      {new Date(item.createdAt).toLocaleDateString('en-IN')}
                    </span>
                    <span 
                      style={{ 
                        fontSize: '0.78rem', 
                        fontWeight: 800, 
                        color: item.atsScore >= 70 ? '#10B981' : item.atsScore >= 40 ? '#F59E0B' : '#EF4444' 
                      }}
                    >
                      ATS: {item.atsScore}%
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  );
};

export default ResumeAnalyzerPage;
