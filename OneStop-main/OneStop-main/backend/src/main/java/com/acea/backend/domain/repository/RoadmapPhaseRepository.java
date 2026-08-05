package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.RoadmapPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoadmapPhaseRepository extends JpaRepository<RoadmapPhase, UUID> {
    List<RoadmapPhase> findByRoadmapIdOrderBySequenceAsc(UUID roadmapId);
}
