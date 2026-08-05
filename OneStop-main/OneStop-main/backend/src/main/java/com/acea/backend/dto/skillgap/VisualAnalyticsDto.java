package com.acea.backend.dto.skillgap;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisualAnalyticsDto {

    private List<RadarChartItem> radarChart;
    private List<HeatmapItem> skillHeatmap;
    private List<PriorityMatrixItem> priorityMatrix;
    private List<LearningTimelineStep> learningTimeline;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RadarChartItem {
        private String subject;
        private BigDecimal student;
        private BigDecimal required;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class HeatmapItem {
        private String skillName;
        private String category;
        private BigDecimal currentScore;
        private BigDecimal requiredScore;
        private BigDecimal gap;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PriorityMatrixItem {
        private String skillName;
        private BigDecimal importance;
        private BigDecimal effort;
        private String quadrant;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LearningTimelineStep {
        private Integer stepNumber;
        private String phase;
        private String title;
        private List<String> skillsCovered;
        private Integer estimatedWeeks;
    }
}
