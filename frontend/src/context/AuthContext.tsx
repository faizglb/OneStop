import React, {
  createContext, useContext, useEffect, useReducer, useCallback, useMemo
} from 'react';
import type { User, AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth';
import { authService } from '@/services/authService';
import { clearAuthStorage } from '@/services/api';
import { STORAGE_KEYS } from '@/lib/constants';
import toast from 'react-hot-toast';

// ── State ──────────────────────────────────────────────────────────────
interface AuthState {
  user:         User | null;
  isLoading:    boolean;
  isInitialized: boolean;
}

type AuthAction =
  | { type: 'INITIALIZE'; payload: User | null }
  | { type: 'LOGIN_SUCCESS'; payload: User }
  | { type: 'LOGOUT' }
  | { type: 'UPDATE_USER'; payload: User }
  | { type: 'SET_LOADING'; payload: boolean };

const initialState: AuthState = {
  user:          null,
  isLoading:     false,
  isInitialized: false,
};

function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case 'INITIALIZE':      return { ...state, user: action.payload, isInitialized: true };
    case 'LOGIN_SUCCESS':   return { ...state, user: action.payload, isLoading: false };
    case 'LOGOUT':          return { ...state, user: null, isLoading: false };
    case 'UPDATE_USER':     return { ...state, user: action.payload };
    case 'SET_LOADING':     return { ...state, isLoading: action.payload };
    default:                return state;
  }
}

// ── Context ────────────────────────────────────────────────────────────
interface AuthContextValue {
  user:           User | null;
  isLoading:      boolean;
  isInitialized:  boolean;
  isAuthenticated: boolean;

  login:          (data: LoginRequest) => Promise<void>;
  register:       (data: RegisterRequest) => Promise<void>;
  logout:         () => Promise<void>;
  logoutAll:      () => Promise<void>;
  updateUser:     (user: User) => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// ── Provider ───────────────────────────────────────────────────────────
export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Initialize: restore user from localStorage on app load
  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEYS.USER);
    const token  = localStorage.getItem(STORAGE_KEYS.ACCESS_TOKEN);

    if (stored && token) {
      try {
        const user: User = JSON.parse(stored);
        dispatch({ type: 'INITIALIZE', payload: user });
      } catch {
        clearAuthStorage();
        dispatch({ type: 'INITIALIZE', payload: null });
      }
    } else {
      dispatch({ type: 'INITIALIZE', payload: null });
    }
  }, []);

  const login = useCallback(async (data: LoginRequest) => {
    dispatch({ type: 'SET_LOADING', payload: true });
    try {
      const res = await authService.login(data);
      if (res.data) {
        const { accessToken, refreshToken, user } = res.data;
        localStorage.setItem(STORAGE_KEYS.ACCESS_TOKEN,  accessToken);
        localStorage.setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
        localStorage.setItem(STORAGE_KEYS.USER,          JSON.stringify(user));
        dispatch({ type: 'LOGIN_SUCCESS', payload: user });
      }
    } catch (err: any) {
      dispatch({ type: 'SET_LOADING', payload: false });
      throw err;
    }
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    dispatch({ type: 'SET_LOADING', payload: true });
    try {
      await authService.register(data);
      dispatch({ type: 'SET_LOADING', payload: false });
    } catch (err) {
      dispatch({ type: 'SET_LOADING', payload: false });
      throw err;
    }
  }, []);

  const logout = useCallback(async () => {
    const refreshToken = localStorage.getItem(STORAGE_KEYS.REFRESH_TOKEN) ?? '';
    try { await authService.logout(refreshToken); } catch { /* silent */ }
    clearAuthStorage();
    dispatch({ type: 'LOGOUT' });
  }, []);

  const logoutAll = useCallback(async () => {
    try { await authService.logoutAll(); } catch { /* silent */ }
    clearAuthStorage();
    dispatch({ type: 'LOGOUT' });
  }, []);

  const updateUser = useCallback((user: User) => {
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
    dispatch({ type: 'UPDATE_USER', payload: user });
  }, []);

  const value = useMemo<AuthContextValue>(() => ({
    user:            state.user,
    isLoading:       state.isLoading,
    isInitialized:   state.isInitialized,
    isAuthenticated: !!state.user,
    login, register, logout, logoutAll, updateUser,
  }), [state, login, register, logout, logoutAll, updateUser]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// ── Hook ───────────────────────────────────────────────────────────────
export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
