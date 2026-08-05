import api from './api';
import {
  ChatSession,
  ChatMessage,
  ChatMessageRequest,
  ChatFeedbackRequest
} from '@/types/chat';

export const chatService = {
  createSession: async (): Promise<ChatSession> => {
    const { data } = await api.post<ChatSession>('/chat/session');
    return data;
  },

  getSessions: async (): Promise<ChatSession[]> => {
    const { data } = await api.get<ChatSession[]>('/chat/session');
    return data;
  },

  sendMessage: async (req: ChatMessageRequest): Promise<ChatMessage> => {
    const { data } = await api.post<ChatMessage>('/chat/message', req);
    return data;
  },

  getHistory: async (sessionId: string): Promise<ChatMessage[]> => {
    const { data } = await api.get<ChatMessage[]>(`/chat/history/${sessionId}`);
    return data;
  },

  deleteSession: async (sessionId: string): Promise<void> => {
    await api.delete(`/chat/session/${sessionId}`);
  },

  submitFeedback: async (req: ChatFeedbackRequest): Promise<void> => {
    await api.post('/chat/feedback', req);
  },

  searchConversations: async (query: string): Promise<ChatMessage[]> => {
    const { data } = await api.get<ChatMessage[]>('/chat/search', { params: { query } });
    return data;
  }
};
