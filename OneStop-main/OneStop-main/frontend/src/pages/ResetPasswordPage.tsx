import { useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { authService } from '@/services/authService';
import { ROUTES, PASSWORD_REGEX } from '@/lib/constants';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';

const schema = z.object({
  newPassword:        z.string().min(8, 'Min 8 characters').regex(PASSWORD_REGEX, 'Must include uppercase, lowercase, number & special character'),
  confirmNewPassword: z.string().min(1, 'Please confirm your password'),
}).refine(d => d.newPassword === d.confirmNewPassword, { message: 'Passwords do not match', path: ['confirmNewPassword'] });

type Form = z.infer<typeof schema>;

const EyeIcon = ({ open }: { open: boolean }) => open
  ? <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
  : <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>;

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams();
  const [showPw, setShowPw]    = useState(false);
  const [showCPw, setShowCPw]  = useState(false);
  const [success, setSuccess]   = useState(false);
  const navigate = useNavigate();
  const token = searchParams.get('token');

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<Form>({ resolver: zodResolver(schema) });

  const onSubmit = async (data: Form) => {
    if (!token) return;
    try {
      await authService.resetPassword({ token, newPassword: data.newPassword, confirmNewPassword: data.confirmNewPassword });
      setSuccess(true);
      setTimeout(() => navigate(ROUTES.LOGIN), 3000);
    } catch (err) {
      toast.error((err as AxiosError<any>).response?.data?.message || 'Reset failed. The link may have expired.');
    }
  };

  // Missing token
  if (!token) {
    return (
      <div className="auth-page">
        <div className="auth-left" style={{ background: 'linear-gradient(150deg,#F44336 0%,#C62828 100%)' }}>
          <div className="auth-left-content">
            <Link to={ROUTES.HOME} className="auth-left-logo" style={{ textDecoration: 'none' }}>
              <div className="auth-left-logo-icon">A</div>
              <span className="auth-left-logo-name">ACEA</span>
            </Link>
            <h2 className="auth-left-tagline">Invalid reset link</h2>
            <p className="auth-left-sub">The password reset link is missing or has already been used.</p>
          </div>
        </div>
        <div className="auth-right">
          <div className="auth-form-container">
            <div className="success-screen">
              <div className="success-icon-wrap warning-icon">
                <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                  <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
                </svg>
              </div>
              <h2 className="success-title">Invalid Reset Link</h2>
              <p className="success-message">This link is invalid or has already been used. Please request a new password reset.</p>
              <Link to={ROUTES.FORGOT_PASSWORD} className="btn btn-gradient btn-full" style={{ justifyContent: 'center' }}>
                Request New Link
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <div className="auth-left">
        <div className="auth-left-content">
          <Link to={ROUTES.HOME} className="auth-left-logo" style={{ textDecoration: 'none' }}>
            <div className="auth-left-logo-icon">A</div>
            <span className="auth-left-logo-name">ACEA</span>
          </Link>
          <h2 className="auth-left-tagline">Create a new strong password</h2>
          <p className="auth-left-sub">Choose a password that's at least 8 characters and includes a mix of letters, numbers, and symbols.</p>
          <div className="auth-features" style={{ marginTop: 20 }}>
            {[
              { icon: '✅', title: 'Min 8 characters', desc: 'The longer the better' },
              { icon: '🔡', title: 'Mixed case', desc: 'Include UPPER and lowercase letters' },
              { icon: '🔣', title: 'Special character', desc: 'Add @ # $ ! for extra strength' },
            ].map(f => (
              <div key={f.title} className="auth-feature-item">
                <div className="auth-feature-icon">{f.icon}</div>
                <div className="auth-feature-text"><h4>{f.title}</h4><p>{f.desc}</p></div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="auth-right">
        <div className="auth-form-container">
          {success ? (
            <div className="success-screen">
              <div className="success-icon-wrap">
                <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#10B981" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
              </div>
              <h2 className="success-title">Password Reset! 🎉</h2>
              <p className="success-message">
                Your password has been updated. All active sessions have been logged out for security.
                <br/><span className="success-highlight">Redirecting you to Sign In...</span>
              </p>
              <Link to={ROUTES.LOGIN} className="btn btn-gradient btn-full" style={{ justifyContent: 'center' }}>
                Sign In Now →
              </Link>
            </div>
          ) : (
            <>
              <div className="auth-form-header">
                <h1 className="auth-form-title">Set new password 🔐</h1>
                <p className="auth-form-subtitle">Enter and confirm your new password below</p>
              </div>
              <form className="auth-form" onSubmit={handleSubmit(onSubmit)} noValidate>
                <div className="form-group">
                  <label className="form-label">New Password</label>
                  <div className="input-wrap">
                    <button type="button" className="input-icon-right" onClick={() => setShowPw(v => !v)}>
                      <EyeIcon open={showPw} />
                    </button>
                    <input {...register('newPassword')} type={showPw ? 'text' : 'password'} placeholder="Enter new password"
                      autoComplete="new-password" className={`input has-icon-right${errors.newPassword ? ' is-error' : ''}`} />
                  </div>
                  {errors.newPassword && <span className="form-error">⚠ {errors.newPassword.message}</span>}
                </div>
                <div className="form-group">
                  <label className="form-label">Confirm New Password</label>
                  <div className="input-wrap">
                    <button type="button" className="input-icon-right" onClick={() => setShowCPw(v => !v)}>
                      <EyeIcon open={showCPw} />
                    </button>
                    <input {...register('confirmNewPassword')} type={showCPw ? 'text' : 'password'} placeholder="Repeat new password"
                      autoComplete="new-password" className={`input has-icon-right${errors.confirmNewPassword ? ' is-error' : ''}`} />
                  </div>
                  {errors.confirmNewPassword && <span className="form-error">⚠ {errors.confirmNewPassword.message}</span>}
                </div>
                <button type="submit" disabled={isSubmitting} className="btn btn-gradient btn-full" style={{ height: 46, fontSize: 15 }}>
                  {isSubmitting ? <><span className="spinner" /> Resetting...</> : 'Reset Password →'}
                </button>
              </form>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
