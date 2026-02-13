package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    /**
     * Find category by name
     */
    Optional<Category> findByName(String name);

    /**
     * Find active categories
     */
    List<Category> findByIsActiveTrueOrderBySortOrder();

    /**
     * Find subcategories by parent
     */
    List<Category> findByParentCategoryIdOrderBySortOrder(UUID parentId);
}
