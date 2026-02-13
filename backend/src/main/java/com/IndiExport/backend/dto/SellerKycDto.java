package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.SellerKyc;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO container for Seller KYC.
 */
public class SellerKycDto {

    public static class SellerKycStatusResponse {
        private SellerKyc.VerificationStatus verificationStatus;
        private String rejectionReason;
        private boolean iecUploaded;
        private boolean panUploaded;
        private boolean gstinUploaded;
        private boolean bankDetailsUploaded;
        private LocalDateTime submittedAt;
        private LocalDateTime verifiedAt;

        public SellerKycStatusResponse() {}

        // Getters and Setters
        public SellerKyc.VerificationStatus getVerificationStatus() { return verificationStatus; }
        public void setVerificationStatus(SellerKyc.VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
        public boolean isIecUploaded() { return iecUploaded; }
        public void setIecUploaded(boolean iecUploaded) { this.iecUploaded = iecUploaded; }
        public boolean isPanUploaded() { return panUploaded; }
        public void setPanUploaded(boolean panUploaded) { this.panUploaded = panUploaded; }
        public boolean isGstinUploaded() { return gstinUploaded; }
        public void setGstinUploaded(boolean gstinUploaded) { this.gstinUploaded = gstinUploaded; }
        public boolean isBankDetailsUploaded() { return bankDetailsUploaded; }
        public void setBankDetailsUploaded(boolean bankDetailsUploaded) { this.bankDetailsUploaded = bankDetailsUploaded; }
        public LocalDateTime getSubmittedAt() { return submittedAt; }
        public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
        public LocalDateTime getVerifiedAt() { return verifiedAt; }
        public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    }

    public static class UpdateBankDetailsRequest {
        @NotBlank(message = "Bank account holder name is required")
        private String bankAccountHolderName;

        @NotBlank(message = "Bank account number is required")
        @Pattern(regexp = "^[0-9]{9,18}$", message = "Invalid bank account number")
        private String bankAccountNumber;

        @NotBlank(message = "IFSC code is required")
        @Pattern(regexp = "^[A-Z]{4}[0]{1}[A-Z0-9]{6}$", message = "Invalid IFSC code")
        private String bankIfscCode;

        @NotBlank(message = "Bank name is required")
        private String bankName;

        private String bankBranch;

        @NotBlank(message = "Payout method preference is required")
        private String payoutMethodPreference; // Stripe, Razorpay, etc.

        public UpdateBankDetailsRequest() {}

        // Getters and Setters
        public String getBankAccountHolderName() { return bankAccountHolderName; }
        public void setBankAccountHolderName(String bankAccountHolderName) { this.bankAccountHolderName = bankAccountHolderName; }
        public String getBankAccountNumber() { return bankAccountNumber; }
        public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
        public String getBankIfscCode() { return bankIfscCode; }
        public void setBankIfscCode(String bankIfscCode) { this.bankIfscCode = bankIfscCode; }
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public String getBankBranch() { return bankBranch; }
        public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }
        public String getPayoutMethodPreference() { return payoutMethodPreference; }
        public void setPayoutMethodPreference(String payoutMethodPreference) { this.payoutMethodPreference = payoutMethodPreference; }
    }

    public static class AdminSellerKycResponse {
        private UUID sellerId;
        private String companyName;
        private String iecNumber;
        private String iecDocumentUrl;
        private String panNumber;
        private String panDocumentUrl;
        private String gstinNumber;
        private String gstinDocumentUrl;
        private String bankAccountHolderName;
        private String bankAccountNumberMasked;
        private String bankIfscCode;
        private String bankName;
        private String bankBranch;
        private String payoutMethodPreference;
        private SellerKyc.VerificationStatus verificationStatus;
        private LocalDateTime submittedAt;

        public AdminSellerKycResponse() {}

        public static AdminSellerKycResponseBuilder builder() {
            return new AdminSellerKycResponseBuilder();
        }

        public static class AdminSellerKycResponseBuilder {
            private AdminSellerKycResponse response = new AdminSellerKycResponse();

            public AdminSellerKycResponseBuilder sellerId(UUID sellerId) { response.setSellerId(sellerId); return this; }
            public AdminSellerKycResponseBuilder companyName(String companyName) { response.setCompanyName(companyName); return this; }
            public AdminSellerKycResponseBuilder iecNumber(String iecNumber) { response.setIecNumber(iecNumber); return this; }
            public AdminSellerKycResponseBuilder iecDocumentUrl(String iecDocumentUrl) { response.setIecDocumentUrl(iecDocumentUrl); return this; }
            public AdminSellerKycResponseBuilder panNumber(String panNumber) { response.setPanNumber(panNumber); return this; }
            public AdminSellerKycResponseBuilder panDocumentUrl(String panDocumentUrl) { response.setPanDocumentUrl(panDocumentUrl); return this; }
            public AdminSellerKycResponseBuilder gstinNumber(String gstinNumber) { response.setGstinNumber(gstinNumber); return this; }
            public AdminSellerKycResponseBuilder gstinDocumentUrl(String gstinDocumentUrl) { response.setGstinDocumentUrl(gstinDocumentUrl); return this; }
            public AdminSellerKycResponseBuilder bankAccountHolderName(String bankAccountHolderName) { response.setBankAccountHolderName(bankAccountHolderName); return this; }
            public AdminSellerKycResponseBuilder bankAccountNumberMasked(String bankAccountNumberMasked) { response.setBankAccountNumberMasked(bankAccountNumberMasked); return this; }
            public AdminSellerKycResponseBuilder bankIfscCode(String bankIfscCode) { response.setBankIfscCode(bankIfscCode); return this; }
            public AdminSellerKycResponseBuilder bankName(String bankName) { response.setBankName(bankName); return this; }
            public AdminSellerKycResponseBuilder bankBranch(String bankBranch) { response.setBankBranch(bankBranch); return this; }
            public AdminSellerKycResponseBuilder payoutMethodPreference(String payoutMethodPreference) { response.setPayoutMethodPreference(payoutMethodPreference); return this; }
            public AdminSellerKycResponseBuilder verificationStatus(SellerKyc.VerificationStatus verificationStatus) { response.setVerificationStatus(verificationStatus); return this; }
            public AdminSellerKycResponseBuilder submittedAt(LocalDateTime submittedAt) { response.setSubmittedAt(submittedAt); return this; }
            public AdminSellerKycResponse build() { return response; }
        }

        // Getters and Setters
        public UUID getSellerId() { return sellerId; }
        public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getIecNumber() { return iecNumber; }
        public void setIecNumber(String iecNumber) { this.iecNumber = iecNumber; }
        public String getIecDocumentUrl() { return iecDocumentUrl; }
        public void setIecDocumentUrl(String iecDocumentUrl) { this.iecDocumentUrl = iecDocumentUrl; }
        public String getPanNumber() { return panNumber; }
        public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
        public String getPanDocumentUrl() { return panDocumentUrl; }
        public void setPanDocumentUrl(String panDocumentUrl) { this.panDocumentUrl = panDocumentUrl; }
        public String getGstinNumber() { return gstinNumber; }
        public void setGstinNumber(String gstinNumber) { this.gstinNumber = gstinNumber; }
        public String getGstinDocumentUrl() { return gstinDocumentUrl; }
        public void setGstinDocumentUrl(String gstinDocumentUrl) { this.gstinDocumentUrl = gstinDocumentUrl; }
        public String getBankAccountHolderName() { return bankAccountHolderName; }
        public void setBankAccountHolderName(String bankAccountHolderName) { this.bankAccountHolderName = bankAccountHolderName; }
        public String getBankAccountNumberMasked() { return bankAccountNumberMasked; }
        public void setBankAccountNumberMasked(String bankAccountNumberMasked) { this.bankAccountNumberMasked = bankAccountNumberMasked; }
        public String getBankIfscCode() { return bankIfscCode; }
        public void setBankIfscCode(String bankIfscCode) { this.bankIfscCode = bankIfscCode; }
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public String getBankBranch() { return bankBranch; }
        public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }
        public String getPayoutMethodPreference() { return payoutMethodPreference; }
        public void setPayoutMethodPreference(String payoutMethodPreference) { this.payoutMethodPreference = payoutMethodPreference; }
        public SellerKyc.VerificationStatus getVerificationStatus() { return verificationStatus; }
        public void setVerificationStatus(SellerKyc.VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
        public LocalDateTime getSubmittedAt() { return submittedAt; }
        public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    }

    public static class AdminRejectSellerRequest {
        @NotBlank(message = "Rejection reason is required")
        private String reason;

        public AdminRejectSellerRequest() {}

        // Getters and Setters
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class UploadFileResponse {
        private String url;
        private String fileName;

        public UploadFileResponse() {}

        public UploadFileResponse(String url, String fileName) {
            this.url = url;
            this.fileName = fileName;
        }

        // Getters and Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
    }
}
