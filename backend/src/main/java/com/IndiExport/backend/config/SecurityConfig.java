package com.IndiExport.backend.config;

import com.IndiExport.backend.security.JwtAuthenticationFilter;
import com.IndiExport.backend.security.JwtAuthenticationEntryPoint;
import com.IndiExport.backend.security.JwtAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security Configuration for JWT-based authentication.
 * 
 * Security Rules:
 * - Stateless authentication (JWT)
 * - Public endpoints: /auth/**, /public/**, /products (GET), /categories (GET)
 * - Protected endpoints: require valid JWT access token
 * - CORS enabled for React frontend (localhost:5173)
 * - CSRF disabled for APIs
 * - BCrypt password hashing
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize, @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configure HTTP security: endpoints, authentication, filters
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (stateless JWT doesn't need CSRF protection)
                .csrf(csrf -> csrf.disable())

                // Enable CORS with frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Set session policy to STATELESS (JWT-based)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure endpoint access
                .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/api/v1/auth/signup/**", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                    .requestMatchers("/public/**").permitAll()

                    .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/currency/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/shipping/quote").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/categories").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/terms").permitAll()

                    .requestMatchers("/health", "/health/**").permitAll()
                    .requestMatchers("/actuator", "/actuator/**").permitAll()

                    .anyRequest().authenticated()
                )

                // Exception handling for authentication failures
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAccessDeniedHandler())
                );

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Configuration for React frontend integration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",  // Vite dev server
                "http://localhost:3000",  // Alternative React dev port
                "http://localhost:8081"   // Local testing
                // Add production URL when deployed
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Password encoder bean (BCrypt with strength 12)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12 for security
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }
}
