package com.rodami.campuslink.modules.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** Vue complète d'un événement retournée par l'API */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String titre;
    private String description;
    private Instant dateDebut;
    private Instant dateFin;
    private String lieu;

    /** Catégorie avec sa priorité BDE */
    private Long categoryId;
    private String categoryNom;
    private Short categoryPriorite;

    private Long organisateurId;
    private String organisateurNom;

    private String status;
    private Integer maxParticipants;
    private java.util.UUID shareToken; // TWIST 08 : Partage public

    /** Nombre de participants inscrits (calculé dynamiquement) */
    private Long participantCount;

    private Instant createdAt;
    private Instant updatedAt;
}
