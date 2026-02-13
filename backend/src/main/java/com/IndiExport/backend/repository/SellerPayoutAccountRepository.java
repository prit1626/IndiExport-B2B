package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.SellerPayoutAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerPayoutAccountRepository extends JpaRepository<SellerPayoutAccount, UUID> {

    Optional<SellerPayoutAccount> findBySellerId(UUID sellerId);

    boolean existsBySellerId(UUID sellerId);
}
