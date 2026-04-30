package com.IndiExport.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Temporary utility to fix database check constraints that Hibernate
 * ddl-auto=update might not successfully modify when Enums change.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseConstraintFixer {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    CommandLineRunner fixPaymentProviderConstraint() {
        return args -> {
            try {
                log.info("Checking and updating payments_provider_check constraint...");
                
                // Drop the existing check constraint
                jdbcTemplate.execute("ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_provider_check");
                
                // Re-add the constraint with RAZORPAY and RAZORPAYX included
                // Note: The specific values should match PaymentProvider enum names exactly.
                jdbcTemplate.execute("ALTER TABLE payments ADD CONSTRAINT payments_provider_check " +
                        "CHECK (provider IN ('STRIPE', 'RAZORPAY', 'RAZORPAYX'))");
                
                log.info("Successfully updated payments_provider_check constraint.");
            } catch (Exception e) {
                log.error("Failed to update database constraint: {}", e.getMessage());
                // Non-fatal, application can still start
            }
        };
    }
}
