package com.muic.ssc.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Public API endpoints
                        .requestMatchers("/api/images/generate").permitAll()
                        .requestMatchers("/api/images/all").permitAll()
                        .requestMatchers("/api/likes/*/count").permitAll()

                        // Protected API endpoints
                        .requestMatchers("/api/images/save").authenticated()
                        .requestMatchers("/api/images/upload").authenticated()
                        .requestMatchers("/api/images/user").authenticated()
                        .requestMatchers("/api/images/*/share").authenticated()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.DELETE, "/api/images/**")).authenticated()
                        .requestMatchers("/api/likes/*/toggle").authenticated()

                        // Allow OPTIONS requests for CORS preflight
                        .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.OPTIONS, "/**")).permitAll()

                        // For image/{id} endpoint, use custom authorization in the controller
                        .requestMatchers("/api/images/*").permitAll()

                        // Default access for any other endpoint
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Maintain session policy
                )
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(securityContextRepository) // Keep session-based authentication
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(403);
                            response.getWriter().write("{\"success\":false,\"message\":\"Authentication required\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository(); // Keep your session-based authentication
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",  // Vue local dev
                "https://dreamlab-ai.online", // Production domain
                "http://dreamlab-ai.online" // Allow HTTP version
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}