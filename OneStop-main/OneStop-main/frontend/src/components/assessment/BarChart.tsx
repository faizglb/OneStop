import React from 'react';

interface BarChartProps {
  labels: string[];
  values: number[];
  maxValue?: number;
  showValues?: boolean;
}

const BarChart: React.FC<BarChartProps> = ({
  labels,
  values,
  maxValue = 100,
  showValues = true,
}) => {
  const getBarColor = (val: number) => {
    if (val >= 80) return '#22c55e';
    if (val >= 60) return '#6C3BEB';
    if (val >= 40) return '#f59e0b';
    return '#ef4444';
  };

  return (
    <div className="bar-chart">
      {labels.map((label, i) => {
        const val = values[i] ?? 0;
        const pct = Math.min(100, (val / maxValue) * 100);
        return (
          <div key={label} className="bar-row">
            <div className="bar-label" title={label}>{label}</div>
            <div className="bar-track">
              <div
                className="bar-fill"
                style={{
                  width: `${pct}%`,
                  background: getBarColor(val),
                  transition: `width 0.8s ease ${i * 0.1}s`,
                }}
              />
            </div>
            {showValues && (
              <div className="bar-value">{Math.round(val)}%</div>
            )}
          </div>
        );
      })}
    </div>
  );
};

export default BarChart;
