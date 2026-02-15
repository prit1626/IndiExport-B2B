package com.IndiExport.backend.service.dispute;

import com.IndiExport.backend.dto.dispute.*;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.entity.Role.RoleType;
import com.IndiExport.backend.exception.DisputeExceptions.*;
import com.IndiExport.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final DisputeEvidenceRepository evidenceRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DisputeEscrowLockService escrowLockService;

    // --- Actions ---

    @Transactional
    public DisputeResponse raiseDispute(UUID userId, RaiseDisputeRequest request) {
        // 1. Fetch Order and Validate Ownership
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new DisputeNotAllowedException("Order not found"));

        boolean isBuyer = order.getBuyer().getUser().getId().equals(userId);
        boolean isSeller = order.getSeller().getUser().getId().equals(userId);

        if (!isBuyer && !isSeller) {
            throw new DisputeAccessDeniedException("You are not part of this order");
        }

        RoleType raisedByRole = isBuyer ? RoleType.BUYER : RoleType.SELLER;

        // 2. Validate Order Status (Must be SHIPPED, DELIVERED, COMPLETED, IN_TRANSIT)
        if (!canRaiseDispute(order.getStatus())) {
            throw new DisputeNotAllowedException("Dispute cannot be raised for order in status: " + order.getStatus());
        }

        // 3. Check for existing open dispute
        boolean hasOpenDispute = disputeRepository.existsByOrderIdAndStatusIn(
                order.getId(),
                List.of(DisputeStatus.OPEN, DisputeStatus.EVIDENCE_REQUIRED, DisputeStatus.UNDER_REVIEW)
        );

        if (hasOpenDispute) {
            throw new DisputeAlreadyExistsException("An open dispute already exists for this order");
        }

        // 4. Create Dispute
        Dispute dispute = Dispute.builder()
                .order(order)
                .buyerId(order.getBuyer().getId())
                .sellerId(order.getSeller().getId())
                .raisedByUserId(userId)
                .raisedByRole(raisedByRole)
                .reason(request.getReason())
                .description(request.getDescription())
                .status(DisputeStatus.OPEN)
                .build();

        dispute = disputeRepository.save(dispute);

        // 5. Add Evidence
        if (request.getEvidenceUrls() != null && !request.getEvidenceUrls().isEmpty()) {
            Dispute finalDispute = dispute;
            List<DisputeEvidence> evidences = request.getEvidenceUrls().stream()
                    .map(url -> DisputeEvidence.builder()
                            .dispute(finalDispute)
                            .uploadedByUserId(userId)
                            .uploadedByRole(raisedByRole)
                            .fileUrl(url)
                            .fileType("IMAGE") // Default, can be improved
                            .build())
                    .collect(Collectors.toList());
            evidenceRepository.saveAll(evidences);
            dispute.setEvidence(evidences);
        }

        // 6. Lock Escrow
        escrowLockService.lockFunds(order.getId());

        return mapToResponse(dispute);
    }

    @Transactional
    public EvidenceResponse addEvidence(UUID disputeId, UUID userId, AddEvidenceRequest request) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new DisputeNotFoundException("Dispute not found"));

        // Validate Status
        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new EvidenceUploadNotAllowedException("Cannot add evidence to closed dispute");
        }

        // Validate Ownership
        boolean isBuyer = dispute.getOrder().getBuyer().getUser().getId().equals(userId);
        boolean isSeller = dispute.getOrder().getSeller().getUser().getId().equals(userId);
        
        if (!isBuyer && !isSeller) {
             throw new DisputeAccessDeniedException("You are not a participant in this dispute");
        }
        
        RoleType role = isBuyer ? RoleType.BUYER : RoleType.SELLER;

        DisputeEvidence evidence = DisputeEvidence.builder()
                .dispute(dispute)
                .uploadedByUserId(userId)
                .uploadedByRole(role)
                .fileUrl(request.getFileUrl())
                .fileType(request.getFileType() != null ? request.getFileType() : "IMAGE")
                .build();

        return mapToEvidenceResponse(evidenceRepository.save(evidence));
    }

    // --- Finding / listing ---

    @Transactional(readOnly = true)
    public Page<DisputeListResponse> getDisputesForBuyer(UUID buyerId, Pageable pageable) {
        return disputeRepository.findByBuyerId(buyerId, pageable)
                .map(this::mapToListResponse);
    }

    @Transactional(readOnly = true)
    public Page<DisputeListResponse> getDisputesForSeller(UUID sellerId, Pageable pageable) {
        return disputeRepository.findBySellerId(sellerId, pageable)
                .map(this::mapToListResponse);
    }

    @Transactional(readOnly = true)
    public Page<AdminDisputeResponse> getAllDisputesAdmin(DisputeStatus status, UUID buyerId, UUID sellerId, Pageable pageable) {
        return disputeRepository.findAllByFilters(status, buyerId, sellerId, pageable)
                .map(this::mapToAdminResponse);
    }
    
    @Transactional(readOnly = true)
    public DisputeResponse getDisputeDetails(UUID disputeId, UUID userId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new DisputeNotFoundException("Dispute not found"));
                
        // Access check
        if (!isAdmin(userId) && !isParticipant(dispute, userId)) {
             throw new DisputeAccessDeniedException("Access denied");
        }
        
        return mapToResponse(dispute);
    }

    // --- Helpers ---

    private boolean canRaiseDispute(Order.OrderStatus status) {
        return status == Order.OrderStatus.SHIPPED || 
               status == Order.OrderStatus.IN_TRANSIT || 
               status == Order.OrderStatus.DELIVERED || 
               status == Order.OrderStatus.COMPLETED;
    }

    // Helper to determine if user is buyer or seller for this dispute
    private RoleType determineRole(Dispute dispute, UUID userId) {
       if (dispute.getOrder().getBuyer().getUser().getId().equals(userId)) return RoleType.BUYER;
       if (dispute.getOrder().getSeller().getUser().getId().equals(userId)) return RoleType.SELLER;
       throw new DisputeAccessDeniedException("User is not a participant");
    }
    
    private boolean isParticipant(Dispute dispute, UUID userId) {
        return dispute.getOrder().getBuyer().getUser().getId().equals(userId) ||
               dispute.getOrder().getSeller().getUser().getId().equals(userId);
    }
    
    // Mock admin check - secure it via @PreAuthorize in controller usually
    private boolean isAdmin(UUID userId) {
        // In clean architecture, service shouldn't generally rely on SecurityContext directly if possible,
        // but here we might validly pass a role or check user entity roles
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getRoles().stream().anyMatch(role -> role.getName() == RoleType.ADMIN);
    }
    
    // Dummy helper
    private UUID getBuyerIdFromUser(UUID userId) {
        // Implementation depends on repo structure
        return null; 
    }
    
    private UUID getSellerIdFromUser(UUID userId) {
        return null;
    }

    // --- Mappers ---

    private DisputeResponse mapToResponse(Dispute dispute) {
        return DisputeResponse.builder()
                .id(dispute.getId())
                .orderId(dispute.getOrder().getId())
                .orderNumber(dispute.getOrder().getOrderNumber())
                .buyerId(dispute.getBuyerId())
                .buyerName(dispute.getOrder().getBuyer().getUser().getFullName())
                .sellerId(dispute.getSellerId())
                .companyName(dispute.getOrder().getSeller().getCompanyName())
                .raisedByUserId(dispute.getRaisedByUserId())
                .raisedByRole(dispute.getRaisedByRole())
                .reason(dispute.getReason())
                .description(dispute.getDescription())
                .status(dispute.getStatus())
                .createdAt(dispute.getCreatedAt())
                .updatedAt(dispute.getUpdatedAt())
                .resolvedAt(dispute.getResolvedAt())
                .resolutionAction(dispute.getResolutionAction())
                .resolutionNotes(dispute.getResolutionNotes())
                .partialRefundAmountMinor(dispute.getPartialRefundAmountMinor())
                .evidence(dispute.getEvidence().stream().map(this::mapToEvidenceResponse).collect(Collectors.toList()))
                .build();
    }
    
    private AdminDisputeResponse mapToAdminResponse(Dispute dispute) {
        // Similar to above but with Admin Response DTO
        return AdminDisputeResponse.builder()
                .id(dispute.getId())
                .orderId(dispute.getOrder().getId())
                .orderNumber(dispute.getOrder().getOrderNumber())
                .buyerId(dispute.getBuyerId())
                .buyerName(dispute.getOrder().getBuyer().getUser().getFullName())
                .sellerId(dispute.getSellerId())
                .companyName(dispute.getOrder().getSeller().getCompanyName())
                .raisedByUserId(dispute.getRaisedByUserId())
                .raisedByRole(dispute.getRaisedByRole())
                .reason(dispute.getReason())
                .description(dispute.getDescription())
                .status(dispute.getStatus())
                .createdAt(dispute.getCreatedAt())
                .updatedAt(dispute.getUpdatedAt())
                .resolvedAt(dispute.getResolvedAt())
                .resolvedByAdminId(dispute.getResolvedByAdminId())
                .resolutionAction(dispute.getResolutionAction())
                .resolutionNotes(dispute.getResolutionNotes())
                .partialRefundAmountMinor(dispute.getPartialRefundAmountMinor())
                .evidence(dispute.getEvidence().stream().map(this::mapToEvidenceResponse).collect(Collectors.toList()))
                .build();
    }

    private DisputeListResponse mapToListResponse(Dispute dispute) {
        return DisputeListResponse.builder()
                .id(dispute.getId())
                .orderId(dispute.getOrder().getId())
                .orderNumber(dispute.getOrder().getOrderNumber())
                .reason(dispute.getReason())
                .status(dispute.getStatus())
                .createdAt(dispute.getCreatedAt())
                .build();
    }

    private EvidenceResponse mapToEvidenceResponse(DisputeEvidence evidence) {
        return EvidenceResponse.builder()
                .id(evidence.getId())
                .uploadedByUserId(evidence.getUploadedByUserId())
                .uploadedByRole(evidence.getUploadedByRole())
                .fileUrl(evidence.getFileUrl())
                .fileType(evidence.getFileType())
                .createdAt(evidence.getCreatedAt())
                .build();
    }
}
