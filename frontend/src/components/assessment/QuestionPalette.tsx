import React from 'react';
import type { AnswerMap, FlagMap } from '../../types/assessment';

interface QuestionPaletteProps {
  totalQuestions: number;
  questionIds: string[];
  currentIndex: number;
  answers: AnswerMap;
  flags: FlagMap;
  onNavigate: (index: number) => void;
}

const QuestionPalette: React.FC<QuestionPaletteProps> = ({
  totalQuestions,
  questionIds,
  currentIndex,
  answers,
  flags,
  onNavigate,
}) => {
  const answered  = questionIds.filter(id => answers[id] !== undefined && answers[id] !== null).length;
  const flagged   = questionIds.filter(id => flags[id]).length;
  const skipped   = totalQuestions - answered;

  return (
    <div className="question-palette">
      <div className="palette-header">
        <h3>Question Navigator</h3>
      </div>

      <div className="palette-legend">
        <div className="legend-item">
          <span className="legend-dot answered" />
          <span>Answered ({answered})</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot flagged" />
          <span>Flagged ({flagged})</span>
        </div>
        <div className="legend-item">
          <span className="legend-dot unanswered" />
          <span>Not visited ({skipped})</span>
        </div>
      </div>

      <div className="palette-grid">
        {questionIds.map((qId, index) => {
          const isAnswered = answers[qId] !== undefined && answers[qId] !== null;
          const isFlagged  = flags[qId];
          const isCurrent  = index === currentIndex;

          let status = 'unanswered';
          if (isAnswered && isFlagged) status = 'answered-flagged';
          else if (isAnswered) status = 'answered';
          else if (isFlagged) status = 'flagged';

          return (
            <button
              key={qId}
              className={`palette-btn ${status} ${isCurrent ? 'current' : ''}`}
              onClick={() => onNavigate(index)}
              title={`Question ${index + 1} — ${isAnswered ? 'Answered' : 'Not answered'}${isFlagged ? ' (Flagged)' : ''}`}
            >
              {index + 1}
            </button>
          );
        })}
      </div>

      <div className="palette-summary">
        <div className="summary-stat">
          <span className="stat-number">{answered}</span>
          <span className="stat-label">Done</span>
        </div>
        <div className="summary-stat">
          <span className="stat-number">{skipped}</span>
          <span className="stat-label">Left</span>
        </div>
        <div className="summary-stat">
          <span className="stat-number">{flagged}</span>
          <span className="stat-label">Flagged</span>
        </div>
      </div>
    </div>
  );
};

export default QuestionPalette;
