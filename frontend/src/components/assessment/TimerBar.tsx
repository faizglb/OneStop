import React, { useState, useEffect, useRef } from 'react';

interface TimerBarProps {
  expiresAt: string;   // ISO timestamp from server
  onExpire: () => void;
}

function formatTime(seconds: number): string {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}

const TimerBar: React.FC<TimerBarProps> = ({ expiresAt, onExpire }) => {
  const [secondsLeft, setSecondsLeft] = useState<number>(0);
  const expiredRef = useRef(false);

  useEffect(() => {
    const expiry = new Date(expiresAt).getTime();

    const tick = () => {
      const now = Date.now();
      const remaining = Math.max(0, Math.floor((expiry - now) / 1000));
      setSecondsLeft(remaining);

      if (remaining === 0 && !expiredRef.current) {
        expiredRef.current = true;
        onExpire();
      }
    };

    tick();
    const interval = setInterval(tick, 1000);
    return () => clearInterval(interval);
  }, [expiresAt, onExpire]);

  const isWarning  = secondsLeft <= 300 && secondsLeft > 60;  // < 5 min
  const isDanger   = secondsLeft <= 60;                        // < 1 min
  const isExpired  = secondsLeft === 0;

  return (
    <div className={`timer-bar ${isWarning ? 'timer-warning' : ''} ${isDanger ? 'timer-danger' : ''}`}>
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <circle cx="12" cy="12" r="10"/>
        <polyline points="12 6 12 12 16 14"/>
      </svg>
      <span className="timer-label">Time Remaining</span>
      <span className="timer-value">{isExpired ? 'Expired' : formatTime(secondsLeft)}</span>
      {isWarning && !isDanger && <span className="timer-badge warning">Low Time</span>}
      {isDanger  && !isExpired && <span className="timer-badge danger">Hurry!</span>}
      {isExpired  && <span className="timer-badge expired">Time Up!</span>}
    </div>
  );
};

export default TimerBar;
