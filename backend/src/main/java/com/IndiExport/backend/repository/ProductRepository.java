package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    List<Product> findBySellerIdAndStatusNot(UUID sellerId, Product.ProductStatus status);

    long countBySellerIdAndStatus(UUID sellerId, Product.ProductStatus status);

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId AND p.status != 'DELETED'")
    List<Product> findAllActiveBySeller(UUID sellerId);
}
