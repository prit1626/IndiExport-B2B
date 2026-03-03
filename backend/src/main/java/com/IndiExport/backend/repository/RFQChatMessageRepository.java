package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.RFQChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface RFQChatMessageRepository extends JpaRepository<RFQChatMessage, UUID> {

    Page<RFQChatMessage> findByChatIdOrderByCreatedAtDesc(UUID chatId, Pageable pageable);

    /**
     * Count messages in a chat not sent by the given user, optionally after a timestamp.
     * Used for unread badge calculation.
     */
    @Query("""
        SELECT COUNT(m) FROM RFQChatMessage m
        WHERE m.chat.id = :chatId
          AND m.sender.id <> :userId
    """)
    long countUnreadByChatAndUser(@Param("chatId") UUID chatId, @Param("userId") UUID userId);

    /** Last message in a chat — for preview text in chat list. */
    @Query("""
        SELECT m FROM RFQChatMessage m
        WHERE m.chat.id = :chatId
        ORDER BY m.createdAt DESC
    """)
    Page<RFQChatMessage> findLastMessage(@Param("chatId") UUID chatId, Pageable pageable);
}
