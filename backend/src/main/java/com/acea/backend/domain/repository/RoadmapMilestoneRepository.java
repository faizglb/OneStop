package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.RoadmapMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoadmapMilestoneRepository extends JpaRepository<RoadmapMilestone, UUID> {
    List<RoadmapMilestone> findByRoadmapId(UUID roadmapId);
}
