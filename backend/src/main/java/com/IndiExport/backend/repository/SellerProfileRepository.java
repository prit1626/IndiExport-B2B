package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, UUID> {

    /**
     * Find seller profile by user ID
     */
    Optional<SellerProfile> findByUserId(UUID userId);

    /**
     * Find seller profile by user ID, excluding soft-deleted
     */
    Optional<SellerProfile> findByUserIdAndDeletedAtIsNull(UUID userId);

    /**
     * Check if seller profile exists for user
     */
    boolean existsByUserId(UUID userId);

    /**
     * Find verified sellers for marketplace display
     */
    @Query("SELECT sp FROM SellerProfile sp JOIN sp.kyc k WHERE k.verificationStatus = 'VERIFIED' AND sp.deletedAt IS NULL")
    List<SellerProfile> findVerifiedSellers();

    /**
     * Find sellers with pending KYC verification
     */
    @Query("SELECT sp FROM SellerProfile sp JOIN sp.kyc k WHERE k.verificationStatus = 'PENDING' AND sp.deletedAt IS NULL")
    List<SellerProfile> findPendingVerifications();

    /**
     * Count sellers by country
     */
    long countByCountryAndDeletedAtIsNull(String country);
}
