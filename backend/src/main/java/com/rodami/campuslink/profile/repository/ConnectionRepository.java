package com.rodami.campuslink.profile.repository;

import com.rodami.campuslink.profile.dto.ConnectionHistoryDTO;
import com.rodami.campuslink.profile.entity.Connection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository unifié pour les connexions entre utilisateurs.
 * Colonnes DB : requester_id / receiver_id (schéma unifié).
 */
public interface ConnectionRepository extends ReactiveCrudRepository<Connection, Long> {

    // ---- Module Profile (historique) ----

    @Query("SELECT " +
           "  CASE WHEN c.requester_id = :userId THEN c.receiver_id ELSE c.requester_id END as otherUserId, " +
           "  u.prenom || ' ' || u.nom as otherUserName, " +
           "  c.updated_at as connectedAt, " +
           "  c.status " +
           "FROM connections c " +
           "JOIN users u ON u.id = (CASE WHEN c.requester_id = :userId THEN c.receiver_id ELSE c.requester_id END) " +
           "WHERE (c.requester_id = :userId OR c.receiver_id = :userId) AND c.status = 'ACCEPTED' " +
           "ORDER BY c.updated_at DESC LIMIT 5")
    Flux<ConnectionHistoryDTO> findRecentConnectionsByUserId(Long userId);

    Mono<Long> countBySourceEventIdAndStatus(Long sourceEventId, String status);

    // ---- Module Matching (connexions CRUD) ----

    /** Toutes les connexions acceptées d'un utilisateur */
    @Query("""
        SELECT * FROM connections
        WHERE (requester_id = :userId OR receiver_id = :userId)
          AND status = 'ACCEPTED'
        ORDER BY updated_at DESC
        """)
    Flux<Connection> findAcceptedConnectionsByUserId(Long userId);

    /** Toutes les connexions (tous statuts) d'un utilisateur */
    @Query("""
        SELECT * FROM connections
        WHERE requester_id = :userId OR receiver_id = :userId
        ORDER BY created_at DESC
        """)
    Flux<Connection> findAllConnectionsByUserId(Long userId);

    /** Demandes de connexion en attente reçues par un utilisateur */
    @Query("""
        SELECT * FROM connections
        WHERE receiver_id = :userId AND status = 'PENDING'
        ORDER BY created_at DESC
        """)
    Flux<Connection> findPendingRequestsForUser(Long userId);

    /** Vérifie si une connexion existe déjà entre deux utilisateurs */
    @Query("""
        SELECT COUNT(*) > 0 FROM connections
        WHERE (requester_id = :u1 AND receiver_id = :u2)
           OR (requester_id = :u2 AND receiver_id = :u1)
        """)
    Mono<Boolean> existsBetweenUsers(Long u1, Long u2);

    /** Connexion entre deux utilisateurs précis */
    @Query("""
        SELECT * FROM connections
        WHERE (requester_id = :u1 AND receiver_id = :u2)
           OR (requester_id = :u2 AND receiver_id = :u1)
        LIMIT 1
        """)
    Mono<Connection> findBetweenUsers(Long u1, Long u2);

    /** Connexions provenant d'un événement — mesure d'impact */
    @Query("SELECT * FROM connections WHERE source_event_id = :eventId")
    Flux<Connection> findBySourceEventId(Long eventId);

    @Query("""
        SELECT COUNT(*) FROM connections
        WHERE (requester_id = :userId OR receiver_id = :userId)
          AND status = 'ACCEPTED'
        """)
    Mono<Long> countAcceptedConnectionsByUserId(Long userId);

    /** TWIST 09 : Mise à jour du score de réalité */
    @Query("""
        UPDATE connections 
        SET reality_score = LEAST(1.0, :score), 
            interaction_count = interaction_count + 1, 
            updated_at = NOW() 
        WHERE (requester_id = :u1 AND receiver_id = :u2) 
           OR (requester_id = :u2 AND receiver_id = :u1)
        """)
    Mono<Void> updateRealityScore(Long u1, Long u2, Double score);

    @Query("""
        UPDATE connections 
        SET reality_score = reality_score * 0.5,
            updated_at = NOW()
        WHERE source_event_id = :eventId
        """)
    Mono<Void> penalizeConnectionsForEvent(Long eventId);

    Mono<Long> countByRealityScoreGreaterThan(double score);

    Mono<Long> countBySourceEventIdIsNotNull();
}
