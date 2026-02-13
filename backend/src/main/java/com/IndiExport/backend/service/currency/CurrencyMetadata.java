package com.IndiExport.backend.service.currency;

import com.IndiExport.backend.exception.CurrencyNotSupportedException;

import java.util.Map;
import java.util.Set;

/**
 * ISO 4217 minor-unit metadata for supported currencies.
 *
 * Most currencies have 2 decimal places (100 minor units = 1 major unit),
 * but notable exceptions exist:
 *   - JPY, KRW, VND: 0 decimals (1 minor unit = 1 major unit)
 *   - KWD, BHD, OMR: 3 decimals (1000 minor units = 1 major unit)
 *
 * This registry is the SINGLE SOURCE OF TRUTH for conversion math.
 * Every conversion operation MUST use getMinorUnitMultiplier() to avoid
 * silently producing wrong amounts for zero-decimal currencies.
 */
public final class CurrencyMetadata {

    private CurrencyMetadata() {} // utility class

    /**
     * Map of currency code → number of decimal places (exponent).
     * Only currencies with non-standard exponents need explicit entries;
     * all others default to 2.
     */
    private static final Map<String, Integer> EXPONENT_OVERRIDES = Map.ofEntries(
            // Zero-decimal currencies
            Map.entry("BIF", 0), Map.entry("CLP", 0), Map.entry("DJF", 0),
            Map.entry("GNF", 0), Map.entry("ISK", 0), Map.entry("JPY", 0),
            Map.entry("KMF", 0), Map.entry("KRW", 0), Map.entry("PYG", 0),
            Map.entry("RWF", 0), Map.entry("UGX", 0), Map.entry("VND", 0),
            Map.entry("VUV", 0), Map.entry("XAF", 0), Map.entry("XOF", 0),
            Map.entry("XPF", 0),
            // Three-decimal currencies
            Map.entry("BHD", 3), Map.entry("IQD", 3), Map.entry("JOD", 3),
            Map.entry("KWD", 3), Map.entry("LYD", 3), Map.entry("OMR", 3),
            Map.entry("TND", 3)
    );

    /**
     * Currencies supported by the Frankfurter API (ECB reference rates).
     * INR is the base currency, so it's not in this set.
     */
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of(
            "AUD", "BGN", "BRL", "CAD", "CHF", "CNY", "CZK", "DKK",
            "EUR", "GBP", "HKD", "HUF", "IDR", "ILS", "ISK", "JPY",
            "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PLN", "RON",
            "SEK", "SGD", "THB", "TRY", "USD", "ZAR"
    );

    /**
     * Returns the minor-unit exponent for a currency.
     * USD → 2, JPY → 0, KWD → 3
     */
    public static int getExponent(String currencyCode) {
        return EXPONENT_OVERRIDES.getOrDefault(currencyCode.toUpperCase(), 2);
    }

    /**
     * Returns the multiplier to convert 1 major unit to minor units.
     * USD → 100, JPY → 1, KWD → 1000
     */
    public static long getMinorUnitMultiplier(String currencyCode) {
        int exponent = getExponent(currencyCode);
        long multiplier = 1;
        for (int i = 0; i < exponent; i++) {
            multiplier *= 10;
        }
        return multiplier;
    }

    /**
     * Check if a currency is supported for conversion from INR.
     */
    public static boolean isSupported(String currencyCode) {
        if (currencyCode == null) return false;
        String upper = currencyCode.toUpperCase();
        return "INR".equals(upper) || SUPPORTED_CURRENCIES.contains(upper);
    }

    /**
     * Validate and normalize a currency code. Throws if unsupported.
     */
    public static String validateAndNormalize(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new CurrencyNotSupportedException("(empty)");
        }
        String upper = currencyCode.trim().toUpperCase();
        if (!isSupported(upper)) {
            throw new CurrencyNotSupportedException(upper);
        }
        return upper;
    }
}
