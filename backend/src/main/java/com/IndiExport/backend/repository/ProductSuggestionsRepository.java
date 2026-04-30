package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductSuggestionsRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT DISTINCT p.name FROM Product p " +
            "WHERE p.status = 'ACTIVE' " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY p.name ASC")
    List<String> findSuggestions(@Param("keyword") String keyword, Pageable pageable);
}
