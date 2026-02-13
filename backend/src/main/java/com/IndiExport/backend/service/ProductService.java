package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.ProductDto;
import com.IndiExport.backend.dto.CategoryDto;
import com.IndiExport.backend.entity.Category;
import com.IndiExport.backend.entity.Product;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.entity.Tag;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.exception.ProductExceptions;
import com.IndiExport.backend.exception.ForbiddenException;
import com.IndiExport.backend.repository.CategoryRepository;
import com.IndiExport.backend.repository.ProductRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.TagRepository;
import com.IndiExport.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final SellerPlanService sellerPlanService;
    private final UserRepository userRepository;
    private final com.IndiExport.backend.service.currency.CurrencyConversionService currencyConversionService;

    public ProductService(ProductRepository productRepository,
                          SellerProfileRepository sellerProfileRepository,
                          CategoryRepository categoryRepository,
                          TagRepository tagRepository,
                          SellerPlanService sellerPlanService,
                          UserRepository userRepository,
                          com.IndiExport.backend.service.currency.CurrencyConversionService currencyConversionService) {
        this.productRepository = productRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.sellerPlanService = sellerPlanService;
        this.userRepository = userRepository;
        this.currencyConversionService = currencyConversionService;
    }

    @Transactional
    public ProductDto.ProductResponse createProduct(UUID userId, ProductDto.ProductCreateRequest request) {
        SellerProfile seller = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        // Optional: Ensure seller is verified before allowing ACTIVE status
        if (request.getStatus() == Product.ProductStatus.ACTIVE) {
            if (!seller.isVerified()) {
                throw new ForbiddenException("Seller must be verified to list active products");
            }
            sellerPlanService.validateActiveProductLimit(seller.getId());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        Product product = new Product();
        product.setSeller(seller);
        product.setName(request.getTitle());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPricePaise(request.getPricePaise());
        product.setMinimumOrderQuantity(request.getMinQty());
        product.setQuantityUnit(request.getUnit());
        product.setWeightGrams(request.getWeightGrams());
        product.setLengthMm(request.getLengthMm());
        product.setWidthMm(request.getWidthMm());
        product.setHeightMm(request.getHeightMm());
        product.setHsCode(request.getHsCode());
        product.setIncoterm(request.getIncoterm());
        product.setLeadTimeDays(request.getLeadTimeDays());
        product.setStatus(request.getStatus());
        product.setCreatedBy(user);

        mapCategoriesAndTags(product, request.getCategoryIds(), request.getTagNames());

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Transactional
    public ProductDto.ProductResponse updateProduct(UUID userId, UUID productId, ProductDto.ProductUpdateRequest request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId.toString()));

        if (!product.getSeller().getUser().getId().equals(userId)) {
            throw new ProductExceptions.UnauthorizedProductAccessException("You can only update your own products");
        }

        // Check plan limit if status is changing to ACTIVE
        if (request.getStatus() == Product.ProductStatus.ACTIVE && product.getStatus() != Product.ProductStatus.ACTIVE) {
            if (!product.getSeller().isVerified()) {
                throw new ForbiddenException("Seller must be verified to list active products");
            }
            sellerPlanService.validateActiveProductLimit(product.getSeller().getId());
        }

        if (request.getTitle() != null) product.setName(request.getTitle());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPricePaise() != null) product.setPricePaise(request.getPricePaise());
        if (request.getMinQty() != null) product.setMinimumOrderQuantity(request.getMinQty());
        if (request.getUnit() != null) product.setQuantityUnit(request.getUnit());
        if (request.getWeightGrams() != null) product.setWeightGrams(request.getWeightGrams());
        if (request.getLengthMm() != null) product.setLengthMm(request.getLengthMm());
        if (request.getWidthMm() != null) product.setWidthMm(request.getWidthMm());
        if (request.getHeightMm() != null) product.setHeightMm(request.getHeightMm());
        if (request.getHsCode() != null) product.setHsCode(request.getHsCode());
        if (request.getIncoterm() != null) product.setIncoterm(request.getIncoterm());
        if (request.getLeadTimeDays() != null) product.setLeadTimeDays(request.getLeadTimeDays());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getStatus() != null) product.setStatus(request.getStatus());

        if (request.getCategoryIds() != null || request.getTagNames() != null) {
            mapCategoriesAndTags(product, request.getCategoryIds(), request.getTagNames());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductDto.ProductResponse> getSellerProducts(UUID userId) {
        SellerProfile seller = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));
        
        return productRepository.findBySellerIdAndStatusNot(seller.getId(), Product.ProductStatus.DELETED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto.ProductResponse getProductDetails(UUID productId) {
        return getProductDetails(productId, null);
    }

    @Transactional(readOnly = true)
    public ProductDto.ProductResponse getProductDetails(UUID productId, String targetCurrency) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId.toString()));
        ProductDto.ProductResponse response = mapToResponse(product);

        // Enrich with converted price if a target currency is specified
        if (targetCurrency != null && !targetCurrency.isBlank()) {
            try {
                var result = currencyConversionService.convertFromINR(product.getPricePaise(), targetCurrency);
                com.IndiExport.backend.dto.CurrencyDto.ConvertedPriceInfo convertedPrice = new com.IndiExport.backend.dto.CurrencyDto.ConvertedPriceInfo();
                convertedPrice.setConvertedPriceMinor(result.convertedAmountMinor());
                convertedPrice.setCurrency(result.targetCurrency());
                convertedPrice.setExchangeRateMicros(result.exchangeRateMicros());
                convertedPrice.setRateTimestamp(result.rateTimestamp());
                response.setConvertedPrice(convertedPrice);
            } catch (Exception e) {
                // Return product without converted price rather than failing
            }
        }

        return response;
    }

    @Transactional
    public void deleteProduct(UUID userId, UUID productId) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId.toString()));

        if (!product.getSeller().getUser().getId().equals(userId)) {
            throw new ProductExceptions.UnauthorizedProductAccessException("You can only delete your own products");
        }

        product.setStatus(Product.ProductStatus.DELETED);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    private void mapCategoriesAndTags(Product product, Set<UUID> categoryIds, Set<String> tagNames) {
        if (categoryIds != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
            product.setCategories(categories);
        }

        if (tagNames != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                tags.add(tag);
            }
            product.setTags(tags);
        }
    }

    private ProductDto.ProductResponse mapToResponse(Product product) {
        ProductDto.ProductResponse response = new ProductDto.ProductResponse();
        response.setId(product.getId());
        response.setTitle(product.getName());
        response.setBrand(product.getBrand());
        response.setDescription(product.getDescription());
        response.setSku(product.getSku());
        response.setPricePaise(product.getPricePaise());
        response.setMinQty(product.getMinimumOrderQuantity());
        response.setUnit(product.getQuantityUnit());
        response.setWeightGrams(product.getWeightGrams());
        response.setLengthMm(product.getLengthMm());
        response.setWidthMm(product.getWidthMm());
        response.setHeightMm(product.getHeightMm());
        response.setHsCode(product.getHsCode());
        response.setIncoterm(product.getIncoterm());
        response.setLeadTimeDays(product.getLeadTimeDays());
        response.setStockQuantity(product.getStockQuantity());
        response.setStatus(product.getStatus());
        
        response.setCategories(product.getCategories().stream()
                .map(c -> {
                    CategoryDto.CategoryResponse cr = new CategoryDto.CategoryResponse();
                    cr.setId(c.getId());
                    cr.setName(c.getName());
                    cr.setSlug(c.getSlug());
                    return cr;
                })
                .collect(Collectors.toList()));
        
        response.setTags(product.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        
        response.setMedia(product.getMedia() != null ? product.getMedia().stream()
                .map(m -> {
                    ProductDto.ProductMediaResponse pmr = new ProductDto.ProductMediaResponse();
                    pmr.setId(m.getId());
                    pmr.setUrl(m.getMediaUrl());
                    pmr.setType(m.getMediaType());
                    pmr.setDisplayOrder(m.getDisplayOrder());
                    return pmr;
                })
                .collect(Collectors.toList()) : List.of());
        
        response.setAverageRating(product.getAverageRatingMilli() / 1000.0);
        response.setTotalReviews(product.getTotalReviews());
        
        return response;
    }
}
