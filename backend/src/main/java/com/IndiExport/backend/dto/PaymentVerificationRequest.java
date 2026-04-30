package com.IndiExport.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationRequest {
    private UUID orderId;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}
