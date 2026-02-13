package com.IndiExport.backend.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    
    /**
     * Uploads a file and returns the accessible URL.
     * 
     * @param file The file to upload
     * @param folder The folder naming in the storage provider
     * @return Publicly accessible URL of the uploaded file
     * @throws IOException If upload fails
     */
    String uploadFile(MultipartFile file, String folder) throws IOException;

    /**
     * Deletes a file from storage.
     * 
     * @param fileUrl URL of the file to delete
     */
    void deleteFile(String fileUrl);

    /**
     * Validates if the file is allowed based on type and size.
     * 
     * @param file The file to validate
     * @param allowedTypes Array of allowed MIME types (e.g., "image/png", "application/pdf")
     * @param maxSizeInMb Maximum allowed size in megabytes
     */
    void validateFile(MultipartFile file, String[] allowedTypes, long maxSizeInMb);
}
