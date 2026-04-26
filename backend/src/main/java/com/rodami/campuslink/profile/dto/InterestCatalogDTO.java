package com.rodami.campuslink.profile.dto;

/**
 * DTO pour un tag du catalogue d'intérêts.
 * Utilisé pour alimenter la grille de sélection lors de l'onboarding.
 */
public record InterestCatalogDTO(
    Long id,
    String tag,
    String category,
    String emoji
) {}
