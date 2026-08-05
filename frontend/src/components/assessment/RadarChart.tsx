import React from 'react';

interface RadarChartProps {
  labels: string[];
  values: number[];  // 0-100
  size?: number;
}

const RadarChart: React.FC<RadarChartProps> = ({ labels, values, size = 260 }) => {
  const cx = size / 2;
  const cy = size / 2;
  const radius = (size / 2) - 40;
  const n = labels.length;

  if (n < 3) return null;

  const angleStep = (2 * Math.PI) / n;
  const getPoint = (i: number, r: number) => {
    const angle = i * angleStep - Math.PI / 2;
    return {
      x: cx + r * Math.cos(angle),
      y: cy + r * Math.sin(angle),
    };
  };

  // Grid circles
  const gridLevels = [0.25, 0.5, 0.75, 1.0];

  // Axes
  const axes = labels.map((_, i) => ({
    start: { x: cx, y: cy },
    end: getPoint(i, radius),
  }));

  // Data polygon
  const dataPoints = values.map((v, i) => getPoint(i, (Math.min(100, Math.max(0, v)) / 100) * radius));
  const dataPath = dataPoints
    .map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x.toFixed(1)} ${p.y.toFixed(1)}`)
    .join(' ') + ' Z';

  // Grid polygons
  const gridPaths = gridLevels.map(level => {
    return Array.from({ length: n }, (_, i) => {
      const p = getPoint(i, radius * level);
      return `${i === 0 ? 'M' : 'L'} ${p.x.toFixed(1)} ${p.y.toFixed(1)}`;
    }).join(' ') + ' Z';
  });

  // Label positions
  const labelPoints = labels.map((label, i) => {
    const p = getPoint(i, radius + 28);
    return { ...p, label };
  });

  return (
    <div className="radar-chart-wrapper">
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        {/* Grid */}
        {gridPaths.map((path, i) => (
          <path
            key={i}
            d={path}
            fill="none"
            stroke="rgba(255,255,255,0.1)"
            strokeWidth={1}
          />
        ))}

        {/* Axes */}
        {axes.map((axis, i) => (
          <line
            key={i}
            x1={axis.start.x}
            y1={axis.start.y}
            x2={axis.end.x}
            y2={axis.end.y}
            stroke="rgba(255,255,255,0.15)"
            strokeWidth={1}
          />
        ))}

        {/* Data polygon */}
        <path
          d={dataPath}
          fill="rgba(108,59,235,0.25)"
          stroke="#6C3BEB"
          strokeWidth={2}
        />

        {/* Data dots */}
        {dataPoints.map((p, i) => (
          <circle key={i} cx={p.x} cy={p.y} r={4} fill="#6C3BEB" />
        ))}

        {/* Labels */}
        {labelPoints.map((lp, i) => (
          <text
            key={i}
            x={lp.x}
            y={lp.y}
            textAnchor="middle"
            dominantBaseline="middle"
            fill="rgba(255,255,255,0.75)"
            fontSize="11"
            fontFamily="Inter, sans-serif"
          >
            {lp.label.split(' ')[0]}
          </text>
        ))}
      </svg>
    </div>
  );
};

export default RadarChart;
