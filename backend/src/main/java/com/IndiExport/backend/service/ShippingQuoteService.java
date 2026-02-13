package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.ShippingQuoteDto;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.InvalidDestinationException;
import com.IndiExport.backend.exception.ShippingQuoteFailedException;
import com.IndiExport.backend.repository.ProductRepository;
import com.IndiExport.backend.repository.SellerShippingTemplateRepository;
import com.IndiExport.backend.repository.ShippingQuoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Orchestrates shipping quote calculation:
 *   1. Try seller-specific template match first (country → zone → fallback)
 *   2. If no template matches, use the global calculator
 *
 * Supports both:
 *   - Public quote API (no order required)
 *   - Checkout flow (saves ShippingQuote to order)
 */
@Service
public class ShippingQuoteService {

    private static final Logger log = LoggerFactory.getLogger(ShippingQuoteService.class);

    private final SellerShippingTemplateRepository templateRepository;
    private final ShippingQuoteRepository shippingQuoteRepository;
    private final ProductRepository productRepository;
    private final GlobalShippingCalculatorService globalCalculator;

    public ShippingQuoteService(SellerShippingTemplateRepository templateRepository,
                                  ShippingQuoteRepository shippingQuoteRepository,
                                  ProductRepository productRepository,
                                  GlobalShippingCalculatorService globalCalculator) {
        this.templateRepository = templateRepository;
        this.shippingQuoteRepository = shippingQuoteRepository;
        this.productRepository = productRepository;
        this.globalCalculator = globalCalculator;
    }

    // Valid ISO-2 country codes (subset check)
    private static final Set<String> VALID_ISO2 = Set.of(Locale.getISOCountries());

    // ─────────────────────────────────────────────
    // Public Quote API (no order required)
    // ─────────────────────────────────────────────

    /**
     * Calculate a shipping quote for the public API.
     * Does NOT persist anything — this is a pre-checkout estimate.
     */
    @Transactional(readOnly = true)
    public ShippingQuoteDto.QuoteResponse calculateQuote(ShippingQuoteDto.QuoteRequest request) {
        String country = validateCountryCode(request.getDestinationCountryCode());
        ShippingMode mode = request.getShippingMode();

        // 1. Aggregate weight & volumetric weight from product list
        long totalWeightGrams = 0;
        long totalVolumetricGrams = 0;
        UUID effectiveSellerId = request.getSellerId();

        for (ShippingQuoteDto.QuoteItem item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ShippingQuoteFailedException(
                            "Product not found: " + item.getProductId()));

            int qty = Math.max(1, item.getQuantity());
            totalWeightGrams += (long) qty * product.getWeightGrams();

            // Volumetric weight: (L × W × H) / 5000 per unit (in cm → grams)
            // Dimensions are in mm → convert to cm then apply formula
            long volumetricPerUnit = calculateVolumetricWeightGrams(
                    product.getLengthMm(), product.getWidthMm(), product.getHeightMm());
            totalVolumetricGrams += (long) qty * volumetricPerUnit;

            // If no explicit sellerId, use the first product's seller
            if (effectiveSellerId == null) {
                effectiveSellerId = product.getSeller().getId();
            }
        }

        // Chargeable weight = max(actual, volumetric)
        long chargeableWeight = Math.max(totalWeightGrams, totalVolumetricGrams);
        if (chargeableWeight <= 0) {
            throw new ShippingQuoteFailedException("Chargeable weight must be positive");
        }

        // 2. Try seller template first
        if (effectiveSellerId != null) {
            String zone = globalCalculator.resolveZone(country);
            List<SellerShippingTemplate> templates = templateRepository.findMatchingTemplates(
                    effectiveSellerId, country, zone, mode, chargeableWeight);

            if (!templates.isEmpty()) {
                // COUNTRY match takes priority over ZONE (query orders by destinationType ASC)
                SellerShippingTemplate tmpl = templates.get(0);
                return buildFromTemplate(tmpl, totalWeightGrams, chargeableWeight, country);
            }
        }

        // 3. Fallback to global calculator
        GlobalShippingCalculatorService.QuoteResult result =
                globalCalculator.calculate(mode, country, chargeableWeight);

        ShippingQuoteDto.QuoteResponse response = new ShippingQuoteDto.QuoteResponse();
        response.setShippingCostPaise(result.shippingCostPaise());
        response.setTotalWeightGrams(totalWeightGrams);
        response.setChargeableWeightGrams(chargeableWeight);
        response.setEstimatedDeliveryDaysMin(result.deliveryDaysMin());
        response.setEstimatedDeliveryDaysMax(result.deliveryDaysMax());
        response.setQuoteSource("GLOBAL_CALCULATOR");
        
        ShippingQuoteDto.QuoteBreakdown breakdown = new ShippingQuoteDto.QuoteBreakdown();
        breakdown.setBaseCostPaise(result.baseCostPaise());
        breakdown.setWeightChargePaise(result.weightChargePaise());
        breakdown.setChargeableWeightKg(result.chargeableWeightKg());
        breakdown.setShippingZone(result.zone());
        breakdown.setZoneMultiplier(result.zoneMultiplier());
        response.setBreakdown(breakdown);
        
        response.setQuotedAt(Instant.now());
        return response;
    }

    // ─────────────────────────────────────────────
    // Checkout Flow (saves ShippingQuote to order)
    // ─────────────────────────────────────────────

    /**
     * Calculate and persist a shipping quote for an order.
     * Called by CheckoutService.
     */
    @Transactional
    public ShippingQuote calculateAndSave(Order order, ShippingMode mode,
                                           String destinationCountry, long totalWeightGrams) {
        if (totalWeightGrams <= 0) {
            throw new ShippingQuoteFailedException("Total weight must be positive");
        }

        long chargeableWeight = totalWeightGrams; // in checkout, volumetric is not recalculated

        // Try seller template
        UUID sellerId = order.getSeller().getId();
        String country = destinationCountry.toUpperCase();
        String zone = globalCalculator.resolveZone(country);

        List<SellerShippingTemplate> templates = templateRepository.findMatchingTemplates(
                sellerId, country, zone, mode, chargeableWeight);

        long shippingCostPaise;
        int daysMin, daysMax;
        String quoteSource;

        if (!templates.isEmpty()) {
            SellerShippingTemplate tmpl = templates.get(0);
            long weightKg = Math.max(1, (chargeableWeight + 999) / 1000);
            shippingCostPaise = tmpl.getBaseCostPaise() + (tmpl.getCostPerKgPaise() * weightKg);
            daysMin = tmpl.getEstimatedDeliveryDaysMin();
            daysMax = tmpl.getEstimatedDeliveryDaysMax();
            quoteSource = "SELLER_TEMPLATE";
            log.info("Seller template {} matched for order {}", tmpl.getId(), order.getId());
        } else {
            GlobalShippingCalculatorService.QuoteResult result =
                    globalCalculator.calculate(mode, country, chargeableWeight);
            shippingCostPaise = result.shippingCostPaise();
            daysMin = result.deliveryDaysMin();
            daysMax = result.deliveryDaysMax();
            quoteSource = "GLOBAL_CALCULATOR";
            log.info("Global calculator used for order {}", order.getId());
        }

        ShippingQuote quote = new ShippingQuote();
        quote.setOrder(order);
        quote.setMode(mode);
        quote.setDestinationCountry(country);
        quote.setTotalWeightGrams(totalWeightGrams);
        quote.setChargeableWeightGrams(chargeableWeight);
        quote.setShippingCostPaise(shippingCostPaise);
        quote.setEstimatedDeliveryDaysMin(daysMin);
        quote.setEstimatedDeliveryDaysMax(daysMax);
        quote.setQuoteSource(quoteSource);

        ShippingQuote saved = shippingQuoteRepository.save(quote);
        log.info("Shipping quote saved for order {}: {} paise [{}]",
                order.getId(), shippingCostPaise, quoteSource);
        return saved;
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private ShippingQuoteDto.QuoteResponse buildFromTemplate(
            SellerShippingTemplate tmpl, long totalWeightGrams,
            long chargeableWeight, String country) {

        long weightKg = Math.max(1, (chargeableWeight + 999) / 1000);
        long baseCost = tmpl.getBaseCostPaise();
        long weightCharge = tmpl.getCostPerKgPaise() * weightKg;
        long totalCost = baseCost + weightCharge;

        ShippingQuoteDto.QuoteResponse response = new ShippingQuoteDto.QuoteResponse();
        response.setShippingCostPaise(totalCost);
        response.setTotalWeightGrams(totalWeightGrams);
        response.setChargeableWeightGrams(chargeableWeight);
        response.setEstimatedDeliveryDaysMin(tmpl.getEstimatedDeliveryDaysMin());
        response.setEstimatedDeliveryDaysMax(tmpl.getEstimatedDeliveryDaysMax());
        response.setQuoteSource("SELLER_TEMPLATE");
        
        ShippingQuoteDto.QuoteBreakdown breakdown = new ShippingQuoteDto.QuoteBreakdown();
        breakdown.setBaseCostPaise(baseCost);
        breakdown.setWeightChargePaise(weightCharge);
        breakdown.setChargeableWeightKg(weightKg);
        breakdown.setShippingZone(tmpl.getDestinationValue());
        breakdown.setZoneMultiplier(1.0);
        response.setBreakdown(breakdown);
        
        response.setQuotedAt(Instant.now());
        return response;
    }

    /**
     * Volumetric weight: (L_cm × W_cm × H_cm) / 5000 → converted to grams.
     * Dimensions come in mm. Formula: (L*W*H) / 5_000_000_000 → kg → * 1000 → grams.
     * Simplified: (L_mm * W_mm * H_mm) / 5_000_000 grams.
     */
    static long calculateVolumetricWeightGrams(int lengthMm, int widthMm, int heightMm) {
        if (lengthMm <= 0 || widthMm <= 0 || heightMm <= 0) return 0;
        long volumeMm3 = (long) lengthMm * widthMm * heightMm;
        return Math.max(1, volumeMm3 / 5_000_000L);
    }

    private String validateCountryCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidDestinationException("");
        }
        String upper = code.trim().toUpperCase();
        if (upper.length() != 2 || !VALID_ISO2.contains(upper)) {
            throw new InvalidDestinationException(code);
        }
        return upper;
    }
}
