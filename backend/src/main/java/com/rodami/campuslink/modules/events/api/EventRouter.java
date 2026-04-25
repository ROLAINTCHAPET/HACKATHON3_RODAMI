package com.rodami.campuslink.modules.events.api;

import com.rodami.campuslink.modules.events.domain.EventCategory;
import com.rodami.campuslink.modules.events.dto.EventRequest;
import com.rodami.campuslink.modules.events.dto.EventResponse;
import com.rodami.campuslink.modules.events.dto.RegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Routeur WebFlux fonctionnel — Module Gestion des Événements.
 * Les annotations @RouterOperations permettent à springdoc de générer la Swagger UI.
 */
@Configuration
public class EventRouter {

    @Bean
    @RouterOperations({
        // ---- Lecture publique ----
        @RouterOperation(
            path = "/api/events", method = RequestMethod.GET,
            beanClass = EventHandler.class, beanMethod = "getAllEvents",
            operation = @Operation(
                operationId = "getAllEvents", tags = {"📅 Événements"},
                summary = "Lister les événements publiés",
                description = """
                    Retourne tous les événements publiés, triés par **priorité BDE** (décroissant) puis par date.
                    
                    **Filtres disponibles (query params) :**
                    - `?keyword=machine-learning` → recherche dans le titre et la description
                    - `?categoryId=3` → filtre par catégorie
                    - `?upcoming` → uniquement les événements à venir
                    """,
                parameters = {
                    @Parameter(name = "keyword",    in = ParameterIn.QUERY, required = false, description = "Mot-clé de recherche"),
                    @Parameter(name = "categoryId", in = ParameterIn.QUERY, required = false, description = "ID de catégorie"),
                    @Parameter(name = "upcoming",   in = ParameterIn.QUERY, required = false, description = "Événements à venir uniquement")
                },
                responses = @ApiResponse(responseCode = "200", description = "Liste des événements",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventResponse.class))))
            )
        ),
        @RouterOperation(
            path = "/api/events/{id}", method = RequestMethod.GET,
            beanClass = EventHandler.class, beanMethod = "getEvent",
            operation = @Operation(
                operationId = "getEvent", tags = {"📅 Événements"},
                summary = "Détail d'un événement",
                description = "Retourne le détail enrichi d'un événement (catégorie, organisateur, nombre de participants).",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "ID de l'événement"),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Événement trouvé",
                        content = @Content(schema = @Schema(implementation = EventResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Événement introuvable")
                }
            )
        ),
        @RouterOperation(
            path = "/api/categories", method = RequestMethod.GET,
            beanClass = EventHandler.class, beanMethod = "getCategories",
            operation = @Operation(
                operationId = "getCategories", tags = {"📅 Événements"},
                summary = "Lister les catégories",
                description = "Retourne toutes les catégories d'événements triées par priorité BDE décroissante.",
                responses = @ApiResponse(responseCode = "200", description = "Liste des catégories",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventCategory.class))))
            )
        ),
        // ---- Gestion BDE/Admin ----
        @RouterOperation(
            path = "/api/events", method = RequestMethod.POST,
            beanClass = EventHandler.class, beanMethod = "createEvent",
            operation = @Operation(
                operationId = "createEvent", tags = {"📅 Événements"},
                summary = "Créer un événement 🔒 BDE/Admin",
                description = """
                    Crée un nouvel événement en statut **DRAFT**.
                    L'événement doit ensuite être publié via `PATCH /api/events/{id}/publish`.
                    
                    > Nécessite le rôle `BDE` ou `ADMIN`.
                    """,
                requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = EventRequest.class))),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Événement créé (DRAFT)",
                        content = @Content(schema = @Schema(implementation = EventResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Données invalides ou catégorie introuvable"),
                    @ApiResponse(responseCode = "403", description = "Rôle insuffisant")
                }
            )
        ),
        @RouterOperation(
            path = "/api/events/{id}", method = RequestMethod.PUT,
            beanClass = EventHandler.class, beanMethod = "updateEvent",
            operation = @Operation(
                operationId = "updateEvent", tags = {"📅 Événements"},
                summary = "Modifier un événement 🔒 BDE/Admin",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = EventRequest.class))),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Événement mis à jour",
                        content = @Content(schema = @Schema(implementation = EventResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Événement introuvable")
                }
            )
        ),
        @RouterOperation(
            path = "/api/events/{id}/publish", method = RequestMethod.PATCH,
            beanClass = EventHandler.class, beanMethod = "publishEvent",
            operation = @Operation(
                operationId = "publishEvent", tags = {"📅 Événements"},
                summary = "Publier un événement 🔒 BDE/Admin",
                description = "Passe le statut de DRAFT à **PUBLISHED**. L'événement devient visible dans le flux.",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Événement publié",
                        content = @Content(schema = @Schema(implementation = EventResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Événement introuvable")
                }
            )
        ),
        @RouterOperation(
            path = "/api/events/{id}/cancel", method = RequestMethod.PATCH,
            beanClass = EventHandler.class, beanMethod = "cancelEvent",
            operation = @Operation(
                operationId = "cancelEvent", tags = {"📅 Événements"},
                summary = "Annuler un événement 🔒 BDE/Admin",
                description = "Passe le statut à **CANCELLED**. L'événement disparaît du flux public.",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = @ApiResponse(responseCode = "200", description = "Événement annulé",
                    content = @Content(schema = @Schema(implementation = EventResponse.class)))
            )
        ),
        @RouterOperation(
            path = "/api/events/{id}", method = RequestMethod.DELETE,
            beanClass = EventHandler.class, beanMethod = "deleteEvent",
            operation = @Operation(
                operationId = "deleteEvent", tags = {"📅 Événements"},
                summary = "Supprimer un événement 🔒 BDE/Admin",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = {
                    @ApiResponse(responseCode = "204", description = "Événement supprimé"),
                    @ApiResponse(responseCode = "404", description = "Événement introuvable")
                }
            )
        ),
        // ---- Inscriptions ----
        @RouterOperation(
            path = "/api/events/{id}/register", method = RequestMethod.POST,
            beanClass = EventHandler.class, beanMethod = "register",
            operation = @Operation(
                operationId = "register", tags = {"📋 Inscriptions"},
                summary = "S'inscrire à un événement",
                description = """
                    Inscrit l'utilisateur connecté à l'événement.
                    
                    **Validations :**
                    - L'événement doit être en statut PUBLISHED
                    - L'utilisateur ne doit pas déjà être inscrit
                    - La capacité max ne doit pas être atteinte (si définie)
                    """,
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "ID de l'événement"),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Inscription créée",
                        content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Déjà inscrit, événement complet ou non publié")
                }
            )
        ),
        @RouterOperation(
            path = "/api/events/{id}/register", method = RequestMethod.DELETE,
            beanClass = EventHandler.class, beanMethod = "unregister",
            operation = @Operation(
                operationId = "unregister", tags = {"📋 Inscriptions"},
                summary = "Se désinscrire d'un événement",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = {
                    @ApiResponse(responseCode = "204", description = "Désinscription effectuée"),
                    @ApiResponse(responseCode = "400", description = "Utilisateur non inscrit")
                }
            )
        ),
        @RouterOperation(
            path = "/api/events/{id}/participants", method = RequestMethod.GET,
            beanClass = EventHandler.class, beanMethod = "getParticipants",
            operation = @Operation(
                operationId = "getParticipants", tags = {"📋 Inscriptions"},
                summary = "Liste des participants à un événement",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = @ApiResponse(responseCode = "200", description = "Liste des inscrits",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegistrationResponse.class))))
            )
        ),
        @RouterOperation(
            path = "/api/events/{id}/co-participants", method = RequestMethod.GET,
            beanClass = EventHandler.class, beanMethod = "getCoParticipants",
            operation = @Operation(
                operationId = "getCoParticipants", tags = {"📋 Inscriptions"},
                summary = "Co-participants d'un événement (pont Matching)",
                description = """
                    Retourne les IDs des autres utilisateurs inscrits au même événement.
                    
                    **Usage** : permet au module Matching de suggérer des connexions entre participants 
                    d'un même événement (via `sourceEventId` dans `ConnectionRequest`).
                    """,
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = @ApiResponse(responseCode = "200", description = "Liste des IDs co-participants",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "integer", format = "int64"))))
            )
        ),
        @RouterOperation(
            path = "/api/users/{id}/registrations", method = RequestMethod.GET,
            beanClass = EventHandler.class, beanMethod = "getUserRegistrations",
            operation = @Operation(
                operationId = "getUserRegistrations", tags = {"📋 Inscriptions"},
                summary = "Événements d'un utilisateur",
                description = "Retourne tous les événements auxquels un utilisateur est inscrit.",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "ID de l'utilisateur"),
                responses = @ApiResponse(responseCode = "200", description = "Liste des inscriptions",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RegistrationResponse.class))))
            )
        )
    })
    public RouterFunction<ServerResponse> eventRoutes(EventHandler handler) {
        return RouterFunctions.route()
            .GET( "/api/events",                         handler::getAllEvents)
            .GET( "/api/events/{id}",                    handler::getEvent)
            .GET( "/api/categories",                     handler::getCategories)
            .POST(  "/api/events",                       handler::createEvent)
            .PUT(   "/api/events/{id}",                  handler::updateEvent)
            .PATCH( "/api/events/{id}/publish",          handler::publishEvent)
            .PATCH( "/api/events/{id}/cancel",           handler::cancelEvent)
            .DELETE("/api/events/{id}",                  handler::deleteEvent)
            .POST(  "/api/events/{id}/register",         handler::register)
            .DELETE("/api/events/{id}/register",         handler::unregister)
            .GET(   "/api/events/{id}/participants",     handler::getParticipants)
            .GET(   "/api/events/{id}/co-participants",  handler::getCoParticipants)
            .GET(   "/api/users/{id}/registrations",     handler::getUserRegistrations)
            .build();
    }
}
