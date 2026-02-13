package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    /**
     * Find tag by name
     */
    Optional<Tag> findByName(String name);
}
