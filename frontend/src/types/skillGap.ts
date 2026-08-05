export interface ReadinessBreakdown {
  overallReadinessScore: number;
  technicalReadiness: number;
  softSkillReadiness: number;
  projectReadiness: number;
  interviewReadiness: number;
  resumeReadiness: number;
  codingReadiness: number;
}

export interface ImprovementRecommendation {
  id?: string;
  resourceId?: string;
  projectId?: string;
  certificateId?: string;
  title: string;
  description: string;
  recommendationType: 'RESOURCE' | 'PROJECT' | 'COURSE' | 'CERTIFICATION' | 'PRACTICE';
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  estimatedDuration: string;
  url?: string;
}

export interface SkillGapItem {
  id?: string;
  skillName: string;
  requiredLevel: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
  currentLevel: 'NONE' | 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';
  gapPercentage: number;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  estimatedLearningHours: number;
  status: 'MISSING' | 'WEAK' | 'STRONG' | 'EMERGING';
  category: 'TECHNICAL' | 'SOFT_SKILL' | 'CODING';
  recommendations?: ImprovementRecommendation[];
}

export interface RadarChartItem {
  subject: string;
  student: number;
  required: number;
}

export interface HeatmapItem {
  skillName: string;
  category: string;
  currentScore: number;
  requiredScore: number;
  gap: number;
}

export interface PriorityMatrixItem {
  skillName: string;
  importance: number;
  effort: number;
  quadrant: 'Quick Win' | 'Strategic Investment' | 'Secondary' | 'Low Priority';
}

export interface LearningTimelineStep {
  stepNumber: number;
  phase: string;
  title: string;
  skillsCovered: string[];
  estimatedWeeks: number;
}

export interface VisualAnalytics {
  radarChart: RadarChartItem[];
  skillHeatmap: HeatmapItem[];
  priorityMatrix: PriorityMatrixItem[];
  learningTimeline: LearningTimelineStep[];
}

export interface SmartInsight {
  title: string;
  description: string;
  insightType: 'POSITIVE' | 'WARNING' | 'ACTIONABLE' | 'TIP';
}

export interface SkillGapReport {
  id: string;
  studentId: string;
  careerId: string;
  targetCareer: String;
  overallReadinessScore: number;
  overallGapPercentage: number;
  estimatedJobReadinessMonths: number;
  readinessBreakdown: ReadinessBreakdown;
  skillGaps: SkillGapItem[];
  improvementRecommendations: ImprovementRecommendation[];
  visualAnalytics: VisualAnalytics;
  smartInsights: SmartInsight[];
  createdAt: string;
  updatedAt: string;
}

export interface SkillGapAnalyzeRequest {
  targetCareer: string;
  careerId?: string;
}
