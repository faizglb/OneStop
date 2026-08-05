import React, { useState } from 'react';

interface ChatInputProps {
  onSendMessage: (msg: string) => void;
  isLoading: boolean;
}

export const ChatInput: React.FC<ChatInputProps> = ({ onSendMessage, isLoading }) => {
  const [text, setText] = useState('');

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      submit();
    }
  };

  const submit = () => {
    if (!text.trim() || isLoading) return;
    onSendMessage(text.trim());
    setText('');
  };

  return (
    <div className="p-3 bg-white border-t border-gray-200">
      <div className="relative flex items-center bg-gray-50 border border-gray-300 rounded-2xl focus-within:ring-2 focus-within:ring-indigo-500 focus-within:border-indigo-500 transition-all">
        <textarea
          rows={1}
          value={text}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Ask your AI Career Mentor a question... (Press Enter to send)"
          className="w-full py-3 pl-4 pr-12 text-sm bg-transparent resize-none focus:outline-none max-h-32 min-h-[44px]"
        />
        <button
          onClick={submit}
          disabled={!text.trim() || isLoading}
          className="absolute right-2 p-2 bg-indigo-600 text-white rounded-xl hover:bg-indigo-700 disabled:opacity-40 disabled:hover:bg-indigo-600 transition-all"
        >
          {isLoading ? (
            <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
          ) : (
            <svg className="w-4 h-4 rotate-90" fill="currentColor" viewBox="0 0 20 20">
              <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
            </svg>
          )}
        </button>
      </div>
    </div>
  );
};
