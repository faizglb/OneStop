import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuth } from '@/context/AuthContext';
import { ROUTES } from '@/lib/constants';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';

const schema = z.object({
  email:    z.string().email('Please enter a valid email address'),
  password: z.string().min(1, 'Password is required'),
});
type Form = z.infer<typeof schema>;

const EyeIcon = ({ open }: { open: boolean }) => open
  ? <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
  : <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>;

const MailIcon = () => <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>;

const authFeatures = [
  { icon: '🤖', title: 'AI Career Guidance', desc: 'Get personalized career recommendations powered by AI' },
  { icon: '🎓', title: '50,000+ Colleges', desc: 'Find and compare the best institutions across India' },
  { icon: '🏆', title: 'Mock Interviews', desc: 'Practice interviews with real-time AI feedback' },
];

export default function LoginPage() {
  const [showPw, setShowPw] = useState(false);
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as any)?.from?.pathname || ROUTES.DASHBOARD;

  const { register, handleSubmit, formState: { errors } } = useForm<Form>({ resolver: zodResolver(schema) });

  const onSubmit = async (data: Form) => {
    try {
      await login(data);
      toast.success('Welcome back! 🎉');
      navigate(from, { replace: true });
    } catch (err) {
      const msg = (err as AxiosError<any>).response?.data?.message || 'Invalid email or password';
      toast.error(msg);
    }
  };

  return (
    <div className="auth-page">
      {/* Left Branding Panel */}
      <div className="auth-left">
        <div className="auth-left-content">
          <Link to={ROUTES.HOME} className="auth-left-logo" style={{ textDecoration: 'none' }}>
            <div className="auth-left-logo-icon">A</div>
            <span className="auth-left-logo-name">ACEA</span>
          </Link>

          <h2 className="auth-left-tagline">
            Unlock your career potential with AI-powered guidance
          </h2>
          <p className="auth-left-sub">
            Join 50,000+ students navigating their academic and professional journey with confidence.
          </p>

          <div className="auth-features">
            {authFeatures.map(f => (
              <div key={f.title} className="auth-feature-item">
                <div className="auth-feature-icon">{f.icon}</div>
                <div className="auth-feature-text">
                  <h4>{f.title}</h4>
                  <p>{f.desc}</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="auth-left-footer">
          <div className="auth-testimonial">
            <p className="auth-testimonial-text">
              "ACEA helped me transition from engineering to product management. The AI roadmap was spot on!"
            </p>
            <div className="auth-testimonial-author">
              <div className="auth-testimonial-avatar">AK</div>
              <div>
                <div className="auth-testimonial-name">Arjun Kapoor</div>
                <div className="auth-testimonial-role">Product Manager @ Flipkart</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Right Form Panel */}
      <div className="auth-right">
        <div className="auth-form-container">
          <div className="auth-form-header">
            <h1 className="auth-form-title">Welcome back! 👋</h1>
            <p className="auth-form-subtitle">Enter your credentials to access your ACEA account</p>
          </div>

          <form className="auth-form" onSubmit={handleSubmit(onSubmit)} noValidate>
            {/* Email */}
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <div className="input-wrap">
                <span className="input-icon-left"><MailIcon /></span>
                <input
                  {...register('email')}
                  type="email"
                  placeholder="you@example.com"
                  autoComplete="email"
                  className={`input has-icon-left${errors.email ? ' is-error' : ''}`}
                />
              </div>
              {errors.email && <span className="form-error">⚠ {errors.email.message}</span>}
            </div>

            {/* Password */}
            <div className="form-group">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <label className="form-label">Password</label>
                <Link to={ROUTES.FORGOT_PASSWORD} style={{ fontSize: 13, color: 'var(--primary)', fontWeight: 600, textDecoration: 'none' }}>
                  Forgot password?
                </Link>
              </div>
              <div className="input-wrap">
                <button type="button" className="input-icon-right" style={{ right: 12 }} onClick={() => setShowPw(v => !v)}>
                  <EyeIcon open={showPw} />
                </button>
                <input
                  {...register('password')}
                  type={showPw ? 'text' : 'password'}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  className={`input has-icon-right${errors.password ? ' is-error' : ''}`}
                />
              </div>
              {errors.password && <span className="form-error">⚠ {errors.password.message}</span>}
            </div>

            <button type="submit" disabled={isLoading} className="btn btn-gradient btn-full" style={{ height: 46, fontSize: 15, marginTop: 4 }}>
              {isLoading ? <><span className="spinner" /> Signing in...</> : 'Sign In to ACEA →'}
            </button>
          </form>

          <div className="auth-footer-link">
            Don't have an account? <Link to={ROUTES.REGISTER}>Create one free</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
