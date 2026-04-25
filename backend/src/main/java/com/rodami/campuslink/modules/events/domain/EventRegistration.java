package com.rodami.campuslink.modules.events.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Inscription d'un utilisateur à un événement.
 * Correspond à la table 'event_registrations'.
 *
 * Clé unique : (event_id, user_id) — un utilisateur ne peut s'inscrire qu'une fois.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_registrations")
public class EventRegistration {

    @Id
    private Long id;

    @Column("event_id")
    private Long eventId;

    @Column("user_id")
    private Long userId;

    @CreatedDate
    @Column("registered_at")
    private Instant registeredAt;
}
