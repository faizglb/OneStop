package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.SystemMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SystemMetricsRepository extends JpaRepository<SystemMetrics, UUID> {
    List<SystemMetrics> findTop20ByMetricKeyOrderByCreatedAtDesc(String metricKey);
}
