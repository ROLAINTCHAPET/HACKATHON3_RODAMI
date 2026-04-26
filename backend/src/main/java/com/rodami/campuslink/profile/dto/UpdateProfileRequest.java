package com.rodami.campuslink.profile.dto;

import java.util.List;

/**
 * Mise à jour partielle du profil.
 * RF-04 : enrichissement progressif par l'usage (pas de formulaire obligatoire).
 * Tous les champs sont optionnels — on ne met à jour que ce qui est fourni.
 */
public record UpdateProfileRequest(
    String filiere,
    Short annee,
    String statut,
    List<String> interestsToAdd,
    List<String> interestsToRemove
) {}
