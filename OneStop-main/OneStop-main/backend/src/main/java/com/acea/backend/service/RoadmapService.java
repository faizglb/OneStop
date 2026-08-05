package com.acea.backend.service;

import com.acea.backend.dto.roadmap.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RoadmapService {

    /** Generate a new personalized career roadmap using FastAPI matching engine. */
    RoadmapResponse generateRoadmap(UUID studentId, RoadmapGenerateRequest request);

    /** Get active roadmap or all roadmaps for student. */
    List<RoadmapResponse> getStudentRoadmaps(UUID studentId);

    /** Get full roadmap profile by ID. */
    RoadmapResponse getRoadmapById(UUID roadmapId, UUID studentId);

    /** Update roadmap status (e.g. PAUSED, ACTIVE) or details. */
    RoadmapResponse updateRoadmap(UUID roadmapId, UUID studentId, String status);

    /** Delete a roadmap. */
    void deleteRoadmap(UUID roadmapId, UUID studentId);

    /** Get filtered list of tasks for the student. */
    List<RoadmapTaskResponse> getStudentTasks(UUID studentId, String status, LocalDate dueDate);

    /** Update task completion status or due date. */
    RoadmapTaskResponse updateTaskStatus(UUID taskId, UUID studentId, TaskStatusUpdateRequest request);

    /** Get overall progress metrics, completion streak, and task stats. */
    RoadmapProgressResponse getRoadmapProgress(UUID studentId);

    /** Dynamically regenerate uncompleted tasks when student falls behind schedule. */
    RoadmapResponse regenerateRoadmap(UUID roadmapId, UUID studentId);
}
