package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.*;
import com.acea.backend.domain.enums.ChatSender;
import com.acea.backend.domain.enums.ChatStatus;
import com.acea.backend.domain.repository.*;
import com.acea.backend.dto.chat.*;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.ChatAiClientService;
import com.acea.backend.service.ChatContextAggregatorService;
import com.acea.backend.service.ChatService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatFeedbackRepository feedbackRepository;
    private final ChatContextAggregatorService contextAggregatorService;
    private final ChatAiClientService aiClientService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ChatSessionResponse createSession(User user) {
        ChatSession session = ChatSession.builder()
                .student(user)
                .title("New Conversation")
                .status(ChatStatus.ACTIVE)
                .messageCount(0)
                .build();

        ChatSession saved = sessionRepository.save(session);
        log.info("Created chat session: {} for user: {}", saved.getId(), user.getEmail());

        return mapToSessionResponse(saved, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getSessions(User user) {
        List<ChatSession> sessions = sessionRepository.findByStudentAndStatusOrderByLastMessageAtDesc(user, ChatStatus.ACTIVE);
        return sessions.stream().map(s -> {
            List<ChatMessage> messages = messageRepository.findBySessionOrderByTimestampAsc(s);
            String lastPreview = messages.isEmpty() ? null : messages.get(messages.size() - 1).getMessage();
            return mapToSessionResponse(s, lastPreview);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(User user, ChatMessageRequest request) {
        ChatSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findByIdAndStudent(request.getSessionId(), user)
                    .orElseThrow(() -> new ResourceNotFoundException("Chat session not found or access denied."));
        } else {
            // Auto-create session if none provided
            session = ChatSession.builder()
                    .student(user)
                    .title(generateTitleFromMessage(request.getMessage()))
                    .status(ChatStatus.ACTIVE)
                    .messageCount(0)
                    .build();
            session = sessionRepository.save(session);
        }

        // Auto-update title if it's the first message
        if ("New Conversation".equals(session.getTitle()) && session.getMessageCount() == 0) {
            session.setTitle(generateTitleFromMessage(request.getMessage()));
        }

        // 1. Save User Message
        ChatMessage userMsg = ChatMessage.builder()
                .session(session)
                .sender(ChatSender.USER)
                .message(request.getMessage())
                .messageType("TEXT")
                .build();

        messageRepository.save(userMsg);
        session.incrementMessageCount();

        // 2. Build Student Context Snapshot
        StudentContextDto contextDto = contextAggregatorService.buildContextForStudent(user);

        // 3. Call AI Chatbot Microservice
        ChatAiRequest aiRequest = ChatAiRequest.builder()
                .sessionId(session.getId().toString())
                .studentId(user.getId().toString())
                .message(request.getMessage())
                .studentContext(contextDto)
                .build();

        ChatAiResponse aiResponse = aiClientService.chat(aiRequest);

        // 4. Save Assistant Message
        String sourcesJson = null;
        if (aiResponse.getSources() != null && !aiResponse.getSources().isEmpty()) {
            try {
                sourcesJson = objectMapper.writeValueAsString(aiResponse.getSources());
            } catch (Exception e) {
                log.warn("Failed to serialize sources to JSON", e);
            }
        }

        ChatMessage assistantMsg = ChatMessage.builder()
                .session(session)
                .sender(ChatSender.ASSISTANT)
                .message(aiResponse.getResponse())
                .messageType("TEXT")
                .agentUsed(aiResponse.getAgentUsed())
                .confidenceScore(aiResponse.getConfidenceScore())
                .sources(sourcesJson)
                .build();

        ChatMessage savedAssistantMsg = messageRepository.save(assistantMsg);
        session.incrementMessageCount();
        session.setLastMessageAt(OffsetDateTime.now());
        sessionRepository.save(session);

        return mapToMessageResponse(savedAssistantMsg, aiResponse.getSources());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getHistory(User user, UUID sessionId) {
        ChatSession session = sessionRepository.findByIdAndStudent(sessionId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found or access denied."));

        List<ChatMessage> messages = messageRepository.findBySessionOrderByTimestampAsc(session);
        return messages.stream().map(m -> {
            List<SourceCitationDto> citations = deserializeSources(m.getSources());
            return mapToMessageResponse(m, citations);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSession(User user, UUID sessionId) {
        ChatSession session = sessionRepository.findByIdAndStudent(sessionId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found or access denied."));

        session.setStatus(ChatStatus.ARCHIVED);
        sessionRepository.save(session);
        log.info("Archived chat session: {}", sessionId);
    }

    @Override
    @Transactional
    public void submitFeedback(User user, ChatFeedbackRequest request) {
        ChatMessage message = messageRepository.findById(request.getMessageId())
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + request.getMessageId()));

        if (!message.getSession().getStudent().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Access denied to message feedback.");
        }

        ChatFeedback feedback = feedbackRepository.findByMessage(message)
                .orElse(ChatFeedback.builder().message(message).build());

        feedback.setRating(request.getRating());
        feedback.setFeedbackText(request.getFeedbackText());

        feedbackRepository.save(feedback);
        log.info("Saved rating {} for message: {}", request.getRating(), message.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> searchConversations(User user, String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<ChatMessage> messages = messageRepository.searchByStudentAndQuery(user.getId(), query.trim());
        return messages.stream().map(m -> {
            List<SourceCitationDto> citations = deserializeSources(m.getSources());
            return mapToMessageResponse(m, citations);
        }).collect(Collectors.toList());
    }

    private String generateTitleFromMessage(String message) {
        if (message == null) return "New Conversation";
        String trimmed = message.trim();
        if (trimmed.length() <= 40) return trimmed;
        return trimmed.substring(0, 37) + "...";
    }

    private ChatSessionResponse mapToSessionResponse(ChatSession session, String lastPreview) {
        return ChatSessionResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .status(session.getStatus().name())
                .startedAt(session.getStartedAt())
                .lastMessageAt(session.getLastMessageAt())
                .messageCount(session.getMessageCount())
                .lastMessagePreview(lastPreview)
                .build();
    }

    private ChatMessageResponse mapToMessageResponse(ChatMessage message, List<SourceCitationDto> sources) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .sessionId(message.getSession().getId())
                .sender(message.getSender().name())
                .message(message.getMessage())
                .messageType(message.getMessageType())
                .agentUsed(message.getAgentUsed())
                .confidenceScore(message.getConfidenceScore())
                .sources(sources)
                .timestamp(message.getTimestamp())
                .build();
    }

    private List<SourceCitationDto> deserializeSources(String sourcesJson) {
        if (sourcesJson == null || sourcesJson.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(sourcesJson, new TypeReference<List<SourceCitationDto>>() {});
        } catch (Exception e) {
            log.warn("Could not deserialize sources JSON", e);
            return new ArrayList<>();
        }
    }
}
