import api from './api';
import type {
  AssessmentSummary,
  StartSessionResponse,
  AssessmentResult,
  AssessmentHistory,
} from '../types/assessment';

const BASE = '/assessments';

/** Fetch all active assessments for the landing page. */
export const getAvailableAssessments = (): Promise<AssessmentSummary[]> =>
  api.get(BASE).then(r => r.data);

/** Fetch a single assessment's info (instructions page). */
export const getAssessmentById = (id: string): Promise<AssessmentSummary> =>
  api.get(`${BASE}/${id}`).then(r => r.data);

/** Start or resume an assessment session. */
export const startAssessment = (assessmentId: string): Promise<StartSessionResponse> =>
  api.post(`${BASE}/start`, { assessmentId }).then(r => r.data);

/** Autosave a single answer (idempotent). */
export const saveAnswer = (
  resultId: string,
  questionId: string,
  selectedOptionId: string | null,
  isFlagged: boolean
): Promise<void> =>
  api.post(`${BASE}/save-answer`, { resultId, questionId, selectedOptionId, isFlagged });

/** Submit the assessment for scoring. */
export const submitAssessment = (
  resultId: string,
  timeTakenSecs: number
): Promise<AssessmentResult> =>
  api.post(`${BASE}/submit`, { resultId, timeTakenSecs }).then(r => r.data);

/** Fetch a scored result by ID. */
export const getResult = (resultId: string): Promise<AssessmentResult> =>
  api.get(`${BASE}/result/${resultId}`).then(r => r.data);

/** Fetch the current user's assessment history. */
export const getHistory = (): Promise<AssessmentHistory[]> =>
  api.get(`${BASE}/history`).then(r => r.data);
