package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_participants", indexes = {
        @Index(name = "idx_participants_chat_id", columnList = "chat_id"),
        @Index(name = "idx_participants_user_id", columnList = "user_id")
})
@Getter
@Setter
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role.RoleType role;

    @Column
    private Instant lastReadAt;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
