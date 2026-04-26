package com.rodami.campuslink.profile.service;

import com.rodami.campuslink.profile.entity.Interest;
import com.rodami.campuslink.profile.repository.InterestCatalogRepository;
import com.rodami.campuslink.profile.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Service de gestion du Cold Start (TWIST 01).
 *
 * Stratégie pour les nouveaux utilisateurs sans historique :
 * 1. Utilisateur avec intérêts → recommander les événements/utilisateurs ayant des intérêts communs
 * 2. Utilisateur sans rien → utiliser les intérêts les plus populaires du campus
 * 3. Cache Redis des intérêts populaires (TTL 1h) pour performance
 *
 * Le CDC dit : "Valeur visible dès la première connexion même sans aucune connexion sociale"
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ColdStartService {

    private final InterestRepository interestRepository;
    private final InterestCatalogRepository catalogRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    private static final String POPULAR_TAGS_KEY = "campuslink:popular_tags";
    private static final Duration POPULAR_TAGS_TTL = Duration.ofHours(1);
    private static final int DEFAULT_POPULAR_LIMIT = 10;

    /**
     * Récupère les tags les plus populaires sur le campus.
     * Utilise un cache Redis avec TTL de 1h pour éviter les requêtes répétitives
     * (12 000 utilisateurs = beaucoup de recalculs potentiels).
     */
    public Flux<String> getPopularTags(int limit) {
        // Essayer le cache Redis d'abord
        return redisTemplate.opsForList()
            .range(POPULAR_TAGS_KEY, 0, limit - 1)
            .switchIfEmpty(
                // Cache miss → calculer et cacher
                refreshPopularTags(limit)
            );
    }

    /**
     * Recalcule les tags populaires depuis la BDD et les met en cache Redis.
     */
    private Flux<String> refreshPopularTags(int limit) {
        return interestRepository.findPopularTags(limit)
            .map(Interest::getTag)
            .collectList()
            .flatMapMany(tags -> {
                if (tags.isEmpty()) {
                    // ✅ TWIST 02 : si aucun utilisateur n'a d'intérêts,
                    // utiliser le catalogue prédéfini comme fallback
                    log.info("Aucun intérêt populaire trouvé — fallback sur le catalogue");
                    return catalogRepository.findAllByOrderByDisplayOrderAsc()
                        .map(cat -> cat.getTag())
                        .take(limit);
                }

                // Mettre en cache Redis
                return redisTemplate.opsForList()
                    .rightPushAll(POPULAR_TAGS_KEY, tags)
                    .then(redisTemplate.expire(POPULAR_TAGS_KEY, POPULAR_TAGS_TTL))
                    .thenMany(Flux.fromIterable(tags));
            });
    }

    /**
     * Génère des suggestions de fallback pour un nouvel utilisateur.
     *
     * TWIST 01 : Suggérer du contenu pertinent même sans historique.
     *
     * Stratégie :
     * - Si l'utilisateur a des intérêts → utiliser ses intérêts
     * - Si l'utilisateur n'a aucun intérêt → utiliser les tags populaires
     * - Dans tous les cas, retourner quelque chose (jamais une liste vide)
     */
    public Mono<List<String>> getFallbackSuggestions(Long userId) {
        return interestRepository.findByUserId(userId)
            .collectList()
            .flatMap(userInterests -> {
                // ✅ TWIST 02 : vérifier si la liste est vide
                if (userInterests != null && !userInterests.isEmpty()) {
                    // L'utilisateur a des intérêts → les utiliser
                    List<String> tags = userInterests.stream()
                        .map(Interest::getTag)
                        .toList();
                    return Mono.just(tags);
                }

                // Aucun intérêt → fallback sur les tags populaires
                return getPopularTags(DEFAULT_POPULAR_LIMIT)
                    .collectList()
                    .map(popularTags -> {
                        // ✅ TWIST 02 : dernier fallback si vraiment rien
                        if (popularTags == null || popularTags.isEmpty()) {
                            return List.of("Sport", "Culture", "Tech", "Social", "Académique");
                        }
                        return popularTags;
                    });
            });
    }

    /**
     * Force le rafraîchissement du cache des tags populaires.
     * Peut être appelé par un scheduler ou manuellement.
     */
    public Mono<Void> invalidateCache() {
        return redisTemplate.delete(POPULAR_TAGS_KEY).then();
    }
}
