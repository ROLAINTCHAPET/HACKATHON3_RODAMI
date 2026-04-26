package com.rodami.campuslink.profile.service;

import java.time.Instant;

import com.rodami.campuslink.profile.dto.AuthRequest;
import com.rodami.campuslink.profile.dto.AuthResponse;
import com.rodami.campuslink.profile.dto.RegisterRequest;
import com.rodami.campuslink.profile.entity.User;
import com.rodami.campuslink.profile.repository.InterestRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import com.rodami.campuslink.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service d'authentification — JWT maison (jjwt).
 * Gère register et login avec génération de tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inscription — crée un COMPTE uniquement.
     */
    public Mono<AuthResponse> register(RegisterRequest request) {
        return userRepository.existsByEmail(request.email())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new IllegalArgumentException("Cet email est déjà utilisé"));
                }
                User user = User.builder()
                    .nom(request.nom())
                    .prenom(request.prenom())
                    .email(request.email())
                    .passwordHash(passwordEncoder.encode(request.password()))
                    .role("USER")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
                return userRepository.save(user).flatMap(this::buildAuthResponse);
            });
    }

    /**
     * Login — vérifie les credentials et retourne un JWT.
     */
    public Mono<AuthResponse> login(AuthRequest request) {
        return userRepository.findByEmail(request.email())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Email ou mot de passe incorrect")))
            .flatMap(user -> {
                if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                    return Mono.error(new IllegalArgumentException("Email ou mot de passe incorrect"));
                }
                return buildAuthResponse(user);
            });
    }

    /**
     * Construit la réponse d'auth avec JWT et indicateur de complétion de profil.
     */
    public Mono<AuthResponse> buildAuthResponse(User user) {
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        return interestRepository.countByUserId(user.getId())
            .defaultIfEmpty(0L)
            .map(interestCount -> new AuthResponse(
                token,
                user.getId(),
                user.getDisplayName(),
                user.getRole() != null ? user.getRole() : "USER",
                interestCount >= 3  // profil considéré complet si 3+ intérêts
            ));
    }
}
