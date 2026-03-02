package com.IndiExport.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String razorpayOrderId;
    private String key;
    private long amountMinor;
    private String currency;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private Map<String, String> notes;
}
