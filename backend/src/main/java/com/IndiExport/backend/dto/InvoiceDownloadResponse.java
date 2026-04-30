package com.IndiExport.backend.dto;

import java.util.UUID;

public class InvoiceDownloadResponse {
    private UUID id;
    private String fileName;
    private String contentType; // "application/pdf"
    private String downloadUrl; // Presigned URL or public URL

    public InvoiceDownloadResponse() {}

    public InvoiceDownloadResponse(UUID id, String fileName, String contentType, String downloadUrl) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.downloadUrl = downloadUrl;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}
