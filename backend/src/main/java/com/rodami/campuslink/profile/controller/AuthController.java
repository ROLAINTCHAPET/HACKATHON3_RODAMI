package com.rodami.campuslink.profile.controller;

import com.rodami.campuslink.profile.dto.AuthRequest;
import com.rodami.campuslink.profile.dto.AuthResponse;
import com.rodami.campuslink.profile.dto.RegisterRequest;
import com.rodami.campuslink.profile.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Contrôleur d'authentification — endpoints publics.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
