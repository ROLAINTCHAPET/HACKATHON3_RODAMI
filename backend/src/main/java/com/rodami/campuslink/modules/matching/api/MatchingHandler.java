package com.rodami.campuslink.modules.matching.api;

import com.rodami.campuslink.modules.matching.dto.ConnectionRequest;
import com.rodami.campuslink.modules.matching.dto.InterestRequest;
import com.rodami.campuslink.modules.matching.dto.UserRequest;
import com.rodami.campuslink.modules.matching.service.ConnectionService;
import com.rodami.campuslink.modules.matching.service.RecommendationService;
import com.rodami.campuslink.modules.matching.service.UserService;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

/**
 * Handler WebFlux fonctionnel pour le module Matching.
 * Chaque méthode correspond à un endpoint défini dans MatchingRouter.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingHandler {

    private final UserService userService;
    private final RecommendationService recommendationService;
    private final ConnectionService connectionService;
    private final UserRepository userRepository;

    // ================================================================
    // USERS
    // ================================================================

    /** GET /api/users/{id} — Profil complet */
    public Mono<ServerResponse> getProfile(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("id"));
        return userService.getProfile(userId)
                .flatMap(profile -> ok().bodyValue(profile))
                .onErrorResume(this::handleError);
    }

    /** POST /api/users — Créer un utilisateur */
    public Mono<ServerResponse> createUser(ServerRequest req) {
        return req.bodyToMono(UserRequest.class)
                .flatMap(userService::createUser)
                .flatMap(profile -> created(req.uri()).bodyValue(profile))
                .onErrorResume(this::handleError);
    }

    /** PUT /api/users/{id} — Mettre à jour le profil */
    public Mono<ServerResponse> updateProfile(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("id"));
        return req.bodyToMono(UserRequest.class)
                .flatMap(body -> userService.updateProfile(userId, body))
                .flatMap(profile -> ok().bodyValue(profile))
                .onErrorResume(this::handleError);
    }

    /** GET /api/users/{id}/interests — Intérêts d'un utilisateur */
    public Mono<ServerResponse> getInterests(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("id"));
        return ok().body(userService.getInterests(userId),
                com.rodami.campuslink.profile.entity.Interest.class)
                .onErrorResume(this::handleError);
    }

    /** POST /api/users/{id}/interests — Ajouter un intérêt */
    public Mono<ServerResponse> addInterest(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("id"));
        return req.bodyToMono(InterestRequest.class)
                .flatMap(ir -> userService.addInterest(userId, ir)
                        .then(recommendationService.invalidateCache(userId)))
                .then(ok().build())
                .onErrorResume(this::handleError);
    }

    /** DELETE /api/users/{id}/interests/{tag} — Supprimer un intérêt */
    public Mono<ServerResponse> removeInterest(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("id"));
        String tag = req.pathVariable("tag");
        return userService.removeInterest(userId, tag)
                .then(recommendationService.invalidateCache(userId))
                .then(noContent().build())
                .onErrorResume(this::handleError);
    }

    // ================================================================
    // RECOMMANDATIONS
    // ================================================================

    /**
     * GET /api/users/{id}/recommendations — Flux PULL
     * Retourne les utilisateurs avec le plus d'intérêts communs.
     */
    public Mono<ServerResponse> getRecommendations(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("id"));
        log.info("[PULL] Recommandations demandées pour userId={}", userId);
        return ok().body(
                recommendationService.getRecommendations(userId),
                com.rodami.campuslink.modules.matching.dto.UserProfile.class
        ).onErrorResume(this::handleError);
    }

    /**
     * GET /api/users/{id}/discovery — Flux PUSH
     * Retourne des utilisateurs hors de la bulle de filtre.
     */
    public Mono<ServerResponse> getDiscovery(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("id"));
        log.info("[PUSH] Découverte hors-bulle demandée pour userId={}", userId);
        return ok().body(
                recommendationService.getDiscovery(userId),
                com.rodami.campuslink.modules.matching.dto.UserProfile.class
        ).onErrorResume(this::handleError);
    }

    // ================================================================
    // CONNEXIONS
    // ================================================================

    /** POST /api/connections — Créer une demande de connexion */
    public Mono<ServerResponse> createConnection(ServerRequest req) {
        return getAuthenticatedUserId()
                .flatMap(requesterId -> req.bodyToMono(ConnectionRequest.class)
                        .flatMap(body -> connectionService.createConnection(requesterId, body)))
                .flatMap(conn -> created(req.uri()).bodyValue(conn))
                .onErrorResume(this::handleError);
    }

    /** GET /api/connections — Connexions de l'utilisateur connecté */
    public Mono<ServerResponse> getConnections(ServerRequest req) {
        return getAuthenticatedUserId()
                .flatMapMany(connectionService::getConnections)
                .collectList()
                .flatMap(list -> ok().bodyValue(list))
                .onErrorResume(this::handleError);
    }

    /** GET /api/connections/pending — Demandes en attente */
    public Mono<ServerResponse> getPendingRequests(ServerRequest req) {
        return getAuthenticatedUserId()
                .flatMapMany(connectionService::getPendingRequests)
                .collectList()
                .flatMap(list -> ok().bodyValue(list))
                .onErrorResume(this::handleError);
    }

    /** PUT /api/connections/{id}/status?value=ACCEPTED|BLOCKED */
    public Mono<ServerResponse> updateConnectionStatus(ServerRequest req) {
        Long connId = Long.parseLong(req.pathVariable("id"));
        String newStatus = req.queryParam("value").orElse("ACCEPTED");
        return getAuthenticatedUserId()
                .flatMap(uid -> connectionService.updateStatus(connId, uid, newStatus))
                .flatMap(conn -> ok().bodyValue(conn))
                .onErrorResume(this::handleError);
    }

    /** DELETE /api/connections/{id} — Supprimer une connexion */
    public Mono<ServerResponse> deleteConnection(ServerRequest req) {
        Long connId = Long.parseLong(req.pathVariable("id"));
        return getAuthenticatedUserId()
                .flatMap(uid -> connectionService.deleteConnection(connId, uid))
                .then(noContent().build())
                .onErrorResume(this::handleError);
    }

    // ================================================================
    // Utilitaires
    // ================================================================

    /**
     * Résout le userId à partir de l'email stocké dans le subject JWT.
     * Compatible avec le JwtUtil du Module 1 qui met l'email comme subject.
     */
    private Mono<Long> getAuthenticatedUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(emailOrId -> {
                    try {
                        return Mono.just(Long.parseLong(emailOrId));
                    } catch (NumberFormatException e) {
                        return userRepository.findByEmail(emailOrId)
                                .map(user -> user.getId())
                                .switchIfEmpty(Mono.just(-1L));
                    }
                })
                .onErrorReturn(-1L);
    }

    private Mono<ServerResponse> handleError(Throwable ex) {
        log.error("[Matching] Erreur : {}", ex.getMessage());
        if (ex instanceof com.rodami.campuslink.common.exception.ResourceNotFoundException) {
            return status(HttpStatus.NOT_FOUND)
                    .bodyValue(Map.of("error", ex.getMessage()));
        }
        if (ex instanceof IllegalArgumentException) {
            return badRequest().bodyValue(Map.of("error", ex.getMessage()));
        }
        return status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(Map.of("error", "Erreur interne"));
    }
}
