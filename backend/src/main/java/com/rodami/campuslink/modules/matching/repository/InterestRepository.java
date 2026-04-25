package com.rodami.campuslink.modules.matching.repository;

import com.rodami.campuslink.modules.matching.domain.Interest;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface InterestRepository extends ReactiveCrudRepository<Interest, Long> {

    /** Tous les intérêts d'un utilisateur */
    Flux<Interest> findByUserId(Long userId);

    /** Tags d'un utilisateur (pour l'algorithme de matching) */
    @Query("SELECT tag FROM interests WHERE user_id = :userId")
    Flux<String> findTagsByUserId(Long userId);

    /**
     * Utilisateurs qui partagent au moins un tag avec l'utilisateur cible,
     * triés par nombre de tags communs (flux PULL).
     * Exclusion de l'utilisateur lui-même et de ses connexions existantes.
     */
    @Query("""
        SELECT i2.user_id, COUNT(*) AS common_count
        FROM interests i1
        JOIN interests i2 ON i1.tag = i2.tag
        WHERE i1.user_id = :userId
          AND i2.user_id <> :userId
          AND i2.user_id NOT IN (
              SELECT CASE WHEN user_id_1 = :userId THEN user_id_2 ELSE user_id_1 END
              FROM connections
              WHERE (user_id_1 = :userId OR user_id_2 = :userId)
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
              SELECT CASE WHEN user_id_1 = :userId THEN user_id_2 ELSE user_id_1 END
              FROM connections
              WHERE (user_id_1 = :userId OR user_id_2 = :userId)
          )
        ORDER BY RANDOM()
        LIMIT :maxResults
        """)
    Flux<Long> findPushDiscoveryUserIds(Long userId, int maxResults);

    /** Supprimer un intérêt spécifique d'un utilisateur */
    @Query("DELETE FROM interests WHERE user_id = :userId AND tag = :tag")
    Mono<Void> deleteByUserIdAndTag(Long userId, String tag);
}
