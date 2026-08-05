import React from 'react';

interface ChatSearchProps {
  query: string;
  onQueryChange: (q: string) => void;
}

export const ChatSearch: React.FC<ChatSearchProps> = ({ query, onQueryChange }) => {
  return (
    <div className="relative p-2">
      <input
        type="text"
        value={query}
        onChange={(e) => onQueryChange(e.target.value)}
        placeholder="Search messages..."
        className="w-full pl-8 pr-3 py-1.5 text-xs bg-gray-50 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-indigo-500"
      />
      <span className="absolute left-4 top-3 text-gray-400 text-xs">🔍</span>
    </div>
  );
};
