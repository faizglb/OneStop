package com.acea.backend.controller;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.chat.*;
import com.acea.backend.security.UserPrincipal;
import com.acea.backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "AI Career Chatbot", description = "Endpoints for multi-turn RAG Chatbot conversations, sessions, and feedback")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/session")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Create Chat Session", description = "Initialise a new active chat conversation session.")
    public ResponseEntity<ChatSessionResponse> createSession(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.getUser();
        ChatSessionResponse session = chatService.createSession(currentUser);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/session")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Chat Sessions", description = "Retrieve list of all active chat sessions for the authenticated student.")
    public ResponseEntity<List<ChatSessionResponse>> getSessions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User currentUser = principal.getUser();
        List<ChatSessionResponse> sessions = chatService.getSessions(currentUser);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/message")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Send Chat Message", description = "Send a message to the AI Career Advisor. Automatically routes to specialised agents using RAG and student profile context.")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChatMessageRequest request
    ) {
        User currentUser = principal.getUser();
        ChatMessageResponse response = chatService.sendMessage(currentUser, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{sessionId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get Session History", description = "Retrieve all messages for a specific chat session ordered by timestamp.")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID sessionId
    ) {
        User currentUser = principal.getUser();
        List<ChatMessageResponse> history = chatService.getHistory(currentUser, sessionId);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/session/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Delete/Archive Session", description = "Archive a chat session.")
    public ResponseEntity<Void> deleteSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id
    ) {
        User currentUser = principal.getUser();
        chatService.deleteSession(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/feedback")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit Message Feedback", description = "Submit 1-5 star rating and optional text feedback for an assistant response.")
    public ResponseEntity<Void> submitFeedback(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChatFeedbackRequest request
    ) {
        User currentUser = principal.getUser();
        chatService.submitFeedback(currentUser, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Search Conversation History", description = "Search across all chat messages by keyword.")
    public ResponseEntity<List<ChatMessageResponse>> searchConversations(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String query
    ) {
        User currentUser = principal.getUser();
        List<ChatMessageResponse> results = chatService.searchConversations(currentUser, query);
        return ResponseEntity.ok(results);
    }
}
