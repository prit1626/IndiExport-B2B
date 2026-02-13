package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.ShippingQuoteDto;
import com.IndiExport.backend.entity.ShippingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * Global zone-based shipping calculator — the fallback when no seller template matches.
 *
 * Formula: (baseCostPerKg * zoneMultiplier * ceil(chargeableWeightKg)) 
 *
 * Zone system:
 *   DOMESTIC (IN)         : 0.5x
 *   ASIA    (Zone A)      : 1.0x
 *   EUROPE  (Zone B)      : 1.5x
 *   AMERICAS/AFRICA (C)   : 2.0x
 */
@Service
public class GlobalShippingCalculatorService {

    private static final Logger log = LoggerFactory.getLogger(GlobalShippingCalculatorService.class);

    // Base rate per kg in INR paise by shipping mode
    private static final Map<ShippingMode, Long> BASE_RATE_PAISE = Map.of(
            ShippingMode.AIR,      85_00L,   // ₹85/kg
            ShippingMode.SEA,      25_00L,   // ₹25/kg
            ShippingMode.ROAD,     15_00L,   // ₹15/kg
            ShippingMode.COURIER, 120_00L    // ₹120/kg
    );

    // Estimated delivery days [min, max] by mode
    private static final Map<ShippingMode, int[]> DELIVERY_DAYS = Map.of(
            ShippingMode.AIR,     new int[]{3, 7},
            ShippingMode.SEA,     new int[]{20, 45},
            ShippingMode.ROAD,    new int[]{7, 15},
            ShippingMode.COURIER, new int[]{5, 10}
    );

    // Zone A: Asia / Oceania
    private static final Set<String> ZONE_A = Set.of(
            "BD", "BT", "CN", "HK", "ID", "JP", "KH", "KR", "LA", "LK", "MM",
            "MN", "MO", "MY", "NP", "PH", "PK", "SG", "TH", "TW", "VN",
            "AU", "NZ", "FJ"
    );
    // Zone B: Europe / Middle East / CIS
    private static final Set<String> ZONE_B = Set.of(
            "AE", "AF", "AM", "AT", "AZ", "BA", "BE", "BG", "BH", "BY",
            "CH", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GB",
            "GE", "GR", "HR", "HU", "IE", "IL", "IQ", "IR", "IS", "IT",
            "JO", "KW", "KZ", "LB", "LT", "LU", "LV", "MD", "ME", "MK",
            "MT", "NL", "NO", "OM", "PL", "PT", "QA", "RO", "RS", "RU",
            "SA", "SE", "SI", "SK", "TR", "UA", "UZ", "YE"
    );
    // Everything else = Zone C (Americas, Africa)

    public GlobalShippingCalculatorService() {}

    /**
     * Resolve the zone name for a country.
     */
    public String resolveZone(String countryCode) {
        if (countryCode == null) return "AMERICAS";
        String upper = countryCode.toUpperCase();
        if ("IN".equals(upper)) return "DOMESTIC";
        if (ZONE_A.contains(upper)) return "ASIA";
        if (ZONE_B.contains(upper)) return "EUROPE";
        return "AMERICAS";
    }

    /**
     * Calculate shipping cost using the global formula.
     */
    public QuoteResult calculate(ShippingMode mode, String destinationCountry,
                                 long chargeableWeightGrams) {
        long baseRate = BASE_RATE_PAISE.getOrDefault(mode, BASE_RATE_PAISE.get(ShippingMode.SEA));
        String zone = resolveZone(destinationCountry);
        double multiplier = getZoneMultiplier(zone);

        // ceil(weight in kg) — minimum 1 kg
        long weightKg = Math.max(1, (chargeableWeightGrams + 999) / 1000);
        long weightCharge = Math.round(baseRate * multiplier * weightKg);

        int[] days = DELIVERY_DAYS.getOrDefault(mode, new int[]{10, 30});

        log.debug("Global calc: {} mode, {} zone ({}x), {} kg → {} paise",
                mode, zone, multiplier, weightKg, weightCharge);

        return new QuoteResult(
                weightCharge,
                0L,           // no base cost in global; it's all per-kg
                weightCharge,
                weightKg,
                zone,
                multiplier,
                days[0],
                days[1]
        );
    }

    private double getZoneMultiplier(String zone) {
        return switch (zone) {
            case "DOMESTIC" -> 0.5;
            case "ASIA"     -> 1.0;
            case "EUROPE"   -> 1.5;
            default         -> 2.0; // AMERICAS / AFRICA
        };
    }

    /**
     * Immutable result from the global calculator.
     */
    public record QuoteResult(
            long shippingCostPaise,
            long baseCostPaise,
            long weightChargePaise,
            long chargeableWeightKg,
            String zone,
            double zoneMultiplier,
            int deliveryDaysMin,
            int deliveryDaysMax
    ) {}
}
