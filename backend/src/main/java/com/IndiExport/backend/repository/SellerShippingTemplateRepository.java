package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.SellerShippingTemplate;
import com.IndiExport.backend.entity.ShippingMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellerShippingTemplateRepository extends JpaRepository<SellerShippingTemplate, UUID> {

    List<SellerShippingTemplate> findBySellerIdAndActiveTrue(UUID sellerId);

    List<SellerShippingTemplate> findBySellerId(UUID sellerId);

    /**
     * Find matching templates for a seller + destination + mode.
     * Matches COUNTRY exactly, or ZONE by zone name.
     * Filters by weight range (null bounds = unbounded).
     */
    @Query("""
        SELECT t FROM SellerShippingTemplate t
        WHERE t.seller.id = :sellerId
          AND t.shippingMode = :mode
          AND t.active = true
          AND (
              (t.destinationType = 'COUNTRY' AND t.destinationValue = :country)
              OR (t.destinationType = 'ZONE' AND t.destinationValue = :zone)
          )
          AND (t.minWeightGrams IS NULL OR t.minWeightGrams <= :weightGrams)
          AND (t.maxWeightGrams IS NULL OR t.maxWeightGrams >= :weightGrams)
        ORDER BY t.destinationType ASC
    """)
    List<SellerShippingTemplate> findMatchingTemplates(
            @Param("sellerId") UUID sellerId,
            @Param("country") String country,
            @Param("zone") String zone,
            @Param("mode") ShippingMode mode,
            @Param("weightGrams") long weightGrams
    );
}
