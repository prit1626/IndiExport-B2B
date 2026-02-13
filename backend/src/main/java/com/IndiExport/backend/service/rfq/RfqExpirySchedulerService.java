package com.IndiExport.backend.service.rfq;

import com.IndiExport.backend.entity.RFQ;
import com.IndiExport.backend.entity.RfqStatus;
import com.IndiExport.backend.repository.RfqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RfqExpirySchedulerService {

    private final RfqRepository rfqRepository;

    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
    @Transactional
    public void expireOldRfqs() {
        Instant cutoff = Instant.now().minus(30, ChronoUnit.DAYS); // Example: 30 days validity
        
        // Find OPEN or UNDER_NEGOTIATION RFQs older than cutoff
        List<RFQ> expiredRfqs = rfqRepository.findByStatusAndCreatedAtBefore(RfqStatus.OPEN, cutoff);
        expiredRfqs.addAll(rfqRepository.findByStatusAndCreatedAtBefore(RfqStatus.UNDER_NEGOTIATION, cutoff));
        
        for (RFQ rfq : expiredRfqs) {
            rfq.setStatus(RfqStatus.EXPIRED);
        }
        
        rfqRepository.saveAll(expiredRfqs);
    }
}
