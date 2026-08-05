package com.acea.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDocumentDto {
    private UUID id;
    private String filename;
    private String fileType;
    private Long fileSize;
    private String docCategory;
    private int chunkCount;
    private String vectorId;
    private String status;
    private UUID uploadedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
