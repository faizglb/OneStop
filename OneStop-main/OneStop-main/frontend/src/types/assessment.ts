// Assessment module types
export type AssessmentType = 'APTITUDE' | 'PERSONALITY' | 'INTEREST';
export type AssessmentStatus = 'ACTIVE' | 'INACTIVE' | 'DRAFT';
export type ResultStatus = 'IN_PROGRESS' | 'COMPLETED' | 'ABANDONED';
export type Difficulty = 'EASY' | 'MEDIUM' | 'HARD';
export type TraitLevel = 'LOW' | 'MODERATE' | 'HIGH';

export interface AssessmentSummary {
  id: string;
  assessmentType: AssessmentType;
  title: string;
  description: string;
  timeLimitMinutes: number;
  totalQuestions: number;
  status: AssessmentStatus;
}

export interface OptionResponse {
  id: string;
  optionText: string;
  optionOrder: number;
}

export interface QuestionResponse {
  id: string;
  categoryId: string;
  categoryName: string;
  questionText: string;
  difficulty: Difficulty;
  marks: number;
  questionOrder: number;
  options: OptionResponse[];
}

export interface StartSessionResponse {
  resultId: string;
  assessmentId: string;
  assessmentType: AssessmentType;
  title: string;
  timeLimitMinutes: number;
  totalQuestions: number;
  startedAt: string;
  expiresAt: string;
  resumed: boolean;
  questions: QuestionResponse[];
}

export interface SectionScore {
  categoryId: string;
  categoryName: string;
  rawScore: number;
  maxScore: number;
  percentage: number;
  isStrength: boolean;
}

export interface TraitScore {
  traitKey: string;
  traitLabel: string;
  normalizedScore: number;
  level: TraitLevel;
  description: string;
}

export interface AssessmentResult {
  resultId: string;
  assessmentId: string;
  assessmentType: AssessmentType;
  assessmentTitle: string;
  totalScore: number;
  maxScore: number;
  percentage: number;
  percentile: number;
  status: ResultStatus;
  timeTakenSecs: number;
  completedAt: string;
  sectionScores: SectionScore[];
  traitScores: TraitScore[];
  strengths: string[];
  weaknesses: string[];
  chartLabels: string[];
  chartValues: number[];
}

export interface AssessmentHistory {
  resultId: string;
  assessmentId: string;
  assessmentType: AssessmentType;
  assessmentTitle: string;
  percentage: number;
  status: ResultStatus;
  timeTakenSecs: number;
  completedAt: string;
  startedAt: string;
}

// UI state types
export interface AnswerMap {
  [questionId: string]: string | null; // questionId → selectedOptionId (null = skipped)
}

export interface FlagMap {
  [questionId: string]: boolean;
}
