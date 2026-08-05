import api from './api';
import type { CollegeResponse, CollegeCompareResponse, CollegeRecommendationResponse } from '../types/college';

/** Search and filter colleges. */
export const searchColleges = (params: {
  search?: string;
  state?: string;
  type?: string;
  maxFees?: number;
  minPlacementPct?: number;
  hostelRequired?: boolean;
  page?: number;
  size?: number;
}): Promise<any> =>
  api.get('/colleges', { params }).then(r => r.data);

/** Get a single college by ID. */
export const getCollegeById = (id: string): Promise<CollegeResponse> =>
  api.get(`/colleges/${id}`).then(r => r.data);

/** Bookmark a college. */
export const bookmarkCollege = (collegeId: string): Promise<CollegeResponse> =>
  api.post('/bookmarks', null, { params: { collegeId } }).then(r => r.data);

/** Get all bookmarks. */
export const getBookmarkedColleges = (): Promise<CollegeResponse[]> =>
  api.get('/bookmarks').then(r => r.data);

/** Remove bookmark. */
export const removeBookmark = (collegeId: string): Promise<void> =>
  api.delete(`/bookmarks/${collegeId}`).then(r => r.data);

/** Compare up to 3 colleges. */
export const compareColleges = (collegeIds: string[]): Promise<CollegeCompareResponse> =>
  api.post('/compare', collegeIds).then(r => r.data);

/** Generate college recommendations. */
export const generateCollegeRecommendations = (payload: {
  entranceExam: string;
  examRank: number;
  reservationCategory: string;
  maxBudget: number;
  hostelRequired: boolean;
  locationPreference?: string;
  preferredCourse?: string;
}): Promise<CollegeRecommendationResponse[]> =>
  api.post('/college-recommendations/generate', payload).then(r => r.data);

/** Get all current college recommendations. */
export const getCollegeRecommendations = (): Promise<CollegeRecommendationResponse[]> =>
  api.get('/college-recommendations').then(r => r.data);
