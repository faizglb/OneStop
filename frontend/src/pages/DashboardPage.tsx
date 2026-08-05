import React, { useState, useEffect, useRef, useCallback } from 'react';
import toast from 'react-hot-toast';
import { profileService, StudentProfileRequest } from '@/services/profileService';
import { clearAuthStorage } from '@/services/api';
import { useNavigate } from 'react-router-dom';

export default function DashboardPage() {
  const navigate = useNavigate();

  // ── States ───────────────────────────────────────────────────────────────
  const [profile, setProfile] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [showWizard, setShowWizard] = useState(false);
  const [wizardStep, setWizardStep] = useState(1);
  const [activeTab, setActiveTab] = useState('overview');

  // Sub-resource modals/forms states
  const [showSkillForm, setShowSkillForm] = useState(false);
  const [skillForm, setSkillForm] = useState({ skillName: '', skillLevel: 'BEGINNER', experienceMonths: 0 });

  const [showInterestForm, setShowInterestForm] = useState(false);
  const [interestForm, setInterestForm] = useState({ interestName: '' });

  const [showGoalForm, setShowGoalForm] = useState(false);
  const [goalForm, setGoalForm] = useState({ goalTitle: '', goalDescription: '', targetYear: 2027, priority: 'MEDIUM' });

  const [showProjectForm, setShowProjectForm] = useState(false);
  const [projectForm, setProjectForm] = useState({ projectTitle: '', description: '', techStack: '', githubLink: '', liveLink: '' });

  const [showCertificateForm, setShowCertificateForm] = useState(false);
  const [certificateForm, setCertificateForm] = useState({ certificateName: '', platform: '', issueDate: '', certificateUrl: '' });

  // Wizard/Profile Edit fields
  const [profileForm, setProfileForm] = useState<StudentProfileRequest>({
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    gender: 'MALE',
    phoneNumber: '',
    profilePhoto: '',
    country: 'India',
    state: '',
    district: '',
    city: '',
    address: '',
    pinCode: '',
    schoolName: '',
    collegeName: '',
    currentCourse: '',
    branch: '',
    currentYear: 1,
    educationLevel: 'UNDERGRADUATE',
    tenthPercentage: 0,
    twelfthPercentage: 0,
    graduationCgpa: 0,
    currentCgpa: 0,
    preferredStudyMode: 'OFFLINE',
    familyIncome: 0,
    category: 'GENERAL',
    caste: '',
    religion: '',
    disabilityStatus: 'NONE',
    languagesKnown: [],
    careerGoal: '',
    bio: '',
    linkedinUrl: '',
    githubUrl: '',
    portfolioUrl: '',
    resumeUrl: '',
  });

  const [langInput, setLangInput] = useState('');

  // Drag & drop state
  const [dragActive, setDragActive] = useState({ photo: false, resume: false, cert: false });

  // File input refs
  const photoInputRef = useRef<HTMLInputElement>(null);
  const resumeInputRef = useRef<HTMLInputElement>(null);
  const resumeTabInputRef = useRef<HTMLInputElement>(null);
  const certInputRef = useRef<HTMLInputElement>(null);

  // ── Fetch Profile on Mount ───────────────────────────────────────────────
  const fetchProfile = async () => {
    try {
      setLoading(true);
      const data = await profileService.getProfile();
      setProfile(data);
      setProfileForm({
        ...data,
        languagesKnown: data.languagesKnown || [],
      });
      setShowWizard(false);
    } catch (err: any) {
      if (err.response?.status === 404) {
        setShowWizard(true);
        // Load draft from localstorage if available
        const draft = localStorage.getItem('acea_profile_draft');
        if (draft) {
          try {
            setProfileForm(JSON.parse(draft));
            toast.success('Restored draft profile changes');
          } catch (_) {}
        }
      } else {
        toast.error('Failed to load profile details');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  // ── Autosave Draft to LocalStorage ────────────────────────────────────────
  useEffect(() => {
    if (showWizard) {
      localStorage.setItem('acea_profile_draft', JSON.stringify(profileForm));
    }
  }, [profileForm, showWizard]);

  // ── Logout ───────────────────────────────────────────────────────────────
  const handleLogout = () => {
    clearAuthStorage();
    toast.success('Logged out successfully');
    navigate('/login');
  };

  // ── File Upload Handlers ──────────────────────────────────────────────────
  const handleFileUpload = async (file: File, type: 'photo' | 'resume' | 'cert') => {
    try {
      setSaving(true);
      toast.loading(`Uploading ${type}...`, { id: 'upload-toast' });

      // Fallback: use a local blob URL if Cloudinary is not configured
      const tryCloudinaryOrFallback = async (uploadFn: () => Promise<{ url: string }>) => {
        try {
          const res = await uploadFn();
          return res.url;
        } catch {
          // Cloudinary not configured — use local blob URL for preview
          return URL.createObjectURL(file);
        }
      };

      if (type === 'photo') {
        const secureUrl = await tryCloudinaryOrFallback(() => profileService.uploadProfilePhoto(file));
        setProfileForm((prev) => ({ ...prev, profilePhoto: secureUrl }));
        // Only auto-save if we have a real URL (not a local blob: preview)
        if (profile && !secureUrl.startsWith('blob:')) {
          await profileService.saveProfile({ ...profileForm, profilePhoto: secureUrl });
          fetchProfile();
        }
      } else if (type === 'resume') {
        const secureUrl = await tryCloudinaryOrFallback(() => profileService.uploadResume(file));
        setProfileForm((prev) => ({ ...prev, resumeUrl: secureUrl }));
        // Only auto-save if we have a real URL (not a local blob: preview)
        if (profile && !secureUrl.startsWith('blob:')) {
          try {
            await profileService.saveProfile({ ...profileForm, resumeUrl: secureUrl });
            fetchProfile();
          } catch (_) { /* ignore */ }
        }
      } else if (type === 'cert') {
        const secureUrl = await tryCloudinaryOrFallback(() => profileService.uploadCertificateFile(file));
        setCertificateForm((prev) => ({ ...prev, certificateUrl: secureUrl }));
      }
      toast.success(`${type.charAt(0).toUpperCase() + type.slice(1)} uploaded successfully`, { id: 'upload-toast' });
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'File upload failed', { id: 'upload-toast' });
    } finally {
      setSaving(false);
    }
  };

  const handleDrag = (e: React.DragEvent, type: 'photo' | 'resume' | 'cert', active: boolean) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive((prev) => ({ ...prev, [type]: active }));
  };

  const handleDrop = (e: React.DragEvent, type: 'photo' | 'resume' | 'cert') => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive((prev) => ({ ...prev, [type]: false }));
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileUpload(e.dataTransfer.files[0], type);
    }
  };

  // ── Profile Main Actions ──────────────────────────────────────────────────
  const handleSaveProfile = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    try {
      setSaving(true);
      // Strip blob: URLs — they are local-only previews and will fail backend @URL validation.
      // Real URLs (from Cloudinary) will be preserved; blob: URLs are simply cleared.
      const payload: StudentProfileRequest = {
        ...profileForm,
        profilePhoto: profileForm.profilePhoto?.startsWith('blob:') ? '' : profileForm.profilePhoto,
        resumeUrl: profileForm.resumeUrl?.startsWith('blob:') ? '' : profileForm.resumeUrl,
      };
      const res = await profileService.saveProfile(payload);
      setProfile(res);
      localStorage.removeItem('acea_profile_draft');
      setShowWizard(false);
      toast.success('Student profile saved successfully!');
      fetchProfile();
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to save profile');
    } finally {
      setSaving(false);
    }
  };

  // ── Sub-resource Add Handlers ─────────────────────────────────────────────
  const handleAddSkill = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await profileService.addSkill(skillForm);
      toast.success('Skill added');
      setShowSkillForm(false);
      setSkillForm({ skillName: '', skillLevel: 'BEGINNER', experienceMonths: 0 });
      fetchProfile();
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to add skill');
    }
  };

  const handleAddInterest = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await profileService.addInterest(interestForm);
      toast.success('Interest added');
      setShowInterestForm(false);
      setInterestForm({ interestName: '' });
      fetchProfile();
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to add interest');
    }
  };

  const handleAddGoal = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await profileService.addGoal(goalForm);
      toast.success('Career goal added');
      setShowGoalForm(false);
      setGoalForm({ goalTitle: '', goalDescription: '', targetYear: 2027, priority: 'MEDIUM' });
      fetchProfile();
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to add goal');
    }
  };

  const handleAddProject = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const techList = projectForm.techStack.split(',').map((t) => t.trim()).filter(Boolean);
      await profileService.addProject({
        ...projectForm,
        techStack: techList,
      });
      toast.success('Project added');
      setShowProjectForm(false);
      setProjectForm({ projectTitle: '', description: '', techStack: '', githubLink: '', liveLink: '' });
      fetchProfile();
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to add project');
    }
  };

  const handleAddCertificate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await profileService.addCertificate(certificateForm);
      toast.success('Certificate added');
      setShowCertificateForm(false);
      setCertificateForm({ certificateName: '', platform: '', issueDate: '', certificateUrl: '' });
      fetchProfile();
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to add certificate');
    }
  };

  // ── Delete Sub-resource Handlers ──────────────────────────────────────────
  const handleDeleteSkill = async (id: string) => {
    if (confirm('Delete this skill?')) {
      try {
        await profileService.deleteSkill(id);
        toast.success('Skill deleted');
        fetchProfile();
      } catch (_) {
        toast.error('Failed to delete skill');
      }
    }
  };

  const handleDeleteInterest = async (id: string) => {
    if (confirm('Delete this interest?')) {
      try {
        await profileService.deleteInterest(id);
        toast.success('Interest deleted');
        fetchProfile();
      } catch (_) {
        toast.error('Failed to delete interest');
      }
    }
  };

  const handleDeleteGoal = async (id: string) => {
    if (confirm('Delete this goal?')) {
      try {
        await profileService.deleteGoal(id);
        toast.success('Goal deleted');
        fetchProfile();
      } catch (_) {
        toast.error('Failed to delete goal');
      }
    }
  };

  const handleDeleteProject = async (id: string) => {
    if (confirm('Delete this project?')) {
      try {
        await profileService.deleteProject(id);
        toast.success('Project deleted');
        fetchProfile();
      } catch (_) {
        toast.error('Failed to delete project');
      }
    }
  };

  const handleDeleteCertificate = async (id: string) => {
    if (confirm('Delete this certificate?')) {
      try {
        await profileService.deleteCertificate(id);
        toast.success('Certificate deleted');
        fetchProfile();
      } catch (_) {
        toast.error('Failed to delete certificate');
      }
    }
  };

  // ── Languages known chips helper ──────────────────────────────────────────
  const addLanguage = () => {
    if (langInput.trim() && !profileForm.languagesKnown.includes(langInput.trim())) {
      setProfileForm((prev) => ({
        ...prev,
        languagesKnown: [...prev.languagesKnown, langInput.trim()],
      }));
      setLangInput('');
    }
  };

  const removeLanguage = (lang: string) => {
    setProfileForm((prev) => ({
      ...prev,
      languagesKnown: prev.languagesKnown.filter((l) => l !== lang),
    }));
  };

  // ── Render Helpers ────────────────────────────────────────────────────────

  if (loading) {
    return (
      <div className="landing-layout" style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <div style={{ textAlign: 'center' }}>
          <div className="auth-loader" style={{ margin: '0 auto 20px' }}></div>
          <p style={{ color: 'var(--text-light)', fontWeight: 500 }}>Initializing your Profile Dashboard...</p>
        </div>
      </div>
    );
  }

  // WIZARD FOR FIRST TIME INITIALIZATION
  if (showWizard) {
    return (
      <div className="landing-layout" style={{ minHeight: '100vh', padding: '40px 20px' }}>
        <div className="auth-card" style={{ maxWidth: '800px', margin: '0 auto', padding: '40px' }}>
          
          {/* Header */}
          <div style={{ textAlign: 'center', marginBottom: '30px' }}>
            <h2 className="hero-title" style={{ fontSize: '28px', color: 'var(--text-dark)', marginBottom: '8px' }}>
              Complete Your Student Profile
            </h2>
            <p style={{ color: 'var(--text-light)' }}>Power future AI assessments & recommendations (Autosave active)</p>
          </div>

          {/* Progress Steps */}
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '40px', position: 'relative' }}>
            <div style={{ position: 'absolute', top: '15px', left: '10%', right: '10%', height: '2px', background: '#E5E7EB', zIndex: 1 }}>
              <div style={{ height: '100%', background: 'var(--primary)', width: `${((wizardStep - 1) / 3) * 100}%`, transition: 'width 0.3s ease' }}></div>
            </div>
            {[1, 2, 3, 4].map((step) => (
              <div key={step} style={{ position: 'relative', zIndex: 2, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <div style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '50%',
                  background: wizardStep >= step ? 'var(--primary)' : '#E5E7EB',
                  color: wizardStep >= step ? '#FFFFFF' : 'var(--text-light)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontWeight: 'bold',
                  fontSize: '14px',
                  boxShadow: wizardStep === step ? '0 0 0 4px rgba(108, 63, 235, 0.2)' : 'none',
                }}>
                  {step}
                </div>
                <span style={{ fontSize: '12px', marginTop: '8px', color: wizardStep >= step ? 'var(--text-dark)' : 'var(--text-light)', fontWeight: 500 }}>
                  {step === 1 ? 'Personal' : step === 2 ? 'Academics' : step === 3 ? 'Demographics' : 'Bio & Resume'}
                </span>
              </div>
            ))}
          </div>

          {/* Step Contents */}
          <form onSubmit={(e) => e.preventDefault()}>
            {wizardStep === 1 && (
              <div>
                <h3 style={{ fontSize: '18px', marginBottom: '20px', fontWeight: 600, color: 'var(--text-dark)' }}>Personal details</h3>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                  <div className="form-group">
                    <label className="form-label">First Name *</label>
                    <input type="text" className="form-input" required value={profileForm.firstName} onChange={(e) => setProfileForm({ ...profileForm, firstName: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Last Name *</label>
                    <input type="text" className="form-input" required value={profileForm.lastName} onChange={(e) => setProfileForm({ ...profileForm, lastName: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Date of Birth *</label>
                    <input type="date" className="form-input" required value={profileForm.dateOfBirth} onChange={(e) => setProfileForm({ ...profileForm, dateOfBirth: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Gender *</label>
                    <select className="form-input" value={profileForm.gender} onChange={(e) => setProfileForm({ ...profileForm, gender: e.target.value })}>
                      <option value="MALE">Male</option>
                      <option value="FEMALE">Female</option>
                      <option value="OTHER">Other</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Phone Number *</label>
                    <input type="tel" className="form-input" required placeholder="+919876543210" value={profileForm.phoneNumber} onChange={(e) => setProfileForm({ ...profileForm, phoneNumber: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Address *</label>
                    <input type="text" className="form-input" required value={profileForm.address} onChange={(e) => setProfileForm({ ...profileForm, address: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">City *</label>
                    <input type="text" className="form-input" required value={profileForm.city} onChange={(e) => setProfileForm({ ...profileForm, city: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">District *</label>
                    <input type="text" className="form-input" required value={profileForm.district} onChange={(e) => setProfileForm({ ...profileForm, district: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">State *</label>
                    <input type="text" className="form-input" required value={profileForm.state} onChange={(e) => setProfileForm({ ...profileForm, state: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">PIN Code *</label>
                    <input type="text" className="form-input" required value={profileForm.pinCode} onChange={(e) => setProfileForm({ ...profileForm, pinCode: e.target.value })} />
                  </div>
                </div>
              </div>
            )}

            {wizardStep === 2 && (
              <div>
                <h3 style={{ fontSize: '18px', marginBottom: '20px', fontWeight: 600, color: 'var(--text-dark)' }}>Academic Information</h3>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                  <div className="form-group">
                    <label className="form-label">School Name *</label>
                    <input type="text" className="form-input" required value={profileForm.schoolName} onChange={(e) => setProfileForm({ ...profileForm, schoolName: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">College / Institution Name *</label>
                    <input type="text" className="form-input" required value={profileForm.collegeName} onChange={(e) => setProfileForm({ ...profileForm, collegeName: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Education Level *</label>
                    <input type="text" className="form-input" required placeholder="e.g. UNDERGRADUATE" value={profileForm.educationLevel} onChange={(e) => setProfileForm({ ...profileForm, educationLevel: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Current Course *</label>
                    <input type="text" className="form-input" required placeholder="e.g. B.Tech" value={profileForm.currentCourse} onChange={(e) => setProfileForm({ ...profileForm, currentCourse: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Branch *</label>
                    <input type="text" className="form-input" required placeholder="e.g. Computer Science" value={profileForm.branch} onChange={(e) => setProfileForm({ ...profileForm, branch: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Current Year *</label>
                    <input type="number" className="form-input" required min={1} max={6} value={profileForm.currentYear} onChange={(e) => setProfileForm({ ...profileForm, currentYear: parseInt(e.target.value) || 1 })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">10th Percentage *</label>
                    <input type="number" step="0.01" className="form-input" required value={profileForm.tenthPercentage} onChange={(e) => setProfileForm({ ...profileForm, tenthPercentage: parseFloat(e.target.value) || 0 })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">12th / Diploma Percentage *</label>
                    <input type="number" step="0.01" className="form-input" required value={profileForm.twelfthPercentage} onChange={(e) => setProfileForm({ ...profileForm, twelfthPercentage: parseFloat(e.target.value) || 0 })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Current CGPA (Scale 10.0) *</label>
                    <input type="number" step="0.01" className="form-input" required value={profileForm.currentCgpa} onChange={(e) => setProfileForm({ ...profileForm, currentCgpa: parseFloat(e.target.value) || 0 })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Preferred Study Mode *</label>
                    <select className="form-input" value={profileForm.preferredStudyMode} onChange={(e) => setProfileForm({ ...profileForm, preferredStudyMode: e.target.value })}>
                      <option value="OFFLINE">Offline Classroom</option>
                      <option value="ONLINE">Online Self-Paced</option>
                      <option value="HYBRID">Hybrid Study</option>
                    </select>
                  </div>
                </div>
              </div>
            )}

            {wizardStep === 3 && (
              <div>
                <h3 style={{ fontSize: '18px', marginBottom: '20px', fontWeight: 600, color: 'var(--text-dark)' }}>Demographics & Background</h3>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                  <div className="form-group">
                    <label className="form-label">Annual Family Income (INR) *</label>
                    <input type="number" className="form-input" required value={profileForm.familyIncome} onChange={(e) => setProfileForm({ ...profileForm, familyIncome: parseFloat(e.target.value) || 0 })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Category *</label>
                    <input type="text" className="form-input" required placeholder="e.g. GENERAL, OBC, SC, ST" value={profileForm.category} onChange={(e) => setProfileForm({ ...profileForm, category: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Caste *</label>
                    <input type="text" className="form-input" required value={profileForm.caste} onChange={(e) => setProfileForm({ ...profileForm, caste: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Religion (Optional)</label>
                    <input type="text" className="form-input" value={profileForm.religion} onChange={(e) => setProfileForm({ ...profileForm, religion: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Disability Status *</label>
                    <select className="form-input" value={profileForm.disabilityStatus} onChange={(e) => setProfileForm({ ...profileForm, disabilityStatus: e.target.value })}>
                      <option value="NONE">None</option>
                      <option value="LOCOMOTOR">Locomotor Disability</option>
                      <option value="VISUAL">Visual Impairment</option>
                      <option value="HEARING">Hearing Impairment</option>
                      <option value="OTHER">Other Disability</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Languages Known * (Press Enter or Add)</label>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <input type="text" className="form-input" placeholder="e.g. English" value={langInput} onChange={(e) => setLangInput(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && addLanguage()} />
                      <button type="button" className="btn-primary" style={{ padding: '0 16px' }} onClick={addLanguage}>Add</button>
                    </div>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '10px' }}>
                      {profileForm.languagesKnown.map((lang) => (
                        <span key={lang} className="skill-tag" style={{ background: '#ECECFE', color: 'var(--primary)', display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                          {lang}
                          <button type="button" style={{ border: 'none', background: 'transparent', cursor: 'pointer', color: 'red', fontWeight: 'bold' }} onClick={() => removeLanguage(lang)}>×</button>
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            )}

            {wizardStep === 4 && (
              <div>
                <h3 style={{ fontSize: '18px', marginBottom: '20px', fontWeight: 600, color: 'var(--text-dark)' }}>Bio, Social URLs & Resume</h3>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr' }}>
                  <div className="form-group">
                    <label className="form-label">Career Goal *</label>
                    <textarea className="form-input" style={{ height: '80px' }} required placeholder="e.g. I want to become a full-stack engineer and work on high scale architectures." value={profileForm.careerGoal} onChange={(e) => setProfileForm({ ...profileForm, careerGoal: e.target.value })}></textarea>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Short Professional Bio *</label>
                    <textarea className="form-input" style={{ height: '100px' }} required placeholder="Describe your interests, academics, and projects..." value={profileForm.bio} onChange={(e) => setProfileForm({ ...profileForm, bio: e.target.value })}></textarea>
                  </div>
                </div>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr', marginTop: '15px' }}>
                  <div className="form-group">
                    <label className="form-label">LinkedIn Profile URL</label>
                    <input type="url" className="form-input" placeholder="https://linkedin.com/in/username" value={profileForm.linkedinUrl} onChange={(e) => setProfileForm({ ...profileForm, linkedinUrl: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">GitHub Profile URL</label>
                    <input type="url" className="form-input" placeholder="https://github.com/username" value={profileForm.githubUrl} onChange={(e) => setProfileForm({ ...profileForm, githubUrl: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Portfolio Website URL</label>
                    <input type="url" className="form-input" placeholder="https://username.dev" value={profileForm.portfolioUrl} onChange={(e) => setProfileForm({ ...profileForm, portfolioUrl: e.target.value })} />
                  </div>
                </div>

                {/* Upload Photo & Resume */}
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginTop: '20px' }}>
                  <div>
                    <label className="form-label">Profile Photo (Max 2MB)</label>
                    <div
                      onClick={() => photoInputRef.current?.click()}
                      onDragEnter={(e) => handleDrag(e, 'photo', true)}
                      onDragOver={(e) => handleDrag(e, 'photo', true)}
                      onDragLeave={(e) => handleDrag(e, 'photo', false)}
                      onDrop={(e) => handleDrop(e, 'photo')}
                      style={{
                        border: dragActive.photo ? '2px dashed var(--primary)' : '2px dashed #D1D5DB',
                        borderRadius: 'var(--radius-md)',
                        padding: '20px',
                        textAlign: 'center',
                        background: dragActive.photo ? '#ECECFE' : '#FAFAFC',
                        cursor: 'pointer',
                      }}
                    >
                      {profileForm.profilePhoto ? (
                        <div>
                          <img src={profileForm.profilePhoto} alt="Preview" style={{ width: '70px', height: '70px', borderRadius: '50%', objectFit: 'cover', marginBottom: '8px' }} />
                          <p style={{ fontSize: '12px', color: '#16A34A', fontWeight: 600 }}>✓ Photo uploaded — click to replace</p>
                        </div>
                      ) : (
                        <p style={{ fontSize: '13px', color: 'var(--text-light)' }}>
                          Click or drag & drop photo here
                        </p>
                      )}
                    </div>
                    <input ref={photoInputRef} type="file" accept="image/*" style={{ display: 'none' }} onChange={(e) => e.target.files?.[0] && handleFileUpload(e.target.files[0], 'photo')} />
                  </div>

                  <div>
                    <label className="form-label">Resume PDF (Max 5MB)</label>
                    <div
                      onClick={() => resumeInputRef.current?.click()}
                      onDragEnter={(e) => handleDrag(e, 'resume', true)}
                      onDragOver={(e) => handleDrag(e, 'resume', true)}
                      onDragLeave={(e) => handleDrag(e, 'resume', false)}
                      onDrop={(e) => handleDrop(e, 'resume')}
                      style={{
                        border: dragActive.resume ? '2px dashed var(--primary)' : '2px dashed #D1D5DB',
                        borderRadius: 'var(--radius-md)',
                        padding: '20px',
                        textAlign: 'center',
                        background: dragActive.resume ? '#ECECFE' : '#FAFAFC',
                        cursor: 'pointer',
                      }}
                    >
                      {profileForm.resumeUrl ? (
                        <div>
                          <p style={{ fontSize: '14px', color: '#16A34A', fontWeight: 700 }}>✓ Resume Uploaded</p>
                          <p style={{ fontSize: '12px', color: 'var(--text-light)' }}>Click to replace</p>
                        </div>
                      ) : (
                        <p style={{ fontSize: '13px', color: 'var(--text-light)' }}>
                          Click or drag & drop resume PDF here
                        </p>
                      )}
                    </div>
                    <input ref={resumeInputRef} type="file" accept=".pdf,.doc,.docx" style={{ display: 'none' }} onChange={(e) => e.target.files?.[0] && handleFileUpload(e.target.files[0], 'resume')} />
                  </div>
                </div>
              </div>
            )}

            {/* Wizard Actions */}
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '40px' }}>
              {wizardStep > 1 ? (
                <button type="button" className="btn-secondary" onClick={() => setWizardStep(wizardStep - 1)}>Back</button>
              ) : (
                <div></div>
              )}
              {wizardStep < 4 ? (
                <button type="button" className="btn-primary" onClick={() => setWizardStep(wizardStep + 1)}>Next</button>
              ) : (
                <button type="button" className="btn-primary" disabled={saving} onClick={() => handleSaveProfile()}>
                  {saving ? 'Saving...' : 'Finish & Create Profile'}
                </button>
              )}
            </div>
          </form>
        </div>
      </div>
    );
  }

  // STANDARD PROFILE DASHBOARD VIEW
  return (
    <div className="landing-layout" style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      
      {/* Navigation */}
      <header className="landing-header">
        <div className="header-container">
          <div className="header-logo">
            <div className="logo-icon">A</div>
            <span className="logo-text">ACEA Advisor</span>
          </div>
          <nav className="header-nav">
            <span style={{ fontWeight: 600, color: 'var(--text-dark)' }}>Welcome, {profile?.firstName}</span>
            <button className="btn-secondary" style={{ padding: '6px 12px', fontSize: '13px' }} onClick={handleLogout}>Logout</button>
          </nav>
        </div>
      </header>

      {/* Main Container */}
      <main style={{ flex: 1, padding: '40px 20px', maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
        <div style={{ display: 'grid', gridTemplateColumns: '280px 1fr', gap: '30px' }}>
          
          {/* Left Column: Sidebar & Completeness Progress */}
          <div>
            {/* Completeness Card */}
            <div className="auth-card" style={{ padding: '24px', textAlign: 'center', marginBottom: '20px' }}>
              <div style={{ position: 'relative', width: '120px', height: '120px', margin: '0 auto 16px' }}>
                <svg viewBox="0 0 36 36" style={{ width: '100%', height: '100%', transform: 'rotate(-90deg)' }}>
                  <path d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke="#ECECFE" strokeWidth="3" />
                  <path d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" fill="none" stroke="var(--primary)" strokeWidth="3" strokeDasharray={`${profile?.profileCompletionPct || 0}, 100`} style={{ transition: 'stroke-dasharray 0.5s ease' }} />
                </svg>
                <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', fontWeight: 'bold', fontSize: '22px', color: 'var(--primary)' }}>
                  {profile?.profileCompletionPct || 0}%
                </div>
              </div>
              <h4 style={{ fontSize: '15px', color: 'var(--text-dark)', fontWeight: 600, marginBottom: '4px' }}>Profile completeness</h4>
              <p style={{ fontSize: '12px', color: 'var(--text-light)' }}>Powering your career recommendations</p>
            </div>

            {/* Navigation Sidebar */}
            <div className="auth-card" style={{ padding: '16px' }}>
              <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: '6px' }}>
                {[
                  { id: 'overview', label: 'Dashboard Overview' },
                  { id: 'personal', label: 'Personal Information' },
                  { id: 'academic', label: 'Academic History' },
                  { id: 'bio', label: 'Bio & Social Links' },
                  { id: 'skills', label: 'Skills List' },
                  { id: 'interests', label: 'Interests & Hobbies' },
                  { id: 'goals', label: 'Career Goals' },
                  { id: 'projects', label: 'Academic Projects' },
                  { id: 'certificates', label: 'Certifications' },
                  { id: 'resume', label: 'Resume PDF' },
                ].map((tab) => (
                  <li key={tab.id}>
                    <button
                      onClick={() => setActiveTab(tab.id)}
                      style={{
                        width: '100%',
                        textAlign: 'left',
                        padding: '12px 16px',
                        border: 'none',
                        borderRadius: 'var(--radius-md)',
                        background: activeTab === tab.id ? 'var(--primary)' : 'transparent',
                        color: activeTab === tab.id ? '#FFFFFF' : 'var(--text-dark)',
                        fontWeight: activeTab === tab.id ? 600 : 500,
                        fontSize: '14px',
                        cursor: 'pointer',
                        transition: 'all 0.2s',
                      }}
                    >
                      {tab.label}
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* Right Column: Tab Contents */}
          <div className="auth-card" style={{ padding: '30px' }}>
            
            {/* TAB: OVERVIEW */}
            {activeTab === 'overview' && (
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px', marginBottom: '30px' }}>
                  <img src={profile?.profilePhoto || 'https://via.placeholder.com/150'} alt="Avatar" style={{ width: '80px', height: '80px', borderRadius: '50%', objectFit: 'cover', border: '2px solid var(--primary)' }} />
                  <div>
                    <h2 style={{ fontSize: '22px', fontWeight: 'bold', color: 'var(--text-dark)' }}>{profile?.firstName} {profile?.lastName}</h2>
                    <p style={{ color: 'var(--text-light)', fontSize: '14px' }}>{profile?.currentCourse} student at {profile?.collegeName}</p>
                    <span className="skill-tag" style={{ background: '#ECECFE', color: 'var(--primary)', fontWeight: 600, fontSize: '11px', marginTop: '6px', display: 'inline-block' }}>
                      Completeness: {profile?.profileCompletionPct}%
                    </span>
                  </div>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                  <div className="info-card" style={{ padding: '20px', border: '1px solid #E5E7EB', borderRadius: 'var(--radius-md)' }}>
                    <h4 style={{ fontWeight: 600, marginBottom: '10px' }}>Recent Skills</h4>
                    {profile?.skills?.length === 0 ? (
                      <p style={{ fontSize: '13px', color: 'var(--text-light)' }}>No skills added yet.</p>
                    ) : (
                      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                        {profile?.skills?.map((sk: any) => (
                          <span key={sk.id} className="skill-tag">{sk.skillName} ({sk.skillLevel})</span>
                        ))}
                      </div>
                    )}
                  </div>

                  <div className="info-card" style={{ padding: '20px', border: '1px solid #E5E7EB', borderRadius: 'var(--radius-md)' }}>
                    <h4 style={{ fontWeight: 600, marginBottom: '10px' }}>Career Target</h4>
                    <p style={{ fontSize: '14px', color: 'var(--text-dark)' }}>{profile?.careerGoal}</p>
                  </div>
                </div>

                <div style={{ marginTop: '20px', padding: '20px', background: 'rgba(108,59,235,0.04)', border: '1px solid rgba(108,59,235,0.15)', borderRadius: 'var(--radius-md)' }}>
                  <h4 style={{ fontWeight: 600, marginBottom: '10px', color: 'var(--primary)' }}>AI Career Development Tools</h4>
                  <p style={{ fontSize: '13px', color: 'var(--text-light)', marginBottom: '16px' }}>Take diagnostics tests and get tailored career recommendation plans based on your aptitude, personality traits, and interests.</p>
                  <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
                    <button className="btn-primary" style={{ padding: '10px 16px', fontSize: '13px' }} onClick={() => navigate('/assessments')}>Diagnostic Assessments</button>
                    <button className="btn-secondary" style={{ padding: '10px 16px', fontSize: '13px' }} onClick={() => navigate('/recommendations')}>AI Career Recommendations</button>
                    <button className="btn-ghost" style={{ padding: '10px 16px', fontSize: '13px', borderColor: 'var(--primary)', color: 'var(--primary)' }} onClick={() => navigate('/colleges')}>College Finder & AI Match</button>
                    <button className="btn-ghost" style={{ padding: '10px 16px', fontSize: '13px', borderColor: 'var(--primary)', color: 'var(--primary)' }} onClick={() => navigate('/scholarships')}>Scholarship Finder & AI Match</button>
                    <button className="btn-ghost" style={{ padding: '10px 16px', fontSize: '13px', borderColor: 'var(--primary)', color: 'var(--primary)' }} onClick={() => navigate('/resources')}>Learning Hub & Skill Match</button>
                    <button className="btn-ghost" style={{ padding: '10px 16px', fontSize: '13px', borderColor: 'var(--primary)', color: 'var(--primary)' }} onClick={() => navigate('/roadmaps')}>Career Roadmap Generator</button>
                    <button className="btn-ghost" style={{ padding: '10px 16px', fontSize: '13px', borderColor: 'var(--primary)', color: 'var(--primary)' }} onClick={() => navigate('/resume-analyzer')}>AI Resume Analyzer</button>
                    <button className="btn-ghost" style={{ padding: '10px 16px', fontSize: '13px', borderColor: 'var(--primary)', color: 'var(--primary)' }} onClick={() => navigate('/skill-gap')}>AI Skill Gap Analysis</button>
                    <button className="btn-primary" style={{ padding: '10px 16px', fontSize: '13px', background: 'linear-gradient(135deg, #6C3FEB, #8B5CF6)', boxShadow: '0 4px 15px rgba(108,63,235,0.4)' }} onClick={() => navigate('/chat')}>💬 AI Career Chatbot</button>
                  </div>
                </div>

                <div style={{ marginTop: '30px' }}>
                  <h4 style={{ fontWeight: 600, marginBottom: '10px' }}>Bio</h4>
                  <p style={{ fontSize: '14px', color: 'var(--text-dark)', lineHeight: '1.6' }}>{profile?.bio}</p>
                </div>
              </div>
            )}

            {/* TAB: PERSONAL */}
            {activeTab === 'personal' && (
              <form onSubmit={handleSaveProfile}>
                <h3 style={{ fontSize: '18px', fontWeight: 600, marginBottom: '20px' }}>Edit Personal Details</h3>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                  <div className="form-group">
                    <label className="form-label">First Name</label>
                    <input type="text" className="form-input" required value={profileForm.firstName} onChange={(e) => setProfileForm({ ...profileForm, firstName: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Last Name</label>
                    <input type="text" className="form-input" required value={profileForm.lastName} onChange={(e) => setProfileForm({ ...profileForm, lastName: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Phone Number</label>
                    <input type="tel" className="form-input" required value={profileForm.phoneNumber} onChange={(e) => setProfileForm({ ...profileForm, phoneNumber: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Country</label>
                    <input type="text" className="form-input" required value={profileForm.country} onChange={(e) => setProfileForm({ ...profileForm, country: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">State</label>
                    <input type="text" className="form-input" required value={profileForm.state} onChange={(e) => setProfileForm({ ...profileForm, state: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">City</label>
                    <input type="text" className="form-input" required value={profileForm.city} onChange={(e) => setProfileForm({ ...profileForm, city: e.target.value })} />
                  </div>
                </div>
                <button type="submit" className="btn-primary" style={{ marginTop: '20px' }} disabled={saving}>
                  {saving ? 'Saving...' : 'Save Personal Details'}
                </button>
              </form>
            )}

            {/* TAB: ACADEMIC */}
            {activeTab === 'academic' && (
              <form onSubmit={handleSaveProfile}>
                <h3 style={{ fontSize: '18px', fontWeight: 600, marginBottom: '20px' }}>Edit Academic Details</h3>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                  <div className="form-group">
                    <label className="form-label">College Name</label>
                    <input type="text" className="form-input" required value={profileForm.collegeName} onChange={(e) => setProfileForm({ ...profileForm, collegeName: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Current Course</label>
                    <input type="text" className="form-input" required value={profileForm.currentCourse} onChange={(e) => setProfileForm({ ...profileForm, currentCourse: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Branch / Department</label>
                    <input type="text" className="form-input" required value={profileForm.branch} onChange={(e) => setProfileForm({ ...profileForm, branch: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Current Year</label>
                    <input type="number" className="form-input" required value={profileForm.currentYear} onChange={(e) => setProfileForm({ ...profileForm, currentYear: parseInt(e.target.value) || 1 })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Current CGPA (Scale 10.0)</label>
                    <input type="number" step="0.01" className="form-input" required value={profileForm.currentCgpa} onChange={(e) => setProfileForm({ ...profileForm, currentCgpa: parseFloat(e.target.value) || 0 })} />
                  </div>
                </div>
                <button type="submit" className="btn-primary" style={{ marginTop: '20px' }} disabled={saving}>
                  {saving ? 'Saving...' : 'Save Academic Details'}
                </button>
              </form>
            )}

            {/* TAB: BIO & SOCIAL LINKS */}
            {activeTab === 'bio' && (
              <form onSubmit={handleSaveProfile}>
                <h3 style={{ fontSize: '18px', fontWeight: 600, marginBottom: '20px' }}>Bio & Social Links</h3>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr' }}>
                  <div className="form-group">
                    <label className="form-label">Career Goal</label>
                    <textarea className="form-input" style={{ height: '80px', resize: 'vertical' }} placeholder="e.g. I want to become a full-stack engineer..." value={profileForm.careerGoal} onChange={(e) => setProfileForm({ ...profileForm, careerGoal: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Short Professional Bio</label>
                    <textarea className="form-input" style={{ height: '120px', resize: 'vertical' }} placeholder="Describe your interests, academics, and projects..." value={profileForm.bio} onChange={(e) => setProfileForm({ ...profileForm, bio: e.target.value })} />
                  </div>
                </div>
                <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr', marginTop: '16px' }}>
                  <div className="form-group">
                    <label className="form-label">LinkedIn URL</label>
                    <input type="url" className="form-input" placeholder="https://linkedin.com/in/username" value={profileForm.linkedinUrl || ''} onChange={(e) => setProfileForm({ ...profileForm, linkedinUrl: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">GitHub URL</label>
                    <input type="url" className="form-input" placeholder="https://github.com/username" value={profileForm.githubUrl || ''} onChange={(e) => setProfileForm({ ...profileForm, githubUrl: e.target.value })} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Portfolio Website</label>
                    <input type="url" className="form-input" placeholder="https://yourportfolio.dev" value={profileForm.portfolioUrl || ''} onChange={(e) => setProfileForm({ ...profileForm, portfolioUrl: e.target.value })} />
                  </div>
                </div>

                {/* Profile Photo Upload */}
                <div style={{ marginTop: '24px' }}>
                  <label className="form-label" style={{ display: 'block', marginBottom: '10px' }}>Profile Photo (Max 2MB — JPG/PNG/WEBP)</label>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                    {profileForm.profilePhoto && (
                      <img src={profileForm.profilePhoto} alt="Avatar" style={{ width: '72px', height: '72px', borderRadius: '50%', objectFit: 'cover', border: '2px solid var(--primary)' }} />
                    )}
                    <div
                      onClick={() => photoInputRef.current?.click()}
                      onDragEnter={(e) => handleDrag(e, 'photo', true)}
                      onDragOver={(e) => handleDrag(e, 'photo', true)}
                      onDragLeave={(e) => handleDrag(e, 'photo', false)}
                      onDrop={(e) => handleDrop(e, 'photo')}
                      style={{
                        flex: 1,
                        border: dragActive.photo ? '2px dashed var(--primary)' : '2px dashed #D1D5DB',
                        borderRadius: 'var(--radius-md)',
                        padding: '20px',
                        textAlign: 'center',
                        background: dragActive.photo ? '#ECECFE' : '#FAFAFC',
                        cursor: 'pointer',
                      }}
                    >
                      <p style={{ fontSize: '14px', color: 'var(--text-light)', margin: 0 }}>
                        {profileForm.profilePhoto ? '✓ Photo uploaded — click to replace' : 'Click or drag & drop photo here'}
                      </p>
                    </div>
                    <input ref={photoInputRef} type="file" accept="image/*" style={{ display: 'none' }} onChange={(e) => e.target.files?.[0] && handleFileUpload(e.target.files[0], 'photo')} />
                    <button type="button" className="btn-secondary" style={{ padding: '10px 16px', fontSize: '13px', whiteSpace: 'nowrap' }} onClick={() => photoInputRef.current?.click()}>
                      Browse Photo
                    </button>
                  </div>
                </div>

                <button type="submit" className="btn-primary" style={{ marginTop: '24px' }} disabled={saving}>
                  {saving ? 'Saving...' : 'Save Bio & Links'}
                </button>
              </form>
            )}

            {/* TAB: SKILLS */}
            {activeTab === 'skills' && (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '18px', fontWeight: 600 }}>Your Skills</h3>
                  <button className="btn-primary" onClick={() => setShowSkillForm(!showSkillForm)}>
                    {showSkillForm ? 'Close Form' : 'Add Skill'}
                  </button>
                </div>

                {showSkillForm && (
                  <form onSubmit={handleAddSkill} className="auth-card" style={{ padding: '20px', background: '#FAFAFC', marginBottom: '20px' }}>
                    <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                      <div className="form-group">
                        <label className="form-label">Skill Name</label>
                        <input type="text" className="form-input" required placeholder="e.g. Java" value={skillForm.skillName} onChange={(e) => setSkillForm({ ...skillForm, skillName: e.target.value })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Skill Level</label>
                        <select className="form-input" value={skillForm.skillLevel} onChange={(e) => setSkillForm({ ...skillForm, skillLevel: e.target.value })}>
                          <option value="BEGINNER">Beginner</option>
                          <option value="INTERMEDIATE">Intermediate</option>
                          <option value="ADVANCED">Advanced</option>
                        </select>
                      </div>
                      <div className="form-group">
                        <label className="form-label">Experience in Months</label>
                        <input type="number" className="form-input" required value={skillForm.experienceMonths} onChange={(e) => setSkillForm({ ...skillForm, experienceMonths: parseInt(e.target.value) || 0 })} />
                      </div>
                    </div>
                    <button type="submit" className="btn-primary" style={{ marginTop: '15px' }}>Save Skill</button>
                  </form>
                )}

                {profile?.skills?.length === 0 ? (
                  <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-light)' }}>
                    No skills added yet. Add a skill to build your profile.
                  </div>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    {profile?.skills?.map((sk: any) => (
                      <div key={sk.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '12px 16px', border: '1px solid #E5E7EB', borderRadius: 'var(--radius-md)' }}>
                        <div>
                          <strong style={{ color: 'var(--text-dark)' }}>{sk.skillName}</strong>
                          <span className="skill-tag" style={{ marginLeft: '10px', background: '#E5E7EB', color: 'var(--text-dark)', fontSize: '11px' }}>
                            {sk.skillLevel}
                          </span>
                          <span style={{ fontSize: '12px', color: 'var(--text-light)', marginLeft: '15px' }}>{sk.experienceMonths} months exp</span>
                        </div>
                        <button className="btn-secondary" style={{ color: 'red', border: 'none', background: 'transparent', padding: '4px', cursor: 'pointer' }} onClick={() => handleDeleteSkill(sk.id)}>Delete</button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* TAB: INTERESTS */}
            {activeTab === 'interests' && (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '18px', fontWeight: 600 }}>Your Interests & Hobbies</h3>
                  <button className="btn-primary" onClick={() => setShowInterestForm(!showInterestForm)}>
                    {showInterestForm ? 'Close Form' : 'Add Interest'}
                  </button>
                </div>

                {showInterestForm && (
                  <form onSubmit={handleAddInterest} className="auth-card" style={{ padding: '20px', background: '#FAFAFC', marginBottom: '20px' }}>
                    <div className="form-group">
                      <label className="form-label">Interest / Domain Name</label>
                      <input type="text" className="form-input" required placeholder="e.g. Machine Learning" value={interestForm.interestName} onChange={(e) => setInterestForm({ ...interestForm, interestName: e.target.value })} />
                    </div>
                    <button type="submit" className="btn-primary" style={{ marginTop: '15px' }}>Save Interest</button>
                  </form>
                )}

                {profile?.interests?.length === 0 ? (
                  <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-light)' }}>
                    No interests added yet.
                  </div>
                ) : (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                    {profile?.interests?.map((item: any) => (
                      <span key={item.id} className="skill-tag" style={{ background: '#ECECFE', color: 'var(--primary)', padding: '10px 14px', borderRadius: 'var(--radius-md)', display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
                        {item.interestName}
                        <button type="button" style={{ border: 'none', background: 'transparent', color: 'red', cursor: 'pointer', fontWeight: 'bold' }} onClick={() => handleDeleteInterest(item.id)}>×</button>
                      </span>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* TAB: GOALS */}
            {activeTab === 'goals' && (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '18px', fontWeight: 600 }}>Career & Academic Goals</h3>
                  <button className="btn-primary" onClick={() => setShowGoalForm(!showGoalForm)}>
                    {showGoalForm ? 'Close Form' : 'Add Goal'}
                  </button>
                </div>

                {showGoalForm && (
                  <form onSubmit={handleAddGoal} className="auth-card" style={{ padding: '20px', background: '#FAFAFC', marginBottom: '20px' }}>
                    <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                      <div className="form-group">
                        <label className="form-label">Goal Title</label>
                        <input type="text" className="form-input" required placeholder="e.g. Master React Native" value={goalForm.goalTitle} onChange={(e) => setGoalForm({ ...goalForm, goalTitle: e.target.value })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Target Year</label>
                        <input type="number" className="form-input" required value={goalForm.targetYear} onChange={(e) => setGoalForm({ ...goalForm, targetYear: parseInt(e.target.value) || 2027 })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Priority</label>
                        <select className="form-input" value={goalForm.priority} onChange={(e) => setGoalForm({ ...goalForm, priority: e.target.value })}>
                          <option value="HIGH">High Priority</option>
                          <option value="MEDIUM">Medium Priority</option>
                          <option value="LOW">Low Priority</option>
                        </select>
                      </div>
                      <div className="form-group" style={{ gridColumn: 'span 2' }}>
                        <label className="form-label">Description</label>
                        <textarea className="form-input" required placeholder="Describe what actions you will take..." value={goalForm.goalDescription} onChange={(e) => setGoalForm({ ...goalForm, goalDescription: e.target.value })}></textarea>
                      </div>
                    </div>
                    <button type="submit" className="btn-primary" style={{ marginTop: '15px' }}>Save Goal</button>
                  </form>
                )}

                {profile?.goals?.length === 0 ? (
                  <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-light)' }}>
                    No goals added yet.
                  </div>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    {profile?.goals?.map((goal: any) => (
                      <div key={goal.id} style={{ padding: '16px', border: '1px solid #E5E7EB', borderRadius: 'var(--radius-md)' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                          <h4 style={{ fontWeight: 600, color: 'var(--text-dark)' }}>{goal.goalTitle}</h4>
                          <div>
                            <span className="skill-tag" style={{ background: goal.priority === 'HIGH' ? '#FEE2E2' : '#FEF3C7', color: goal.priority === 'HIGH' ? 'red' : 'orange', fontSize: '11px', marginRight: '10px' }}>
                              {goal.priority}
                            </span>
                            <button className="btn-secondary" style={{ color: 'red', border: 'none', background: 'transparent', padding: 0, cursor: 'pointer' }} onClick={() => handleDeleteGoal(goal.id)}>Delete</button>
                          </div>
                        </div>
                        <p style={{ fontSize: '13px', color: 'var(--text-light)', marginBottom: '8px' }}>Target: {goal.targetYear}</p>
                        <p style={{ fontSize: '14px', color: 'var(--text-dark)', lineHeight: '1.5' }}>{goal.goalDescription}</p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* TAB: PROJECTS */}
            {activeTab === 'projects' && (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '18px', fontWeight: 600 }}>Academic & Self Projects</h3>
                  <button className="btn-primary" onClick={() => setShowProjectForm(!showProjectForm)}>
                    {showProjectForm ? 'Close Form' : 'Add Project'}
                  </button>
                </div>

                {showProjectForm && (
                  <form onSubmit={handleAddProject} className="auth-card" style={{ padding: '20px', background: '#FAFAFC', marginBottom: '20px' }}>
                    <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                      <div className="form-group">
                        <label className="form-label">Project Title</label>
                        <input type="text" className="form-input" required placeholder="e.g. AI Chatbot" value={projectForm.projectTitle} onChange={(e) => setProjectForm({ ...projectForm, projectTitle: e.target.value })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Tech Stack (comma-separated)</label>
                        <input type="text" className="form-input" required placeholder="React, Node.js, PostgreSQL" value={projectForm.techStack} onChange={(e) => setProjectForm({ ...projectForm, techStack: e.target.value })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">GitHub Repository Link</label>
                        <input type="url" className="form-input" placeholder="https://github.com/..." value={projectForm.githubLink} onChange={(e) => setProjectForm({ ...projectForm, githubLink: e.target.value })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Live Deployment Link</label>
                        <input type="url" className="form-input" placeholder="https://..." value={projectForm.liveLink} onChange={(e) => setProjectForm({ ...projectForm, liveLink: e.target.value })} />
                      </div>
                      <div className="form-group" style={{ gridColumn: 'span 2' }}>
                        <label className="form-label">Project Description</label>
                        <textarea className="form-input" required placeholder="Describe the problem solved, architecture, and your contributions..." value={projectForm.description} onChange={(e) => setProjectForm({ ...projectForm, description: e.target.value })}></textarea>
                      </div>
                    </div>
                    <button type="submit" className="btn-primary" style={{ marginTop: '15px' }}>Save Project</button>
                  </form>
                )}

                {profile?.projects?.length === 0 ? (
                  <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-light)' }}>
                    No projects added yet.
                  </div>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    {profile?.projects?.map((proj: any) => (
                      <div key={proj.id} style={{ padding: '20px', border: '1px solid #E5E7EB', borderRadius: 'var(--radius-md)' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                          <h4 style={{ fontWeight: 600, color: 'var(--text-dark)' }}>{proj.projectTitle}</h4>
                          <button className="btn-secondary" style={{ color: 'red', border: 'none', background: 'transparent', padding: 0, cursor: 'pointer' }} onClick={() => handleDeleteProject(proj.id)}>Delete</button>
                        </div>
                        <p style={{ fontSize: '14px', color: 'var(--text-dark)', marginBottom: '12px' }}>{proj.description}</p>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginBottom: '12px' }}>
                          {proj.techStack?.map((tech: string) => (
                            <span key={tech} className="skill-tag" style={{ background: '#F3F4F6', color: '#374151' }}>{tech}</span>
                          ))}
                        </div>
                        <div style={{ display: 'flex', gap: '15px', fontSize: '13px' }}>
                          {proj.githubLink && <a href={proj.githubLink} target="_blank" rel="noopener noreferrer" style={{ color: 'var(--primary)', fontWeight: 500 }}>GitHub Code</a>}
                          {proj.liveLink && <a href={proj.liveLink} target="_blank" rel="noopener noreferrer" style={{ color: 'var(--primary)', fontWeight: 500 }}>Live Link</a>}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* TAB: CERTIFICATES */}
            {activeTab === 'certificates' && (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '18px', fontWeight: 600 }}>Certifications</h3>
                  <button className="btn-primary" onClick={() => setShowCertificateForm(!showCertificateForm)}>
                    {showCertificateForm ? 'Close Form' : 'Add Certificate'}
                  </button>
                </div>

                {showCertificateForm && (
                  <form onSubmit={handleAddCertificate} className="auth-card" style={{ padding: '20px', background: '#FAFAFC', marginBottom: '20px' }}>
                    <div className="auth-form-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                      <div className="form-group">
                        <label className="form-label">Certificate Name</label>
                        <input type="text" className="form-input" required placeholder="e.g. AWS Cloud Practitioner" value={certificateForm.certificateName} onChange={(e) => setCertificateForm({ ...certificateForm, certificateName: e.target.value })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Issuing Platform</label>
                        <input type="text" className="form-input" required placeholder="e.g. Coursera, Udemy" value={certificateForm.platform} onChange={(e) => setCertificateForm({ ...certificateForm, platform: e.target.value })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Issue Date</label>
                        <input type="date" className="form-input" required value={certificateForm.issueDate} onChange={(e) => setCertificateForm({ ...certificateForm, issueDate: e.target.value })} />
                      </div>
                      <div className="form-group">
                        <label className="form-label">Upload Certificate Document (PDF/JPG)</label>
                        <div
                          onDragEnter={(e) => handleDrag(e, 'cert', true)}
                          onDragOver={(e) => handleDrag(e, 'cert', true)}
                          onDragLeave={(e) => handleDrag(e, 'cert', false)}
                          onDrop={(e) => handleDrop(e, 'cert')}
                          style={{
                            border: dragActive.cert ? '2px dashed var(--primary)' : '2px dashed #E5E7EB',
                            borderRadius: 'var(--radius-md)',
                            padding: '12px',
                            textAlign: 'center',
                            background: dragActive.cert ? '#ECECFE' : '#FAFAFC',
                            cursor: 'pointer',
                          }}
                        >
                          {certificateForm.certificateUrl ? (
                            <span style={{ color: 'green', fontSize: '13px', fontWeight: 600 }}>✓ File uploaded successfully</span>
                          ) : (
                            <span style={{ fontSize: '12px', color: 'var(--text-light)' }}>Drop file or Browse</span>
                          )}
                          <input type="file" style={{ display: 'none' }} id="cert-file-input" onChange={(e) => e.target.files?.[0] && handleFileUpload(e.target.files[0], 'cert')} />
                          <label htmlFor="cert-file-input" style={{ position: 'absolute', top: 0, bottom: 0, left: 0, right: 0, cursor: 'pointer' }}></label>
                        </div>
                      </div>
                    </div>
                    <button type="submit" className="btn-primary" style={{ marginTop: '15px' }} disabled={!certificateForm.certificateUrl}>Save Certificate</button>
                  </form>
                )}

                {profile?.certifications?.length === 0 ? (
                  <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-light)' }}>
                    No certificates added yet.
                  </div>
                ) : (
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                    {profile?.certifications?.map((cert: any) => (
                      <div key={cert.id} style={{ padding: '16px', border: '1px solid #E5E7EB', borderRadius: 'var(--radius-md)' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                          <h4 style={{ fontWeight: 600, color: 'var(--text-dark)' }}>{cert.certificateName}</h4>
                          <button className="btn-secondary" style={{ color: 'red', border: 'none', background: 'transparent', padding: 0, cursor: 'pointer' }} onClick={() => handleDeleteCertificate(cert.id)}>Delete</button>
                        </div>
                        <p style={{ fontSize: '13px', color: 'var(--text-light)' }}>Platform: {cert.platform} | Issued: {cert.issueDate}</p>
                        <a href={cert.certificateUrl} target="_blank" rel="noopener noreferrer" style={{ display: 'inline-block', marginTop: '10px', fontSize: '13px', color: 'var(--primary)', fontWeight: 600 }}>
                          View Certificate Document
                        </a>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {/* TAB: RESUME */}
            {activeTab === 'resume' && (
              <div>
                <h3 style={{ fontSize: '18px', fontWeight: 600, marginBottom: '15px' }}>Academic Resume PDF</h3>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
                  
                  {/* Upload side */}
                  <div>
                    <p style={{ color: 'var(--text-light)', fontSize: '14px', marginBottom: '16px', lineHeight: '1.6' }}>
                      Upload your latest academic or professional resume PDF (Max size 5MB).
                    </p>

                    <div
                      onClick={() => resumeTabInputRef.current?.click()}
                      onDragEnter={(e) => handleDrag(e, 'resume', true)}
                      onDragOver={(e) => handleDrag(e, 'resume', true)}
                      onDragLeave={(e) => handleDrag(e, 'resume', false)}
                      onDrop={(e) => handleDrop(e, 'resume')}
                      style={{
                        border: dragActive.resume ? '2px dashed var(--primary)' : '2px dashed #D1D5DB',
                        borderRadius: 'var(--radius-md)',
                        padding: '40px 20px',
                        textAlign: 'center',
                        background: dragActive.resume ? '#ECECFE' : '#FAFAFC',
                        cursor: 'pointer',
                        marginBottom: '16px',
                      }}
                    >
                      {profileForm.resumeUrl ? (
                        <div>
                          <p style={{ color: '#16A34A', fontWeight: 700, fontSize: '16px', marginBottom: '6px' }}>✓ Resume Uploaded</p>
                          <p style={{ fontSize: '12px', color: 'var(--text-light)' }}>Click or drag & drop to replace with a new PDF</p>
                        </div>
                      ) : (
                        <div>
                          <p style={{ fontSize: '32px', marginBottom: '8px' }}>📄</p>
                          <p style={{ fontSize: '14px', color: 'var(--text-dark)', fontWeight: 600, marginBottom: '4px' }}>Drag & drop your resume here</p>
                          <p style={{ fontSize: '13px', color: 'var(--text-light)' }}>or click anywhere in this box to browse</p>
                        </div>
                      )}
                    </div>
                    <input ref={resumeTabInputRef} type="file" accept=".pdf,.doc,.docx" style={{ display: 'none' }} onChange={(e) => e.target.files?.[0] && handleFileUpload(e.target.files[0], 'resume')} />
                    <button
                      type="button"
                      className="btn-primary"
                      style={{ width: '100%', padding: '12px', fontSize: '14px' }}
                      onClick={() => resumeTabInputRef.current?.click()}
                      disabled={saving}
                    >
                      {saving ? 'Uploading...' : '📂 Browse & Upload Resume'}
                    </button>
                  </div>

                  {/* Preview side */}
                  <div>
                    {profileForm.resumeUrl ? (
                      <div style={{ height: '350px', border: '1px solid #E5E7EB', borderRadius: 'var(--radius-md)', overflow: 'hidden', background: '#E5E7EB' }}>
                        <iframe src={profileForm.resumeUrl} title="Resume Preview" width="100%" height="100%" style={{ border: 'none' }}></iframe>
                      </div>
                    ) : (
                      <div style={{ height: '350px', border: '1px solid #E5E7EB', borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-light)', background: '#FAFAFC' }}>
                        No resume uploaded yet.
                      </div>
                    )}
                  </div>

                </div>
              </div>
            )}

          </div>

        </div>
      </main>

    </div>
  );
}
