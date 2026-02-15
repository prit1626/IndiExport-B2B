package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.ReviewMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewMediaRepository extends JpaRepository<ReviewMedia, UUID> {
}
