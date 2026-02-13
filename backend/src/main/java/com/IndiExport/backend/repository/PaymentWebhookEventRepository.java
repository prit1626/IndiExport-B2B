package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.PaymentProvider;
import com.IndiExport.backend.entity.PaymentWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentWebhookEventRepository extends JpaRepository<PaymentWebhookEvent, UUID> {

    /** Check for duplicate webhook by provider + eventId. */
    boolean existsByProviderAndEventId(PaymentProvider provider, String eventId);

    Optional<PaymentWebhookEvent> findByProviderAndEventId(PaymentProvider provider, String eventId);
}
