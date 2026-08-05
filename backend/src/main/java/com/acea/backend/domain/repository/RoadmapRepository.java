package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, UUID> {

    @Query("SELECT DISTINCT r FROM Roadmap r " +
           "LEFT JOIN FETCH r.phases p " +
           "LEFT JOIN FETCH p.tasks t " +
           "LEFT JOIN FETCH r.milestones m " +
           "WHERE r.student.id = :studentId ORDER BY r.createdAt DESC")
    List<Roadmap> findByStudentIdWithDetails(@Param("studentId") UUID studentId);

    @Query("SELECT r FROM Roadmap r " +
           "LEFT JOIN FETCH r.phases p " +
           "LEFT JOIN FETCH p.tasks t " +
           "LEFT JOIN FETCH r.milestones m " +
           "WHERE r.id = :id AND r.student.id = :studentId")
    Optional<Roadmap> findByIdAndStudentIdWithDetails(@Param("id") UUID id, @Param("studentId") UUID studentId);

    Optional<Roadmap> findByStudentIdAndStatus(UUID studentId, String status);

    boolean existsByStudentIdAndStatus(UUID studentId, String status);
}
