import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useAuth } from '@/context/AuthContext';
import { ROUTES, PASSWORD_REGEX } from '@/lib/constants';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';

const schema = z.object({
  firstName:       z.string().min(2, 'Min 2 characters').max(100),
  lastName:        z.string().min(2, 'Min 2 characters').max(100),
  email:           z.string().email('Please enter a valid email'),
  password:        z.string().min(8, 'Min 8 characters').regex(PASSWORD_REGEX, 'Must include uppercase, lowercase, number & special character'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
  agreeTerms:      z.boolean().refine(v => v === true, 'You must accept the terms'),
}).refine(d => d.password === d.confirmPassword, { message: 'Passwords do not match', path: ['confirmPassword'] });

type Form = z.infer<typeof schema>;

const EyeIcon = ({ open }: { open: boolean }) => open
  ? <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
  : <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>;

function getStrength(pw: string): { score: number; label: string; color: string } {
  if (!pw) return { score: 0, label: '', color: '' };
  let s = 0;
  if (pw.length >= 8)              s++;
  if (/[A-Z]/.test(pw))           s++;
  if (/[a-z]/.test(pw))           s++;
  if (/\d/.test(pw))              s++;
  if (/[@$!%*?&_#]/.test(pw))     s++;
  const map: Record<number, { label: string; color: string }> = {
    1: { label: 'Weak',   color: 'var(--error)'   },
    2: { label: 'Fair',   color: 'var(--warning)'  },
    3: { label: 'Good',   color: 'var(--info)'     },
    4: { label: 'Strong', color: 'var(--success)'  },
    5: { label: 'Strong', color: 'var(--success)'  },
  };
  return { score: s, ...(map[s] || { label: '', color: '' }) };
}

const authFeatures = [
  { icon: '🎯', title: 'Skill Gap Analysis', desc: 'Identify skills you need for your dream role' },
  { icon: '📄', title: 'AI Resume Builder', desc: 'Create ATS-optimized resumes in minutes' },
  { icon: '📊', title: 'Salary Benchmarks', desc: 'Know your worth with real market data' },
];

export default function SignupPage() {
  const [showPw, setShowPw]        = useState(false);
  const [showCPw, setShowCPw]      = useState(false);
  const [registered, setRegistered] = useState(false);
  const { register: registerUser, isLoading } = useAuth();

  const { register, handleSubmit, watch, formState: { errors } } = useForm<Form>({
    resolver: zodResolver(schema), mode: 'onChange',
  });

  const pwVal = watch('password', '');
  const strength = getStrength(pwVal);

  const onSubmit = async (data: Form) => {
    try {
      await registerUser({ firstName: data.firstName, lastName: data.lastName, email: data.email, password: data.password, confirmPassword: data.confirmPassword });
      setRegistered(true);
    } catch (err) {
      const msg = (err as AxiosError<any>).response?.data?.message || 'Registration failed. Please try again.';
      toast.error(msg);
    }
  };

  if (registered) {
    return (
      <div className="auth-page">
        <div className="auth-left">
          <div className="auth-left-content">
            <Link to={ROUTES.HOME} className="auth-left-logo" style={{ textDecoration: 'none' }}>
              <div className="auth-left-logo-icon">A</div>
              <span className="auth-left-logo-name">ACEA</span>
            </Link>
            <h2 className="auth-left-tagline">You're almost there!</h2>
            <p className="auth-left-sub">Verify your email to unlock all ACEA features and start your career journey.</p>
          </div>
        </div>
        <div className="auth-right">
          <div className="auth-form-container">
            <div className="success-screen">
              <div className="success-icon-wrap">
                <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#10B981" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
              </div>
              <h2 className="success-title">Check your inbox! 📬</h2>
              <p className="success-message">
                We've sent a verification link to your email address.<br/>
                <span className="success-highlight">Please verify to activate your account.</span>
              </p>
              <Link to={ROUTES.LOGIN} className="btn btn-gradient btn-full" style={{ justifyContent: 'center' }}>
                Go to Sign In →
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-page">
      {/* Left panel */}
      <div className="auth-left">
        <div className="auth-left-content">
          <Link to={ROUTES.HOME} className="auth-left-logo" style={{ textDecoration: 'none' }}>
            <div className="auth-left-logo-icon">A</div>
            <span className="auth-left-logo-name">ACEA</span>
          </Link>
          <h2 className="auth-left-tagline">Join 50,000+ students building stellar careers</h2>
          <p className="auth-left-sub">Create your free account and get instant access to AI career guidance, college finder, and skill analysis tools.</p>
          <div className="auth-features">
            {authFeatures.map(f => (
              <div key={f.title} className="auth-feature-item">
                <div className="auth-feature-icon">{f.icon}</div>
                <div className="auth-feature-text"><h4>{f.title}</h4><p>{f.desc}</p></div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Right form */}
      <div className="auth-right">
        <div className="auth-form-container" style={{ maxWidth: 460 }}>
          <div className="auth-form-header">
            <h1 className="auth-form-title">Create your account ✨</h1>
            <p className="auth-form-subtitle">Free forever · No credit card required</p>
          </div>

          <form className="auth-form" onSubmit={handleSubmit(onSubmit)} noValidate>
            {/* Name Row */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <div className="form-group">
                <label className="form-label">First Name</label>
                <input {...register('firstName')} placeholder="Rahul" className={`input${errors.firstName ? ' is-error' : ''}`} />
                {errors.firstName && <span className="form-error">⚠ {errors.firstName.message}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">Last Name</label>
                <input {...register('lastName')} placeholder="Sharma" className={`input${errors.lastName ? ' is-error' : ''}`} />
                {errors.lastName && <span className="form-error">⚠ {errors.lastName.message}</span>}
              </div>
            </div>

            {/* Email */}
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <input {...register('email')} type="email" placeholder="you@example.com" autoComplete="email"
                className={`input${errors.email ? ' is-error' : ''}`} />
              {errors.email && <span className="form-error">⚠ {errors.email.message}</span>}
            </div>

            {/* Password */}
            <div className="form-group">
              <label className="form-label">Password</label>
              <div className="input-wrap">
                <button type="button" className="input-icon-right" onClick={() => setShowPw(v => !v)}>
                  <EyeIcon open={showPw} />
                </button>
                <input {...register('password')} type={showPw ? 'text' : 'password'} placeholder="Create a strong password"
                  autoComplete="new-password" className={`input has-icon-right${errors.password ? ' is-error' : ''}`} />
              </div>
              {/* Strength bar */}
              {pwVal.length > 0 && (
                <div className="pw-strength">
                  <div className="pw-strength-bars">
                    {[1,2,3,4,5].map(i => (
                      <div key={i} className="pw-strength-bar"
                        style={{ background: i <= strength.score ? strength.color : 'var(--gray-200)' }} />
                    ))}
                  </div>
                  <span className="pw-strength-label" style={{ color: strength.color }}>{strength.label}</span>
                </div>
              )}
              {errors.password && <span className="form-error">⚠ {errors.password.message}</span>}
            </div>

            {/* Confirm Password */}
            <div className="form-group">
              <label className="form-label">Confirm Password</label>
              <div className="input-wrap">
                <button type="button" className="input-icon-right" onClick={() => setShowCPw(v => !v)}>
                  <EyeIcon open={showCPw} />
                </button>
                <input {...register('confirmPassword')} type={showCPw ? 'text' : 'password'} placeholder="Repeat your password"
                  autoComplete="new-password" className={`input has-icon-right${errors.confirmPassword ? ' is-error' : ''}`} />
              </div>
              {errors.confirmPassword && <span className="form-error">⚠ {errors.confirmPassword.message}</span>}
            </div>

            {/* Terms */}
            <div className="form-group">
              <div className="checkbox-group">
                <input {...register('agreeTerms')} type="checkbox" id="terms" className="checkbox" />
                <label htmlFor="terms" className="checkbox-label">
                  I agree to the <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>
                </label>
              </div>
              {errors.agreeTerms && <span className="form-error">⚠ {errors.agreeTerms.message}</span>}
            </div>

            <button type="submit" disabled={isLoading} className="btn btn-gradient btn-full" style={{ height: 46, fontSize: 15 }}>
              {isLoading ? <><span className="spinner" /> Creating account...</> : 'Create Free Account →'}
            </button>
          </form>

          <div className="auth-footer-link">
            Already have an account? <Link to={ROUTES.LOGIN}>Sign in</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
