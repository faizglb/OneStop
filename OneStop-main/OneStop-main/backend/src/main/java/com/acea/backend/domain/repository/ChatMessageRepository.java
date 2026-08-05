package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ChatMessage;
import com.acea.backend.domain.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findBySessionOrderByTimestampAsc(ChatSession session);

    List<ChatMessage> findBySessionIdOrderByTimestampAsc(UUID sessionId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.session.student.id = :studentId AND LOWER(cm.message) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY cm.timestamp DESC")
    List<ChatMessage> searchByStudentAndQuery(@Param("studentId") UUID studentId, @Param("query") String query);
}
