package com.IndiExport.backend.dto;

import java.time.Instant;

/**
 * DTOs for currency conversion API and embedded price info.
 */
public class CurrencyDto {

    /**
     * Response for GET /api/v1/currency/convert
     */
    public static class CurrencyConvertResponse {
        private long baseAmountMinor;
        private String baseCurrency;
        private long convertedAmountMinor;
        private String targetCurrency;
        private long exchangeRateMicros;
        private Instant rateTimestamp;
        private String providerName;

        public CurrencyConvertResponse() {}

        public static CurrencyConvertResponseBuilder builder() {
            return new CurrencyConvertResponseBuilder();
        }

        public static class CurrencyConvertResponseBuilder {
            private CurrencyConvertResponse response = new CurrencyConvertResponse();
            public CurrencyConvertResponseBuilder baseAmountMinor(long amount) { response.setBaseAmountMinor(amount); return this; }
            public CurrencyConvertResponseBuilder baseCurrency(String currency) { response.setBaseCurrency(currency); return this; }
            public CurrencyConvertResponseBuilder convertedAmountMinor(long amount) { response.setConvertedAmountMinor(amount); return this; }
            public CurrencyConvertResponseBuilder targetCurrency(String currency) { response.setTargetCurrency(currency); return this; }
            public CurrencyConvertResponseBuilder exchangeRateMicros(long rate) { response.setExchangeRateMicros(rate); return this; }
            public CurrencyConvertResponseBuilder rateTimestamp(Instant at) { response.setRateTimestamp(at); return this; }
            public CurrencyConvertResponseBuilder providerName(String name) { response.setProviderName(name); return this; }
            public CurrencyConvertResponse build() { return response; }
        }

        // Getters and Setters
        public long getBaseAmountMinor() { return baseAmountMinor; }
        public void setBaseAmountMinor(long baseAmountMinor) { this.baseAmountMinor = baseAmountMinor; }
        public String getBaseCurrency() { return baseCurrency; }
        public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
        public long getConvertedAmountMinor() { return convertedAmountMinor; }
        public void setConvertedAmountMinor(long convertedAmountMinor) { this.convertedAmountMinor = convertedAmountMinor; }
        public String getTargetCurrency() { return targetCurrency; }
        public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }
        public long getExchangeRateMicros() { return exchangeRateMicros; }
        public void setExchangeRateMicros(long exchangeRateMicros) { this.exchangeRateMicros = exchangeRateMicros; }
        public Instant getRateTimestamp() { return rateTimestamp; }
        public void setRateTimestamp(Instant rateTimestamp) { this.rateTimestamp = rateTimestamp; }
        public String getProviderName() { return providerName; }
        public void setProviderName(String providerName) { this.providerName = providerName; }
    }

    /**
     * Embedded in product responses when a ?currency= param is provided.
     * Shows the converted price alongside the original INR price.
     */
    public static class ConvertedPriceInfo {
        private long convertedPriceMinor;
        private String currency;
        private long exchangeRateMicros;
        private Instant rateTimestamp;

        public ConvertedPriceInfo() {}

        public static ConvertedPriceInfoBuilder builder() {
            return new ConvertedPriceInfoBuilder();
        }

        public static class ConvertedPriceInfoBuilder {
            private ConvertedPriceInfo info = new ConvertedPriceInfo();
            public ConvertedPriceInfoBuilder convertedPriceMinor(long price) { info.setConvertedPriceMinor(price); return this; }
            public ConvertedPriceInfoBuilder currency(String currency) { info.setCurrency(currency); return this; }
            public ConvertedPriceInfoBuilder exchangeRateMicros(long rate) { info.setExchangeRateMicros(rate); return this; }
            public ConvertedPriceInfoBuilder rateTimestamp(Instant at) { info.setRateTimestamp(at); return this; }
            public ConvertedPriceInfo build() { return info; }
        }

        // Getters and Setters
        public long getConvertedPriceMinor() { return convertedPriceMinor; }
        public void setConvertedPriceMinor(long convertedPriceMinor) { this.convertedPriceMinor = convertedPriceMinor; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public long getExchangeRateMicros() { return exchangeRateMicros; }
        public void setExchangeRateMicros(long exchangeRateMicros) { this.exchangeRateMicros = exchangeRateMicros; }
        public Instant getRateTimestamp() { return rateTimestamp; }
        public void setRateTimestamp(Instant rateTimestamp) { this.rateTimestamp = rateTimestamp; }
    }
}
