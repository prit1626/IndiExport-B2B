package com.IndiExport.backend.service.impl;

import com.IndiExport.backend.exception.BusinessRuleViolationException;
import com.IndiExport.backend.service.FileStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageImpl implements FileStorageService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "IndiExport/" + folder));
        
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public void deleteFile(String fileUrl) {
        // Implementation for Cloudinary deletion would require extracting public_id from URL
        // Skipped for MVP unless strictly necessary
    }

    @Override
    public void validateFile(MultipartFile file, String[] allowedTypes, long maxSizeInMb) {
        if (file.isEmpty()) {
            throw new BusinessRuleViolationException("Cannot upload empty file");
        }

        if (file.getSize() > maxSizeInMb * 1024 * 1024) {
            throw new BusinessRuleViolationException("File size exceeds limit of " + maxSizeInMb + "MB");
        }

        String contentType = file.getContentType();
        boolean isAllowed = Arrays.asList(allowedTypes).contains(contentType);
        
        if (!isAllowed) {
            throw new BusinessRuleViolationException("File type " + contentType + " is not allowed. Allowed: " + Arrays.toString(allowedTypes));
        }
    }
}
