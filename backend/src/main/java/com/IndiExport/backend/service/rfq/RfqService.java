package com.IndiExport.backend.service.rfq;

import com.IndiExport.backend.dto.*;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RfqService {

    private static final Logger log = LoggerFactory.getLogger(RfqService.class);

    private final RfqRepository rfqRepository;
    private final RfqMediaRepository rfqMediaRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final RfqQuoteRepository rfqQuoteRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final com.IndiExport.backend.service.currency.CurrencyService currencyService;

    public RfqService(RfqRepository rfqRepository,
                      RfqMediaRepository rfqMediaRepository,
                      BuyerProfileRepository buyerProfileRepository,
                      RfqQuoteRepository rfqQuoteRepository,
                      ProductRepository productRepository,
                      SellerProfileRepository sellerProfileRepository,
                      com.IndiExport.backend.service.currency.CurrencyService currencyService) {
        this.rfqRepository = rfqRepository;
        this.rfqMediaRepository = rfqMediaRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.rfqQuoteRepository = rfqQuoteRepository;
        this.productRepository = productRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.currencyService = currencyService;
    }

    @Transactional
    public BuyerRfqResponse createRfq(UUID buyerId, BuyerRfqCreateRequest request) {
        BuyerProfile buyer = buyerProfileRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer profile not found"));

        RFQ rfq = new RFQ();
        rfq.setBuyer(buyer);
        rfq.setTitle(request.getTitle());
        rfq.setDetails(request.getDetails());
        rfq.setQuantity(request.getQuantity());
        rfq.setUnit(request.getUnit());
        rfq.setDestinationCountry(request.getDestinationCountry());
        rfq.setDestinationAddressJson(request.getDestinationAddressJson());
        rfq.setShippingMode(request.getShippingMode());
        rfq.setIncoterm(request.getIncoterm());
        rfq.setTargetPriceMinor(request.getTargetPriceMinor());
        rfq.setTargetCurrency(request.getTargetCurrency());
        rfq.setStatus(RfqStatus.OPEN);
        // Category linkage if needed
        // if (request.getCategoryId() != null) ...

        rfq = rfqRepository.save(rfq);

        if (request.getMediaUrls() != null && !request.getMediaUrls().isEmpty()) {
            List<RfqMedia> mediaList = new ArrayList<>();
            for (String url : request.getMediaUrls()) {
                RfqMedia m = new RfqMedia();
                m.setRfq(rfq);
                m.setUrl(url);
                mediaList.add(m);
            }
            rfqMediaRepository.saveAll(mediaList);
            rfq.setMedia(mediaList);
        }

        return mapToBuyerResponse(rfq);
    }

    public BuyerRfqResponse getRfqForBuyer(UUID buyerId, UUID rfqId) {
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RfqNotFoundException("RFQ not found"));

        if (!rfq.getBuyer().getId().equals(buyerId)) {
            throw new RfqAccessDeniedException("You are not authorized to view this RFQ");
        }

        return mapToBuyerResponse(rfq);
    }

    public BuyerRfqResponse getRfqForSeller(UUID rfqId) {
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RfqNotFoundException("RFQ not found"));

        // Sellers can only view RFQs that are open or under negotiation
        if (rfq.getStatus() != RfqStatus.OPEN && rfq.getStatus() != RfqStatus.UNDER_NEGOTIATION) {
            throw new RfqAccessDeniedException("This RFQ is no longer accepting quotes");
        }

        return mapToBuyerResponse(rfq);
    }

    public Page<BuyerRfqResponse> getBuyerRfqs(UUID buyerId, Pageable pageable) {
        return rfqRepository.findByBuyerId(buyerId, pageable)
                .map(this::mapToBuyerResponse);
    }

    // Specification for Seller Filtering
    public Page<SellerRfqListResponse> searchRfqs(
            String keyword,
            String categoryId,
            String destinationCountry,
            Integer minQty,
            Integer maxQty,
            ShippingMode shippingMode,
            Incoterm incoterm,
            Pageable pageable) {

        Specification<RFQ> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Status must be OPEN or UNDER_NEGOTIATION
            predicates.add(root.get("status").in(RfqStatus.OPEN, RfqStatus.UNDER_NEGOTIATION));

            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likePattern),
                        cb.like(cb.lower(root.get("details")), likePattern)));
            }
            if (StringUtils.hasText(destinationCountry)) {
                predicates.add(cb.equal(root.get("destinationCountry"), destinationCountry));
            }
            if (minQty != null) {
                predicates.add(cb.ge(root.get("quantity"), minQty));
            }
            if (maxQty != null) {
                predicates.add(cb.le(root.get("quantity"), maxQty));
            }
            if (shippingMode != null) {
                predicates.add(cb.equal(root.get("shippingMode"), shippingMode));
            }
            if (incoterm != null) {
                predicates.add(cb.equal(root.get("incoterm"), incoterm));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), UUID.fromString(categoryId)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return rfqRepository.findAll(spec, pageable).map(this::mapToSellerListResponse);
    }

    public Page<SellerRfqListResponse> getRecommendedRfqs(UUID userId, Pageable pageable) {
        SellerProfile seller = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        List<Product> products = productRepository.findAllActiveBySeller(seller.getId());

        if (products.isEmpty()) {
            return Page.empty(pageable);
        }

        Set<String> keywords = new HashSet<>();
        for (Product product : products) {
            keywords.addAll(com.IndiExport.backend.util.KeywordUtil.extractKeywords(
                    product.getName(),
                    product.getDescription()
            ));
            if (product.getTags() != null) {
                keywords.addAll(product.getTags().stream()
                        .map(Tag::getName)
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet()));
            }
        }

        if (keywords.isEmpty()) {
            return Page.empty(pageable);
        }

        Specification<RFQ> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Status must be OPEN or UNDER_NEGOTIATION
            predicates.add(root.get("status").in(RfqStatus.OPEN, RfqStatus.UNDER_NEGOTIATION));

            List<Predicate> keywordPredicates = new ArrayList<>();
            for (String keyword : keywords) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                keywordPredicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likePattern),
                        cb.like(cb.lower(root.get("details")), likePattern)
                ));
            }

            if (!keywordPredicates.isEmpty()) {
                predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return rfqRepository.findAll(spec, pageable).map(this::mapToSellerListResponse);
    }

    @Transactional
    public void cancelRfq(UUID buyerId, UUID rfqId) {
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RfqNotFoundException("RFQ not found"));

        if (!rfq.getBuyer().getId().equals(buyerId)) {
            throw new RfqAccessDeniedException("Not authorized to cancel this RFQ");
        }

        if (rfq.getStatus() == RfqStatus.FINALIZED || rfq.getStatus() == RfqStatus.CONVERTED_TO_ORDER) {
            throw new InvalidRfqStateException("Cannot cancel finalized RFQ");
        }

        rfq.setStatus(RfqStatus.CANCELLED);
        rfqRepository.save(rfq);
    }

    private BuyerRfqResponse mapToBuyerResponse(RFQ rfq) {
        BuyerRfqResponse response = new BuyerRfqResponse();
        response.setId(rfq.getId());
        response.setTitle(rfq.getTitle());
        response.setDetails(rfq.getDetails());
        response.setQuantity(rfq.getQuantity());
        response.setUnit(rfq.getUnit());
        response.setDestinationCountry(rfq.getDestinationCountry());
        response.setShippingMode(rfq.getShippingMode());
        response.setIncoterm(rfq.getIncoterm());
        response.setTargetPriceMinor(rfq.getTargetPriceMinor());
        response.setTargetCurrency(rfq.getTargetCurrency());
        if (rfq.getTargetPriceMinor() != null && rfq.getTargetCurrency() != null) {
            try {
                response.setTargetPriceINRPaise(
                        currencyService.convertToINR(rfq.getTargetPriceMinor(), rfq.getTargetCurrency()));
            } catch (Exception e) {
                log.warn("Failed to convert RFQ target price to INR: {}", e.getMessage());
            }
        }
        response.setStatus(rfq.getStatus());
        response.setCreatedAt(rfq.getCreatedAt());

        if (rfq.getMedia() != null) {
            response.setMedia(rfq.getMedia().stream().map(m -> {
                RfqMediaResponse mr = new RfqMediaResponse();
                mr.setId(m.getId());
                mr.setUrl(m.getUrl());
                mr.setMediaType(m.getMediaType());
                return mr;
            }).collect(Collectors.toList()));
        }

        response.setQuoteCount(rfq.getQuotes() != null ? rfq.getQuotes().size() : 0);

        if (rfq.getQuotes() != null) {
            response.setQuotes(rfq.getQuotes().stream().map(q -> {
                SellerQuoteResponse sqr = new SellerQuoteResponse();
                sqr.setId(q.getId());
                sqr.setSellerId(q.getSeller().getId());
                sqr.setSellerName(q.getSeller().getCompanyName());
                sqr.setVerifiedSeller(q.getSeller().isVerified());
                sqr.setQuotedPriceInrPaise(q.getQuotedPriceInrPaise());
                sqr.setShippingEstimateInrPaise(q.getShippingEstimateInrPaise());
                sqr.setLeadTimeDays(q.getLeadTimeDays());
                sqr.setValidityUntil(q.getValidityUntil());
                sqr.setStatus(q.getStatus());
                sqr.setCreatedAt(q.getCreatedAt());

                // Convert seller's INR quote to buyer's target currency
                if (rfq.getTargetCurrency() != null) {
                    try {
                        sqr.setTargetCurrency(rfq.getTargetCurrency());
                        sqr.setConvertedPriceMinor(
                                currencyService.convertFromINR(q.getQuotedPriceInrPaise(), rfq.getTargetCurrency())
                                        .getConvertedPriceMinor());
                    } catch (Exception e) {
                        log.warn("Failed to convert seller quote to buyer currency: {}", e.getMessage());
                    }
                }

                return sqr;
            }).collect(Collectors.toList()));
        }

        return response;
    }

    private SellerRfqListResponse mapToSellerListResponse(RFQ rfq) {
        SellerRfqListResponse response = new SellerRfqListResponse();
        response.setId(rfq.getId());
        response.setTitle(rfq.getTitle());
        response.setQuantity(rfq.getQuantity());
        response.setUnit(rfq.getUnit());
        response.setDestinationCountry(rfq.getDestinationCountry());
        response.setShippingMode(rfq.getShippingMode());
        response.setIncoterm(rfq.getIncoterm());
        response.setStatus(rfq.getStatus());

        // Include original target info for seller visibility
        response.setTargetPriceMinor(rfq.getTargetPriceMinor());
        response.setTargetCurrency(rfq.getTargetCurrency());

        if (rfq.getTargetPriceMinor() != null && rfq.getTargetCurrency() != null) {
            try {
                response.setTargetPriceINRPaise(
                        currencyService.convertToINR(rfq.getTargetPriceMinor(), rfq.getTargetCurrency()));
            } catch (Exception e) {
                log.warn("Failed to convert RFQ target price to INR (seller list): {}", e.getMessage());
            }
        }
        response.setCreatedAt(rfq.getCreatedAt());
        response.setCategoryName(rfq.getCategory() != null ? rfq.getCategory().getName() : null);
        response.setQuoteCount(rfq.getQuotes() != null ? rfq.getQuotes().size() : 0);
        return response;
    }
}
