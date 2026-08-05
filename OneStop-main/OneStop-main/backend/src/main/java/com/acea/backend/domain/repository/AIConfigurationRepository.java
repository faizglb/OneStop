package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.AIConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AIConfigurationRepository extends JpaRepository<AIConfiguration, UUID> {
    Optional<AIConfiguration> findByConfigKey(String configKey);
}
