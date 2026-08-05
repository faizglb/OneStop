import React from 'react';

interface ScoreCircleProps {
  percentage: number;
  size?: number;
  label?: string;
  sublabel?: string;
}

const ScoreCircle: React.FC<ScoreCircleProps> = ({
  percentage,
  size = 140,
  label,
  sublabel,
}) => {
  const radius = (size - 16) / 2;
  const circumference = 2 * Math.PI * radius;
  const clampedPct = Math.min(100, Math.max(0, percentage));
  const strokeDashoffset = circumference - (clampedPct / 100) * circumference;

  const getColor = () => {
    if (clampedPct >= 80) return '#22c55e';  // green
    if (clampedPct >= 60) return '#6C3BEB';  // brand purple
    if (clampedPct >= 40) return '#f59e0b';  // amber
    return '#ef4444';                          // red
  };

  return (
    <div className="score-circle-wrapper" style={{ width: size, height: size }}>
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        {/* Track */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="rgba(255,255,255,0.1)"
          strokeWidth={8}
        />
        {/* Progress */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke={getColor()}
          strokeWidth={8}
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={strokeDashoffset}
          style={{ transition: 'stroke-dashoffset 1s ease-in-out' }}
        />
      </svg>
      <div className="score-circle-content">
        <span className="score-circle-pct" style={{ color: getColor() }}>
          {Math.round(clampedPct)}%
        </span>
        {label    && <span className="score-circle-label">{label}</span>}
        {sublabel && <span className="score-circle-sublabel">{sublabel}</span>}
      </div>
    </div>
  );
};

export default ScoreCircle;
