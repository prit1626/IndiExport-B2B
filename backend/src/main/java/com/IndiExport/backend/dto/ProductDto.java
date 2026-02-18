package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.Incoterm;
import com.IndiExport.backend.entity.Product;
import com.IndiExport.backend.entity.ProductMedia;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ProductDto {

    public static class ProductCreateRequest {
        @NotBlank(message = "Product title is required")
        private String title;
        private String brand;
        private String description;
        @NotBlank(message = "SKU is required")
        private String sku;
        @Min(value = 1, message = "Price must be at least 1 paise")
        private long pricePaise;
        @Min(value = 1, message = "Minimum quantity must be at least 1")
        private int minQty;
        @NotBlank(message = "Unit is required")
        private String unit;
        @Min(value = 0, message = "Weight cannot be negative")
        private long weightGrams;
        @Min(value = 0, message = "Length cannot be negative")
        private int lengthMm;
        @Min(value = 0, message = "Width cannot be negative")
        private int widthMm;
        @Min(value = 0, message = "Height cannot be negative")
        private int heightMm;
        private String hsCode;
        private Incoterm incoterm;
        private int leadTimeDays = 7;
        private Set<UUID> categoryIds;
        private Set<String> tagNames;
        private Product.ProductStatus status = Product.ProductStatus.DRAFT;

        public ProductCreateRequest() {}

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public long getPricePaise() { return pricePaise; }
        public void setPricePaise(long pricePaise) { this.pricePaise = pricePaise; }
        public int getMinQty() { return minQty; }
        public void setMinQty(int minQty) { this.minQty = minQty; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public long getWeightGrams() { return weightGrams; }
        public void setWeightGrams(long weightGrams) { this.weightGrams = weightGrams; }
        public int getLengthMm() { return lengthMm; }
        public void setLengthMm(int lengthMm) { this.lengthMm = lengthMm; }
        public int getWidthMm() { return widthMm; }
        public void setWidthMm(int widthMm) { this.widthMm = widthMm; }
        public int getHeightMm() { return heightMm; }
        public void setHeightMm(int heightMm) { this.heightMm = heightMm; }
        public String getHsCode() { return hsCode; }
        public void setHsCode(String hsCode) { this.hsCode = hsCode; }
        public Incoterm getIncoterm() { return incoterm; }
        public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }
        public int getLeadTimeDays() { return leadTimeDays; }
        public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }
        public Set<UUID> getCategoryIds() { return categoryIds; }
        public void setCategoryIds(Set<UUID> categoryIds) { this.categoryIds = categoryIds; }
        public Set<String> getTagNames() { return tagNames; }
        public void setTagNames(Set<String> tagNames) { this.tagNames = tagNames; }
        public Product.ProductStatus getStatus() { return status; }
        public void setStatus(Product.ProductStatus status) { this.status = status; }
    }

    public static class ProductUpdateRequest {
        private String title;
        private String brand;
        private String description;
        private Long pricePaise;
        private Integer minQty;
        private String unit;
        private Long weightGrams;
        private Integer lengthMm;
        private Integer widthMm;
        private Integer heightMm;
        private String hsCode;
        private Incoterm incoterm;
        private Integer leadTimeDays;
        private Integer stockQuantity;
        private Set<UUID> categoryIds;
        private Set<String> tagNames;
        private Product.ProductStatus status;

        public ProductUpdateRequest() {}

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getPricePaise() { return pricePaise; }
        public void setPricePaise(Long pricePaise) { this.pricePaise = pricePaise; }
        public Integer getMinQty() { return minQty; }
        public void setMinQty(Integer minQty) { this.minQty = minQty; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public Long getWeightGrams() { return weightGrams; }
        public void setWeightGrams(Long weightGrams) { this.weightGrams = weightGrams; }
        public Integer getLengthMm() { return lengthMm; }
        public void setLengthMm(Integer lengthMm) { this.lengthMm = lengthMm; }
        public Integer getWidthMm() { return widthMm; }
        public void setWidthMm(Integer widthMm) { this.widthMm = widthMm; }
        public Integer getHeightMm() { return heightMm; }
        public void setHeightMm(Integer heightMm) { this.heightMm = heightMm; }
        public String getHsCode() { return hsCode; }
        public void setHsCode(String hsCode) { this.hsCode = hsCode; }
        public Incoterm getIncoterm() { return incoterm; }
        public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }
        public Integer getLeadTimeDays() { return leadTimeDays; }
        public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public Set<UUID> getCategoryIds() { return categoryIds; }
        public void setCategoryIds(Set<UUID> categoryIds) { this.categoryIds = categoryIds; }
        public Set<String> getTagNames() { return tagNames; }
        public void setTagNames(Set<String> tagNames) { this.tagNames = tagNames; }
        public Product.ProductStatus getStatus() { return status; }
        public void setStatus(Product.ProductStatus status) { this.status = status; }
    }

    public static class ProductResponse {
        private UUID id;
        private String title;
        private String brand;
        private String description;
        private String sku;
        private long pricePaise;
        private int minQty;
        private String unit;
        private long weightGrams;
        private int lengthMm;
        private int widthMm;
        private int heightMm;
        private String hsCode;
        private Incoterm incoterm;
        private int leadTimeDays;
        private int stockQuantity;
        private Product.ProductStatus status;
        private List<ProductMediaResponse> media;
        private List<CategoryDto.CategoryResponse> categories;
        private List<String> tags;
        private double averageRating;
        private int totalReviews;
        private CurrencyDto.ConvertedPriceInfo convertedPrice;
        private SellerBasicInfo seller;

        public ProductResponse() {}

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public long getPricePaise() { return pricePaise; }
        public void setPricePaise(long pricePaise) { this.pricePaise = pricePaise; }
        public int getMinQty() { return minQty; }
        public void setMinQty(int minQty) { this.minQty = minQty; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public long getWeightGrams() { return weightGrams; }
        public void setWeightGrams(long weightGrams) { this.weightGrams = weightGrams; }
        public int getLengthMm() { return lengthMm; }
        public void setLengthMm(int lengthMm) { this.lengthMm = lengthMm; }
        public int getWidthMm() { return widthMm; }
        public void setWidthMm(int widthMm) { this.widthMm = widthMm; }
        public int getHeightMm() { return heightMm; }
        public void setHeightMm(int heightMm) { this.heightMm = heightMm; }
        public String getHsCode() { return hsCode; }
        public void setHsCode(String hsCode) { this.hsCode = hsCode; }
        public Incoterm getIncoterm() { return incoterm; }
        public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }
        public int getLeadTimeDays() { return leadTimeDays; }
        public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }
        public int getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
        public Product.ProductStatus getStatus() { return status; }
        public void setStatus(Product.ProductStatus status) { this.status = status; }
        public List<ProductMediaResponse> getMedia() { return media; }
        public void setMedia(List<ProductMediaResponse> media) { this.media = media; }
        public List<CategoryDto.CategoryResponse> getCategories() { return categories; }
        public void setCategories(List<CategoryDto.CategoryResponse> categories) { this.categories = categories; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        public int totalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
        public CurrencyDto.ConvertedPriceInfo getConvertedPrice() { return convertedPrice; }
        public void setConvertedPrice(CurrencyDto.ConvertedPriceInfo convertedPrice) { this.convertedPrice = convertedPrice; }
        public SellerBasicInfo getSeller() { return seller; }
        public void setSeller(SellerBasicInfo seller) { this.seller = seller; }
    }

    public static class ProductMediaResponse {
        private UUID id;
        private String url;
        private ProductMedia.MediaType type;
        private int displayOrder;

        public ProductMediaResponse() {}

        public static ProductMediaResponseBuilder builder() {
            return new ProductMediaResponseBuilder();
        }

        public static class ProductMediaResponseBuilder {
            private ProductMediaResponse response = new ProductMediaResponse();

            public ProductMediaResponseBuilder id(UUID id) { response.setId(id); return this; }
            public ProductMediaResponseBuilder url(String url) { response.setUrl(url); return this; }
            public ProductMediaResponseBuilder type(ProductMedia.MediaType type) { response.setType(type); return this; }
            public ProductMediaResponseBuilder displayOrder(int displayOrder) { response.setDisplayOrder(displayOrder); return this; }
            public ProductMediaResponse build() { return response; }
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public ProductMedia.MediaType getType() { return type; }
        public void setType(ProductMedia.MediaType type) { this.type = type; }
        public int getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
    }

    public static class BuyerProductCardResponse {
        private UUID id;
        private String title;
        private String brand;
        private long pricePaise;
        private String unit;
        private String thumbnail;
        private double averageRating;
        private int totalReviews;
        private Incoterm incoterm;
        private String originCountry;
        private int leadTimeDays;
        private SellerBasicInfo seller;
        private CurrencyDto.ConvertedPriceInfo convertedPrice;

        public BuyerProductCardResponse() {}

        public static BuyerProductCardResponseBuilder builder() {
            return new BuyerProductCardResponseBuilder();
        }

        public static class BuyerProductCardResponseBuilder {
            private BuyerProductCardResponse response = new BuyerProductCardResponse();
            public BuyerProductCardResponseBuilder id(UUID id) { response.setId(id); return this; }
            public BuyerProductCardResponseBuilder title(String title) { response.setTitle(title); return this; }
            public BuyerProductCardResponseBuilder brand(String brand) { response.setBrand(brand); return this; }
            public BuyerProductCardResponseBuilder pricePaise(long pricePaise) { response.setPricePaise(pricePaise); return this; }
            public BuyerProductCardResponseBuilder unit(String unit) { response.setUnit(unit); return this; }
            public BuyerProductCardResponseBuilder thumbnail(String thumbnail) { response.setThumbnail(thumbnail); return this; }
            public BuyerProductCardResponseBuilder averageRating(double averageRating) { response.setAverageRating(averageRating); return this; }
            public BuyerProductCardResponseBuilder totalReviews(int totalReviews) { response.setTotalReviews(totalReviews); return this; }
            public BuyerProductCardResponseBuilder incoterm(Incoterm incoterm) { response.setIncoterm(incoterm); return this; }
            public BuyerProductCardResponseBuilder originCountry(String originCountry) { response.setOriginCountry(originCountry); return this; }
            public BuyerProductCardResponseBuilder leadTimeDays(int leadTimeDays) { response.setLeadTimeDays(leadTimeDays); return this; }
            public BuyerProductCardResponseBuilder seller(SellerBasicInfo seller) { response.setSeller(seller); return this; }
            public BuyerProductCardResponseBuilder convertedPrice(CurrencyDto.ConvertedPriceInfo convertedPrice) { response.setConvertedPrice(convertedPrice); return this; }
            public BuyerProductCardResponse build() { return response; }
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public long getPricePaise() { return pricePaise; }
        public void setPricePaise(long pricePaise) { this.pricePaise = pricePaise; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public String getThumbnail() { return thumbnail; }
        public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
        public double getAverageRating() { return averageRating; }
        public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
        public Incoterm getIncoterm() { return incoterm; }
        public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }
        public String getOriginCountry() { return originCountry; }
        public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }
        public int getLeadTimeDays() { return leadTimeDays; }
        public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }
        public SellerBasicInfo getSeller() { return seller; }
        public void setSeller(SellerBasicInfo seller) { this.seller = seller; }
        public CurrencyDto.ConvertedPriceInfo getConvertedPrice() { return convertedPrice; }
        public void setConvertedPrice(CurrencyDto.ConvertedPriceInfo convertedPrice) { this.convertedPrice = convertedPrice; }
    }

    public static class SellerBasicInfo {
        private UUID id;
        private String companyName;
        private boolean verified;

        public SellerBasicInfo() {}

        public static SellerBasicInfoBuilder builder() {
            return new SellerBasicInfoBuilder();
        }

        public static class SellerBasicInfoBuilder {
            private SellerBasicInfo info = new SellerBasicInfo();
            public SellerBasicInfoBuilder id(UUID id) { info.setId(id); return this; }
            public SellerBasicInfoBuilder companyName(String companyName) { info.setCompanyName(companyName); return this; }
            public SellerBasicInfoBuilder verified(boolean verified) { info.setVerified(verified); return this; }
            public SellerBasicInfo build() { return info; }
        }

        public SellerBasicInfo(UUID id, String companyName, boolean verified) {
            this.id = id;
            this.companyName = companyName;
            this.verified = verified;
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public boolean isVerified() { return verified; }
        public void setVerified(boolean verified) { this.verified = verified; }
    }

    public static class ProductFilterRequest {
        private String keyword;
        private UUID categoryId;
        private UUID sellerId;
        private Long minPricePaise;
        private Long maxPricePaise;
        private Double minRating;
        private Boolean verifiedSeller;
        private Incoterm incoterm;
        private String hsCode;
        private Integer maxLeadTimeDays;
        private String country;
        private int page = 0;
        private int size = 20;
        private String sortBy = "newest";

        public ProductFilterRequest() {}

        // Getters and Setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public UUID getCategoryId() { return categoryId; }
        public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
        public UUID getSellerId() { return sellerId; }
        public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
        public Long getMinPricePaise() { return minPricePaise; }
        public void setMinPricePaise(Long minPricePaise) { this.minPricePaise = minPricePaise; }
        public Long getMaxPricePaise() { return maxPricePaise; }
        public void setMaxPricePaise(Long maxPricePaise) { this.maxPricePaise = maxPricePaise; }
        public Double getMinRating() { return minRating; }
        public void setMinRating(Double minRating) { this.minRating = minRating; }
        public Boolean getVerifiedSeller() { return verifiedSeller; }
        public void setVerifiedSeller(Boolean verifiedSeller) { this.verifiedSeller = verifiedSeller; }
        public Incoterm getIncoterm() { return incoterm; }
        public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }
        public String getHsCode() { return hsCode; }
        public void setHsCode(String hsCode) { this.hsCode = hsCode; }
        public Integer getMaxLeadTimeDays() { return maxLeadTimeDays; }
        public void setMaxLeadTimeDays(Integer maxLeadTimeDays) { this.maxLeadTimeDays = maxLeadTimeDays; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    }
}
