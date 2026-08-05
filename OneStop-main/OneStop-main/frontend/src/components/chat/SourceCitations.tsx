import React, { useState } from 'react';
import { SourceCitation } from '@/types/chat';

interface SourceCitationsProps {
  sources?: SourceCitation[];
}

export const SourceCitations: React.FC<SourceCitationsProps> = ({ sources }) => {
  const [isOpen, setIsOpen] = useState(false);

  if (!sources || sources.length === 0) return null;

  return (
    <div className="mt-3 border-t border-gray-200/60 pt-2 text-xs">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center space-x-1.5 text-indigo-600 hover:text-indigo-700 font-medium transition-colors"
      >
        <span>📚 Cited Sources ({sources.length})</span>
        <span className="text-[10px]">{isOpen ? '▲' : '▼'}</span>
      </button>

      {isOpen && (
        <div className="mt-2 space-y-2 pl-2 border-l-2 border-indigo-200">
          {sources.map((src, i) => (
            <div key={i} className="p-2 bg-gray-50 rounded border border-gray-100 text-gray-700">
              <div className="flex justify-between items-center mb-1">
                <span className="font-semibold text-gray-900">{src.documentTitle}</span>
                <span className="bg-indigo-100 text-indigo-800 text-[10px] px-1.5 py-0.5 rounded font-mono">
                  {Math.round(src.relevance_score ? src.relevance_score * 100 : (src.relevanceScore || 0) * 100)}% match
                </span>
              </div>
              <p className="italic text-gray-600 line-clamp-2">{src.chunkText}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
