import { Link } from 'react-router-dom';
import { ROUTES } from '@/lib/constants';

export default function NotFoundPage() {
  return (
    <div className="notfound-page">
      <div className="notfound-inner">
        {/* Logo */}
        <Link to={ROUTES.HOME} style={{ display: 'inline-flex', alignItems: 'center', gap: 10, textDecoration: 'none', marginBottom: 40 }}>
          <div className="navbar-logo-icon">A</div>
          <span style={{ fontSize: 22, fontWeight: 800, color: 'var(--primary)', fontFamily: 'var(--font-display)' }}>ACEA</span>
        </Link>

        {/* 404 */}
        <div className="notfound-code">404</div>

        <h1 className="notfound-title">Page Not Found</h1>
        <p className="notfound-subtitle">
          Oops! The page you're looking for doesn't exist or has been moved.
          Let's get you back on track.
        </p>

        <div className="notfound-actions">
          <Link to={ROUTES.HOME} className="btn btn-gradient btn-lg">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            Back to Home
          </Link>
          <Link to={ROUTES.LOGIN} className="btn btn-secondary btn-lg">
            Sign In
          </Link>
        </div>

        {/* Illustration */}
        <div style={{ marginTop: 48, opacity: 0.06 }}>
          <svg width="200" height="120" viewBox="0 0 200 120" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="100" cy="60" r="50" stroke="#6C3BEB" strokeWidth="3"/>
            <path d="M75 60 Q100 30 125 60 Q100 90 75 60" stroke="#6C3BEB" strokeWidth="2" fill="none"/>
            <circle cx="88" cy="52" r="5" fill="#6C3BEB"/>
            <circle cx="112" cy="52" r="5" fill="#6C3BEB"/>
          </svg>
        </div>
      </div>
    </div>
  );
}
