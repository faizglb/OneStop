import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { authService } from '@/services/authService';
import { ROUTES } from '@/lib/constants';

type Status = 'loading' | 'success' | 'error' | 'missing';

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState<Status>('loading');
  const [message, setMessage] = useState('');
  const token = searchParams.get('token');

  useEffect(() => {
    if (!token) { setStatus('missing'); return; }
    authService.verifyEmail(token)
      .then(() => setStatus('success'))
      .catch(err => { setMessage(err.response?.data?.message || ''); setStatus('error'); });
  }, [token]);

  const states = {
    loading: {
      icon: (
        <div className="verify-spinner-wrap">
          <div className="spinner-lg" />
        </div>
      ),
      title: 'Verifying your email...',
      body: 'Please wait a moment while we verify your email address.',
      cta: null,
    },
    success: {
      icon: (
        <div className="verify-spinner-wrap" style={{ background: 'var(--success-light)' }}>
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#10B981" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <polyline points="20 6 9 17 4 12"/>
          </svg>
        </div>
      ),
      title: 'Email Verified! 🎉',
      body: 'Your email has been successfully verified. You can now sign in to your ACEA account and start exploring.',
      cta: <Link to={ROUTES.LOGIN} className="btn btn-gradient btn-full" style={{ justifyContent: 'center', marginTop: 8 }}>Sign In to ACEA →</Link>,
    },
    error: {
      icon: (
        <div className="verify-spinner-wrap" style={{ background: 'var(--error-light)' }}>
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#EF4444" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
          </svg>
        </div>
      ),
      title: 'Verification Failed',
      body: message || 'This verification link is invalid or has already expired. Please request a new one.',
      cta: (
        <div style={{ display: 'flex', gap: 12, flexDirection: 'column' }}>
          <Link to={ROUTES.LOGIN} className="btn btn-gradient btn-full" style={{ justifyContent: 'center' }}>Go to Sign In</Link>
        </div>
      ),
    },
    missing: {
      icon: (
        <div className="verify-spinner-wrap" style={{ background: 'var(--warning-light)' }}>
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
        </div>
      ),
      title: 'Invalid Link',
      body: 'No verification token was found in the URL. Please use the link from your verification email.',
      cta: <Link to={ROUTES.LOGIN} className="btn btn-secondary btn-full" style={{ justifyContent: 'center', marginTop: 8 }}>← Back to Sign In</Link>,
    },
  };

  const s = states[status];

  return (
    <div className="verify-page">
      <div className="verify-card">
        {/* Logo */}
        <Link to={ROUTES.HOME} style={{ display: 'inline-flex', alignItems: 'center', gap: 8, textDecoration: 'none', marginBottom: 32, color: 'var(--primary)', fontWeight: 800, fontSize: 18 }}>
          <div className="navbar-logo-icon">A</div>
          ACEA
        </Link>

        {s.icon}
        <h2 style={{ fontSize: 22, fontWeight: 800, color: 'var(--text-primary)', marginBottom: 10 }}>{s.title}</h2>
        <p style={{ fontSize: 14, color: 'var(--text-secondary)', lineHeight: 1.7, marginBottom: s.cta ? 24 : 0 }}>{s.body}</p>
        {s.cta}
      </div>
    </div>
  );
}
