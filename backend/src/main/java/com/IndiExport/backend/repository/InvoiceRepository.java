package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Invoice;
import com.IndiExport.backend.entity.InvoiceSide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByBuyerIdAndSide(UUID buyerId, InvoiceSide side, Pageable pageable);

    Page<Invoice> findBySellerIdAndSide(UUID sellerId, InvoiceSide side, Pageable pageable);

    Page<Invoice> findByOrderId(UUID orderId, Pageable pageable);
}
