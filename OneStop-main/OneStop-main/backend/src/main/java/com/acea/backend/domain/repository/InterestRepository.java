package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterestRepository extends JpaRepository<Interest, UUID> {
    List<Interest> findByStudentProfileId(UUID studentProfileId);
}
