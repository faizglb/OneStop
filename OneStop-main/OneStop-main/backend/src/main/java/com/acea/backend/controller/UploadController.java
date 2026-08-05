package com.acea.backend.controller;

import com.acea.backend.dto.response.ApiResponse;
import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.acea.backend.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Tag(name = "Uploads", description = "File Uploads Management APIs (Cloudinary Integration)")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UploadController {

    private final CloudinaryService cloudinaryService;

    private static final List<String> PHOTO_EXTENSIONS = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
    private static final List<String> RESUME_EXTENSIONS = Arrays.asList("application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    private static final List<String> CERT_EXTENSIONS = Arrays.asList("application/pdf", "image/jpeg", "image/jpg", "image/png");

    private static final long MAX_PHOTO_SIZE = 2 * 1024 * 1024; // 2MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PostMapping("/profile-photo")
    @Operation(summary = "Upload student profile photo", description = "Max size: 2MB. Allowed types: JPG, JPEG, PNG, WEBP")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file) {
        
        validateFile(file, PHOTO_EXTENSIONS, MAX_PHOTO_SIZE, "Profile photo");
        String url = cloudinaryService.uploadFile(file, "profiles/photos");
        return ResponseEntity.ok(ApiResponse.success("Profile photo uploaded successfully", Map.of("url", url)));
    }

    @PostMapping("/resume")
    @Operation(summary = "Upload student resume", description = "Max size: 5MB. Allowed types: PDF, DOC, DOCX")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadResume(
            @RequestParam("file") MultipartFile file) {
        
        validateFile(file, RESUME_EXTENSIONS, MAX_FILE_SIZE, "Resume");
        String url = cloudinaryService.uploadFile(file, "profiles/resumes");
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", Map.of("url", url)));
    }

    @PostMapping("/certificate")
    @Operation(summary = "Upload student certification document", description = "Max size: 5MB. Allowed types: PDF, JPG, JPEG, PNG")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadCertificate(
            @RequestParam("file") MultipartFile file) {
        
        validateFile(file, CERT_EXTENSIONS, MAX_FILE_SIZE, "Certificate");
        String url = cloudinaryService.uploadFile(file, "profiles/certificates");
        return ResponseEntity.ok(ApiResponse.success("Certificate uploaded successfully", Map.of("url", url)));
    }

    private void validateFile(MultipartFile file, List<String> allowedTypes, long maxSize, String fileLabel) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, fileLabel + " file cannot be empty");
        }

        String contentType = file.getContentType();
        log.info("Validating {} upload: size={}, type={}", fileLabel, file.getSize(), contentType);

        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, 
                String.format("Invalid file type for %s. Allowed types: %s", fileLabel, allowedTypes));
        }

        if (file.getSize() > maxSize) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, 
                String.format("%s file size exceeds the limit of %d MB", fileLabel, maxSize / (1024 * 1024)));
        }
    }
}
