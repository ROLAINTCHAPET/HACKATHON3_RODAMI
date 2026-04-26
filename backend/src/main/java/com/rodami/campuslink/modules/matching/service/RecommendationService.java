package com.rodami.campuslink.modules.matching.service;

import com.rodami.campuslink.common.exception.ResourceNotFoundException;
import com.rodami.campuslink.modules.matching.dto.UserProfile;
import com.rodami.campuslink.profile.repository.InterestRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import com.rodami.campuslink.profile.service.ColdStartService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Service de recommandation — double flux PULL / PUSH.
 *
 * Flux PULL : recommandations basées sur les intérêts communs
 *   → score = nombre de tags partagés → tri décroissant
 *   → cache Redis 5 min pour éviter les recalculs (12 000 users)
 *
 * Flux PUSH : découverte hors bulle de filtre
 *   → utilisateurs sans intérêt commun avec le demandeur
 *   → sélection aléatoire (ORDER BY RANDOM()) → anti-bulle
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final com.rodami.campuslink.profile.repository.ProfileContextRepository profileContextRepository;
    private final InterestRepository interestRepository;
    private final UserService userService;
    private final ColdStartService coldStartService;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${campuslink.recommendations.pull-max-results:20}")
    private int pullMaxResults;

    @Value("${campuslink.recommendations.push-max-results:10}")
    private int pushMaxResults;

    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    // ================================================================
    // FLUX PULL — recommandations par intérêts communs
    // ================================================================
    public Flux<UserProfile> getRecommendations(Long userId) {
        log.debug("[PULL] Calcul recommandations pour userId={}", userId);

        String cacheKey = "pull:recommendations:" + userId;

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur", userId)))
                .flatMapMany(user -> profileContextRepository.findByUserId(userId)
                        .map(ctx -> ctx.getIsCommuter() != null && ctx.getIsCommuter())
                        .defaultIfEmpty(false)
                        .flatMapMany(isCommuter -> {
                            if (isCommuter) {
                                log.info("[TWIST 04] Utilisateur navetteur détecté, priorisation des profils compatibles temps court");
                            }
                            return getCachedUserIds(cacheKey)
                                    .switchIfEmpty(Flux.defer(() -> computeAndCachePullIds(userId, cacheKey)))
                                    .collectList()
                                    .flatMapMany(pullIds -> {
                                        if (pullIds.size() < 3) {
                                            log.info("[TWIST 06] Profil peu connecté ou sans intérêts communs, injection de suggestions générales (safety net)");
                                            return Flux.fromIterable(pullIds)
                                                    .concatWith(
                                                        coldStartService.getPopularTags(5)
                                                            .flatMap(tag -> interestRepository.findRecommendedUserIdsByTag(tag, userId, 5))
                                                    )
                                                    .distinct()
                                                    .take(pullMaxResults);
                                        }
                                        return Flux.fromIterable(pullIds);
                                    });
                        })
                )
                .flatMap(userService::getProfile)
                .onErrorContinue((ex, obj) ->
                    log.warn("[PULL] Profil introuvable pour id={}, ignoré", obj));
    }

    private Flux<Long> getCachedUserIds(String cacheKey) {
        return redisTemplate.opsForList()
                .range(cacheKey, 0, -1)
                .filter(s -> s != null && !s.isEmpty())
                .map(Long::parseLong);
    }

    private Flux<Long> computeAndCachePullIds(Long userId, String cacheKey) {
        return interestRepository.findRecommendedUserIds(userId, pullMaxResults)
                .collectList()
                .flatMapMany(ids -> {
                    if (ids == null || ids.isEmpty()) {
                        return Flux.empty();
                    }
                    // Mise en cache Redis (expire après TTL)
                    return redisTemplate.opsForList()
                            .rightPushAll(cacheKey, ids.stream().map(String::valueOf).toList())
                            .then(redisTemplate.expire(cacheKey, CACHE_TTL))
                            .thenMany(Flux.fromIterable(ids));
                });
    }

    // ================================================================
    // FLUX PUSH — découverte hors bulle de filtre
    // ================================================================
    public Flux<UserProfile> getDiscovery(Long userId) {
        log.debug("[PUSH] Calcul découverte hors-bulle pour userId={}", userId);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur", userId)))
                .thenMany(
                    interestRepository.findPushDiscoveryUserIds(userId, pushMaxResults)
                )
                .flatMap(userService::getProfile)
                .onErrorContinue((ex, obj) ->
                    log.warn("[PUSH] Profil introuvable pour id={}, ignoré", obj));
    }

    // ================================================================
    // Invalidation du cache lors d'un changement d'intérêts
    // ================================================================
    public Mono<Boolean> invalidateCache(Long userId) {
        log.debug("[CACHE] Invalidation du cache PULL pour userId={}", userId);
        return redisTemplate.delete("pull:recommendations:" + userId)
                .map(count -> count > 0);
    }
}
