package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ChatFeedback;
import com.acea.backend.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatFeedbackRepository extends JpaRepository<ChatFeedback, UUID> {

    Optional<ChatFeedback> findByMessage(ChatMessage message);

    Optional<ChatFeedback> findByMessageId(UUID messageId);
}
