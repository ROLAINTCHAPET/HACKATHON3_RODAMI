package com.rodami.campuslink.modules.events.repository;

import com.rodami.campuslink.modules.events.domain.EventRegistration;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EventRegistrationRepository extends ReactiveCrudRepository<EventRegistration, Long> {

    /** Toutes les inscriptions d'un événement */
    Flux<EventRegistration> findByEventId(Long eventId);

    /** Tous les événements auxquels un utilisateur est inscrit */
    Flux<EventRegistration> findByUserId(Long userId);

    /** Vérifie si un utilisateur est déjà inscrit à un événement */
    @Query("SELECT COUNT(*) > 0 FROM event_registrations WHERE event_id = :eventId AND user_id = :userId")
    Mono<Boolean> existsByEventIdAndUserId(Long eventId, Long userId);

    /** Supprimer l'inscription d'un utilisateur à un événement */
    @Query("DELETE FROM event_registrations WHERE event_id = :eventId AND user_id = :userId")
    Mono<Void> deleteByEventIdAndUserId(Long eventId, Long userId);

    /**
     * IDs des utilisateurs inscrits à un événement — utilisé pour la mise en relation
     * (participants d'un même événement = connexion potentielle).
     */
    @Query("SELECT user_id FROM event_registrations WHERE event_id = :eventId")
    Flux<Long> findUserIdsByEventId(Long eventId);
}
