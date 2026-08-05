package com.acea.backend.service;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.chat.*;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    ChatSessionResponse createSession(User user);

    List<ChatSessionResponse> getSessions(User user);

    ChatMessageResponse sendMessage(User user, ChatMessageRequest request);

    List<ChatMessageResponse> getHistory(User user, UUID sessionId);

    void deleteSession(User user, UUID sessionId);

    void submitFeedback(User user, ChatFeedbackRequest request);

    List<ChatMessageResponse> searchConversations(User user, String query);
}
