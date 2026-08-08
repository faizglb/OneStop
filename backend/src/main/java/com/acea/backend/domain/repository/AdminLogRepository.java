package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, UUID> {
    Page<AdminLog> findByModuleContainingIgnoreCaseOrActionContainingIgnoreCaseOrActorEmailContainingIgnoreCase(
        String module, String action, String actorEmail, Pageable pageable
    );
}
