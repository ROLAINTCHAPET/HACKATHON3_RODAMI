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
 * Catégorie d'événement — niveaux de priorité définis par le BDE.
 * Correspond à la table 'event_categories'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_categories")
public class EventCategory {

    @Id
    private Long id;

    /** Nom de la catégorie : Sport, Culture, Académique, Social, Informatique… */
    private String nom;

    /**
     * Priorité de diffusion définie par le BDE (plus élevé = plus visible dans le flux).
     * Règle de gouvernance ajustable par le BDE, dans le cadre fixé par l'Admin.
     */
    private Short priorite;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;
}
