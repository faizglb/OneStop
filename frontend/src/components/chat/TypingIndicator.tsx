import React from 'react';

export const TypingIndicator: React.FC = () => {
  return (
    <div className="flex items-center space-x-2 p-3 bg-gray-100 rounded-2xl w-24 dark:bg-gray-800">
      <div className="w-2.5 h-2.5 bg-indigo-500 rounded-full animate-bounce [animation-delay:-0.3s]"></div>
      <div className="w-2.5 h-2.5 bg-indigo-500 rounded-full animate-bounce [animation-delay:-0.15s]"></div>
      <div className="w-2.5 h-2.5 bg-indigo-500 rounded-full animate-bounce"></div>
    </div>
  );
};
