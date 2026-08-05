import api from './api';
import type { RecommendationResponse } from '../types/recommendation';

const BASE = '/recommendations';

/** Generate fresh career recommendations based on student profile and scores. */
export const generateRecommendations = (): Promise<RecommendationResponse[]> =>
  api.post(`${BASE}/generate`).then(r => r.data);

/** Get all current recommendations. */
export const getRecommendations = (): Promise<RecommendationResponse[]> =>
  api.get(BASE).then(r => r.data);

/** Get career recommendations list. */
export const getCareers = (): Promise<RecommendationResponse[]> =>
  api.get(`${BASE}/careers`).then(r => r.data);

/** Get list of recommended skills. */
export const getSkills = (): Promise<any[]> =>
  api.get(`${BASE}/skills`).then(r => r.data);

/** Get list of recommended certifications. */
export const getCertifications = (): Promise<any[]> =>
  api.get(`${BASE}/certifications`).then(r => r.data);

/** Get list of recommended projects. */
export const getProjects = (): Promise<any[]> =>
  api.get(`${BASE}/projects`).then(r => r.data);

/** Toggle bookmark status of a recommendation. */
export const toggleBookmark = (recommendationId: string): Promise<RecommendationResponse> =>
  api.patch(`${BASE}/bookmark/${recommendationId}`).then(r => r.data);

/** Clear recommendations state for regeneration. */
export const clearRecommendations = (): Promise<void> =>
  api.delete(`${BASE}/regenerate`).then(r => r.data);
