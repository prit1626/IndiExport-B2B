package com.IndiExport.backend.service.currency;

import com.IndiExport.backend.exception.ExternalApiException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Client for the Frankfurter API (https://frankfurter.app).
 * Backed by European Central Bank reference rates. Free, no API key.
 *
 * Returns rates as raw doubles. Callers MUST convert to rateMicros (long)
 * immediately to avoid floating-point drift.
 */
@Component
public class FrankfurterExchangeRateClient {

    private static final Logger log = LoggerFactory.getLogger(FrankfurterExchangeRateClient.class);
    private static final String PROVIDER_NAME = "frankfurter.app";

    private final RestClient restClient;
    private final String baseUrl;

    public FrankfurterExchangeRateClient(
            RestClient restClient,
            @Value("${currency.provider.base-url:https://api.frankfurter.app}") String baseUrl) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
    }

    /**
     * Fetch the latest exchange rate for INR → targetCurrency.
     *
     * @param targetCurrency ISO 4217 currency code (e.g. "USD", "EUR")
     * @return the exchange rate as a double (e.g. 0.01195 for INR→USD)
     * @throws ExternalApiException if the API call fails
     */
    public double fetchRate(String targetCurrency) {
        String url = baseUrl + "/latest?from=INR&to=" + targetCurrency;
        log.info("Fetching exchange rate from {} for INR → {}", PROVIDER_NAME, targetCurrency);

        try {
            JsonNode response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null || !response.has("rates")) {
                throw new ExternalApiException(PROVIDER_NAME,
                        "Invalid response format: missing 'rates' field");
            }

            JsonNode rates = response.get("rates");
            if (!rates.has(targetCurrency)) {
                throw new ExternalApiException(PROVIDER_NAME,
                        "Currency '" + targetCurrency + "' not found in response");
            }

            double rate = rates.get(targetCurrency).asDouble();
            log.info("Fetched rate: 1 INR = {} {}", rate, targetCurrency);
            return rate;

        } catch (RestClientException e) {
            log.error("Failed to fetch exchange rate from {}: {}", PROVIDER_NAME, e.getMessage());
            throw new ExternalApiException(PROVIDER_NAME, e);
        }
    }

    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
