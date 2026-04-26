package com.rodami.campuslink.modules.events.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Événement créé par une association ou un acteur institutionnel.
 * Correspond à la table 'events'.
 *
 * Statuts : DRAFT → PUBLISHED → CANCELLED | PAST
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table("events")
public class Event {

    @Id
    private Long id;

    private String titre;

    private String description;

    @Column("date_debut")
    private Instant dateDebut;

    @Column("date_fin")
    private Instant dateFin;

    private String lieu;

    @Column("category_id")
    private Long categoryId;

    /** Utilisateur (BDE ou Admin) qui a créé l'événement */
    @Column("organisateur_id")
    private Long organisateurId;

    /**
     * Statut du cycle de vie de l'événement.
     * DRAFT → validé par BDE → PUBLISHED → (auto) PAST | CANCELLED
     */
    private String status;

    /** Capacité max de participants (null = illimitée) */
    @Column("max_participants")
    private Integer maxParticipants;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
