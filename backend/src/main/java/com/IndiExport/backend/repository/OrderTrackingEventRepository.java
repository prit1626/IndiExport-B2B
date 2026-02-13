package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.OrderTrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderTrackingEventRepository extends JpaRepository<OrderTrackingEvent, UUID> {

    List<OrderTrackingEvent> findByTrackingIdOrderByEventTimeDesc(UUID trackingId);
}
