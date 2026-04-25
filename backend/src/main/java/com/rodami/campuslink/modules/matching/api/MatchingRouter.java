package com.rodami.campuslink.modules.matching.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Routeur WebFlux fonctionnel — Module Mise en Relation.
 *
 * Endpoints exposés :
 *
 *  === UTILISATEURS ===
 *  POST   /api/users                              → Créer un utilisateur
 *  GET    /api/users/{id}                         → Profil complet
 *  PUT    /api/users/{id}                         → Mettre à jour le profil
 *  GET    /api/users/{id}/interests               → Intérêts déclarés
 *  POST   /api/users/{id}/interests               → Ajouter un intérêt
 *  DELETE /api/users/{id}/interests/{tag}         → Supprimer un intérêt
 *
 *  === RECOMMANDATIONS ===
 *  GET    /api/users/{id}/recommendations         → Flux PULL (intérêts communs)
 *  GET    /api/users/{id}/discovery               → Flux PUSH (hors bulle)
 *
 *  === CONNEXIONS ===
 *  POST   /api/connections                        → Demande de connexion
 *  GET    /api/connections                        → Mes connexions ACCEPTED
 *  GET    /api/connections/pending                → Demandes en attente
 *  PUT    /api/connections/{id}/status?value=X    → Accepter/Bloquer
 *  DELETE /api/connections/{id}                   → Supprimer une connexion
 */
@Configuration
public class MatchingRouter {

    @Bean
    public RouterFunction<ServerResponse> matchingRoutes(MatchingHandler handler) {
        return RouterFunctions.route()
            // ----- Utilisateurs -----
            .POST("/api/users",                              handler::createUser)
            .GET( "/api/users/{id}",                         handler::getProfile)
            .PUT( "/api/users/{id}",                         handler::updateProfile)
            .GET( "/api/users/{id}/interests",               handler::getInterests)
            .POST("/api/users/{id}/interests",               handler::addInterest)
            .DELETE("/api/users/{id}/interests/{tag}",       handler::removeInterest)
            // ----- Recommandations -----
            .GET( "/api/users/{id}/recommendations",         handler::getRecommendations)
            .GET( "/api/users/{id}/discovery",               handler::getDiscovery)
            // ----- Connexions -----
            .POST(  "/api/connections",                      handler::createConnection)
            .GET(   "/api/connections",                      handler::getConnections)
            .GET(   "/api/connections/pending",              handler::getPendingRequests)
            .PUT(   "/api/connections/{id}/status",          handler::updateConnectionStatus)
            .DELETE("/api/connections/{id}",                 handler::deleteConnection)
            .build();
    }
}
