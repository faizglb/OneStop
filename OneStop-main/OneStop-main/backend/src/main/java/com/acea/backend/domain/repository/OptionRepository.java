package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OptionRepository extends JpaRepository<Option, UUID> {

    List<Option> findByQuestionIdOrderByOptionOrderAsc(UUID questionId);

    Optional<Option> findByIdAndQuestionId(UUID optionId, UUID questionId);
}
