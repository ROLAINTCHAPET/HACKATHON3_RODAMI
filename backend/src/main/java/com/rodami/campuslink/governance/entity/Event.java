package com.rodami.campuslink.governance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Entité simplifiée pour les événements (utilisée par la gouvernance).
 */
@Table("events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    private Long id;

    private String titre;
    private String description;

    @Column("date_debut")
    private Instant dateDebut;

    @Column("category_id")
    private Long categoryId;

    @Column("organisateur_id")
    private Long organisateurId;

    @Column("association_id")
    private Long associationId;

    private String status;
}
