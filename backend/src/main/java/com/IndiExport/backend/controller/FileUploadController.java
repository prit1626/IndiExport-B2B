package com.IndiExport.backend.controller;

import com.IndiExport.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic single-file upload endpoint — uploads to Cloudinary via FileStorageService.
 * Returns the public HTTPS Cloudinary URL.
 *
 * POST /api/v1/upload
 * Content-Type: multipart/form-data
 * Field: "file"
 *
 * Response: { "url": "https://res.cloudinary.com/..." }
 */
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileStorageService fileStorageService;

    private static final String[] ALLOWED_TYPES = {
        "image/jpeg", "image/png", "image/webp", "image/gif",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file) {

        fileStorageService.validateFile(file, ALLOWED_TYPES, 10); // 10 MB max

        String url;
        try {
            url = fileStorageService.uploadFile(file, "rfq");
        } catch (IOException e) {
            log.error("Cloudinary upload failed for file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }

        log.info("Uploaded file to Cloudinary: {}", url);

        Map<String, String> body = new HashMap<>();
        body.put("url", url);
        body.put("fileName", file.getOriginalFilename());
        body.put("contentType", file.getContentType());
        return ResponseEntity.ok(body);
    }
}
