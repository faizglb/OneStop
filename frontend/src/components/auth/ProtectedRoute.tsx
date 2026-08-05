import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/context/AuthContext';
import { ROUTES } from '@/lib/constants';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireEmailVerification?: boolean;
}

/**
 * Wraps routes that require authentication.
 * Redirects to /login with the original path preserved in state.
 */
export default function ProtectedRoute({
  children,
  requireEmailVerification = false,
}: ProtectedRouteProps) {
  const { isAuthenticated, isInitialized, user } = useAuth();
  const location = useLocation();

  // Wait for auth state to initialize before making redirect decisions
  if (!isInitialized) {
    return (
      <div className="min-h-screen bg-navy-900 flex items-center justify-center">
        <div className="flex flex-col items-center gap-4">
          <div className="w-12 h-12 rounded-full border-2 border-purple-500/30 border-t-purple-500 animate-spin" />
          <p className="text-gray-500 text-sm">Loading...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} state={{ from: location }} replace />;
  }

  if (requireEmailVerification && user && !user.emailVerified) {
    return <Navigate to={ROUTES.VERIFY_EMAIL} replace />;
  }

  return <>{children}</>;
}
