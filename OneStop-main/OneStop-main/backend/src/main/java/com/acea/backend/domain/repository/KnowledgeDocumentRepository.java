package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, UUID> {
    List<KnowledgeDocument> findByStatus(String status);
    List<KnowledgeDocument> findByDocCategory(String docCategory);
}
