package com.IndiExport.backend.config;

import com.IndiExport.backend.entity.Role;
import com.IndiExport.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Data initializer to seed required roles on application startup.
 * Creates BUYER, SELLER, and ADMIN roles if they don't already exist.
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            for (Role.RoleType roleType : Role.RoleType.values()) {
                if (roleRepository.findByName(roleType).isEmpty()) {
                    Role role = Role.builder()
                            .name(roleType)
                            .description(roleType.name() + " role")
                            .build();
                    roleRepository.save(role);
                }
            }
        };
    }
}
