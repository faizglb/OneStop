package com.acea.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "system_metrics",
    schema = "admin_schema",
    indexes = {
        @Index(name = "idx_system_metrics_key", columnList = "metric_key"),
        @Index(name = "idx_system_metrics_created_at", columnList = "created_at")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SystemMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "metric_key", nullable = false, length = 100)
    private String metricKey; // e.g. "cpu_usage", "memory_usage", "storage_usage", "api_latency", "error_rate", "ai_tokens"

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "metric_type", nullable = false, length = 50)
    private String metricType; // e.g. "PERCENTAGE", "BYTES", "MILLISECONDS", "COUNT"

    @Column(name = "notes", length = 255)
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
