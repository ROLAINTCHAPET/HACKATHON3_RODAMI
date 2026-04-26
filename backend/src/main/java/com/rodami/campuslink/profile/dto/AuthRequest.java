package com.rodami.campuslink.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Requête de connexion (login).
 */
public record AuthRequest(
    @NotBlank(message = "L'email est requis")
    @Email(message = "Email invalide")
    String email,

    @NotBlank(message = "Le mot de passe est requis svp ")
    String password
) {}
