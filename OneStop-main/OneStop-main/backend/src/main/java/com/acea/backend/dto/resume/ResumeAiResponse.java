package com.acea.backend.dto.resume;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response from the Python FastAPI AI service after resume analysis.
 */
public record ResumeAiResponse(

        @JsonProperty("atsScore")
        BigDecimal atsScore,

        @JsonProperty("overallScore")
        BigDecimal overallScore,

        @JsonProperty("wordCount")
        Integer wordCount,

        @JsonProperty("pageCount")
        Integer pageCount,

        @JsonProperty("scoreBreakdown")
        ScoreBreakdownDto scoreBreakdown,

        @JsonProperty("sections")
        List<SectionDto> sections,

        @JsonProperty("keywords")
        List<KeywordDto> keywords,

        @JsonProperty("suggestions")
        List<SuggestionDto> suggestions
) {
    public record ScoreBreakdownDto(
            @JsonProperty("keywordScore")    BigDecimal keywordScore,
            @JsonProperty("sectionScore")    BigDecimal sectionScore,
            @JsonProperty("formattingScore") BigDecimal formattingScore,
            @JsonProperty("experienceScore") BigDecimal experienceScore,
            @JsonProperty("educationScore")  BigDecimal educationScore,
            @JsonProperty("grammarScore")    BigDecimal grammarScore
    ) {}

    public record SectionDto(
            @JsonProperty("sectionName")     String sectionName,
            @JsonProperty("detected")        Boolean detected,
            @JsonProperty("quality")         String quality,
            @JsonProperty("contentSnippet")  String contentSnippet
    ) {}

    public record KeywordDto(
            @JsonProperty("keyword")    String keyword,
            @JsonProperty("category")   String category,
            @JsonProperty("found")      Boolean found,
            @JsonProperty("frequency")  Integer frequency,
            @JsonProperty("importance") String importance
    ) {}

    public record SuggestionDto(
            @JsonProperty("category")    String category,
            @JsonProperty("priority")    String priority,
            @JsonProperty("title")       String title,
            @JsonProperty("description") String description,
            @JsonProperty("example")     String example
    ) {}
}
