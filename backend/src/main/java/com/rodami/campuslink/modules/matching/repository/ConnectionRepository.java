package com.rodami.campuslink.modules.matching.repository;

import com.rodami.campuslink.modules.matching.domain.Connection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConnectionRepository extends ReactiveCrudRepository<Connection, Long> {

    /** Toutes les connexions acceptées d'un utilisateur */
    @Query("""
        SELECT * FROM connections
        WHERE (user_id_1 = :userId OR user_id_2 = :userId)
          AND status = 'ACCEPTED'
        ORDER BY updated_at DESC
        """)
    Flux<Connection> findAcceptedConnectionsByUserId(Long userId);

    /** Toutes les connexions (tous statuts) d'un utilisateur */
    @Query("""
        SELECT * FROM connections
        WHERE user_id_1 = :userId OR user_id_2 = :userId
        ORDER BY created_at DESC
        """)
    Flux<Connection> findAllConnectionsByUserId(Long userId);

    /** Demandes de connexion en attente reçues par un utilisateur */
    @Query("""
        SELECT * FROM connections
        WHERE user_id_2 = :userId AND status = 'PENDING'
        ORDER BY created_at DESC
        """)
    Flux<Connection> findPendingRequestsForUser(Long userId);

    /** Vérifie si une connexion existe déjà entre deux utilisateurs */
    @Query("""
        SELECT COUNT(*) > 0 FROM connections
        WHERE (user_id_1 = :u1 AND user_id_2 = :u2)
           OR (user_id_1 = :u2 AND user_id_2 = :u1)
        """)
    Mono<Boolean> existsBetweenUsers(Long u1, Long u2);

    /** Connexion entre deux utilisateurs précis */
    @Query("""
        SELECT * FROM connections
        WHERE (user_id_1 = :u1 AND user_id_2 = :u2)
           OR (user_id_1 = :u2 AND user_id_2 = :u1)
        LIMIT 1
        """)
    Mono<Connection> findBetweenUsers(Long u1, Long u2);

    /** Connexions provenant d'un événement — mesure d'impact */
    @Query("SELECT * FROM connections WHERE source_event_id = :eventId")
    Flux<Connection> findBySourceEventId(Long eventId);

    /** Nombre de connexions acceptées d'un utilisateur */
    @Query("""
        SELECT COUNT(*) FROM connections
        WHERE (user_id_1 = :userId OR user_id_2 = :userId)
          AND status = 'ACCEPTED'
        """)
    Mono<Long> countAcceptedConnectionsByUserId(Long userId);
}
