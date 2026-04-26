package com.rodami.campuslink.profile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Couche 2 — Contexte académique.
 * Filière, année, statut — mis à jour chaque rentrée.
 * Tous les champs sont nullable (TWIST 02 : 60% des profils sont vides).
 */
@Table("profile_contexts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileContext {

    @Id
    private Long id;
    private Long userId;
    private String filiere;
    
    private Short annee;

    @Builder.Default
    private String statut = "ETUDIANT";
    
    @Column("is_commuter")
    private Boolean isCommuter; // Twist 04 : Navetteur (temps limité)
    
    private Instant updatedAt;

    /**
     * Retourne le statut avec fallback — TWIST 02.
     */
    public String getSafeStatut() {
        return statut != null ? statut : "ETUDIANT";
    }

    /**
     * Retourne une description lisible du contexte académique.
     * Null-safe — ne crash jamais.
     */
    public String getAcademicSummary() {
        if (filiere != null && annee != null) {
            return filiere + " — Année " + annee;
        }
        if (filiere != null) return filiere;
        if (annee != null) return "Année " + annee;
        return null; // Aucune info académique — le UI affichera un fallback
    }
}
