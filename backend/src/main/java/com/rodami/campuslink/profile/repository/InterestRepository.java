package com.rodami.campuslink.profile.repository;

import com.rodami.campuslink.profile.entity.Interest;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface InterestRepository extends ReactiveCrudRepository<Interest, Long> {

    Flux<Interest> findByUserId(Long userId);

    Mono<Void> deleteByUserIdAndTag(Long userId, String tag);

    Mono<Long> countByUserId(Long userId);

    /** Tags d'un utilisateur (pour l'algorithme de matching) */
    @Query("SELECT tag FROM interests WHERE user_id = :userId")
    Flux<String> findTagsByUserId(Long userId);

    /**
     * Trouve les tags les plus populaires sur tout le campus.
     * Utilisé par le ColdStartService (TWIST 01) pour recommander
     * du contenu pertinent à un nouvel utilisateur sans historique.
     */
    @Query("SELECT tag, COUNT(*) as cnt FROM interests GROUP BY tag ORDER BY cnt DESC LIMIT :limit")
    Flux<Interest> findPopularTags(int limit);

    /**
     * Utilisateurs qui partagent au moins un tag avec l'utilisateur cible,
     * triés par nombre de tags communs (flux PULL).
     * Exclusion de l'utilisateur lui-même et de ses connexions existantes.
     * Colonnes : requester_id / receiver_id (cohérent avec le schéma unifié).
     */
    @Query("""
        SELECT i2.user_id, COUNT(*) AS common_count
        FROM interests i1
        JOIN interests i2 ON i1.tag = i2.tag
        WHERE i1.user_id = :userId
          AND i2.user_id <> :userId
          AND i2.user_id NOT IN (
              SELECT CASE WHEN requester_id = :userId THEN receiver_id ELSE requester_id END
              FROM connections
              WHERE (requester_id = :userId OR receiver_id = :userId)
                AND status = 'ACCEPTED'
          )
        GROUP BY i2.user_id
        ORDER BY common_count DESC
        LIMIT :maxResults
        """)
    Flux<Long> findRecommendedUserIds(Long userId, int maxResults);

    /**
     * Utilisateurs qui NE partagent AUCUN intérêt avec l'utilisateur cible
     * (flux PUSH — anti bulle de filtre).
     */
    @Query("""
        SELECT DISTINCT u.id
        FROM users u
        WHERE u.id <> :userId
          AND u.id NOT IN (
              SELECT i2.user_id FROM interests i1
              JOIN interests i2 ON i1.tag = i2.tag
              WHERE i1.user_id = :userId AND i2.user_id <> :userId
          )
          AND u.id NOT IN (
              SELECT CASE WHEN requester_id = :userId THEN receiver_id ELSE requester_id END
              FROM connections
              WHERE (requester_id = :userId OR receiver_id = :userId)
          )
        ORDER BY RANDOM()
        LIMIT :maxResults
        """)
    Flux<Long> findPushDiscoveryUserIds(Long userId, int maxResults);
}
