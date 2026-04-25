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

    /**
     * Trouve les tags les plus populaires sur tout le campus.
     * Utilisé par le ColdStartService (TWIST 01) pour recommander
     * du contenu pertinent à un nouvel utilisateur sans historique.
     */
    @Query("SELECT tag, COUNT(*) as cnt FROM interests GROUP BY tag ORDER BY cnt DESC LIMIT :limit")
    Flux<Interest> findPopularTags(int limit);
}
