package com.rodami.campuslink.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeExchange(ex -> ex
                // Actuator public
                .pathMatchers("/actuator/**").permitAll()
                // Swagger UI & OpenAPI
                .pathMatchers("/webjars/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Auth publique (login + dev-token)
                .pathMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                // Onboarding public (inscription + profil en un flow)
                .pathMatchers(HttpMethod.POST, "/api/profiles/onboard").permitAll()
                // Inscription utilisateur (Module Matching)
                .pathMatchers(HttpMethod.POST, "/api/users").permitAll()
                // Catalogue d'intérêts public (nécessaire pour l'onboarding)
                .pathMatchers(HttpMethod.GET, "/api/interests/catalog").permitAll()
                // Lecture événements publique
                .pathMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/categories").permitAll()
                // Création/modification/publication événements — BDE ou Admin
                .pathMatchers(HttpMethod.POST,  "/api/events/**").hasAnyRole("BDE", "ADMIN")
                .pathMatchers(HttpMethod.PUT,   "/api/events/**").hasAnyRole("BDE", "ADMIN")
                .pathMatchers(HttpMethod.PATCH, "/api/events/**").hasAnyRole("BDE", "ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/api/events/**").hasAnyRole("BDE", "ADMIN")
                // Gouvernance — Admin ou BDE
                .pathMatchers("/api/governance/**").hasAnyRole("BDE", "ADMIN")
                // Mise en relation (Matching) — Authentifié
                .pathMatchers("/api/users/**", "/api/connections/**").authenticated()
                // Tout le reste authentifié
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
