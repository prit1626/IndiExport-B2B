package com.IndiExport.backend.service.invoice;

import com.IndiExport.backend.entity.InvoiceNumberSequence;
import com.IndiExport.backend.entity.InvoiceType;
import com.IndiExport.backend.repository.InvoiceNumberSequenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class InvoiceNumberGeneratorService {

    private final InvoiceNumberSequenceRepository sequenceRepository;

    public InvoiceNumberGeneratorService(InvoiceNumberSequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    /**
     * Generates a unique invoice number.
     * Format:
     * - FINAL: INV-{YYYY}-{SEQ} (e.g., INV-2026-00042)
     * - PROFORMA: PRF-{YYYY}-{SEQ} (e.g., PRF-2026-00042)
     *
     * Uses pessimistic locking to ensure uniqueness.
     * Requires a new transaction to avoid long-held locks.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateNextNumber(InvoiceType type) {
        int currentYear = Year.now().getValue();
        String prefix = (type == InvoiceType.FINAL) ? "INV" : "PRF";
        String key = prefix + "-" + currentYear;

        InvoiceNumberSequence sequence = sequenceRepository.findBySequenceKeyForUpdate(key)
                .orElseGet(() -> new InvoiceNumberSequence(key, 0));

        sequence.setCurrentVal(sequence.getCurrentVal() + 1);
        sequenceRepository.save(sequence);

        return String.format("%s-%05d", key, sequence.getCurrentVal());
    }
}
