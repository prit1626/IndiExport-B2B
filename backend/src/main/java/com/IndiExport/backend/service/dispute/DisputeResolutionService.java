package com.IndiExport.backend.service.dispute;

import com.IndiExport.backend.dto.dispute.AdminDisputeResponse;
import com.IndiExport.backend.dto.dispute.AdminResolveDisputeRequest;
import com.IndiExport.backend.entity.Dispute;
import com.IndiExport.backend.entity.DisputeResolutionAction;
import com.IndiExport.backend.entity.DisputeStatus;
import com.IndiExport.backend.exception.DisputeExceptions.*;
import com.IndiExport.backend.repository.DisputeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisputeResolutionService {

    private final DisputeRepository disputeRepository;
    private final DisputeEscrowLockService escrowLockService;
    private final DisputeService disputeService; // To reuse mappers/get logic if needed, or better expose mapper

    @Transactional
    public Dispute resolveDispute(UUID disputeId, UUID adminId, AdminResolveDisputeRequest request) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new DisputeNotFoundException("Dispute not found"));

        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new DisputeResolutionException("Dispute is already closed");
        }

        DisputeResolutionAction action = request.getResolutionAction();
        
        // Validation
        if (action == DisputeResolutionAction.PARTIAL_REFUND) {
            if (request.getPartialRefundAmountMinor() == null || request.getPartialRefundAmountMinor() <= 0) {
                throw new InvalidRefundAmountException("Partial refund amount must be positive");
            }
            long paymentAmount = dispute.getOrder().getPayment().getAmountMinor();
            if (request.getPartialRefundAmountMinor() > paymentAmount) {
                 throw new InvalidRefundAmountException("Refund amount cannot exceed payment amount");
            }
        }

        // Apply Resolution
        dispute.setResolutionAction(action);
        dispute.setResolutionNotes(request.getResolutionNotes());
        dispute.setResolvedAt(Instant.now());
        dispute.setResolvedByAdminId(adminId);
        
        if (action == DisputeResolutionAction.PARTIAL_REFUND) {
            dispute.setPartialRefundAmountMinor(request.getPartialRefundAmountMinor());
        }

        // Update Status
        if (action == DisputeResolutionAction.REJECT) {
            dispute.setStatus(DisputeStatus.REJECTED);
        } else {
            dispute.setStatus(DisputeStatus.RESOLVED);
        }

        dispute = disputeRepository.save(dispute);

        // Execute Financials (Mock)
        executeFinancialResolution(dispute, action);

        // Unlock Escrow (Funds dealt with)
        escrowLockService.unlockFunds(dispute.getOrder().getId());

        return dispute;
    }

    private void executeFinancialResolution(Dispute dispute, DisputeResolutionAction action) {
        log.info("Executing financial resolution for dispute {}: Action={}", dispute.getId(), action);
        // Here we would call StripeService.refund(...)
        // For MVP, we presume the escrow unlock allows payout script to pick it up or we mark payment as REFUNDED directly.
        // If REFUND -> Mark payment refunded
        // If PARTIAL -> Trigger partial refund, release rest
        // If REJECT -> Release to seller (standard flow picks up unlocked)
    }
}
