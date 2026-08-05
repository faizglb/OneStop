import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ROUTES } from '@/lib/constants';

const features = [
  {
    emoji: '🤖',
    bg: '#EEE6FF',
    title: 'AI Career Advisor',
    desc: 'Personalized career guidance powered by RAG AI trained on millions of real career trajectories.',
  },
  {
    emoji: '🎓',
    bg: '#E0F7FA',
    title: 'College Finder',
    desc: 'Search and compare 50,000+ colleges by location, fees, placement, and entrance exam scores.',
  },
  {
    emoji: '🎯',
    bg: '#FFF3E0',
    title: 'Skill Gap Analysis',
    desc: 'Identify exactly what skills you need for your dream role with a personalized AI roadmap.',
  },
  {
    emoji: '📄',
    bg: '#FCE4EC',
    title: 'Resume Analyzer',
    desc: 'ATS-optimized resume scoring with actionable improvement suggestions from AI.',
  },
  {
    emoji: '📊',
    bg: '#E8F5E9',
    title: 'Salary Intelligence',
    desc: 'Real-time salary benchmarks by role, city, and experience to help you negotiate better.',
  },
  {
    emoji: '🏆',
    bg: '#FFF8E1',
    title: 'Mock Interviews',
    desc: 'Practice with AI-powered mock interviews and get instant feedback on your answers.',
  },
];

const stats = [
  { value: '50K+', label: 'Active Students' },
  { value: '2,000+', label: 'Partner Colleges' },
  { value: '98%', label: 'Placement Success' },
  { value: '4.9 ★', label: 'Average Rating' },
];

const steps = [
  { n: '01', title: 'Create Your Profile', desc: 'Tell us about your education background, interests, and career goals. Takes just 3 minutes.' },
  { n: '02', title: 'AI Assessment', desc: 'Our AI analyzes your profile and maps it against thousands of real career pathways.' },
  { n: '03', title: 'Follow Your Roadmap', desc: 'Receive personalized course recommendations, college picks, and mentor connections.' },
];

const testimonials = [
  {
    text: '"ACEA helped me pinpoint my skill gaps and build a precise study plan. I got into Google in just 8 months of focused preparation!"',
    name: 'Priya Mehta',
    role: 'Software Engineer @ Google',
    avatar: 'PM',
    bg: 'linear-gradient(135deg,#6C3BEB,#9D6EFF)',
  },
  {
    text: '"The AI advisor felt like talking to a real senior mentor. It recommended the exact courses and certifications I needed."',
    name: 'Arjun Kapoor',
    role: 'Data Scientist @ Flipkart',
    avatar: 'AK',
    bg: 'linear-gradient(135deg,#00B7C2,#4CAF50)',
  },
  {
    text: '"From selecting the right MBA college to acing group discussions — ACEA was my go-to platform throughout the process."',
    name: 'Sneha Reddy',
    role: 'Product Manager @ Razorpay',
    avatar: 'SR',
    bg: 'linear-gradient(135deg,#F44336,#9C27B0)',
  },
];

const navLinks = ['Features', 'Colleges', 'Scholarships', 'Jobs', 'Mentors'];

export default function LandingPage() {
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const fn = () => setScrolled(window.scrollY > 10);
    window.addEventListener('scroll', fn);
    return () => window.removeEventListener('scroll', fn);
  }, []);

  return (
    <div>
      {/* ── Navbar ─────────────────────────────────────────── */}
      <nav className="navbar" style={scrolled ? { boxShadow: '0 2px 12px rgba(0,0,0,0.08)' } : {}}>
        <div className="navbar-inner">
          <Link to={ROUTES.HOME} className="navbar-logo" style={{ textDecoration: 'none' }}>
            <div className="navbar-logo-icon">A</div>
            ACEA
          </Link>

          <div className="navbar-links">
            {navLinks.map(link => (
              <span key={link} className="navbar-link">{link}</span>
            ))}
          </div>

          <div className="navbar-actions">
            <Link to={ROUTES.LOGIN} className="btn btn-ghost btn-sm">Sign In</Link>
            <Link to={ROUTES.REGISTER} className="btn btn-gradient btn-sm">
              Get Started Free →
            </Link>
          </div>
        </div>
      </nav>

      {/* ── Hero ───────────────────────────────────────────── */}
      <section className="hero">
        <div className="hero-inner">
          <div className="animate-fade-in">
            <div className="hero-badge">
              <span className="dot" />
              Powered by GPT-4 + RAG Technology
            </div>
            <h1 className="hero-title">
              Your AI-Powered<br />
              <span className="highlight">Career & Education</span><br />
              Advisor
            </h1>
            <p className="hero-subtitle">
              From choosing the right college to landing your dream job — ACEA guides
              every step with real-time AI, personalized roadmaps, and industry intelligence.
            </p>
            <div className="hero-actions">
              <Link to={ROUTES.REGISTER} className="btn btn-gradient btn-xl">
                Start for Free →
              </Link>
              <Link to={ROUTES.LOGIN} className="btn btn-secondary btn-xl">
                Sign In
              </Link>
            </div>
            <div className="hero-trust">
              {['No credit card required', 'Free forever plan', 'GDPR compliant'].map(t => (
                <div key={t} className="hero-trust-item">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                  {t}
                </div>
              ))}
            </div>
          </div>

          {/* Right visual */}
          <div className="hero-visual" style={{ position: 'relative', padding: '32px 24px 24px' }}>
            <div className="hero-card-float top-left" style={{ position: 'absolute', top: 0, left: 0, zIndex: 2 }}>
              <div className="hero-float-icon" style={{ background: '#EEE6FF' }}>🎯</div>
              <div>
                <div style={{ fontWeight: 700, fontSize: 13, color: '#1F2937' }}>Skill Match</div>
                <div style={{ fontSize: 12, color: '#6B7280' }}>94% compatibility</div>
              </div>
            </div>

            <div className="hero-card-main" style={{ marginTop: 20 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 18 }}>
                <div style={{ width: 40, height: 40, borderRadius: 10, background: 'linear-gradient(135deg,#6C3BEB,#9D6EFF)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 18 }}>🤖</div>
                <div>
                  <div style={{ fontWeight: 700, fontSize: 14, color: '#1F2937' }}>AI Career Advisor</div>
                  <div style={{ fontSize: 12, color: '#10B981', display: 'flex', alignItems: 'center', gap: 4 }}>
                    <span style={{ width: 6, height: 6, borderRadius: '50%', background: '#10B981', display: 'inline-block' }}/>
                    Active
                  </div>
                </div>
              </div>
              <div style={{ background: '#F9FAFB', borderRadius: 10, padding: '14px 16px', marginBottom: 12, border: '1px solid #E5E7EB' }}>
                <div style={{ fontSize: 13, color: '#4B5563', lineHeight: 1.6 }}>
                  Based on your profile, I recommend targeting <strong style={{ color: '#6C3BEB' }}>Product Management</strong> roles. Your skills in data analysis and communication are a strong match.
                </div>
              </div>
              <div style={{ display: 'flex', gap: 8, marginBottom: 14 }}>
                {['Product Manager', 'UX Lead', 'Business Analyst'].map(r => (
                  <span key={r} style={{ fontSize: 11, background: '#EEE6FF', color: '#6C3BEB', padding: '4px 10px', borderRadius: 20, fontWeight: 600 }}>{r}</span>
                ))}
              </div>
              <div style={{ display: 'flex', gap: 8 }}>
                <div style={{ flex: 1, background: '#EEE6FF', borderRadius: 8, padding: '10px 12px', textAlign: 'center' }}>
                  <div style={{ fontSize: 18, fontWeight: 800, color: '#6C3BEB' }}>87%</div>
                  <div style={{ fontSize: 11, color: '#6B7280' }}>Profile Score</div>
                </div>
                <div style={{ flex: 1, background: '#E8F5E9', borderRadius: 8, padding: '10px 12px', textAlign: 'center' }}>
                  <div style={{ fontSize: 18, fontWeight: 800, color: '#10B981' }}>12</div>
                  <div style={{ fontSize: 11, color: '#6B7280' }}>Matches Found</div>
                </div>
                <div style={{ flex: 1, background: '#E0F7FA', borderRadius: 8, padding: '10px 12px', textAlign: 'center' }}>
                  <div style={{ fontSize: 18, fontWeight: 800, color: '#00B7C2' }}>3</div>
                  <div style={{ fontSize: 11, color: '#6B7280' }}>New Skills</div>
                </div>
              </div>
            </div>

            <div className="hero-card-float" style={{ position: 'absolute', bottom: 0, right: -20, background: 'white', border: '1px solid #E5E7EB', borderRadius: 12, padding: '12px 16px', boxShadow: '0 8px 24px rgba(0,0,0,0.1)', display: 'flex', alignItems: 'center', gap: 10 }}>
              <div className="hero-float-icon" style={{ background: '#E8F5E9', fontSize: 20 }}>🎓</div>
              <div>
                <div style={{ fontWeight: 700, fontSize: 13, color: '#1F2937' }}>College Match</div>
                <div style={{ fontSize: 12, color: '#10B981' }}>32 colleges found</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ── Stats ──────────────────────────────────────────── */}
      <div className="stats-bar">
        <div className="stats-bar-inner">
          {stats.map(s => (
            <div key={s.label} className="stat-item">
              <div className="stat-value">{s.value}</div>
              <div className="stat-label">{s.label}</div>
            </div>
          ))}
        </div>
      </div>

      {/* ── Features ───────────────────────────────────────── */}
      <section className="section">
        <div className="section-inner">
          <div className="section-header">
            <div className="section-tag">✦ Platform Features</div>
            <h2 className="section-title">Everything you need to succeed</h2>
            <p className="section-subtitle">
              One unified platform replacing 10+ separate tools — AI advisor, college finder,
              skill analyzer, resume builder, and more.
            </p>
          </div>
          <div className="features-grid">
            {features.map(f => (
              <div key={f.title} className="feature-card">
                <div className="feature-icon" style={{ background: f.bg }}>{f.emoji}</div>
                <h3 className="feature-title">{f.title}</h3>
                <p className="feature-desc">{f.desc}</p>
                <span className="feature-link">
                  Learn more
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
                  </svg>
                </span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── How It Works ───────────────────────────────────── */}
      <section className="section section-alt">
        <div className="section-inner">
          <div className="section-header">
            <div className="section-tag">🚀 How It Works</div>
            <h2 className="section-title">Get started in 3 simple steps</h2>
            <p className="section-subtitle">Your personalized career journey begins immediately after signup</p>
          </div>
          <div className="steps-grid">
            {steps.map(s => (
              <div key={s.n} className="step-item">
                <div className="step-number">{s.n}</div>
                <h3 className="step-title">{s.title}</h3>
                <p className="step-desc">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Testimonials ───────────────────────────────────── */}
      <section className="section">
        <div className="section-inner">
          <div className="section-header">
            <div className="section-tag">💬 Testimonials</div>
            <h2 className="section-title">Loved by 50,000+ students</h2>
            <p className="section-subtitle">Real results from real students across India</p>
          </div>
          <div className="testimonials-grid">
            {testimonials.map(t => (
              <div key={t.name} className="testimonial-card">
                <div className="testimonial-stars">{'★★★★★'}</div>
                <p className="testimonial-text">{t.text}</p>
                <div className="testimonial-author">
                  <div className="testimonial-avatar" style={{ background: t.bg }}>{t.avatar}</div>
                  <div>
                    <div className="testimonial-name">{t.name}</div>
                    <div className="testimonial-role">{t.role}</div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA ────────────────────────────────────────────── */}
      <section className="cta-section">
        <div className="cta-inner">
          <h2 className="cta-title">Ready to accelerate your career?</h2>
          <p className="cta-subtitle">
            Join 50,000+ students who transformed their careers with AI-powered guidance.
            Start free — no credit card required.
          </p>
          <div className="cta-actions">
            <Link to={ROUTES.REGISTER} className="btn btn-white btn-xl">
              Create Free Account →
            </Link>
            <Link to={ROUTES.LOGIN} className="btn btn-outline-white btn-xl">
              Sign In
            </Link>
          </div>
        </div>
      </section>

      {/* ── Footer ─────────────────────────────────────────── */}
      <footer className="footer">
        <div className="footer-inner">
          <div className="footer-grid">
            <div className="footer-brand">
              <div className="footer-logo">
                <div className="navbar-logo-icon">A</div>
                ACEA
              </div>
              <p className="footer-brand-desc">
                AI-powered career and education advisor helping students in India navigate their
                academic and professional journey with confidence.
              </p>
            </div>
            <div>
              <div className="footer-col-title">Platform</div>
              {['AI Advisor', 'College Finder', 'Skill Analysis', 'Resume Builder', 'Mock Interviews'].map(l => (
                <span key={l} className="footer-link">{l}</span>
              ))}
            </div>
            <div>
              <div className="footer-col-title">Resources</div>
              {['Blog', 'Career Guides', 'Scholarships', 'Mentors', 'Events'].map(l => (
                <span key={l} className="footer-link">{l}</span>
              ))}
            </div>
            <div>
              <div className="footer-col-title">Company</div>
              {['About Us', 'Careers', 'Press', 'Contact', 'Privacy Policy'].map(l => (
                <span key={l} className="footer-link">{l}</span>
              ))}
            </div>
          </div>
          <div className="footer-bottom">
            <span>© 2026 ACEA Platform. All rights reserved.</span>
            <span>Made with ❤️ for students in India</span>
          </div>
        </div>
      </footer>
    </div>
  );
}
