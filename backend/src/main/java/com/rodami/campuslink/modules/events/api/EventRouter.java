package com.rodami.campuslink.modules.events.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Routeur WebFlux fonctionnel — Module Gestion des Événements.
 *
 * Endpoints exposés :
 *
 *  === ÉVÉNEMENTS (lecture publique) ===
 *  GET    /api/events                          → Tous les publiés (filtrables: ?keyword=, ?categoryId=, ?upcoming)
 *  GET    /api/events/{id}                     → Détail d'un événement
 *  GET    /api/categories                      → Toutes les catégories (triées par priorité BDE)
 *
 *  === ÉVÉNEMENTS (BDE/Admin) ===
 *  POST   /api/events                          → Créer (status=DRAFT)
 *  PUT    /api/events/{id}                     → Modifier
 *  PATCH  /api/events/{id}/publish             → Publier (DRAFT→PUBLISHED)
 *  PATCH  /api/events/{id}/cancel              → Annuler (→CANCELLED)
 *  DELETE /api/events/{id}                     → Supprimer
 *
 *  === INSCRIPTIONS ===
 *  POST   /api/events/{id}/register            → S'inscrire
 *  DELETE /api/events/{id}/register            → Se désinscrire
 *  GET    /api/events/{id}/participants        → Liste des inscrits
 *  GET    /api/events/{id}/co-participants     → Co-participants (→ suggestions Matching)
 *  GET    /api/users/{id}/registrations        → Événements d'un utilisateur
 */
@Configuration
public class EventRouter {

    @Bean
    public RouterFunction<ServerResponse> eventRoutes(EventHandler handler) {
        return RouterFunctions.route()
            // ----- Lecture publique -----
            .GET( "/api/events",                         handler::getAllEvents)
            .GET( "/api/events/{id}",                    handler::getEvent)
            .GET( "/api/categories",                     handler::getCategories)
            // ----- Gestion BDE/Admin -----
            .POST(  "/api/events",                       handler::createEvent)
            .PUT(   "/api/events/{id}",                  handler::updateEvent)
            .PATCH( "/api/events/{id}/publish",          handler::publishEvent)
            .PATCH( "/api/events/{id}/cancel",           handler::cancelEvent)
            .DELETE("/api/events/{id}",                  handler::deleteEvent)
            // ----- Inscriptions -----
            .POST(  "/api/events/{id}/register",         handler::register)
            .DELETE("/api/events/{id}/register",         handler::unregister)
            .GET(   "/api/events/{id}/participants",     handler::getParticipants)
            .GET(   "/api/events/{id}/co-participants",  handler::getCoParticipants)
            .GET(   "/api/users/{id}/registrations",     handler::getUserRegistrations)
            .build();
    }
}
