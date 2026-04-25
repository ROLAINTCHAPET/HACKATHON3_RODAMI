package com.rodami.campuslink.profile.dto;

/**
 * Réponse d'authentification contenant le JWT et les infos essentielles.
 */
public record AuthResponse(
    String token,
    Long userId,
    String displayName,
    String role,
    boolean profileComplete
) {}
