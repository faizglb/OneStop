package com.acea.backend.service;

import com.acea.backend.dto.request.*;
import com.acea.backend.dto.response.*;

import java.util.List;
import java.util.UUID;

public interface StudentProfileService {

    // Profile CRUD
    StudentProfileResponse getProfile(UUID userId);
    StudentProfileResponse createOrUpdateProfile(UUID userId, StudentProfileRequest request);
    void deleteProfile(UUID userId);

    // Skills CRUD
    SkillResponse addSkill(UUID userId, SkillRequest request);
    List<SkillResponse> getSkills(UUID userId);
    SkillResponse updateSkill(UUID userId, UUID skillId, SkillRequest request);
    void deleteSkill(UUID userId, UUID skillId);

    // Interests CRUD
    InterestResponse addInterest(UUID userId, InterestRequest request);
    List<InterestResponse> getInterests(UUID userId);
    void deleteInterest(UUID userId, UUID interestId);

    // Goals CRUD
    GoalResponse addGoal(UUID userId, GoalRequest request);
    List<GoalResponse> getGoals(UUID userId);
    GoalResponse updateGoal(UUID userId, UUID goalId, GoalRequest request);
    void deleteGoal(UUID userId, UUID goalId);

    // Projects CRUD
    ProjectResponse addProject(UUID userId, ProjectRequest request);
    List<ProjectResponse> getProjects(UUID userId);
    ProjectResponse updateProject(UUID userId, UUID projectId, ProjectRequest request);
    void deleteProject(UUID userId, UUID projectId);

    // Certifications CRUD
    CertificationResponse addCertification(UUID userId, CertificationRequest request);
    List<CertificationResponse> getCertifications(UUID userId);
    void deleteCertification(UUID userId, UUID certificationId);
}
