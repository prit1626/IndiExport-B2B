package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminSettingsRepository extends JpaRepository<AdminSettings, UUID> {
    
    // We expect only one row, so we can use a query that limits to 1
    @Query(value = "SELECT * FROM admin_settings LIMIT 1", nativeQuery = true)
    Optional<AdminSettings> findFirst();
}
