package com.rodami.campuslink.modules.matching.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Contexte académique de l'utilisateur — couche évolutive annuellement.
 * Correspond à la table 'profile_contexts'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("profile_contexts")
public class ProfileContext {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    /** Filière académique (ex : Informatique, Génie Civil…) */
    private String filiere;

    /** Année d'étude (1 à 8) */
    private Short annee;

    /** ETUDIANT | ENSEIGNANT | ADMINISTRATION */
    private String statut;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
