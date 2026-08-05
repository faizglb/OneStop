package com.acea.backend.domain.entity;

import com.acea.backend.domain.enums.ChatSender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages", schema = "identity_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatSender sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "message_type", length = 20)
    @Builder.Default
    private String messageType = "TEXT";

    @Column(name = "agent_used", length = 50)
    private String agentUsed;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(columnDefinition = "JSONB")
    private String sources; // JSON array string of citations

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime timestamp;
}
