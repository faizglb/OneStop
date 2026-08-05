package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ResultAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultAnswerRepository extends JpaRepository<ResultAnswer, UUID> {

    Optional<ResultAnswer> findByResultIdAndQuestionId(UUID resultId, UUID questionId);

    @Query("SELECT ra FROM ResultAnswer ra " +
           "JOIN FETCH ra.question q " +
           "JOIN FETCH ra.selectedOption o " +
           "WHERE ra.result.id = :resultId")
    List<ResultAnswer> findWithDetailsForResult(@Param("resultId") UUID resultId);

    List<ResultAnswer> findByResultIdOrderByAnsweredAtAsc(UUID resultId);

    long countByResultId(UUID resultId);
}
