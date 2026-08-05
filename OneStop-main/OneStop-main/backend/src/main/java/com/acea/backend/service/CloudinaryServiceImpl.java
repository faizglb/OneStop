package com.acea.backend.service;

import com.acea.backend.exception.BadRequestException;
import com.acea.backend.exception.ErrorCode;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "Cannot upload empty file");
        }

        try {
            log.info("Uploading file to Cloudinary folder: {}", folder);
            Map<?, ?> options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto"
            );
            
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String url = (String) uploadResult.get("secure_url");
            log.info("Upload completed successfully. URL: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new BadRequestException(ErrorCode.VALIDATION_FAILED, "File upload failed: " + e.getMessage());
        }
    }
}
