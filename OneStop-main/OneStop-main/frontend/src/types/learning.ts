export interface LearningResourceResponse {
  id: string;
  title: string;
  description: string;
  platform: string;
  resourceType: string;
  category: string;
  skill: string;
  difficulty: string;
  duration: string;
  language: string;
  isFree: boolean;
  price: number;
  rating: number;
  thumbnail: string | null;
  resourceUrl: string;
  author: string | null;
  provider: string | null;
  certificateAvailable: boolean;
  isBookmarked: boolean;
  progressStatus: string | null; // STARTED | IN_PROGRESS | COMPLETED
  progressPercentage: number | null;
}

export interface LearningProgressResponse {
  id: string;
  resourceId: string;
  title: string;
  platform: string;
  category: string;
  status: string; // STARTED | IN_PROGRESS | COMPLETED
  progressPercentage: number;
  startedAt: string;
  completedAt: string | null;
  lastAccessed: string;
}

export interface LearningRecommendationResponse {
  id: string;
  resourceId: string;
  title: string;
  platform: string;
  resourceType: string;
  difficulty: string;
  duration: string;
  isFree: boolean;
  rating: number;
  thumbnail: string | null;
  resourceUrl: string;
  recommendedSkill: string;
  priority: string; // HIGH | MEDIUM | LOW
  confidenceScore: number;
  reason: string;
  estimatedCompletionTime: string;
  createdAt: string;
}
