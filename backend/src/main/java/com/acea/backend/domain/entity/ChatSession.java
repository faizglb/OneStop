package com.acea.backend.domain.entity;

import com.acea.backend.domain.enums.ChatStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions", schema = "identity_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, length = 200)
    @Builder.Default
    private String title = "New Conversation";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ChatStatus status = ChatStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private OffsetDateTime startedAt;

    @UpdateTimestamp
    @Column(name = "last_message_at", nullable = false)
    private OffsetDateTime lastMessageAt;

    @Column(name = "message_count", nullable = false)
    @Builder.Default
    private Integer messageCount = 0;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    public void incrementMessageCount() {
        this.messageCount = (this.messageCount == null ? 0 : this.messageCount) + 1;
    }
}
