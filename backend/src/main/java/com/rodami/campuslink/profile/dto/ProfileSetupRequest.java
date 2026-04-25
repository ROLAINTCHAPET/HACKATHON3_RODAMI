package com.rodami.campuslink.profile.dto;

import java.util.List;

/**
 * Requête pour ajuster le profil après la création du compte.
 * Tous les champs sont optionnels pour respecter le TWIST 02.
 */
public record ProfileSetupRequest(
    List<String> interests,
    String filiere,
    Short annee
) {}
