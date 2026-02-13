package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email ignoring soft delete
     */
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);
}
