package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.UserTermsAcceptance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTermsAcceptanceRepository extends JpaRepository<UserTermsAcceptance, UUID> {
    
    boolean existsByUserIdAndTermsVersionId(UUID userId, UUID termsVersionId);
    
    Optional<UserTermsAcceptance> findByUserIdAndTermsVersionId(UUID userId, UUID termsVersionId);
}
