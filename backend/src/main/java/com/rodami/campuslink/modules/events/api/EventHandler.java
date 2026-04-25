package com.rodami.campuslink.modules.events.api;

import com.rodami.campuslink.modules.events.dto.EventRequest;
import com.rodami.campuslink.modules.events.service.EventRegistrationService;
import com.rodami.campuslink.modules.events.service.EventService;
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
 * Handler WebFlux fonctionnel — Module Gestion des Événements.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final EventService eventService;
    private final EventRegistrationService registrationService;

    // ================================================================
    // EVENTS — Lecture (publique)
    // ================================================================

    /** GET /api/events — tous les événements publiés, triés par priorité BDE */
    public Mono<ServerResponse> getAllEvents(ServerRequest req) {
        // Filtres optionnels via query params
        if (req.queryParam("keyword").isPresent()) {
            return ok().body(
                    eventService.search(req.queryParam("keyword").get()),
                    com.rodami.campuslink.modules.events.dto.EventResponse.class
            );
        }
        if (req.queryParam("categoryId").isPresent()) {
            Long catId = Long.parseLong(req.queryParam("categoryId").get());
            return ok().body(eventService.getByCategory(catId),
                    com.rodami.campuslink.modules.events.dto.EventResponse.class);
        }
        if (req.queryParam("upcoming").isPresent()) {
            return ok().body(eventService.getUpcoming(),
                    com.rodami.campuslink.modules.events.dto.EventResponse.class);
        }
        return ok().body(eventService.getAllPublished(),
                com.rodami.campuslink.modules.events.dto.EventResponse.class);
    }

    /** GET /api/events/{id} — détail d'un événement */
    public Mono<ServerResponse> getEvent(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return eventService.getById(id)
                .flatMap(e -> ok().bodyValue(e))
                .onErrorResume(this::handleError);
    }

    /** GET /api/categories — toutes les catégories triées par priorité */
    public Mono<ServerResponse> getCategories(ServerRequest req) {
        return ok().body(eventService.getAllCategories(),
                com.rodami.campuslink.modules.events.domain.EventCategory.class);
    }

    // ================================================================
    // EVENTS — Création / Modification (BDE/Admin)
    // ================================================================

    /** POST /api/events — créer un événement (rôle BDE/Admin) */
    public Mono<ServerResponse> createEvent(ServerRequest req) {
        return getAuthenticatedUserId()
                .flatMap(uid -> req.bodyToMono(EventRequest.class)
                        .flatMap(body -> eventService.createEvent(uid, body)))
                .flatMap(event -> created(req.uri()).bodyValue(event))
                .onErrorResume(this::handleError);
    }

    /** PUT /api/events/{id} — modifier un événement */
    public Mono<ServerResponse> updateEvent(ServerRequest req) {
        Long eventId = Long.parseLong(req.pathVariable("id"));
        return getAuthenticatedUserId()
                .flatMap(uid -> req.bodyToMono(EventRequest.class)
                        .flatMap(body -> eventService.updateEvent(eventId, uid, body)))
                .flatMap(event -> ok().bodyValue(event))
                .onErrorResume(this::handleError);
    }

    /** PATCH /api/events/{id}/publish — publier un événement */
    public Mono<ServerResponse> publishEvent(ServerRequest req) {
        Long eventId = Long.parseLong(req.pathVariable("id"));
        return eventService.publish(eventId)
                .flatMap(e -> ok().bodyValue(e))
                .onErrorResume(this::handleError);
    }

    /** PATCH /api/events/{id}/cancel — annuler un événement */
    public Mono<ServerResponse> cancelEvent(ServerRequest req) {
        Long eventId = Long.parseLong(req.pathVariable("id"));
        return eventService.cancel(eventId)
                .flatMap(e -> ok().bodyValue(e))
                .onErrorResume(this::handleError);
    }

    /** DELETE /api/events/{id} — supprimer un événement */
    public Mono<ServerResponse> deleteEvent(ServerRequest req) {
        Long eventId = Long.parseLong(req.pathVariable("id"));
        return eventService.deleteEvent(eventId)
                .then(noContent().build())
                .onErrorResume(this::handleError);
    }

    // ================================================================
    // INSCRIPTIONS
    // ================================================================

    /** POST /api/events/{id}/register — s'inscrire à un événement */
    public Mono<ServerResponse> register(ServerRequest req) {
        Long eventId = Long.parseLong(req.pathVariable("id"));
        return getAuthenticatedUserId()
                .flatMap(uid -> registrationService.register(eventId, uid))
                .flatMap(reg -> status(HttpStatus.CREATED).bodyValue(reg))
                .onErrorResume(this::handleError);
    }

    /** DELETE /api/events/{id}/register — se désinscrire */
    public Mono<ServerResponse> unregister(ServerRequest req) {
        Long eventId = Long.parseLong(req.pathVariable("id"));
        return getAuthenticatedUserId()
                .flatMap(uid -> registrationService.unregister(eventId, uid))
                .then(noContent().build())
                .onErrorResume(this::handleError);
    }

    /** GET /api/events/{id}/participants — liste des inscrits */
    public Mono<ServerResponse> getParticipants(ServerRequest req) {
        Long eventId = Long.parseLong(req.pathVariable("id"));
        return ok().body(registrationService.getParticipants(eventId),
                com.rodami.campuslink.modules.events.dto.RegistrationResponse.class)
                .onErrorResume(this::handleError);
    }

    /** GET /api/users/{id}/registrations — événements d'un utilisateur */
    public Mono<ServerResponse> getUserRegistrations(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("id"));
        return ok().body(registrationService.getUserRegistrations(userId),
                com.rodami.campuslink.modules.events.dto.RegistrationResponse.class)
                .onErrorResume(this::handleError);
    }

    /**
     * GET /api/events/{id}/co-participants — co-participants (pour suggérer des connexions).
     * Intégration avec le module Matching.
     */
    public Mono<ServerResponse> getCoParticipants(ServerRequest req) {
        Long eventId = Long.parseLong(req.pathVariable("id"));
        return getAuthenticatedUserId()
                .flatMap(uid -> registrationService.getCoParticipantIds(eventId, uid).collectList())
                .flatMap(ids -> ok().bodyValue(ids))
                .onErrorResume(this::handleError);
    }

    // ================================================================
    // Utilitaires
    // ================================================================

    private Mono<Long> getAuthenticatedUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .map(Long::parseLong)
                .onErrorReturn(-1L);
    }

    private Mono<ServerResponse> handleError(Throwable ex) {
        log.error("[Events] Erreur : {}", ex.getMessage());
        if (ex instanceof com.rodami.campuslink.common.exception.ResourceNotFoundException) {
            return status(HttpStatus.NOT_FOUND).bodyValue(Map.of("error", ex.getMessage()));
        }
        if (ex instanceof IllegalArgumentException) {
            return badRequest().bodyValue(Map.of("error", ex.getMessage()));
        }
        return status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(Map.of("error", "Erreur interne"));
    }
}
