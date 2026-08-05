// ── Types ─────────────────────────────────────────────────────────────────

export type Role = 'STUDENT' | 'PREMIUM_STUDENT' | 'MENTOR' | 'HR_MANAGER' | 'ADMIN' | 'SUPER_ADMIN';

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  phone?: string;
  avatarUrl?: string;
  role: Role;
  accountTier: 'FREE' | 'PREMIUM' | 'ENTERPRISE';
  emailVerified: boolean;
  active: boolean;
  locale: string;
  timezone: string;
  lastLoginAt?: string;
  createdAt: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface AuthResponse extends AuthTokens {
  user: User;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  phone?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmNewPassword: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}
