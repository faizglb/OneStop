package com.acea.backend.service;

import com.acea.backend.dto.admin.KnowledgeDocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AdminKnowledgeBaseService {
    List<KnowledgeDocumentDto> getAllDocuments();
    KnowledgeDocumentDto uploadDocument(MultipartFile file, String category, UUID adminId);
    void deleteDocument(UUID docId, UUID adminId);
    void reindexKnowledgeBase(UUID adminId);
}
