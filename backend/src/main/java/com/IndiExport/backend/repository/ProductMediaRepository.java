package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, UUID> {
    List<ProductMedia> findByProductIdOrderByDisplayOrderAsc(UUID productId);
    void deleteByProductId(UUID productId);
}
