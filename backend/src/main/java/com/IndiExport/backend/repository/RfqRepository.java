package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.RFQ;
import com.IndiExport.backend.entity.RfqStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface RfqRepository extends JpaRepository<RFQ, UUID>, JpaSpecificationExecutor<RFQ> {

    Page<RFQ> findByBuyerId(UUID buyerId, Pageable pageable);

    // For expiry Job
    List<RFQ> findByStatusAndCreatedAtBefore(RfqStatus status, Instant cutoff);
}
