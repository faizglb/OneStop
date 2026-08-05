package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ChatSession;
import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.enums.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    List<ChatSession> findByStudentOrderByLastMessageAtDesc(User student);

    List<ChatSession> findByStudentAndStatusOrderByLastMessageAtDesc(User student, ChatStatus status);

    Optional<ChatSession> findByIdAndStudent(UUID id, User student);

    long countByStudentAndStatus(User student, ChatStatus status);
}
