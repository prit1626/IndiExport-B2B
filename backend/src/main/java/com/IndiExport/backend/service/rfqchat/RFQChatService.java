package com.IndiExport.backend.service.rfqchat;

import com.IndiExport.backend.dto.rfqchat.*;
import com.IndiExport.backend.dto.RfqFinalizeResponse;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.exception.RFQChatExceptions.*;
import com.IndiExport.backend.repository.*;
import com.IndiExport.backend.service.rfq.RfqFinalizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RFQChatService {

    private final RFQChatRepository chatRepository;
    private final RFQChatMessageRepository messageRepository;
    private final RfqRepository rfqRepository;
    private final UserRepository userRepository;
    private final RfqFinalizeService rfqFinalizeService;
    private final SimpMessagingTemplate messagingTemplate;

    // ─────────────────────────────────────────────────────────────────
    // Start / Get Chat
    // ─────────────────────────────────────────────────────────────────

    /**
     * Idempotent: creates or returns the existing chat for this (RFQ, seller) pair.
     * Only a seller can initiate.
     */
    @Transactional
    public RFQChatListResponse startChat(UUID rfqId, UUID sellerId) {
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RfqNotFoundException("RFQ not found"));

        if (rfq.getStatus() == RfqStatus.CANCELLED || rfq.getStatus() == RfqStatus.EXPIRED
                || rfq.getStatus() == RfqStatus.CONVERTED_TO_ORDER) {
            throw new RFQClosedException("Cannot start a chat – RFQ is " + rfq.getStatus());
        }

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        User buyer = userRepository.findById(rfq.getBuyer().getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        RFQChat chat = chatRepository.findByRfqIdAndSellerId(rfqId, sellerId)
                .orElseGet(() -> {
                    RFQChat c = new RFQChat();
                    c.setRfq(rfq);
                    c.setBuyer(buyer);
                    c.setSeller(seller);
                    log.info("Creating new RFQ chat for rfq={} seller={}", rfqId, sellerId);
                    return chatRepository.save(c);
                });

        return toListResponse(chat, sellerId);
    }

    // ─────────────────────────────────────────────────────────────────
    // Chat Lists
    // ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<RFQChatListResponse> getBuyerChats(UUID buyerId, Pageable pageable) {
        return chatRepository.findByBuyerId(buyerId, pageable)
                .map(c -> toListResponse(c, buyerId));
    }

    @Transactional(readOnly = true)
    public Page<RFQChatListResponse> getSellerChats(UUID sellerId, Pageable pageable) {
        return chatRepository.findBySellerId(sellerId, pageable)
                .map(c -> toListResponse(c, sellerId));
    }

    // ─────────────────────────────────────────────────────────────────
    // Messages
    // ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<RFQChatMessageResponse> getMessages(UUID chatId, UUID userId, Pageable pageable) {
        RFQChat chat = requireChat(chatId);
        assertParticipant(chat, userId);
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chatId, pageable)
                .map(this::toMessageResponse);
    }

    @Transactional
    public RFQChatMessageResponse sendMessage(UUID chatId, UUID senderId, SendMessageRequest req) {
        RFQChat chat = requireChat(chatId);
        assertParticipant(chat, senderId);
        assertChatActive(chat);
        assertRfqNegotiable(chat.getRfq());

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RFQChatMessage msg = new RFQChatMessage();
        msg.setChat(chat);
        msg.setSender(sender);
        msg.setMessageType(req.getMessageType());
        msg.setMessageText(req.getMessageText());
        msg.setAttachmentUrl(req.getAttachmentUrl());
        msg.setAttachmentFileName(req.getAttachmentFileName());
        msg = messageRepository.save(msg);

        RFQChatMessageResponse response = toMessageResponse(msg);
        broadcast(chatId, "NEW_MESSAGE", response);
        return response;
    }

    @Transactional
    public RFQChatMessageResponse sendPriceProposal(UUID chatId, UUID sellerId, PriceProposalRequest req) {
        RFQChat chat = requireChat(chatId);

        // Only the seller of this chat can send a proposal
        if (!chat.getSeller().getId().equals(sellerId)) {
            throw new UnauthorizedChatAccessException("Only the seller of this chat can send a price proposal");
        }
        assertChatActive(chat);
        assertRfqNegotiable(chat.getRfq());

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        // Save PRICE_PROPOSAL message
        RFQChatMessage msg = new RFQChatMessage();
        msg.setChat(chat);
        msg.setSender(seller);
        msg.setMessageType(RFQChatMessageType.PRICE_PROPOSAL);
        msg.setProposedPriceMinor(req.getProposedPriceMinor());
        msg.setCurrency(req.getCurrency());
        msg.setLeadTimeDays(req.getLeadTimeDays());
        msg = messageRepository.save(msg);

        // Move RFQ → UNDER_NEGOTIATION if still OPEN
        if (chat.getRfq().getStatus() == RfqStatus.OPEN) {
            chat.getRfq().setStatus(RfqStatus.UNDER_NEGOTIATION);
            rfqRepository.save(chat.getRfq());
        }

        // Sync proposal into an RfqQuote immediately so it appears on the Buyer RFQ details view
        createQuoteFromProposal(chat.getRfq(), seller, msg);

        // Post SYSTEM message
        postSystemMessage(chat, "Seller proposed a new price: "
                + req.getProposedPriceMinor() + " " + req.getCurrency()
                + ", lead time " + req.getLeadTimeDays() + " days");

        RFQChatMessageResponse response = toMessageResponse(msg);
        broadcast(chatId, "PRICE_PROPOSAL", response);
        return response;
    }

    /**
     * Buyer accepts a price proposal message.
     * This trigggers RFQ finalization → Order creation.
     * Lock: uses RFQ @Version for optimistic locking — concurrent accepts throw
     * OptimisticLockException (mapped to 409).
     */
    @Transactional
    public AcceptProposalResponse acceptProposal(UUID chatId, UUID buyerId, UUID messageId) {
        RFQChat chat = requireChat(chatId);

        // Only the buyer of this chat can accept
        if (!chat.getBuyer().getId().equals(buyerId)) {
            throw new UnauthorizedChatAccessException("Only the buyer can accept a price proposal");
        }
        assertChatActive(chat);

        RFQChatMessage proposal = messageRepository.findById(messageId)
                .orElseThrow(() -> new RFQChatMessageNotFoundException("Message not found"));

        if (proposal.getMessageType() != RFQChatMessageType.PRICE_PROPOSAL) {
            throw new IllegalArgumentException("Target message is not a price proposal");
        }
        if (proposal.isAccepted()) {
            throw new ProposalAlreadyAcceptedException("This proposal has already been accepted");
        }
        if (!proposal.getChat().getId().equals(chatId)) {
            throw new UnauthorizedChatAccessException("Message does not belong to this chat");
        }

        // Mark this message as accepted
        proposal.setAccepted(true);
        messageRepository.save(proposal);

        // Finalize RFQ via existing service — this creates RfqQuote + Order + Invoice
        // We need to create a quote from the proposal first, then finalize
        RFQ rfq = chat.getRfq();
        RfqQuote quote = createQuoteFromProposal(rfq, chat.getSeller(), proposal);

        // Fetch buyer profile id
        UUID buyerProfileId = rfq.getBuyer().getId();
        RfqFinalizeResponse finalizeResp = rfqFinalizeService.finalizeRfq(buyerProfileId, rfq.getId(), quote.getId());

        // Prevent Hibernate dirty-checking from reverting the RFQ state when saving the chat later
        chat.getRfq().setStatus(com.IndiExport.backend.entity.RfqStatus.CONVERTED_TO_ORDER);
        
        // Lock chat
        chat.setActive(false);
        chat.setClosedAt(Instant.now());
        chatRepository.save(chat);

        // Post SYSTEM message
        postSystemMessage(chat, "Buyer accepted the price proposal. Order created.");

        // Broadcast PROPOSAL_ACCEPTED with orderId so UI can redirect
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "PROPOSAL_ACCEPTED");
        payload.put("orderId", finalizeResp.getOrderId());
        messagingTemplate.convertAndSend("/topic/rfq-chat/" + chatId, payload);

        log.info("Proposal accepted in chatId={}, order={}", chatId, finalizeResp.getOrderId());

        return AcceptProposalResponse.builder()
                .orderId(finalizeResp.getOrderId())
                .invoiceId(finalizeResp.getInvoiceId())
                .message("Order created successfully")
                .paymentRequired(true)
                .build();
    }

    @Transactional
    public void markRead(UUID chatId, UUID userId) {
        // For simplicity: reading all messages implicitly marks them read.
        // A proper unread-tracking table is out of scope for MVP.
        // This endpoint exists for the frontend to call when chat is opened.
        log.debug("markRead called for chatId={} userId={}", chatId, userId);
    }

    // ─────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────

    private RFQChat requireChat(UUID chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RFQChatNotFoundException("Chat not found: " + chatId));
    }

    private void assertParticipant(RFQChat chat, UUID userId) {
        if (!chat.getBuyer().getId().equals(userId) && !chat.getSeller().getId().equals(userId)) {
            throw new UnauthorizedChatAccessException("You are not a participant of this chat");
        }
    }

    private void assertChatActive(RFQChat chat) {
        if (!chat.isActive()) {
            throw new RFQClosedException("This negotiation is closed");
        }
    }

    private void assertRfqNegotiable(RFQ rfq) {
        if (rfq.getStatus() == RfqStatus.EXPIRED) {
            throw new RFQExpiredException("RFQ has expired");
        }
        if (rfq.getStatus() == RfqStatus.CANCELLED
                || rfq.getStatus() == RfqStatus.FINALIZED
                || rfq.getStatus() == RfqStatus.CONVERTED_TO_ORDER) {
            throw new RFQClosedException("RFQ is not open for negotiation (status=" + rfq.getStatus() + ")");
        }
    }

    private void postSystemMessage(RFQChat chat, String text) {
        User systemUser = chat.getBuyer(); // system messages are attributed to buyer's account for simplicity
        RFQChatMessage sys = new RFQChatMessage();
        sys.setChat(chat);
        sys.setSender(systemUser);
        sys.setMessageType(RFQChatMessageType.SYSTEM);
        sys.setMessageText(text);
        sys = messageRepository.save(sys);
        broadcast(chat.getId(), "SYSTEM_EVENT", toMessageResponse(sys));
    }

    private void broadcast(UUID chatId, String eventType, Object payload) {
        Map<String, Object> envelope = new HashMap<>();
        envelope.put("event", eventType);
        envelope.put("data", payload);
        messagingTemplate.convertAndSend("/topic/rfq-chat/" + chatId, envelope);
    }

    private RfqQuote createQuoteFromProposal(RFQ rfq, User sellerUser, RFQChatMessage proposal) {
        // We need SellerProfile — look it up from sellerUser
        com.IndiExport.backend.entity.SellerProfile sellerProfile = sellerUser.getSellerProfile();
        if (sellerProfile == null) {
            throw new ResourceNotFoundException("Seller profile not found for user " + sellerUser.getId());
        }

        RfqQuote quote = rfqQuoteRepository.findByRfqIdAndSellerId(rfq.getId(), sellerProfile.getId())
                .orElse(new RfqQuote());

        quote.setRfq(rfq);
        quote.setSeller(sellerProfile);
        quote.setQuotedPriceInrPaise(proposal.getProposedPriceMinor());
        quote.setLeadTimeDays(proposal.getLeadTimeDays());
        quote.setNotes("Accepted via RFQ Chat proposal. Currency: " + proposal.getCurrency());
        quote.setStatus(com.IndiExport.backend.entity.RfqQuoteStatus.ACTIVE);

        // Save via rfqQuoteRepository through service — we'll just save it inline
        return rfqQuoteRepository.save(quote);
    }

    // ─────────────────────────────────────────────────────────────────
    // Mappers
    // ─────────────────────────────────────────────────────────────────

    private RFQChatMessageResponse toMessageResponse(RFQChatMessage msg) {
        String senderRole = msg.getSender().getId().equals(msg.getChat().getSeller().getId())
                ? "SELLER"
                : "BUYER";
        return RFQChatMessageResponse.builder()
                .id(msg.getId())
                .chatId(msg.getChat().getId())
                .senderId(msg.getSender().getId())
                .senderName(msg.getSender().getFullName())
                .senderRole(senderRole)
                .messageType(msg.getMessageType())
                .messageText(msg.getMessageText())
                .attachmentUrl(msg.getAttachmentUrl())
                .attachmentFileName(msg.getAttachmentFileName())
                .proposedPriceMinor(msg.getProposedPriceMinor())
                .currency(msg.getCurrency())
                .leadTimeDays(msg.getLeadTimeDays())
                .accepted(msg.isAccepted())
                .edited(msg.isEdited())
                .editedAt(msg.getEditedAt())
                .createdAt(msg.getCreatedAt())
                .build();
    }

    private RFQChatListResponse toListResponse(RFQChat chat, UUID viewerUserId) {
        RFQ rfq = chat.getRfq();
        boolean viewerIsBuyer = chat.getBuyer().getId().equals(viewerUserId);
        String otherParty = viewerIsBuyer
                ? chat.getSeller().getFullName()
                : chat.getBuyer().getFullName();

        // Last message preview
        String lastMsgPreview = "";
        Instant updatedAt = chat.getCreatedAt();
        List<RFQChatMessage> lastMsgs = messageRepository
                .findLastMessage(chat.getId(), PageRequest.of(0, 1)).getContent();
        if (!lastMsgs.isEmpty()) {
            RFQChatMessage last = lastMsgs.get(0);
            updatedAt = last.getCreatedAt();
            if (last.getMessageType() == RFQChatMessageType.PRICE_PROPOSAL) {
                lastMsgPreview = "💰 Price proposal: " + last.getProposedPriceMinor() + " " + last.getCurrency();
            } else if (last.getMessageType() == RFQChatMessageType.ATTACHMENT) {
                lastMsgPreview = "📎 "
                        + (last.getAttachmentFileName() != null ? last.getAttachmentFileName() : "Attachment");
            } else if (last.getMessageType() == RFQChatMessageType.SYSTEM) {
                lastMsgPreview = "ℹ️ " + last.getMessageText();
            } else {
                lastMsgPreview = last.getMessageText() != null ? last.getMessageText() : "";
            }
        }

        long unread = messageRepository.countUnreadByChatAndUser(chat.getId(), viewerUserId);

        return RFQChatListResponse.builder()
                .chatId(chat.getId())
                .rfqId(rfq.getId())
                .rfqTitle(rfq.getTitle())
                .qty(rfq.getQuantity())
                .unit(rfq.getUnit())
                .destinationCountry(rfq.getDestinationCountry())
                .otherPartyName(otherParty)
                .lastMessagePreview(lastMsgPreview)
                .updatedAt(updatedAt)
                .unreadCount(unread)
                .active(chat.isActive())
                .build();
    }

    // ─────────────────────────────────────────────────────────────────
    // Extra repo dep needed for createQuoteFromProposal
    // ─────────────────────────────────────────────────────────────────
    private final com.IndiExport.backend.repository.RfqQuoteRepository rfqQuoteRepository;
}
