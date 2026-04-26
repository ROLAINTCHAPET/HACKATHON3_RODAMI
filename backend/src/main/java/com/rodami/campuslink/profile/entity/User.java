package com.rodami.campuslink.profile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Couche 1 — Identité stable.
 * Email institutionnel, nom, prénom — immuable.
 */
@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private Long id;
    private String firebaseUid;
    private String nom;
    private String prenom;
    private String email;
    private String passwordHash;
    @Builder.Default
    private String role = "USER";
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Nom d'affichage null-safe — TWIST 02 : ne jamais supposer qu'une donnée existe.
     * Fallback chain : prénom+nom → prénom → partie locale de l'email → "Utilisateur"
     */
    public String getDisplayName() {
        if (prenom != null && nom != null) return prenom + " " + nom;
        if (prenom != null) return prenom;
        if (email != null) return email.split("@")[0];
        return "Utilisateur";
    }
}
