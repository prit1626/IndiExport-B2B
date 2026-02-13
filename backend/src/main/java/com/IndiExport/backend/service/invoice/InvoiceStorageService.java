package com.IndiExport.backend.service.invoice;

import com.IndiExport.backend.exception.BusinessRuleViolationException;
import com.IndiExport.backend.service.FileStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceStorageService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;
    
    // Using the same credentials as main storage service
    // In a real app, might want a specific bucket/folder for invoices
    
    public String uploadInvoicePdf(byte[] pdfBytes, String fileName) {
        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true
            ));

            Map uploadResult = cloudinary.uploader().upload(pdfBytes,
                    ObjectUtils.asMap(
                            "folder", "IndiExport/invoices",
                            "resource_type", "raw", // Important for PDF
                            "public_id", fileName,
                            "overwrite", true
                    ));

            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Invoice storage failed", e);
        }
    }
}
