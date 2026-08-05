import api from './api';
import type { ScholarshipResponse, ScholarshipApplicationHistoryResponse, ScholarshipRecommendationResponse } from '../types/scholarship';

/** Search and filter scholarships. */
export const searchScholarships = (params: {
  search?: string;
  state?: string;
  type?: string;
  educationLevel?: string;
  category?: string;
  minAmount?: number;
  maxIncome?: number;
  page?: number;
  size?: number;
}): Promise<any> =>
  api.get('/scholarships', { params }).then(r => r.data);

/** Get a single scholarship by ID. */
export const getScholarshipById = (id: string): Promise<ScholarshipResponse> =>
  api.get(`/scholarships/${id}`).then(r => r.data);

/** Bookmark a scholarship. */
export const bookmarkScholarship = (scholarshipId: string): Promise<ScholarshipResponse> =>
  api.post('/scholarships/bookmark', null, { params: { scholarshipId } }).then(r => r.data);

/** Get all bookmarked scholarships. */
export const getBookmarkedScholarships = (): Promise<ScholarshipResponse[]> =>
  api.get('/scholarships/bookmarks').then(r => r.data);

/** Remove a bookmark. */
export const removeBookmark = (scholarshipId: string): Promise<void> =>
  api.delete(`/scholarships/bookmarks/${scholarshipId}`).then(r => r.data);

/** Update tracker status. */
export const updateApplicationStatus = (payload: {
  scholarshipId: string;
  status: string; // BOOKMARKED | APPLIED | SHORTLISTED | COMPLETED
  notes?: string;
  reminderDate?: string;
}): Promise<ScholarshipApplicationHistoryResponse> =>
  api.post('/scholarships/application-status', payload).then(r => r.data);

/** Get tracker logs. */
export const getApplicationHistory = (): Promise<ScholarshipApplicationHistoryResponse[]> =>
  api.get('/scholarships/application-history').then(r => r.data);

/** Generate recommendations. */
export const generateScholarshipRecommendations = (): Promise<ScholarshipRecommendationResponse[]> =>
  api.post('/scholarship-recommendations/generate').then(r => r.data);

/** Get current recommendations. */
export const getScholarshipRecommendations = (): Promise<ScholarshipRecommendationResponse[]> =>
  api.get('/scholarship-recommendations').then(r => r.data);
