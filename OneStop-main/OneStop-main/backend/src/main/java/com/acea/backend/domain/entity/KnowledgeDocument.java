package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "knowledge_documents",
    schema = "admin_schema"
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class KnowledgeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "file_type", nullable = false, length = 20)
    private String fileType; // PDF | DOCX | CSV

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "doc_category", nullable = false, length = 100)
    private String docCategory; // CAREERS | COLLEGES | SCHOLARSHIPS | RESUMES | SKILLS | FAQ

    @Column(name = "chunk_count", nullable = false)
    @Builder.Default
    private int chunkCount = 0;

    @Column(name = "vector_id", length = 255)
    private String vectorId;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "INDEXED"; // UPLOADED | INDEXING | INDEXED | FAILED

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
