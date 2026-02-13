package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.ProductDto;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.repository.ProductRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final com.IndiExport.backend.service.currency.CurrencyConversionService currencyConversionService;
    @Transactional(readOnly = true)
    public Page<ProductDto.BuyerProductCardResponse> searchProducts(
            ProductDto.ProductFilterRequest filter, String targetCurrency) {
        Pageable pageable = createPageable(filter);
        Specification<Product> spec = createSpecification(filter);

        return productRepository.findAll(spec, pageable)
                .map(product -> {
                    ProductDto.BuyerProductCardResponse card = mapToCardResponse(product);
                    // Enrich with converted price if currency is specified
                    if (targetCurrency != null && !targetCurrency.isBlank()) {
                        try {
                            var result = currencyConversionService.convertFromINR(
                                    product.getPricePaise(), targetCurrency);
                            card.setConvertedPrice(
                                    com.IndiExport.backend.dto.CurrencyDto.ConvertedPriceInfo.builder()
                                            .convertedPriceMinor(result.convertedAmountMinor())
                                            .currency(result.targetCurrency())
                                            .exchangeRateMicros(result.exchangeRateMicros())
                                            .rateTimestamp(result.rateTimestamp())
                                            .build());
                        } catch (Exception e) {
                            // Price conversion failed, skip enrichment
                        }
                    }
                    return card;
                });
    }

    private Specification<Product> createSpecification(ProductDto.ProductFilterRequest filter) {
        return (root, query, cb) -> {
            // Fix N+1: Fetch media and seller eagerly for the card mapping
            if (Long.class != query.getResultType()) { // Only fetch if not a count query
                root.fetch("media", JoinType.LEFT);
                root.fetch("seller", JoinType.INNER).fetch("kyc", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // 1. Only ACTIVE products (Soft-delete is now handled by @Where)
            predicates.add(cb.equal(root.get("status"), Product.ProductStatus.ACTIVE));

            // 2. Keyword search (name, description, brand, tags)
            if (StringUtils.hasText(filter.getKeyword())) {
                String pattern = "%" + filter.getKeyword().toLowerCase() + "%";
                Join<Product, Tag> tagsJoin = root.join("tags", JoinType.LEFT);
                
                Predicate namePred = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descPred = cb.like(cb.lower(root.get("description")), pattern);
                Predicate brandPred = cb.like(cb.lower(root.get("brand")), pattern);
                Predicate tagPred = cb.like(cb.lower(tagsJoin.get("name")), pattern);
                
                predicates.add(cb.or(namePred, descPred, brandPred, tagPred));
            }

            // 3. Category filter
            if (filter.getCategoryId() != null) {
                Join<Product, Category> catJoin = root.join("categories", JoinType.INNER);
                predicates.add(cb.equal(catJoin.get("id"), filter.getCategoryId()));
            }

            // 4. Price range
            if (filter.getMinPricePaise() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePaise"), filter.getMinPricePaise()));
            }
            if (filter.getMaxPricePaise() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePaise"), filter.getMaxPricePaise()));
            }

            // 5. Rating
            if (filter.getMinRating() != null) {
                int minRatingMilli = (int) (filter.getMinRating() * 1000);
                predicates.add(cb.greaterThanOrEqualTo(root.get("averageRatingMilli"), minRatingMilli));
            }

            // 6. Verified Seller
            if (filter.getVerifiedSeller() != null && filter.getVerifiedSeller()) {
                Join<Product, SellerProfile> sellerJoin = root.join("seller", JoinType.INNER);
                Join<SellerProfile, SellerKyc> kycJoin = sellerJoin.join("kyc", JoinType.INNER);
                predicates.add(cb.equal(kycJoin.get("verificationStatus"), SellerKyc.VerificationStatus.VERIFIED));
            }

            // 7. Incoterm
            if (filter.getIncoterm() != null) {
                predicates.add(cb.equal(root.get("incoterm"), filter.getIncoterm()));
            }

            // 8. HS Code
            if (StringUtils.hasText(filter.getHsCode())) {
                predicates.add(cb.like(root.get("hsCode"), filter.getHsCode() + "%"));
            }

            // 9. Lead time
            if (filter.getMaxLeadTimeDays() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("leadTimeDays"), filter.getMaxLeadTimeDays()));
            }

            // Ensure distinct results due to joins
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Pageable createPageable(ProductDto.ProductFilterRequest filter) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt"); // Default: newest
        
        if (filter.getSortBy() != null) {
            switch (filter.getSortBy()) {
                case "priceAsc":
                    sort = Sort.by(Sort.Direction.ASC, "pricePaise");
                    break;
                case "priceDesc":
                    sort = Sort.by(Sort.Direction.DESC, "pricePaise");
                    break;
                case "rating":
                    sort = Sort.by(Sort.Direction.DESC, "averageRating");
                    break;
                case "popularity":
                    sort = Sort.by(Sort.Direction.DESC, "totalOrders");
                    break;
            }
        }
        
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }

    private ProductDto.BuyerProductCardResponse mapToCardResponse(Product product) {
        String thumbnail = product.getMedia() != null && !product.getMedia().isEmpty() 
                ? product.getMedia().stream()
                    .filter(m -> m.getMediaType() == ProductMedia.MediaType.IMAGE)
                    .findFirst()
                    .map(ProductMedia::getMediaUrl)
                    .orElse(null)
                : null;

        return ProductDto.BuyerProductCardResponse.builder()
                .id(product.getId())
                .title(product.getName())
                .brand(product.getBrand())
                .pricePaise(product.getPricePaise())
                .unit(product.getQuantityUnit())
                .thumbnail(thumbnail)
                .averageRating(product.getAverageRatingMilli() / 1000.0)
                .seller(ProductDto.SellerBasicInfo.builder()
                        .id(product.getSeller().getId())
                        .companyName(product.getSeller().getCompanyName())
                        .verified(product.getSeller().isVerified())
                        .build())
                .build();
    }
}
