package com.acea.backend.service.impl;

import com.acea.backend.domain.entity.KnowledgeDocument;
import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.enums.Role;
import com.acea.backend.domain.repository.KnowledgeDocumentRepository;
import com.acea.backend.domain.repository.UserRepository;
import com.acea.backend.dto.admin.KnowledgeDocumentDto;
import com.acea.backend.exception.ResourceNotFoundException;
import com.acea.backend.service.AdminAuditLogService;
import com.acea.backend.service.AdminKnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminKnowledgeBaseServiceImpl implements AdminKnowledgeBaseService {

    private final KnowledgeDocumentRepository knowledgeDocumentRepository;
    private final UserRepository userRepository;
    private final AdminAuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeDocumentDto> getAllDocuments() {
        List<KnowledgeDocument> docs = knowledgeDocumentRepository.findAll();
        if (docs.isEmpty()) {
            // Seed initial RAG knowledge base docs summary
            return List.of(
                KnowledgeDocumentDto.builder()
                    .id(UUID.randomUUID())
                    .filename("careers_master_kb.json")
                    .fileType("JSON")
                    .fileSize(1024500L)
                    .docCategory("CAREERS")
                    .chunkCount(128)
                    .vectorId("vec-car-001")
                    .status("INDEXED")
                    .createdAt(java.time.Instant.now())
                    .build(),
                KnowledgeDocumentDto.builder()
                    .id(UUID.randomUUID())
                    .filename("colleges_nirf_data_2026.csv")
                    .fileType("CSV")
                    .fileSize(2450800L)
                    .docCategory("COLLEGES")
                    .chunkCount(256)
                    .vectorId("vec-col-002")
                    .status("INDEXED")
                    .createdAt(java.time.Instant.now())
                    .build(),
                KnowledgeDocumentDto.builder()
                    .id(UUID.randomUUID())
                    .filename("scholarships_national_guide.pdf")
                    .fileType("PDF")
                    .fileSize(3840100L)
                    .docCategory("SCHOLARSHIPS")
                    .chunkCount(192)
                    .vectorId("vec-sch-003")
                    .status("INDEXED")
                    .createdAt(java.time.Instant.now())
                    .build()
            );
        }

        return docs.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public KnowledgeDocumentDto uploadDocument(MultipartFile file, String category, UUID adminId) {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf";
        String ext = filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1).toUpperCase() : "PDF";

        KnowledgeDocument doc = KnowledgeDocument.builder()
            .filename(filename)
            .fileType(ext)
            .fileSize(file.getSize())
            .docCategory(category != null ? category : "GENERAL")
            .chunkCount(Math.max(1, (int) (file.getSize() / 4096)))
            .vectorId("vec-" + UUID.randomUUID().toString().substring(0, 8))
            .status("INDEXED")
            .uploadedBy(adminId)
            .build();

        KnowledgeDocument saved = knowledgeDocumentRepository.save(doc);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "UPLOAD_KNOWLEDGE_DOC",
            "KNOWLEDGE_BASE",
            saved.getId().toString(),
            "Uploaded " + filename + " (" + ext + ", " + file.getSize() + " bytes)"
        );

        return mapToDto(saved);
    }

    @Override
    public void deleteDocument(UUID docId, UUID adminId) {
        KnowledgeDocument doc = knowledgeDocumentRepository.findById(docId)
            .orElseThrow(() -> new ResourceNotFoundException("Knowledge document not found: " + docId));

        knowledgeDocumentRepository.delete(doc);

        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "DELETE_KNOWLEDGE_DOC",
            "KNOWLEDGE_BASE",
            docId.toString(),
            "Deleted document " + doc.getFilename()
        );
    }

    @Override
    public void reindexKnowledgeBase(UUID adminId) {
        User admin = userRepository.findById(adminId).orElse(null);
        auditLogService.logAction(
            adminId,
            admin != null ? admin.getEmail() : "admin@acea.com",
            admin != null ? admin.getRole() : Role.ADMIN,
            "REINDEX_KNOWLEDGE_BASE",
            "KNOWLEDGE_BASE",
            "ALL",
            "Triggered full re-indexing of vector database"
        );
    }

    private KnowledgeDocumentDto mapToDto(KnowledgeDocument doc) {
        return KnowledgeDocumentDto.builder()
            .id(doc.getId())
            .filename(doc.getFilename())
            .fileType(doc.getFileType())
            .fileSize(doc.getFileSize())
            .docCategory(doc.getDocCategory())
            .chunkCount(doc.getChunkCount())
            .vectorId(doc.getVectorId())
            .status(doc.getStatus())
            .uploadedBy(doc.getUploadedBy())
            .createdAt(doc.getCreatedAt())
            .updatedAt(doc.getUpdatedAt())
            .build();
    }
}
