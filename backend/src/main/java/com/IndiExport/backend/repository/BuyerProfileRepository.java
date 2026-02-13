package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.BuyerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuyerProfileRepository extends JpaRepository<BuyerProfile, UUID> {

    /**
     * Find buyer profile by user ID
     */
    Optional<BuyerProfile> findByUserId(UUID userId);

    /**
     * Check if buyer profile exists for user
     */
    boolean existsByUserId(UUID userId);

    /**
     * Find by country
     */
    java.util.List<BuyerProfile> findByCountry(String country);
}
