import React from 'react';
import type { OptionResponse } from '../../types/assessment';

interface QuestionCardProps {
  index: number;
  total: number;
  questionText: string;
  categoryName: string;
  marks: number;
  options: OptionResponse[];
  selectedOptionId: string | null;
  isFlagged: boolean;
  onSelect: (optionId: string) => void;
  onFlag: () => void;
}

const QuestionCard: React.FC<QuestionCardProps> = ({
  index,
  total,
  questionText,
  categoryName,
  marks,
  options,
  selectedOptionId,
  isFlagged,
  onSelect,
  onFlag,
}) => {
  return (
    <div className="question-card">
      <div className="question-card-header">
        <div className="question-meta">
          <span className="question-number">Q{index + 1} <span className="question-total">/ {total}</span></span>
          <span className="question-category-badge">{categoryName}</span>
        </div>
        <div className="question-actions-top">
          <span className="question-marks">{marks} {marks === 1 ? 'mark' : 'marks'}</span>
          <button
            className={`flag-btn ${isFlagged ? 'flagged' : ''}`}
            onClick={onFlag}
            title={isFlagged ? 'Unflag question' : 'Flag for review'}
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill={isFlagged ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="2">
              <path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/>
              <line x1="4" y1="22" x2="4" y2="15"/>
            </svg>
            {isFlagged ? 'Flagged' : 'Flag'}
          </button>
        </div>
      </div>

      <div className="question-text">{questionText}</div>

      <div className="options-list">
        {options.map((option, i) => {
          const isSelected = selectedOptionId === option.id;
          const letter = String.fromCharCode(65 + i); // A, B, C, D
          return (
            <button
              key={option.id}
              className={`option-btn ${isSelected ? 'selected' : ''}`}
              onClick={() => onSelect(option.id)}
            >
              <span className="option-letter">{letter}</span>
              <span className="option-text">{option.optionText}</span>
              {isSelected && (
                <span className="option-check">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                </span>
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
};

export default QuestionCard;
