package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.TermsVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TermsVersionRepository extends JpaRepository<TermsVersion, UUID> {

    @Query("SELECT t FROM TermsVersion t WHERE t.isPublished = true ORDER BY t.versionNumber DESC LIMIT 1")
    Optional<TermsVersion> findLatestPublished();

    @Query("SELECT t FROM TermsVersion t ORDER BY t.versionNumber DESC LIMIT 1")
    Optional<TermsVersion> findLatestAny();

    boolean existsByVersionNumber(int versionNumber);
}
