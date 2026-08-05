import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { chatService } from '@/services/chatService';
import { ChatSession, ChatMessage } from '@/types/chat';
import { ConversationList } from '@/components/chat/ConversationList';
import { ChatWindow } from '@/components/chat/ChatWindow';
import toast from 'react-hot-toast';

export const ChatPage: React.FC = () => {
  const { sessionId } = useParams<{ sessionId?: string }>();
  const navigate = useNavigate();

  const [sessions, setSessions] = useState<ChatSession[]>([]);
  const [activeSessionId, setActiveSessionId] = useState<string | undefined>(sessionId);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  // Load Sessions
  const loadSessions = async () => {
    try {
      const data = await chatService.getSessions();
      setSessions(data);
    } catch {
      toast.error('Failed to load chat history.');
    }
  };

  useEffect(() => {
    loadSessions();
  }, []);

  // Load History for Active Session
  useEffect(() => {
    if (sessionId) {
      setActiveSessionId(sessionId);
      chatService.getHistory(sessionId)
        .then(setMessages)
        .catch(() => toast.error('Failed to load session messages.'));
    } else {
      setActiveSessionId(undefined);
      setMessages([]);
    }
  }, [sessionId]);

  // Create New Session
  const handleNewSession = async () => {
    try {
      const newSess = await chatService.createSession();
      setSessions((prev) => [newSess, ...prev]);
      navigate(`/chat/${newSess.id}`);
    } catch {
      toast.error('Failed to create new chat session.');
    }
  };

  // Select Session
  const handleSelectSession = (id: string) => {
    navigate(`/chat/${id}`);
  };

  // Delete Session
  const handleDeleteSession = async (id: string) => {
    try {
      await chatService.deleteSession(id);
      setSessions((prev) => prev.filter((s) => s.id !== id));
      if (activeSessionId === id) {
        navigate('/chat');
      }
      toast.success('Conversation archived.');
    } catch {
      toast.error('Failed to archive session.');
    }
  };

  // Send Message
  const handleSendMessage = async (text: string) => {
    setIsLoading(true);

    // Optimistic local add
    const tempUserMsg: ChatMessage = {
      id: `temp-${Date.now()}`,
      sessionId: activeSessionId || '',
      sender: 'USER',
      message: text,
      timestamp: new Date().toISOString()
    };
    setMessages((prev) => [...prev, tempUserMsg]);

    try {
      const assistantResponse = await chatService.sendMessage({
        sessionId: activeSessionId,
        message: text
      });

      // Update active session ID if it was auto-created
      if (!activeSessionId) {
        setActiveSessionId(assistantResponse.sessionId);
        navigate(`/chat/${assistantResponse.sessionId}`, { replace: true });
        loadSessions();
      }

      setMessages((prev) => [...prev.filter((m) => !m.id.startsWith('temp-')), tempUserMsg, assistantResponse]);
    } catch {
      toast.error('Failed to send message.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex h-screen bg-gray-100 overflow-hidden">
      <ConversationList
        sessions={sessions}
        activeSessionId={activeSessionId}
        onSelectSession={handleSelectSession}
        onNewSession={handleNewSession}
        onDeleteSession={handleDeleteSession}
      />
      <ChatWindow
        messages={messages}
        isLoading={isLoading}
        onSendMessage={handleSendMessage}
        onSelectSuggestedPrompt={handleSendMessage}
      />
    </div>
  );
};

export default ChatPage;
