package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Find role by name
     */
    Optional<Role> findByName(Role.RoleType name);
}
