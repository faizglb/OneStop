package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ImprovementRecommendation;
import com.acea.backend.domain.entity.SkillGap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImprovementRecommendationRepository extends JpaRepository<ImprovementRecommendation, UUID> {

    List<ImprovementRecommendation> findAllBySkillGap(SkillGap skillGap);
}
