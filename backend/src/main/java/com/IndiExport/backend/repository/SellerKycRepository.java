package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.SellerKyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerKycRepository extends JpaRepository<SellerKyc, UUID> {

    Optional<SellerKyc> findBySellerId(UUID sellerId);

    @Query("SELECT k FROM SellerKyc k WHERE k.verificationStatus = :status")
    List<SellerKyc> findByVerificationStatus(@Param("status") SellerKyc.VerificationStatus status);

    @Query("SELECT k FROM SellerKyc k JOIN FETCH k.seller s JOIN FETCH s.user u WHERE k.verificationStatus = 'PENDING'")
    List<SellerKyc> findPendingVerifications();

    boolean existsByIecNumber(String iecNumber);
}
