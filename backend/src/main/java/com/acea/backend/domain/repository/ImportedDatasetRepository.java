package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.ImportedDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImportedDatasetRepository extends JpaRepository<ImportedDataset, UUID> {
    List<ImportedDataset> findTop20ByOrderByCreatedAtDesc();
}
