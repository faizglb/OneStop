import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { authService } from '@/services/authService';
import { ROUTES } from '@/lib/constants';

const schema = z.object({ email: z.string().email('Please enter a valid email address') });
type Form = z.infer<typeof schema>;

export default function ForgotPasswordPage() {
  const [sent, setSent] = useState(false);
  const [sentEmail, setSentEmail] = useState('');
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<Form>({ resolver: zodResolver(schema) });

  const onSubmit = async (data: Form) => {
    try { await authService.forgotPassword({ email: data.email }); } catch {}
    setSentEmail(data.email);
    setSent(true);
  };

  return (
    <div className="auth-page">
      {/* Left panel */}
      <div className="auth-left">
        <div className="auth-left-content">
          <Link to={ROUTES.HOME} className="auth-left-logo" style={{ textDecoration: 'none' }}>
            <div className="auth-left-logo-icon">A</div>
            <span className="auth-left-logo-name">ACEA</span>
          </Link>
          <h2 className="auth-left-tagline">Forgot your password? No worries!</h2>
          <p className="auth-left-sub">We'll send you a secure reset link to your email address. The link expires in 2 hours.</p>
          <div className="auth-features" style={{ marginTop: 20 }}>
            {[
              { icon: '🔒', title: 'Secure Reset', desc: 'One-time link with 2-hour expiry' },
              { icon: '📧', title: 'Email Delivery', desc: 'Instant email from noreply@acea.io' },
              { icon: '🛡️', title: 'Private & Safe', desc: 'Account security is our top priority' },
            ].map(f => (
              <div key={f.title} className="auth-feature-item">
                <div className="auth-feature-icon">{f.icon}</div>
                <div className="auth-feature-text"><h4>{f.title}</h4><p>{f.desc}</p></div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Right panel */}
      <div className="auth-right">
        <div className="auth-form-container">
          {sent ? (
            <div className="success-screen">
              <div className="success-icon-wrap">
                <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#10B981" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                  <polyline points="22,6 12,13 2,6"/>
                </svg>
              </div>
              <h2 className="success-title">Check your email 📬</h2>
              <p className="success-message">
                If an account exists for <span className="success-highlight">{sentEmail}</span>,
                we've sent a password reset link. The link expires in <strong>2 hours</strong>.
              </p>
              <Link to={ROUTES.LOGIN} className="btn btn-gradient btn-full" style={{ justifyContent: 'center' }}>
                ← Back to Sign In
              </Link>
              <div style={{ textAlign: 'center', marginTop: 16, fontSize: 13, color: 'var(--text-muted)' }}>
                Didn't receive it?{' '}
                <span style={{ color: 'var(--primary)', fontWeight: 600, cursor: 'pointer' }}
                  onClick={() => setSent(false)}>Try again</span>
              </div>
            </div>
          ) : (
            <>
              <div className="auth-form-header">
                <h1 className="auth-form-title">Reset your password 🔐</h1>
                <p className="auth-form-subtitle">Enter your registered email address and we'll send you a secure reset link</p>
              </div>

              <form className="auth-form" onSubmit={handleSubmit(onSubmit)} noValidate>
                <div className="form-group">
                  <label className="form-label">Email Address</label>
                  <input {...register('email')} type="email" placeholder="you@example.com"
                    className={`input${errors.email ? ' is-error' : ''}`} />
                  {errors.email && <span className="form-error">⚠ {errors.email.message}</span>}
                </div>
                <button type="submit" disabled={isSubmitting} className="btn btn-gradient btn-full" style={{ height: 46, fontSize: 15 }}>
                  {isSubmitting ? <><span className="spinner" /> Sending link...</> : 'Send Reset Link →'}
                </button>
              </form>

              <div className="auth-footer-link">
                Remember your password? <Link to={ROUTES.LOGIN}>Sign in</Link>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
