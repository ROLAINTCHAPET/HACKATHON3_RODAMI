package com.rodami.campuslink.profile.repository;

import com.rodami.campuslink.profile.dto.EventHistoryDTO;
import com.rodami.campuslink.profile.entity.EventRegistration;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository unifié pour les inscriptions aux événements.
 * Sert à la fois le module Profile (historique) et le module Events (CRUD inscriptions).
 */
public interface EventRegistrationRepository extends ReactiveCrudRepository<EventRegistration, Long> {

    // ---- Module Profile (historique RF-05) ----

    @Query("SELECT er.event_id as eventId, e.titre as title, er.registered_at as registeredAt, e.status " +
           "FROM event_registrations er " +
           "JOIN events e ON er.event_id = e.id " +
           "WHERE er.user_id = :userId " +
           "ORDER BY er.registered_at DESC LIMIT 5")
    Flux<EventHistoryDTO> findRecentHistoryByUserId(Long userId);

    Mono<Long> countByEventId(Long eventId);

    // ---- Module Events (inscriptions CRUD) ----

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
