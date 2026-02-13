package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.ShippingTemplateDto;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.entity.SellerShippingTemplate;
import com.IndiExport.backend.exception.InvalidDestinationException;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.exception.ShippingTemplateNotFoundException;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.SellerShippingTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CRUD service for seller shipping templates.
 */
@Service
public class SellerShippingTemplateService {

    private static final Logger log = LoggerFactory.getLogger(SellerShippingTemplateService.class);

    private final SellerShippingTemplateRepository templateRepository;
    private final SellerProfileRepository sellerProfileRepository;

    public SellerShippingTemplateService(SellerShippingTemplateRepository templateRepository,
                                           SellerProfileRepository sellerProfileRepository) {
        this.templateRepository = templateRepository;
        this.sellerProfileRepository = sellerProfileRepository;
    }

    private static final Set<String> VALID_ISO2 = Set.of(Locale.getISOCountries());
    private static final Set<String> VALID_ZONES = Set.of(
            "DOMESTIC", "ASIA", "EUROPE", "AMERICAS", "AFRICA", "OCEANIA"
    );

    @Transactional
    public ShippingTemplateDto.Response createTemplate(UUID userId, ShippingTemplateDto.CreateRequest req) {
        SellerProfile seller = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        validateTemplate(req.getDestinationType(), req.getDestinationValue(),
                req.getMinWeightGrams(), req.getMaxWeightGrams(),
                req.getEstimatedDeliveryDaysMin(), req.getEstimatedDeliveryDaysMax());

        SellerShippingTemplate template = new SellerShippingTemplate();
        template.setSeller(seller);
        template.setDestinationType(req.getDestinationType());
        template.setDestinationValue(req.getDestinationValue().toUpperCase());
        template.setShippingMode(req.getShippingMode());
        template.setMinWeightGrams(req.getMinWeightGrams());
        template.setMaxWeightGrams(req.getMaxWeightGrams());
        template.setBaseCostPaise(req.getBaseCostPaise());
        template.setCostPerKgPaise(req.getCostPerKgPaise());
        template.setEstimatedDeliveryDaysMin(req.getEstimatedDeliveryDaysMin());
        template.setEstimatedDeliveryDaysMax(req.getEstimatedDeliveryDaysMax());
        template.setActive(true);

        SellerShippingTemplate saved = templateRepository.save(template);
        log.info("Shipping template {} created for seller {}", saved.getId(), seller.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public ShippingTemplateDto.Response updateTemplate(UUID userId, UUID templateId,
                                                        ShippingTemplateDto.UpdateRequest req) {
        SellerProfile seller = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        SellerShippingTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ShippingTemplateNotFoundException(templateId.toString()));

        // Ownership guard: seller can only modify their own templates
        if (!template.getSeller().getId().equals(seller.getId())) {
            throw new ShippingTemplateNotFoundException(templateId.toString());
        }

        // Apply partial updates
        if (req.getDestinationType() != null) template.setDestinationType(req.getDestinationType());
        if (req.getDestinationValue() != null) template.setDestinationValue(req.getDestinationValue().toUpperCase());
        if (req.getShippingMode() != null) template.setShippingMode(req.getShippingMode());
        if (req.getMinWeightGrams() != null) template.setMinWeightGrams(req.getMinWeightGrams());
        if (req.getMaxWeightGrams() != null) template.setMaxWeightGrams(req.getMaxWeightGrams());
        if (req.getBaseCostPaise() != null) template.setBaseCostPaise(req.getBaseCostPaise());
        if (req.getCostPerKgPaise() != null) template.setCostPerKgPaise(req.getCostPerKgPaise());
        if (req.getEstimatedDeliveryDaysMin() != null) template.setEstimatedDeliveryDaysMin(req.getEstimatedDeliveryDaysMin());
        if (req.getEstimatedDeliveryDaysMax() != null) template.setEstimatedDeliveryDaysMax(req.getEstimatedDeliveryDaysMax());
        if (req.getActive() != null) template.setActive(req.getActive());

        // Re-validate after updates
        validateTemplate(template.getDestinationType(), template.getDestinationValue(),
                template.getMinWeightGrams(), template.getMaxWeightGrams(),
                template.getEstimatedDeliveryDaysMin(), template.getEstimatedDeliveryDaysMax());

        SellerShippingTemplate saved = templateRepository.save(template);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ShippingTemplateDto.Response> getTemplates(UUID userId) {
        SellerProfile seller = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        return templateRepository.findBySellerId(seller.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTemplate(UUID userId, UUID templateId) {
        SellerProfile seller = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        SellerShippingTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ShippingTemplateNotFoundException(templateId.toString()));

        if (!template.getSeller().getId().equals(seller.getId())) {
            throw new ShippingTemplateNotFoundException(templateId.toString());
        }

        templateRepository.delete(template);
        log.info("Shipping template {} deleted by seller {}", templateId, seller.getId());
    }

    // ─────────────────────────────────────────────
    // Validation
    // ─────────────────────────────────────────────

    private void validateTemplate(SellerShippingTemplate.DestinationType type, String value,
                                   Long minWeight, Long maxWeight, int daysMin, int daysMax) {
        if (type == SellerShippingTemplate.DestinationType.COUNTRY) {
            String upper = value.toUpperCase();
            if (upper.length() != 2 || !VALID_ISO2.contains(upper)) {
                throw new InvalidDestinationException(value);
            }
        } else if (type == SellerShippingTemplate.DestinationType.ZONE) {
            if (!VALID_ZONES.contains(value.toUpperCase())) {
                throw new InvalidDestinationException(
                        "Invalid zone: " + value + ". Valid: " + VALID_ZONES);
            }
        }

        if (minWeight != null && maxWeight != null && minWeight > maxWeight) {
            throw new IllegalArgumentException(
                    "minWeightGrams (" + minWeight + ") must be <= maxWeightGrams (" + maxWeight + ")");
        }

        if (daysMin > daysMax) {
            throw new IllegalArgumentException(
                    "estimatedDeliveryDaysMin (" + daysMin + ") must be <= estimatedDeliveryDaysMax (" + daysMax + ")");
        }
    }

    private ShippingTemplateDto.Response mapToResponse(SellerShippingTemplate t) {
        ShippingTemplateDto.Response response = new ShippingTemplateDto.Response();
        response.setId(t.getId());
        response.setSellerId(t.getSeller().getId());
        response.setDestinationType(t.getDestinationType());
        response.setDestinationValue(t.getDestinationValue());
        response.setShippingMode(t.getShippingMode());
        response.setMinWeightGrams(t.getMinWeightGrams());
        response.setMaxWeightGrams(t.getMaxWeightGrams());
        response.setBaseCostPaise(t.getBaseCostPaise());
        response.setCostPerKgPaise(t.getCostPerKgPaise());
        response.setEstimatedDeliveryDaysMin(t.getEstimatedDeliveryDaysMin());
        response.setEstimatedDeliveryDaysMax(t.getEstimatedDeliveryDaysMax());
        response.setActive(t.isActive());
        response.setCreatedAt(t.getCreatedAt());
        response.setUpdatedAt(t.getUpdatedAt());
        return response;
    }
}
