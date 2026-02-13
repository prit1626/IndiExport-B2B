package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rfqs", indexes = {
        @Index(name = "idx_rfqs_buyer_id", columnList = "buyer_id"),
        @Index(name = "idx_rfqs_category_id", columnList = "category_id"),
        @Index(name = "idx_rfqs_status", columnList = "status"),
        @Index(name = "idx_rfqs_destination_country", columnList = "destination_country")
})
public class RFQ {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private BuyerProfile buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, length = 50)
    private String unit; // PCS, KG, TON

    @Column(nullable = false, length = 2)
    private String destinationCountry;

    @Column(columnDefinition = "TEXT")
    private String destinationAddressJson;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ShippingMode shippingMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Incoterm incoterm;

    @Column
    private Long targetPriceMinor;

    @Column(length = 3)
    private String targetCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RfqStatus status = RfqStatus.OPEN;

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RfqMedia> media = new ArrayList<>();

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL)
    private List<RfqQuote> quotes = new ArrayList<>();

    @Column
    private UUID acceptedQuoteId; // Denormalized reference for quick access

    @Version
    private Long version;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public RFQ() {}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BuyerProfile getBuyer() { return buyer; }
    public void setBuyer(BuyerProfile buyer) { this.buyer = buyer; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDestinationCountry() { return destinationCountry; }
    public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }

    public String getDestinationAddressJson() { return destinationAddressJson; }
    public void setDestinationAddressJson(String destinationAddressJson) { this.destinationAddressJson = destinationAddressJson; }

    public ShippingMode getShippingMode() { return shippingMode; }
    public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }

    public Incoterm getIncoterm() { return incoterm; }
    public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }

    public Long getTargetPriceMinor() { return targetPriceMinor; }
    public void setTargetPriceMinor(Long targetPriceMinor) { this.targetPriceMinor = targetPriceMinor; }

    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }

    public RfqStatus getStatus() { return status; }
    public void setStatus(RfqStatus status) { this.status = status; }

    public List<RfqMedia> getMedia() { return media; }
    public void setMedia(List<RfqMedia> media) { this.media = media; }

    public List<RfqQuote> getQuotes() { return quotes; }
    public void setQuotes(List<RfqQuote> quotes) { this.quotes = quotes; }

    public UUID getAcceptedQuoteId() { return acceptedQuoteId; }
    public void setAcceptedQuoteId(UUID acceptedQuoteId) { this.acceptedQuoteId = acceptedQuoteId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
