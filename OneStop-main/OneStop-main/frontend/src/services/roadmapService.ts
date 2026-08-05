import api from './api';
import type { RoadmapResponse, RoadmapTaskResponse, RoadmapProgressResponse } from '../types/roadmap';

/** Generate a new AI roadmap. */
export const generateRoadmap = (payload: {
  targetCareer: string;
  targetCompany?: string;
  duration: string; // 30-Day Plan | 60-Day Plan | 90-Day Plan | 6-Month Plan | 1-Year Plan | Custom Plan
  weeklyHours: number;
  startDate?: string;
}): Promise<RoadmapResponse> =>
  api.post('/roadmaps/generate', payload).then(r => r.data);

/** Get all roadmaps for current user. */
export const getStudentRoadmaps = (): Promise<RoadmapResponse[]> =>
  api.get('/roadmaps').then(r => r.data);

/** Get single roadmap by ID. */
export const getRoadmapById = (id: string): Promise<RoadmapResponse> =>
  api.get(`/roadmaps/${id}`).then(r => r.data);

/** Update roadmap status. */
export const updateRoadmapStatus = (id: string, status: string): Promise<RoadmapResponse> =>
  api.put(`/roadmaps/${id}`, null, { params: { status } }).then(r => r.data);

/** Delete roadmap. */
export const deleteRoadmap = (id: string): Promise<void> =>
  api.delete(`/roadmaps/${id}`).then(r => r.data);

/** Get filtered student tasks. */
export const getStudentTasks = (status?: string, dueDate?: string): Promise<RoadmapTaskResponse[]> =>
  api.get('/roadmaps/tasks', { params: { status, dueDate } }).then(r => r.data);

/** Update task status (COMPLETED, IN_PROGRESS, SKIPPED, PENDING) or due date. */
export const updateTaskStatus = (
  taskId: string,
  payload: { status?: string; dueDate?: string }
): Promise<RoadmapTaskResponse> =>
  api.patch(`/roadmaps/tasks/${taskId}`, payload).then(r => r.data);

/** Get overall progress metrics & streak. */
export const getRoadmapProgress = (): Promise<RoadmapProgressResponse> =>
  api.get('/roadmaps/progress').then(r => r.data);

/** Dynamically regenerate uncompleted remaining tasks. */
export const regenerateRoadmap = (roadmapId: string): Promise<RoadmapResponse> =>
  api.post('/roadmaps/regenerate', null, { params: { roadmapId } }).then(r => r.data);
