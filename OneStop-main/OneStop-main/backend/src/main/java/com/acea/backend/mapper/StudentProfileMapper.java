package com.acea.backend.mapper;

import com.acea.backend.domain.entity.*;
import com.acea.backend.dto.request.*;
import com.acea.backend.dto.response.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "interests", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "profileCompletionPct", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudentProfile toEntity(StudentProfileRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "interests", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "profileCompletionPct", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(StudentProfileRequest request, @MappingTarget StudentProfile entity);

    @Mapping(target = "userId", source = "user.id")
    StudentProfileResponse toResponse(StudentProfile entity);

    // Skills
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Skill toEntity(SkillRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(SkillRequest request, @MappingTarget Skill entity);

    SkillResponse toResponse(Skill entity);
    List<SkillResponse> toSkillResponseList(List<Skill> entities);

    // Interests
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Interest toEntity(InterestRequest request);

    InterestResponse toResponse(Interest entity);
    List<InterestResponse> toInterestResponseList(List<Interest> entities);

    // Goals
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Goal toEntity(GoalRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(GoalRequest request, @MappingTarget Goal entity);

    GoalResponse toResponse(Goal entity);
    List<GoalResponse> toGoalResponseList(List<Goal> entities);

    // Certifications
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Certification toEntity(CertificationRequest request);

    CertificationResponse toResponse(Certification entity);
    List<CertificationResponse> toCertificationResponseList(List<Certification> entities);

    // Projects
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Project toEntity(ProjectRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ProjectRequest request, @MappingTarget Project entity);

    ProjectResponse toResponse(Project entity);
    List<ProjectResponse> toProjectResponseList(List<Project> entities);
}
