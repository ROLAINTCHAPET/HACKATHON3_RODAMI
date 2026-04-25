package com.rodami.campuslink.modules.matching.domain;

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
 * Lien durable entre deux utilisateurs — core du système de mise en relation.
 * Correspond à la table 'connections'.
 *
 * Statuts : PENDING → ACCEPTED | BLOCKED
 * sourceEventId permet de tracer l'événement qui a déclenché la connexion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("connections")
public class Connection {

    @Id
    private Long id;

    @Column("user_id_1")
    private Long userId1;

    @Column("user_id_2")
    private Long userId2;

    /** PENDING | ACCEPTED | BLOCKED */
    private String status;

    /**
     * Événement déclencheur de la connexion (nullable).
     * Permet de mesurer l'impact réel des événements sur le réseau social.
     */
    @Column("source_event_id")
    private Long sourceEventId;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
