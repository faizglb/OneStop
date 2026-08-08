package com.acea.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDto {
    private long totalUsers;
    private long activeUsers;
    private long newRegistrations;
    private long assessmentsCompleted;
    private long careerRecommendationsGenerated;
    private long collegeRecommendationsGenerated;
    private long scholarshipRecommendationsGenerated;
    private long roadmapsGenerated;
    private long resumeAnalyses;
    private long skillGapReports;
    private long chatSessions;
    private long dailyActiveUsers;
    private long monthlyActiveUsers;

    // Aggregations / Popular Items
    private List<Map<String, Object>> mostPopularCareers;
    private List<Map<String, Object>> mostPopularColleges;
    private List<Map<String, Object>> mostPopularScholarships;

    // System Health & Resources
    private String systemHealth; // "HEALTHY" | "DEGRADED" | "CRITICAL"
    private double storageUsageGb;
    private double apiUsageRequestsToday;
    private long aiTokenConsumptionToday;
    private double cpuUsagePercent;
    private double memoryUsagePercent;
    private double diskUsagePercent;
    private double apiLatencyMs;
    private double errorRatePercent;
}
