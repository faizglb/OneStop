import api from './api';

export interface AdminDashboardStats {
  totalUsers: number;
  activeUsers: number;
  newRegistrations: number;
  assessmentsCompleted: number;
  careerRecommendationsGenerated: number;
  collegeRecommendationsGenerated: number;
  scholarshipRecommendationsGenerated: number;
  roadmapsGenerated: number;
  resumeAnalyses: number;
  skillGapReports: number;
  chatSessions: number;
  dailyActiveUsers: number;
  monthlyActiveUsers: number;
  mostPopularCareers: Array<{ name: string; count: number; category: string }>;
  mostPopularColleges: Array<{ name: string; views: number; nirfRank: number }>;
  mostPopularScholarships: Array<{ name: string; applications: number; amount: string }>;
  systemHealth: string;
  storageUsageGb: number;
  apiUsageRequestsToday: number;
  aiTokenConsumptionToday: number;
  cpuUsagePercent: number;
  memoryUsagePercent: number;
  diskUsagePercent: number;
  apiLatencyMs: number;
  errorRatePercent: number;
}

export interface AdminUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  avatarUrl?: string;
  role: 'SUPER_ADMIN' | 'ADMIN' | 'CONTENT_MANAGER' | 'STUDENT' | 'PREMIUM_STUDENT' | 'MENTOR' | 'HR_MANAGER';
  accountTier: string;
  active: boolean;
  suspended: boolean;
  suspensionReason?: string;
  emailVerified: boolean;
  lastLoginAt?: string;
  loginCount: number;
  createdAt: string;
}

export interface RolePermission {
  role: string;
  roleName: string;
  description: string;
  permissions: string[];
}

export interface AiConfig {
  llmProvider: 'GEMINI' | 'OPENAI';
  geminiModel: string;
  openAiModel: string;
  temperature: number;
  topP: number;
  maxTokens: number;
  embeddingModel: string;
  similarityThreshold: number;
  chunkingStrategy: string;
}

export interface KnowledgeDocument {
  id: string;
  filename: string;
  fileType: string;
  fileSize: number;
  docCategory: string;
  chunkCount: number;
  vectorId: string;
  status: string;
  createdAt: string;
}

export interface AuditLog {
  id: string;
  actorId: string;
  actorEmail: string;
  actorRole: string;
  action: string;
  module: string;
  targetId?: string;
  details?: string;
  ipAddress?: string;
  createdAt: string;
}

export interface AdminNotification {
  id: string;
  title: string;
  message: string;
  type: string;
  targetRole: string;
  priority: string;
  active: boolean;
  createdAt: string;
}

export const adminService = {
  // Dashboard
  getDashboardStats: async (): Promise<AdminDashboardStats> => {
    const res = await api.get('/admin/dashboard/stats');
    return res.data;
  },

  getSystemHealth: async () => {
    const res = await api.get('/admin/dashboard/health');
    return res.data;
  },

  // Users
  getUsers: async (page = 0, size = 10, search = '', role = '') => {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    if (search) params.append('search', search);
    if (role) params.append('role', role);
    const res = await api.get(`/admin/users?${params.toString()}`);
    return res.data;
  },

  updateUser: async (userId: string, data: Partial<AdminUser>) => {
    const res = await api.put(`/admin/users/${userId}`, data);
    return res.data;
  },

  suspendUser: async (userId: string, reason: string) => {
    const res = await api.post(`/admin/users/${userId}/suspend`, { reason });
    return res.data;
  },

  activateUser: async (userId: string) => {
    const res = await api.post(`/admin/users/${userId}/activate`);
    return res.data;
  },

  deleteUser: async (userId: string) => {
    await api.delete(`/admin/users/${userId}`);
  },

  resetPassword: async (userId: string, newPassword: string) => {
    await api.post(`/admin/users/${userId}/reset-password`, { newPassword });
  },

  assignRole: async (userId: string, role: string) => {
    const res = await api.post(`/admin/users/${userId}/assign-role?role=${role}`);
    return res.data;
  },

  exportUsers: async (): Promise<AdminUser[]> => {
    const res = await api.get('/admin/users/export');
    return res.data;
  },

  // RBAC
  getRoles: async (): Promise<RolePermission[]> => {
    const res = await api.get('/admin/roles');
    return res.data;
  },

  grantPermission: async (role: string, permission: string) => {
    await api.post(`/admin/roles/${role}/permissions/grant?permission=${permission}`);
  },

  revokePermission: async (role: string, permission: string) => {
    await api.post(`/admin/roles/${role}/permissions/revoke?permission=${permission}`);
  },

  // AI Configuration
  getAiConfig: async (): Promise<AiConfig> => {
    const res = await api.get('/admin/ai/config');
    return res.data;
  },

  updateAiConfig: async (config: Partial<AiConfig>): Promise<AiConfig> => {
    const res = await api.put('/admin/ai/config', config);
    return res.data;
  },

  // Knowledge Base
  getKnowledgeDocs: async (): Promise<KnowledgeDocument[]> => {
    const res = await api.get('/admin/ai/knowledge-base');
    return res.data;
  },

  uploadKnowledgeDoc: async (file: File, category: string): Promise<KnowledgeDocument> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('category', category);
    const res = await api.post('/admin/ai/knowledge-base/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data;
  },

  deleteKnowledgeDoc: async (docId: string) => {
    await api.delete(`/admin/ai/knowledge-base/${docId}`);
  },

  reindexKnowledgeBase: async () => {
    await api.post('/admin/ai/knowledge-base/reindex');
  },

  // Audit Logs
  getAuditLogs: async (page = 0, size = 15, query = '') => {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    if (query) params.append('query', query);
    const res = await api.get(`/admin/audit-logs?${params.toString()}`);
    return res.data;
  },

  // Notifications
  getNotifications: async (page = 0, size = 10) => {
    const res = await api.get(`/admin/notifications?page=${page}&size=${size}`);
    return res.data;
  },

  createNotification: async (notification: Partial<AdminNotification>): Promise<AdminNotification> => {
    const res = await api.post('/admin/notifications', notification);
    return res.data;
  },

  deleteNotification: async (id: string) => {
    await api.delete(`/admin/notifications/${id}`);
  },
};
