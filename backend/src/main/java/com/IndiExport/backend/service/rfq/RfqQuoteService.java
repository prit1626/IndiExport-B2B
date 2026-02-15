package com.IndiExport.backend.service.rfq;

import com.IndiExport.backend.dto.SellerQuoteCreateRequest;
import com.IndiExport.backend.dto.SellerQuoteResponse;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.RfqQuoteRepository;
import com.IndiExport.backend.repository.RfqRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RfqQuoteService {

    private static final Logger log = LoggerFactory.getLogger(RfqQuoteService.class);

    private final RfqRepository rfqRepository;
    private final RfqQuoteRepository rfqQuoteRepository;
    private final SellerProfileRepository sellerProfileRepository;

    public RfqQuoteService(RfqRepository rfqRepository,
                            RfqQuoteRepository rfqQuoteRepository,
                            SellerProfileRepository sellerProfileRepository) {
        this.rfqRepository = rfqRepository;
        this.rfqQuoteRepository = rfqQuoteRepository;
        this.sellerProfileRepository = sellerProfileRepository;
    }

    @Transactional
    public SellerQuoteResponse submitQuote(UUID sellerId, UUID rfqId, SellerQuoteCreateRequest request) {
        // 1. Validate RFQ
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RfqNotFoundException("RFQ not found"));

        if (rfq.getStatus() != RfqStatus.OPEN && rfq.getStatus() != RfqStatus.UNDER_NEGOTIATION) {
             throw new InvalidRfqStateException("RFQ is not accepting quotes (Status: " + rfq.getStatus() + ")");
        }

        // 2. Validate Seller
        SellerProfile seller = sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        // BUG FIX: Prevent seller from quoting their own RFQ
        if (rfq.getBuyer().getUser().getId().equals(seller.getUser().getId())) {
            throw new BusinessRuleViolationException("Sellers cannot submit quotes for their own RFQs");
        }

        // BUG FIX: Ensure validityUntil is in the future
        if (request.getValidityUntil() != null && request.getValidityUntil().isBefore(java.time.Instant.now())) {
            throw new ValidationException("Quote validity must be a future date");
        }

        // 3. Create Quote
        RfqQuote quote = new RfqQuote();
        quote.setRfq(rfq);
        quote.setSeller(seller);
        quote.setQuotedPriceInrPaise(request.getQuotedPriceInrPaise());
        quote.setShippingEstimateInrPaise(request.getShippingEstimateInrPaise());
        quote.setLeadTimeDays(request.getLeadTimeDays());
        quote.setNotes(request.getNotes());
        quote.setValidityUntil(request.getValidityUntil());
        quote.setStatus(RfqQuoteStatus.ACTIVE);

        quote = rfqQuoteRepository.save(quote);

        // 4. Update RFQ status if it was OPEN
        if (rfq.getStatus() == RfqStatus.OPEN) {
            rfq.setStatus(RfqStatus.UNDER_NEGOTIATION);
            rfqRepository.save(rfq);
        }

        return mapToResponse(quote);
    }

    private SellerQuoteResponse mapToResponse(RfqQuote quote) {
        SellerQuoteResponse response = new SellerQuoteResponse();
        response.setId(quote.getId());
        response.setSellerId(quote.getSeller().getId());
        response.setSellerName(quote.getSeller().getCompanyName());
        response.setVerifiedSeller(quote.getSeller().isVerified());
        response.setQuotedPriceInrPaise(quote.getQuotedPriceInrPaise());
        response.setShippingEstimateInrPaise(quote.getShippingEstimateInrPaise());
        response.setLeadTimeDays(quote.getLeadTimeDays());
        response.setNotes(quote.getNotes());
        response.setValidityUntil(quote.getValidityUntil());
        response.setStatus(quote.getStatus());
        response.setCreatedAt(quote.getCreatedAt());
        return response;
    }
}
