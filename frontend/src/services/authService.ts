import api from './api';
import type {
  AuthResponse, LoginRequest, RegisterRequest,
  ForgotPasswordRequest, ResetPasswordRequest, User
} from '@/types/auth';
import type { ApiResponse } from '@/types/api';

// ── Auth Service ───────────────────────────────────────────────────────

export const authService = {

  register: async (data: RegisterRequest): Promise<ApiResponse<User>> => {
    const response = await api.post<ApiResponse<User>>('/auth/register', data);
    return response.data;
  },

  login: async (data: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await api.post<ApiResponse<AuthResponse>>('/auth/login', data);
    return response.data;
  },

  logout: async (refreshToken: string): Promise<void> => {
    await api.post('/auth/logout', { refreshToken });
  },

  logoutAll: async (): Promise<void> => {
    await api.post('/auth/logout-all');
  },

  refreshToken: async (refreshToken: string): Promise<ApiResponse<AuthResponse>> => {
    const response = await api.post<ApiResponse<AuthResponse>>('/auth/refresh', { refreshToken });
    return response.data;
  },

  verifyEmail: async (token: string): Promise<ApiResponse<void>> => {
    const response = await api.get<ApiResponse<void>>(`/auth/verify-email?token=${token}`);
    return response.data;
  },

  resendVerification: async (email: string): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>('/auth/resend-verification', { email });
    return response.data;
  },

  forgotPassword: async (data: ForgotPasswordRequest): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>('/auth/forgot-password', data);
    return response.data;
  },

  resetPassword: async (data: ResetPasswordRequest): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>('/auth/reset-password', data);
    return response.data;
  },

  getMe: async (): Promise<ApiResponse<User>> => {
    const response = await api.get<ApiResponse<User>>('/auth/me');
    return response.data;
  },
};
