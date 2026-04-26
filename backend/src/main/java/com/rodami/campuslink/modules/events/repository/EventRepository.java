package com.rodami.campuslink.modules.events.repository;

import com.rodami.campuslink.modules.events.domain.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, Long> {

    /** Tous les événements publiés, triés par priorité de catégorie puis par date */
    @Query("""
        SELECT e.* FROM events e
        LEFT JOIN event_categories ec ON e.category_id = ec.id
        WHERE e.status = 'PUBLISHED' AND e.date_debut >= :now
        ORDER BY ec.priorite DESC NULLS LAST, e.date_debut ASC
        """)
    Flux<Event> findAllPublished(Instant now);

    /** Événements publiés d'une catégorie */
    @Query("""
        SELECT * FROM events
        WHERE status = 'PUBLISHED' AND category_id = :categoryId AND date_debut >= :now
        ORDER BY date_debut ASC
        """)
    Flux<Event> findByCategoryId(Long categoryId, Instant now);

    /** Événements à venir (date_debut >= maintenant) */
    @Query("""
        SELECT e.* FROM events e
        LEFT JOIN event_categories ec ON e.category_id = ec.id
        WHERE e.status = 'PUBLISHED' AND e.date_debut >= :now
        ORDER BY ec.priorite DESC NULLS LAST, e.date_debut ASC
        """)
    Flux<Event> findUpcoming(Instant now);

    /** Événements créés par un organisateur */
    Flux<Event> findByOrganisateurId(Long organisateurId);

    /** Événements par statut */
    Flux<Event> findByStatus(String status);

    /**
     * Passage automatique en PAST des événements terminés.
     * Appelé par un scheduler ou à la demande.
     */
    @Query("""
        UPDATE events SET status = 'PAST', updated_at = NOW()
        WHERE status = 'PUBLISHED'
          AND date_debut < :now
        RETURNING *
        """)
    Flux<Event> markPastEvents(Instant now);

    /** Recherche par mot-clé (titre ou description) */
    @Query("""
        SELECT * FROM events
        WHERE status = 'PUBLISHED'
          AND (LOWER(titre) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY date_debut ASC
        """)
    Flux<Event> searchByKeyword(String keyword);

    /** Nombre de participants inscrits à un événement */
    @Query("SELECT COUNT(*) FROM event_registrations WHERE event_id = :eventId")
    Mono<Long> countParticipants(Long eventId);
}
