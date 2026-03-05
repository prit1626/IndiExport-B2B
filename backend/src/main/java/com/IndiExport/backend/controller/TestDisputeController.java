package com.IndiExport.backend.controller;

import com.IndiExport.backend.dto.dispute.RaiseDisputeRequest;
import com.IndiExport.backend.entity.DisputeReason;
import com.IndiExport.backend.entity.Order;
import com.IndiExport.backend.repository.OrderRepository;
import com.IndiExport.backend.service.dispute.DisputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-dispute")
public class TestDisputeController {

    @Autowired
    private DisputeService disputeService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/raise")
    public String testRaise() {
        try {
            // Find any Shipped or Delivered order
            List<Order> orders = orderRepository.findAll();
            Order order = orders.stream()
                    .filter(o -> o.getStatus() == Order.OrderStatus.SHIPPED
                            || o.getStatus() == Order.OrderStatus.DELIVERED)
                    .findFirst().orElse(null);

            if (order == null)
                return "No valid order found to test dispute.";

            // Use the buyer's user ID
            UUID userId = order.getBuyer().getUser().getId();

            RaiseDisputeRequest request = RaiseDisputeRequest.builder()
                    .orderId(order.getId())
                    .reason(DisputeReason.NOT_AS_DESCRIBED)
                    .description("Test Description Test Description Test Description")
                    .build();

            disputeService.raiseDispute(userId, request);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getClass().getName() + " - " + e.getMessage();
        }
    }
}
