package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "imported_datasets",
    schema = "admin_schema"
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ImportedDataset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "dataset_type", nullable = false, length = 50)
    private String datasetType; // CAREERS | COLLEGES | SCHOLARSHIPS | QUESTIONS

    @Column(name = "record_count", nullable = false)
    private int recordCount;

    @Column(name = "status", nullable = false, length = 30)
    private String status; // PENDING | SUCCESS | FAILED | PARTIAL

    @Column(name = "error_log", columnDefinition = "TEXT")
    private String errorLog;

    @Column(name = "imported_by", nullable = false)
    private UUID importedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
