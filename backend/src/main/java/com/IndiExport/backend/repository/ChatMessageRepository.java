package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    
    Page<ChatMessage> findByChatId(UUID chatId, Pageable pageable);
    
    // For last message preview - though ideally redundant if we store lastMessageId in Chat
    ChatMessage findFirstByChatIdOrderByCreatedAtDesc(UUID chatId);
}
