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
    CommandLineRunner initRoles(RoleRepository roleRepository, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        return args -> {
            try { jdbcTemplate.execute("ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check CASCADE"); } catch (Exception e) { System.out.println("Drop orders constraint failed: " + e.getMessage()); }
            try { jdbcTemplate.execute("ALTER TYPE order_status_enum ADD VALUE IF NOT EXISTS 'PAID'"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TYPE order_status_enum ADD VALUE IF NOT EXISTS 'READY_TO_SHIP'"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TYPE order_status_enum ADD VALUE IF NOT EXISTS 'COMPLETED'"); } catch (Exception e) {}

            try { jdbcTemplate.execute("ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_status_check CASCADE"); } catch (Exception e) { System.out.println("Drop payments constraint failed: " + e.getMessage()); }
            try { jdbcTemplate.execute("ALTER TYPE payment_status_enum ADD VALUE IF NOT EXISTS 'CREATED'"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TYPE payment_status_enum ADD VALUE IF NOT EXISTS 'PAID'"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TYPE payment_status_enum ADD VALUE IF NOT EXISTS 'CAPTURED'"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TYPE payment_status_enum ADD VALUE IF NOT EXISTS 'HOLDING'"); } catch (Exception e) {}
            try { jdbcTemplate.execute("ALTER TYPE payment_status_enum ADD VALUE IF NOT EXISTS 'RELEASED'"); } catch (Exception e) {}

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
