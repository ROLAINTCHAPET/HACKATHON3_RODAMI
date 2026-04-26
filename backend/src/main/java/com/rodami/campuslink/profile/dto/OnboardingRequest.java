package com.rodami.campuslink.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Requête d'onboarding — inscription + profil en un seul flow.
 * RF-01 : Création de profil en moins de 2 minutes.
 * Seuls les intérêts sont obligatoires (3 minimum).
 */
public record OnboardingRequest(
    @NotBlank(message = "Le nom est requis")
    String nom,

    @NotBlank(message = "Le prénom est requis")
    String prenom,

    @NotBlank(message = "L'email est requis")
    @Email(message = "Email invalide")
    String email,

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    String password,

    @NotNull(message = "Les intérêts sont requis")
    @Size(min = 3, message = "Minimum 3 intérêts requis")
    List<@NotBlank String> interests,

    // Optionnels — couche 2 (académique)
    String filiere,
    Short annee
) {}
