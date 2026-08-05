import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface MockEvent {
  date: number;
  title: string;
  amount: string;
}

const DeadlineCalendar: React.FC = () => {
  const navigate = useNavigate();
  const [currentMonth, setCurrentMonth] = useState('October 2026');

  // Seed events for mock calendar display
  const events: Record<number, MockEvent[]> = {
    15: [
      { date: 15, title: 'HDFC Bank Badhte Kadam', amount: '₹50,000' }
    ],
    31: [
      { date: 31, title: 'Central Sector Scheme', amount: '₹20,000' }
    ]
  };

  const daysInMonth = 31;
  const startingDay = 4; // Thursday

  const renderDays = () => {
    const cells = [];
    // Blank padding days
    for (let i = 0; i < startingDay; i++) {
      cells.push(<div key={`blank-${i}`} style={{ padding: 12 }} />);
    }

    // Days in October
    for (let day = 1; day <= daysInMonth; day++) {
      const dayEvents = events[day] || [];
      cells.push(
        <div 
          key={day} 
          style={{ 
            background: 'rgba(255,255,255,0.02)', 
            border: '1px solid rgba(255,255,255,0.05)', 
            borderRadius: 8, 
            padding: 8,
            minHeight: 90,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}
        >
          <span style={{ fontSize: '0.8rem', color: 'var(--gray-400)', fontWeight: 700 }}>{day}</span>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
            {dayEvents.map((ev, idx) => (
              <div 
                key={idx} 
                style={{ 
                  background: 'rgba(108,59,235,0.1)', 
                  borderLeft: '2px solid #a855f7',
                  padding: '2px 4px', 
                  borderRadius: 4, 
                  fontSize: '0.68rem', 
                  color: 'white',
                  whiteSpace: 'nowrap',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis'
                }}
                title={`${ev.title} - ${ev.amount}`}
              >
                {ev.title}
              </div>
            ))}
          </div>
        </div>
      );
    }
    return cells;
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div className="history-header" style={{ marginBottom: 32 }}>
        <div>
          <button className="back-btn" onClick={() => navigate('/scholarships')} style={{ marginBottom: 12 }}>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/>
            </svg>
            Scholarship Finder
          </button>
          <h1 className="history-title">Deadline Calendar</h1>
          <p className="history-subtitle">Visual overview of closing dates and active shortlists timeline schedule</p>
        </div>
      </div>

      <div className="info-summary-card" style={{ padding: 24, borderRadius: 20 }}>
        {/* Month selector bar */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
          <button className="btn-ghost btn-sm" disabled>◀ Prev</button>
          <h2 style={{ fontSize: '1.25rem', color: 'white', margin: 0, fontWeight: 800 }}>{currentMonth}</h2>
          <button className="btn-ghost btn-sm" disabled>Next ▶</button>
        </div>

        {/* Days of week header */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 8, textAlign: 'center', fontWeight: 600, fontSize: '0.85rem', color: 'var(--gray-500)', marginBottom: 12 }}>
          <div>Sun</div>
          <div>Mon</div>
          <div>Tue</div>
          <div>Wed</div>
          <div>Thu</div>
          <div>Fri</div>
          <div>Sat</div>
        </div>

        {/* Grid calendar */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 8 }}>
          {renderDays()}
        </div>
      </div>
    </div>
  );
};

export default DeadlineCalendar;
