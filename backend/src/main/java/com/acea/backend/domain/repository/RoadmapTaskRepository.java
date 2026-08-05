package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.RoadmapTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoadmapTaskRepository extends JpaRepository<RoadmapTask, UUID> {

    @Query("SELECT t FROM RoadmapTask t " +
           "JOIN t.phase p " +
           "JOIN p.roadmap r " +
           "WHERE r.student.id = :studentId " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:dueDate IS NULL OR t.dueDate = :dueDate) " +
           "ORDER BY t.dueDate ASC")
    List<RoadmapTask> findStudentTasks(
            @Param("studentId") UUID studentId,
            @Param("status") String status,
            @Param("dueDate") LocalDate dueDate
    );

    @Query("SELECT COUNT(t) FROM RoadmapTask t JOIN t.phase p JOIN p.roadmap r WHERE r.id = :roadmapId")
    long countByRoadmapId(@Param("roadmapId") UUID roadmapId);

    @Query("SELECT COUNT(t) FROM RoadmapTask t JOIN t.phase p JOIN p.roadmap r WHERE r.id = :roadmapId AND t.status = 'COMPLETED'")
    long countCompletedByRoadmapId(@Param("roadmapId") UUID roadmapId);
}
