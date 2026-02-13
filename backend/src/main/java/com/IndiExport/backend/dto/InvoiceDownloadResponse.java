package com.IndiExport.backend.dto;

public class InvoiceDownloadResponse {
    private String fileName;
    private String contentType; // "application/pdf"
    private String downloadUrl; // Presigned URL or public URL

    public InvoiceDownloadResponse() {}

    public InvoiceDownloadResponse(String fileName, String contentType, String downloadUrl) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.downloadUrl = downloadUrl;
    }

    // Getters and Setters
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}
