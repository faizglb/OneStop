export interface RoadmapTaskResponse {
  id: string;
  phaseId: string;
  phaseName: string;
  title: string;
  description: string;
  taskType: string;
  estimatedHours: number;
  dueDate: string;
  status: string; // PENDING | IN_PROGRESS | COMPLETED | SKIPPED
  priority: string;
  linkedResourceId: string | null;
  linkedResourceTitle: string | null;
  linkedResourceUrl: string | null;
  linkedCertificationId: string | null;
  linkedProjectId: string | null;
}

export interface RoadmapPhaseResponse {
  id: string;
  phaseName: string;
  sequence: number;
  description: string;
  tasks: RoadmapTaskResponse[];
}

export interface RoadmapMilestoneResponse {
  id: string;
  title: string;
  description: string;
  completionCriteria: string;
  status: string;
}

export interface RoadmapResponse {
  id: string;
  title: string;
  targetCareer: string;
  targetCompany: string | null;
  duration: string;
  status: string;
  completionPercentage: number;
  startDate: string;
  endDate: string;
  weeklyHours: number;
  createdAt: string;
  updatedAt: string;
  phases: RoadmapPhaseResponse[];
  milestones: RoadmapMilestoneResponse[];
}

export interface RoadmapProgressResponse {
  roadmapId: string;
  title: string;
  targetCareer: string;
  completionPercentage: number;
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  skippedTasks: number;
  streakDays: number;
}
