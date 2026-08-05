package com.acea.backend.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request payload to trigger AI resume analysis.
 * The file must already be uploaded to Cloudinary via UploadController.
 */
public record ResumeAnalysisRequest(

        @NotBlank(message = "File URL is required")
        String fileUrl,

        @NotBlank(message = "File name is required")
        @Size(max = 512, message = "File name too long")
        String fileName,

        @NotBlank(message = "File type is required")
        @Pattern(regexp = "PDF|DOCX", message = "Supported file types: PDF, DOCX")
        String fileType,

        // Optional: context from Sprint 4 career recommendations
        String targetCareer
) {}
