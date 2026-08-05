import api from './api';
import type { LearningResourceResponse, LearningProgressResponse, LearningRecommendationResponse } from '../types/learning';

/** Search and filter learning resources. */
export const searchResources = (params: {
  search?: string;
  platform?: string;
  difficulty?: string;
  category?: string;
  isFree?: boolean;
  certificateRequired?: boolean;
  page?: number;
  size?: number;
}): Promise<any> =>
  api.get('/resources', { params }).then(r => r.data);

/** Get a single learning resource by ID. */
export const getResourceById = (id: string): Promise<LearningResourceResponse> =>
  api.get(`/resources/${id}`).then(r => r.data);

/** Bookmark a resource. */
export const bookmarkResource = (resourceId: string): Promise<LearningResourceResponse> =>
  api.post('/resources/bookmark', null, { params: { resourceId } }).then(r => r.data);

/** Get all bookmarked resources. */
export const getBookmarkedResources = (): Promise<LearningResourceResponse[]> =>
  api.get('/resources/bookmarks').then(r => r.data);

/** Remove a bookmark. */
export const removeBookmark = (resourceId: string): Promise<void> =>
  api.delete(`/resources/bookmarks/${resourceId}`).then(r => r.data);

/** Update tracker progress. */
export const updateLearningProgress = (payload: {
  resourceId: string;
  status: string; // STARTED | IN_PROGRESS | COMPLETED
  progressPercentage: number;
}): Promise<LearningProgressResponse> =>
  api.post('/learning-progress', payload).then(r => r.data);

/** Get student learning progress logs. */
export const getLearningProgressHistory = (): Promise<LearningProgressResponse[]> =>
  api.get('/learning-progress').then(r => r.data);

/** Generate recommendations. */
export const generateLearningRecommendations = (): Promise<LearningRecommendationResponse[]> =>
  api.post('/learning-recommendations/generate').then(r => r.data);

/** Get current recommendations. */
export const getLearningRecommendations = (): Promise<LearningRecommendationResponse[]> =>
  api.get('/learning-recommendations').then(r => r.data);
export const removeResourceBookmark = removeBookmark; // Alias
