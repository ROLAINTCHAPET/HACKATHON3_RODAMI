package com.rodami.campuslink.modules.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/** Vue complète d'un profil utilisateur retournée par l'API */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String role;

    // Contexte académique
    private String filiere;
    private Short annee;
    private String statut;

    // Intérêts
    private List<String> interests;

    // Métriques sociales
    private Long connectionCount;

    private Instant createdAt;
}
