export interface SourceCitation {
  documentTitle: string;
  sourceType: string;
  chunkText: string;
  relevanceScore: number;
  knowledgeSource: string;
}

export interface ChatMessage {
  id: string;
  sessionId: string;
  sender: 'USER' | 'ASSISTANT';
  message: string;
  messageType?: string;
  agentUsed?: string;
  confidenceScore?: number;
  sources?: SourceCitation[];
  timestamp: string;
}

export interface ChatSession {
  id: string;
  title: string;
  status: 'ACTIVE' | 'ARCHIVED';
  startedAt: string;
  lastMessageAt: string;
  messageCount: number;
  lastMessagePreview?: string;
}

export interface ChatMessageRequest {
  sessionId?: string;
  message: string;
}

export interface ChatFeedbackRequest {
  messageId: string;
  rating: number;
  feedbackText?: string;
}
