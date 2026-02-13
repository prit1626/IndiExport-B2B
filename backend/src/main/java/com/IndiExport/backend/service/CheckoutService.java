package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.CheckoutDto;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.*;
import com.IndiExport.backend.service.currency.CurrencyConversionService;
import com.IndiExport.backend.service.currency.CurrencyMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Orchestrates the checkout workflow:
 *   1. Lock & validate cart items (product active, minQty)
 *   2. Snapshot current prices (protect against mid-checkout price changes)
 *   3. Group by seller → create one Order per seller
 *   4. Calculate shipping per order
 *   5. Lock exchange rate + create currency snapshot
 *   6. Clear cart
 *   7. Return summary
 *
 * Uses PESSIMISTIC_WRITE on cart rows to prevent double-checkout.
 */
@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final OrderCurrencySnapshotRepository currencySnapshotRepository;
    private final CurrencyConversionService currencyConversionService;
    private final ShippingQuoteService shippingQuoteService;
    private final CartService cartService;

    public CheckoutService(CartItemRepository cartItemRepository,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository,
                           BuyerProfileRepository buyerProfileRepository,
                           OrderCurrencySnapshotRepository currencySnapshotRepository,
                           CurrencyConversionService currencyConversionService,
                           ShippingQuoteService shippingQuoteService,
                           CartService cartService) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.currencySnapshotRepository = currencySnapshotRepository;
        this.currencyConversionService = currencyConversionService;
        this.shippingQuoteService = shippingQuoteService;
        this.cartService = cartService;
    }

    // Simple order number generator (in production, use a sequence or distributed ID)
    private static final AtomicInteger ORDER_SEQ = new AtomicInteger(1000);

    @Transactional
    public CheckoutDto.CheckoutResponse checkout(UUID userId, CheckoutDto.CheckoutRequest request) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        String buyerCurrency = CurrencyMetadata.validateAndNormalize(request.getBuyerCurrency());

        // 1. Lock cart items with PESSIMISTIC_WRITE to prevent double-checkout
        List<CartItem> cartItems = cartItemRepository.findByBuyerIdWithLock(buyer.getId());
        if (cartItems.isEmpty()) {
            throw new CartEmptyException();
        }

        // 2. Validate all products and snapshot prices
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStatus() != Product.ProductStatus.ACTIVE) {
                throw new ProductNotActiveException(product.getName());
            }
            if (item.getQuantity() < product.getMinimumOrderQuantity()) {
                throw new MinQtyViolationException(
                        product.getName(), item.getQuantity(), product.getMinimumOrderQuantity());
            }
        }

        // 3. Group cart items by seller
        Map<UUID, List<CartItem>> bySeller = cartItems.stream()
                .collect(Collectors.groupingBy(ci -> ci.getProduct().getSeller().getId()));

        List<CheckoutDto.OrderSummary> orderSummaries = new ArrayList<>();
        long totalSubtotalPaise = 0;
        long totalShippingPaise = 0;
        long totalGrandPaise = 0;
        long totalGrandConverted = 0;
        long exchangeRateMicros = 0;
        Instant rateTimestamp = null;

        // 4. Create one Order per seller
        for (Map.Entry<UUID, List<CartItem>> entry : bySeller.entrySet()) {
            List<CartItem> sellerItems = entry.getValue();
            SellerProfile seller = sellerItems.get(0).getProduct().getSeller();

            // Calculate subtotal and weight for this seller's items
            long orderSubtotalPaise = 0;
            long totalWeightGrams = 0;
            for (CartItem ci : sellerItems) {
                long lineTotal = (long) ci.getQuantity() * ci.getProduct().getPricePaise();
                orderSubtotalPaise += lineTotal;
                totalWeightGrams += (long) ci.getQuantity() * ci.getProduct().getWeightGrams();
            }

            // Generate order number
            String orderNumber = "IE-" + System.currentTimeMillis() + "-" + ORDER_SEQ.getAndIncrement();

            // Create order
            Order order = new Order();
            order.setOrderNumber(orderNumber);
            order.setBuyer(buyer);
            order.setSeller(seller);
            order.setStatus(Order.OrderStatus.PENDING_CONFIRMATION);
            order.setTotalAmountPaise(orderSubtotalPaise); // updated after shipping
            order.setCurrencyCode(buyerCurrency);
            order.setBuyerCountry(request.getShippingAddress().getCountry());
            order.setShippingAddress(request.getShippingAddress().getAddress());
            order.setShippingCity(request.getShippingAddress().getCity());
            order.setShippingState(request.getShippingAddress().getState());
            order.setShippingPostalCode(request.getShippingAddress().getPostalCode());
            order.setShippingCountry(request.getShippingAddress().getCountry());
            order.setShippingMode(request.getShippingMode());
            order.setSpecialInstructions(request.getSpecialInstructions());

            Order savedOrder = orderRepository.save(order);

            // Create order items with price snapshots
            for (CartItem ci : sellerItems) {
                Product product = ci.getProduct();
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setProduct(product);
                orderItem.setProductNameSnapshot(product.getName());
                orderItem.setSkuSnapshot(product.getSku());
                orderItem.setQuantity(ci.getQuantity());
                orderItem.setUnitPricePaise(product.getPricePaise());
                orderItem.setDiscountBasisPoints(0);
                orderItem.setGstBasisPoints(0);
                
                orderItem.calculateLineTotal();
                orderItemRepository.save(orderItem);
            }

            // 5. Calculate shipping
            ShippingQuote quote = shippingQuoteService.calculateAndSave(
                    savedOrder,
                    request.getShippingMode(),
                    request.getShippingAddress().getCountry(),
                    totalWeightGrams
            );

            // Update order total to include shipping
            long orderTotalPaise = orderSubtotalPaise + quote.getShippingCostPaise();
            savedOrder.setTotalAmountPaise(orderTotalPaise);
            orderRepository.save(savedOrder);

            // 6. Lock exchange rate
            CurrencyConversionService.ConversionResult convResult =
                    currencyConversionService.convertFromINR(orderTotalPaise, buyerCurrency);

            exchangeRateMicros = convResult.exchangeRateMicros();
            rateTimestamp = convResult.rateTimestamp();

            OrderCurrencySnapshot snapshot = new OrderCurrencySnapshot();
            snapshot.setOrder(savedOrder);
            snapshot.setBaseCurrency("INR");
            snapshot.setBuyerCurrency(convResult.targetCurrency());
            snapshot.setExchangeRateMicros(convResult.exchangeRateMicros());
            snapshot.setRateTimestamp(convResult.rateTimestamp());
            snapshot.setProviderName(convResult.providerName());
            snapshot.setBaseTotalPaise(orderTotalPaise);
            snapshot.setConvertedTotalMinor(convResult.convertedAmountMinor());
            snapshot.setCreatedAt(Instant.now());
            
            currencySnapshotRepository.save(snapshot);

            // Build summary for this order
            CheckoutDto.OrderSummary summary = new CheckoutDto.OrderSummary();
            summary.setOrderId(savedOrder.getId());
            summary.setOrderNumber(savedOrder.getOrderNumber());
            summary.setSellerCompanyName(seller.getCompanyName());
            summary.setSellerId(seller.getId());
            summary.setItemCount(sellerItems.size());
            summary.setSubtotalPaise(orderSubtotalPaise);
            summary.setShippingCostPaise(quote.getShippingCostPaise());
            summary.setTotalPaise(orderTotalPaise);
            summary.setTotalConverted(convResult.convertedAmountMinor());
            summary.setBuyerCurrency(convResult.targetCurrency());
            summary.setShippingMode(request.getShippingMode());
            summary.setEstimatedDeliveryDaysMin(quote.getEstimatedDeliveryDaysMin());
            summary.setEstimatedDeliveryDaysMax(quote.getEstimatedDeliveryDaysMax());

            orderSummaries.add(summary);
            totalSubtotalPaise += orderSubtotalPaise;
            totalShippingPaise += quote.getShippingCostPaise();
            totalGrandPaise += orderTotalPaise;
            totalGrandConverted += convResult.convertedAmountMinor();
        }

        // 7. Clear cart
        cartService.clearCart(buyer.getId());
        log.info("Checkout complete for buyer {}: {} orders created", userId, orderSummaries.size());

        CheckoutDto.CheckoutResponse response = new CheckoutDto.CheckoutResponse();
        response.setOrders(orderSummaries);
        response.setTotalSubtotalPaise(totalSubtotalPaise);
        response.setTotalShippingPaise(totalShippingPaise);
        response.setGrandTotalPaise(totalGrandPaise);
        response.setGrandTotalConverted(totalGrandConverted);
        response.setBuyerCurrency(buyerCurrency);
        response.setExchangeRateMicros(exchangeRateMicros);
        response.setRateTimestamp(rateTimestamp);
        response.setPaymentRequired(true);
        return response;
    }
}
