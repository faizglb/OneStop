package com.acea.backend.controller.admin;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.admin.KnowledgeDocumentDto;
import com.acea.backend.service.AdminKnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/ai/knowledge-base")
@RequiredArgsConstructor
@Tag(name = "Admin - Knowledge Base Management", description = "Endpoints for managing RAG vector documents and re-indexing")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CONTENT_MANAGER')")
public class AdminAiKnowledgeBaseController {

    private final AdminKnowledgeBaseService adminKnowledgeBaseService;

    @GetMapping
    @Operation(summary = "Get all knowledge base documents and indexing status")
    public ResponseEntity<List<KnowledgeDocumentDto>> getAllDocuments() {
        return ResponseEntity.ok(adminKnowledgeBaseService.getAllDocuments());
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload PDF/DOCX/CSV document into RAG vector store")
    public ResponseEntity<KnowledgeDocumentDto> uploadDocument(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "category", defaultValue = "GENERAL") String category,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        return ResponseEntity.ok(adminKnowledgeBaseService.uploadDocument(file, category, adminId));
    }

    @DeleteMapping("/{docId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete document from RAG knowledge base")
    public ResponseEntity<Void> deleteDocument(
        @PathVariable UUID docId,
        @AuthenticationPrincipal User currentUser
    ) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        adminKnowledgeBaseService.deleteDocument(docId, adminId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reindex")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Re-index entire vector knowledge base")
    public ResponseEntity<Void> reindexKnowledgeBase(@AuthenticationPrincipal User currentUser) {
        UUID adminId = currentUser != null ? currentUser.getId() : UUID.randomUUID();
        adminKnowledgeBaseService.reindexKnowledgeBase(adminId);
        return ResponseEntity.ok().build();
    }
}
