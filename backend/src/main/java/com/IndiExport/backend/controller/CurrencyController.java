package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.CurrencyDto;
import com.IndiExport.backend.exception.InvalidMoneyAmountException;
import com.IndiExport.backend.service.currency.CurrencyConversionService;
import com.IndiExport.backend.service.currency.CurrencyMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public currency conversion endpoint.
 *
 * GET /api/v1/currency/convert?amount=19999&from=INR&to=USD
 */
@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyConversionService conversionService;

    @GetMapping("/convert")
    public ResponseEntity<CurrencyDto.CurrencyConvertResponse> convert(
            @RequestParam long amount,
            @RequestParam String from,
            @RequestParam String to) {

        // Validate 'from' is INR (sellers only price in INR)
        String fromNormalized = CurrencyMetadata.validateAndNormalize(from);
        if (!"INR".equals(fromNormalized)) {
            throw new InvalidMoneyAmountException(
                    "Only conversions from INR are supported. Received: " + from);
        }

        CurrencyConversionService.ConversionResult result =
                conversionService.convertFromINR(amount, to);

        CurrencyDto.CurrencyConvertResponse response = CurrencyDto.CurrencyConvertResponse.builder()
                .baseAmountMinor(result.baseAmountMinor())
                .baseCurrency(result.baseCurrency())
                .convertedAmountMinor(result.convertedAmountMinor())
                .targetCurrency(result.targetCurrency())
                .exchangeRateMicros(result.exchangeRateMicros())
                .rateTimestamp(result.rateTimestamp())
                .providerName(result.providerName())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/currency/supported
     * Returns list of supported currency codes.
     */
    @GetMapping("/supported")
    public ResponseEntity<?> supportedCurrencies() {
        return ResponseEntity.ok(java.util.Map.of(
                "baseCurrency", "INR",
                "supportedTargetCurrencies", java.util.List.of(
                        "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK",
                        "EUR", "GBP", "HKD", "HUF", "IDR", "ILS", "ISK", "JPY",
                        "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RON",
                        "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
                )
        ));
    }
}
