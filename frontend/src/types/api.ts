// ── API Response Types ─────────────────────────────────────────────────────

export interface ApiResponse<T = unknown> {
  success: boolean;
  status: number;
  message: string;
  data?: T;
  errors?: Record<string, string[]>;
  timestamp: string;
  path?: string;
}

export interface ApiError {
  success: false;
  status: number;
  message: string;
  errors?: Record<string, string[]>;
  timestamp: string;
  path?: string;
}

export type PaginatedResponse<T> = ApiResponse<{
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  isFirst: boolean;
  isLast: boolean;
}>;
