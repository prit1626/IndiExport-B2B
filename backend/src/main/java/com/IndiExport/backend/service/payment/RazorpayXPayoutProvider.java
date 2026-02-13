package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.exception.PayoutFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * RazorpayX implementation for seller INR payouts.
 * Uses RazorpayX Payouts API via HTTP to transfer funds to seller bank accounts.
 *
 * Note: The standard Razorpay Java SDK does not expose the RazorpayX Payouts API,
 * so we use java.net.http.HttpClient for direct API calls.
 */
@Service
public class RazorpayXPayoutProvider {

    @Value("${razorpayx.key-id}")
    private String keyId;

    @Value("${razorpayx.key-secret}")
    private String keySecret;

    @Value("${razorpayx.account-number}")
    private String accountNumber;

    private static final String RAZORPAYX_BASE_URL = "https://api.razorpay.com/v1";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create a payout to a seller's fund account via RazorpayX.
     *
     * @param fundAccountId RazorpayX fund account ID
     * @param amountPaise   Amount in INR paise
     * @param referenceId   Internal reference (payment ID)
     * @return RazorpayX payout ID
     */
    public String createPayout(String fundAccountId, long amountPaise, String referenceId) {
        try {
            String payload = objectMapper.writeValueAsString(java.util.Map.of(
                    "account_number", accountNumber,
                    "fund_account_id", fundAccountId,
                    "amount", amountPaise,
                    "currency", "INR",
                    "mode", "NEFT",
                    "purpose", "payout",
                    "queue_if_low_balance", true,
                    "reference_id", referenceId,
                    "narration", "IndiExport Seller Payout"
            ));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RAZORPAYX_BASE_URL + "/payouts"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Basic " + basicAuth())
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new PayoutFailedException("RazorpayX API error: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String payoutId = root.path("id").asText();
            return payoutId;

        } catch (PayoutFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new PayoutFailedException("RazorpayX: " + e.getMessage());
        }
    }

    /**
     * Verify RazorpayX webhook signature using HMAC SHA256.
     */
    public boolean verifyWebhookSignature(String payload, String signature, String webhookSecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = HexFormat.of().formatHex(hash);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String basicAuth() {
        String credentials = keyId + ":" + keySecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
