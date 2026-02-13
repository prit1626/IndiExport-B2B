package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.CartDto;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.CartItemRepository;
import com.IndiExport.backend.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages the buyer's temporary cart.
 * Cart items are NOT orders — they are cleared after successful checkout.
 */
@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       BuyerProfileRepository buyerProfileRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.buyerProfileRepository = buyerProfileRepository;
    }

    /**
     * Add a product to the buyer's cart, or increment quantity if already present.
     */
    @Transactional
    public CartDto.CartItemResponse addToCart(UUID userId, CartDto.CartAddRequest request) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId().toString()));

        // Validate product is active
        if (product.getStatus() != Product.ProductStatus.ACTIVE) {
            throw new ProductNotActiveException(product.getName());
        }

        // Validate minimum order quantity
        if (request.getQuantity() < product.getMinimumOrderQuantity()) {
            throw new MinQtyViolationException(
                    product.getName(), request.getQuantity(), product.getMinimumOrderQuantity());
        }

        ShippingMode mode = request.getShippingMode() != null ? request.getShippingMode() : ShippingMode.SEA;

        // Upsert: if already in cart, update quantity
        CartItem cartItem = cartItemRepository.findByBuyerIdAndProductId(buyer.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    existing.setShippingMode(mode);
                    return existing;
                })
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setBuyer(buyer);
                    newItem.setProduct(product);
                    newItem.setQuantity(request.getQuantity());
                    newItem.setShippingMode(mode);
                    return newItem;
                });

        CartItem saved = cartItemRepository.save(cartItem);
        log.info("Cart item saved for buyer {} product {} qty {}", userId, product.getId(), saved.getQuantity());
        return mapToResponse(saved);
    }

    /**
     * Update a cart item's quantity and/or shipping mode.
     */
    @Transactional
    public CartDto.CartItemResponse updateCartItem(UUID userId, UUID cartItemId, CartDto.CartUpdateRequest request) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        CartItem item = cartItemRepository.findByIdAndBuyerId(cartItemId, buyer.getId())
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId.toString()));

        Product product = item.getProduct();

        // Validate minimum order quantity
        if (request.getQuantity() < product.getMinimumOrderQuantity()) {
            throw new MinQtyViolationException(
                    product.getName(), request.getQuantity(), product.getMinimumOrderQuantity());
        }

        item.setQuantity(request.getQuantity());
        if (request.getShippingMode() != null) {
            item.setShippingMode(request.getShippingMode());
        }

        CartItem saved = cartItemRepository.save(item);
        return mapToResponse(saved);
    }

    /**
     * Remove a single item from the cart.
     */
    @Transactional
    public void removeCartItem(UUID userId, UUID cartItemId) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        CartItem item = cartItemRepository.findByIdAndBuyerId(cartItemId, buyer.getId())
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId.toString()));

        cartItemRepository.delete(item);
        log.info("Cart item {} removed for buyer {}", cartItemId, userId);
    }

    /**
     * Get the full cart with all items and calculated totals.
     */
    @Transactional(readOnly = true)
    public CartDto.CartResponse getCart(UUID userId) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        List<CartItem> items = cartItemRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId());

        List<CartDto.CartItemResponse> itemResponses = items.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        long subtotal = items.stream()
                .mapToLong(i -> (long) i.getQuantity() * i.getProduct().getPricePaise())
                .sum();

        CartDto.CartResponse response = new CartDto.CartResponse();
        response.setItems(itemResponses);
        response.setTotalItems(items.size());
        response.setSubtotalPaise(subtotal);
        response.setEstimatedShippingPaise(0); // estimated at checkout
        response.setGrandTotalPaise(subtotal); // shipping added at checkout
        return response;
    }

    /**
     * Clear the entire cart for a buyer (after successful checkout).
     */
    @Transactional
    public void clearCart(UUID buyerProfileId) {
        cartItemRepository.deleteByBuyerId(buyerProfileId);
        log.info("Cart cleared for buyer profile {}", buyerProfileId);
    }

    private CartDto.CartItemResponse mapToResponse(CartItem item) {
        Product product = item.getProduct();
        SellerProfile seller = product.getSeller();

        CartDto.CartItemResponse response = new CartDto.CartItemResponse();
        response.setId(item.getId());
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setSku(product.getSku());
        response.setUnitPricePaise(product.getPricePaise());
        response.setQuantity(item.getQuantity());
        response.setMinQty(product.getMinimumOrderQuantity());
        response.setLineTotalPaise((long) item.getQuantity() * product.getPricePaise());
        response.setShippingMode(item.getShippingMode());
        response.setWeightGrams(product.getWeightGrams());
        response.setSellerCompanyName(seller.getCompanyName());
        response.setSellerId(seller.getId());
        response.setProductActive(product.getStatus() == Product.ProductStatus.ACTIVE);
        response.setAddedAt(item.getCreatedAt());
        return response;
    }
}
