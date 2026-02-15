package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {
    
    Optional<ChatParticipant> findByChatIdAndUserId(UUID chatId, UUID userId);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chat.id = :chatId AND m.createdAt > :lastReadAt")
    int countUnreadMessages(@Param("chatId") UUID chatId, @Param("lastReadAt") java.time.Instant lastReadAt);
}
