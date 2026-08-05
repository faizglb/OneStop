import React, { useState } from 'react';
import { ChatMessage } from '@/types/chat';
import { SourceCitations } from './SourceCitations';
import { FeedbackDialog } from './FeedbackDialog';
import toast from 'react-hot-toast';
import { chatService } from '@/services/chatService';

interface MessageBubbleProps {
  message: ChatMessage;
}

export const MessageBubble: React.FC<MessageBubbleProps> = ({ message }) => {
  const isUser = message.sender === 'USER';
  const [showFeedbackModal, setShowFeedbackModal] = useState(false);

  const copyToClipboard = () => {
    navigator.clipboard.writeText(message.message);
    toast.success('Message copied to clipboard!');
  };

  const handleFeedbackSubmit = async (rating: number, text: string) => {
    try {
      await chatService.submitFeedback({
        messageId: message.id,
        rating,
        feedbackText: text
      });
      toast.success('Thank you for your feedback!');
    } catch {
      toast.error('Failed to submit feedback.');
    }
  };

  return (
    <div className={`flex w-full my-2.5 ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div className={`max-w-[82%] rounded-2xl px-4 py-3 shadow-sm ${
        isUser
          ? 'bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-br-none'
          : 'bg-white border border-gray-200/80 text-gray-800 rounded-bl-none'
      }`}>
        {/* Header Badge for Assistant */}
        {!isUser && (
          <div className="flex items-center justify-between border-b border-gray-100 pb-1.5 mb-2 text-xs">
            <span className="font-semibold text-indigo-600 flex items-center space-x-1">
              <span>🤖</span>
              <span>{message.agentUsed || 'AI Career Advisor'}</span>
            </span>
            {message.confidenceScore && (
              <span className="text-[10px] bg-indigo-50 text-indigo-700 px-1.5 py-0.5 rounded font-mono">
                {Math.round(message.confidenceScore * 100)}% Confidence
              </span>
            )}
          </div>
        )}

        {/* Message Body */}
        <div className="text-sm leading-relaxed whitespace-pre-wrap font-sans">
          {message.message}
        </div>

        {/* Citations Panel */}
        {!isUser && message.sources && <SourceCitations sources={message.sources} />}

        {/* Actions & Timestamp */}
        <div className={`flex items-center justify-between mt-2 text-[10px] ${isUser ? 'text-indigo-100' : 'text-gray-400'}`}>
          <span>{new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
          {!isUser && (
            <div className="flex items-center space-x-2">
              <button
                onClick={copyToClipboard}
                className="hover:text-gray-600 transition-colors"
                title="Copy Response"
              >
                📋 Copy
              </button>
              <button
                onClick={() => setShowFeedbackModal(true)}
                className="hover:text-indigo-600 transition-colors"
                title="Rate Response"
              >
                👍 Rate
              </button>
            </div>
          )}
        </div>
      </div>

      <FeedbackDialog
        messageId={message.id}
        isOpen={showFeedbackModal}
        onClose={() => setShowFeedbackModal(false)}
        onSubmit={handleFeedbackSubmit}
      />
    </div>
  );
};
