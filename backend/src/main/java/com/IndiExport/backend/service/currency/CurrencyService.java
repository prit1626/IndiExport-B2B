package com.IndiExport.backend.service.currency;

import com.IndiExport.backend.dto.CurrencyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for currency conversion and exchange rate management.
 */
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyConversionService conversionService;

    /**
     * Convert INR paise to target currency minor units.
     */
    public CurrencyDto.ConvertedPriceInfo convertFromINR(long amountPaise, String targetCurrency) {
        CurrencyConversionService.ConversionResult result = conversionService.convertFromINR(amountPaise,
                targetCurrency);

        return CurrencyDto.ConvertedPriceInfo.builder()
                .convertedPriceMinor(result.convertedAmountMinor())
                .currency(result.targetCurrency())
                .exchangeRateMicros(result.exchangeRateMicros())
                .rateTimestamp(result.rateTimestamp())
                .build();
    }

    /**
     * Convert target currency minor units to INR paise.
     */
    public long convertToINR(long amountMinor, String fromCurrency) {
        if (fromCurrency == null || "INR".equalsIgnoreCase(fromCurrency)) {
            return amountMinor;
        }
        return conversionService.convertToINR(amountMinor, fromCurrency).convertedAmountMinor();
    }

    /**
     * Map country code to default currency.
     */
    public String getCurrencyForCountry(String countryCode) {
        if (countryCode == null)
            return "INR";

        return switch (countryCode.toUpperCase()) {
            case "IN" -> "INR";
            case "US" -> "USD";
            case "DE", "FR", "IT", "ES", "NL" -> "EUR";
            case "AE" -> "AED";
            case "GB" -> "GBP";
            case "JP" -> "JPY";
            case "SG" -> "SGD";
            case "AU" -> "AUD";
            case "CA" -> "CAD";
            default -> "INR";
        };
    }
}
