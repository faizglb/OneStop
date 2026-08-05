export interface RecommendedSkill {
  id: string;
  skillName: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  confidence: number;
}

export interface RecommendedCertification {
  id: string;
  certificationName: string;
  provider: string;
  confidence: number;
}

export interface RecommendedProject {
  id: string;
  projectTitle: string;
  projectDescription: string;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  estimatedDuration: string;
}

export interface RecommendationResponse {
  id: string;
  careerName: string;
  careerDescription: string;
  confidenceScore: number;
  matchScore: number;
  reason: string;
  expectedSalary: string;
  industryGrowth: string;
  requiredDegree: string;
  futureScope: string;
  jobDemand: string;
  estimatedLearningTime: string;
  pros: string[];
  challenges: string[];
  isBookmarked: boolean;
  createdAt: string;
  requiredSkills: RecommendedSkill[];
  recommendedCertifications: RecommendedCertification[];
  suggestedProjects: RecommendedProject[];
}
