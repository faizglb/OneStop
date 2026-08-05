import React from 'react';
import { ChatSession } from '@/types/chat';

interface ConversationListProps {
  sessions: ChatSession[];
  activeSessionId?: string;
  onSelectSession: (id: string) => void;
  onNewSession: () => void;
  onDeleteSession: (id: string) => void;
}

export const ConversationList: React.FC<ConversationListProps> = ({
  sessions,
  activeSessionId,
  onSelectSession,
  onNewSession,
  onDeleteSession,
}) => {
  return (
    <div className="w-80 bg-white border-r border-gray-200 flex flex-col h-full">
      {/* Header */}
      <div className="p-4 border-b border-gray-200 flex items-center justify-between">
        <h2 className="font-bold text-gray-800 text-base">Conversations</h2>
        <button
          onClick={onNewSession}
          className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-medium rounded-xl shadow transition-all flex items-center space-x-1"
        >
          <span>+</span>
          <span>New Chat</span>
        </button>
      </div>

      {/* Session List */}
      <div className="flex-1 overflow-y-auto divide-y divide-gray-100">
        {sessions.length === 0 ? (
          <div className="p-6 text-center text-xs text-gray-400">
            No previous conversations yet. Click "New Chat" to begin!
          </div>
        ) : (
          sessions.map((sess) => {
            const isActive = sess.id === activeSessionId;
            return (
              <div
                key={sess.id}
                onClick={() => onSelectSession(sess.id)}
                className={`p-3.5 cursor-pointer flex items-center justify-between group hover:bg-gray-50 transition-colors ${
                  isActive ? 'bg-indigo-50/70 border-l-4 border-indigo-600' : ''
                }`}
              >
                <div className="flex-1 min-w-0 pr-2">
                  <h4 className={`text-xs font-semibold truncate ${isActive ? 'text-indigo-900' : 'text-gray-800'}`}>
                    {sess.title}
                  </h4>
                  {sess.lastMessagePreview && (
                    <p className="text-[11px] text-gray-400 truncate mt-0.5">
                      {sess.lastMessagePreview}
                    </p>
                  )}
                  <span className="text-[10px] text-gray-400 mt-1 block">
                    {new Date(sess.lastMessageAt).toLocaleDateString([], { month: 'short', day: 'numeric' })}
                  </span>
                </div>

                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    onDeleteSession(sess.id);
                  }}
                  className="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-red-500 p-1 text-xs transition-opacity"
                  title="Archive session"
                >
                  🗑️
                </button>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
};
