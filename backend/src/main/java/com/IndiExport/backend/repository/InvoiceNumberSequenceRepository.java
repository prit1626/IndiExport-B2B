package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.InvoiceNumberSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvoiceNumberSequenceRepository extends JpaRepository<InvoiceNumberSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InvoiceNumberSequence s WHERE s.sequenceKey = :key")
    Optional<InvoiceNumberSequence> findBySequenceKeyForUpdate(@Param("key") String key);
}
