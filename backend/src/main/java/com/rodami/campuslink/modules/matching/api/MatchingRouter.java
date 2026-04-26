package com.rodami.campuslink.modules.matching.api;

import com.rodami.campuslink.profile.entity.Interest;
import com.rodami.campuslink.modules.matching.dto.ConnectionRequest;
import com.rodami.campuslink.modules.matching.dto.ConnectionResponse;
import com.rodami.campuslink.modules.matching.dto.InterestRequest;
import com.rodami.campuslink.modules.matching.dto.UserProfile;
import com.rodami.campuslink.modules.matching.dto.UserRequest;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Routeur WebFlux fonctionnel — Module Mise en Relation.
 * Les annotations @RouterOperations permettent à springdoc de générer la Swagger UI.
 */
@Configuration
public class MatchingRouter {

    @Bean
    @RouterOperations({
        // ---- Utilisateurs ----
        @RouterOperation(
            path = "/api/users", method = RequestMethod.POST,
            beanClass = MatchingHandler.class, beanMethod = "createUser",
            operation = @Operation(
                operationId = "createUser", tags = {"👤 Utilisateurs"},
                summary = "Créer un utilisateur",
                description = "Crée un nouvel utilisateur avec son contexte académique et ses intérêts déclarés. Accessible publiquement pour l'enregistrement initial.",
                requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UserRequest.class))),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Utilisateur créé",
                        content = @Content(schema = @Schema(implementation = UserProfile.class))),
                    @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides")
                }
            )
        ),
        @RouterOperation(
            path = "/api/users/{id}", method = RequestMethod.GET,
            beanClass = MatchingHandler.class, beanMethod = "getProfile",
            operation = @Operation(
                operationId = "getProfile", tags = {"👤 Utilisateurs"},
                summary = "Obtenir un profil complet",
                description = "Retourne le profil complet (User + contexte académique + intérêts + nb de connexions).",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "ID de l'utilisateur"),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Profil trouvé",
                        content = @Content(schema = @Schema(implementation = UserProfile.class))),
                    @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
                }
            )
        ),
        @RouterOperation(
            path = "/api/users/{id}", method = RequestMethod.PUT,
            beanClass = MatchingHandler.class, beanMethod = "updateProfile",
            operation = @Operation(
                operationId = "updateProfile", tags = {"👤 Utilisateurs"},
                summary = "Mettre à jour le profil",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UserRequest.class))),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Profil mis à jour",
                        content = @Content(schema = @Schema(implementation = UserProfile.class))),
                    @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
                }
            )
        ),
        // ---- Intérêts ----
        @RouterOperation(
            path = "/api/users/{id}/interests", method = RequestMethod.GET,
            beanClass = MatchingHandler.class, beanMethod = "getInterests",
            operation = @Operation(
                operationId = "getInterests", tags = {"🏷️ Intérêts"},
                summary = "Lister les intérêts d'un utilisateur",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = @ApiResponse(responseCode = "200", description = "Liste des intérêts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Interest.class))))
            )
        ),
        @RouterOperation(
            path = "/api/users/{id}/interests", method = RequestMethod.POST,
            beanClass = MatchingHandler.class, beanMethod = "addInterest",
            operation = @Operation(
                operationId = "addInterest", tags = {"🏷️ Intérêts"},
                summary = "Ajouter un intérêt",
                description = "Ajoute un intérêt et invalide le cache Redis de recommandations PULL.",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = InterestRequest.class))),
                responses = @ApiResponse(responseCode = "200", description = "Intérêt ajouté")
            )
        ),
        @RouterOperation(
            path = "/api/users/{id}/interests/{tag}", method = RequestMethod.DELETE,
            beanClass = MatchingHandler.class, beanMethod = "removeInterest",
            operation = @Operation(
                operationId = "removeInterest", tags = {"🏷️ Intérêts"},
                summary = "Supprimer un intérêt",
                parameters = {
                    @Parameter(name = "id",  in = ParameterIn.PATH, required = true),
                    @Parameter(name = "tag", in = ParameterIn.PATH, required = true, description = "Tag à supprimer (ex: football)")
                },
                responses = @ApiResponse(responseCode = "204", description = "Intérêt supprimé")
            )
        ),
        // ---- Recommandations ----
        @RouterOperation(
            path = "/api/users/{id}/recommendations", method = RequestMethod.GET,
            beanClass = MatchingHandler.class, beanMethod = "getRecommendations",
            operation = @Operation(
                operationId = "getRecommendations", tags = {"🎯 Recommandations"},
                summary = "Flux PULL — Recommandations par intérêts communs",
                description = """
                    Retourne les utilisateurs avec le plus d'intérêts communs.
                    
                    **Algorithme** : score = nombre de tags partagés → tri décroissant.
                    
                    **Cache** : résultat mis en cache Redis pendant 5 minutes.
                    Invalider via `POST /api/users/{id}/interests` ou `DELETE /api/users/{id}/interests/{tag}`.
                    """,
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = @ApiResponse(responseCode = "200", description = "Liste de profils recommandés (triés par score)",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserProfile.class))))
            )
        ),
        @RouterOperation(
            path = "/api/users/{id}/discovery", method = RequestMethod.GET,
            beanClass = MatchingHandler.class, beanMethod = "getDiscovery",
            operation = @Operation(
                operationId = "getDiscovery", tags = {"🎯 Recommandations"},
                summary = "Flux PUSH — Découverte hors bulle de filtre",
                description = """
                    Retourne des utilisateurs qui ne partagent **aucun intérêt** avec le demandeur.
                    
                    **Objectif** : prévenir la bulle de filtre — exposer l'utilisateur à d'autres univers.
                    
                    **Algorithme** : exclusion des tags communs + sélection aléatoire (`ORDER BY RANDOM()`).
                    """,
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = @ApiResponse(responseCode = "200", description = "Liste de profils hors bulle",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserProfile.class))))
            )
        ),
        // ---- Connexions ----
        @RouterOperation(
            path = "/api/connections", method = RequestMethod.POST,
            beanClass = MatchingHandler.class, beanMethod = "createConnection",
            operation = @Operation(
                operationId = "createConnection", tags = {"🤝 Connexions"},
                summary = "Envoyer une demande de connexion",
                description = "Crée une connexion en statut PENDING. L'utilisateur cible peut l'accepter (ACCEPTED) ou la bloquer (BLOCKED).",
                requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ConnectionRequest.class))),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Demande de connexion créée",
                        content = @Content(schema = @Schema(implementation = ConnectionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Connexion déjà existante ou auto-connexion")
                }
            )
        ),
        @RouterOperation(
            path = "/api/connections", method = RequestMethod.GET,
            beanClass = MatchingHandler.class, beanMethod = "getConnections",
            operation = @Operation(
                operationId = "getConnections", tags = {"🤝 Connexions"},
                summary = "Mes connexions acceptées",
                description = "Retourne toutes les connexions de statut ACCEPTED de l'utilisateur connecté.",
                responses = @ApiResponse(responseCode = "200", description = "Liste des connexions",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConnectionResponse.class))))
            )
        ),
        @RouterOperation(
            path = "/api/connections/pending", method = RequestMethod.GET,
            beanClass = MatchingHandler.class, beanMethod = "getPendingRequests",
            operation = @Operation(
                operationId = "getPendingRequests", tags = {"🤝 Connexions"},
                summary = "Demandes de connexion en attente",
                description = "Retourne les demandes reçues (statut PENDING) en attente d'acceptation.",
                responses = @ApiResponse(responseCode = "200", description = "Demandes en attente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConnectionResponse.class))))
            )
        ),
        @RouterOperation(
            path = "/api/connections/{id}/status", method = RequestMethod.PUT,
            beanClass = MatchingHandler.class, beanMethod = "updateConnectionStatus",
            operation = @Operation(
                operationId = "updateConnectionStatus", tags = {"🤝 Connexions"},
                summary = "Accepter ou bloquer une connexion",
                description = "Seul le destinataire peut modifier le statut. Valeurs acceptées : `ACCEPTED`, `BLOCKED`.",
                parameters = {
                    @Parameter(name = "id",    in = ParameterIn.PATH,  required = true, description = "ID de la connexion"),
                    @Parameter(name = "value", in = ParameterIn.QUERY, required = true,
                        description = "Nouveau statut", schema = @Schema(allowableValues = {"ACCEPTED", "BLOCKED"}))
                },
                responses = {
                    @ApiResponse(responseCode = "200", description = "Statut mis à jour",
                        content = @Content(schema = @Schema(implementation = ConnectionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Statut invalide ou non autorisé")
                }
            )
        ),
        @RouterOperation(
            path = "/api/connections/{id}", method = RequestMethod.DELETE,
            beanClass = MatchingHandler.class, beanMethod = "deleteConnection",
            operation = @Operation(
                operationId = "deleteConnection", tags = {"🤝 Connexions"},
                summary = "Supprimer une connexion",
                parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true),
                responses = @ApiResponse(responseCode = "204", description = "Connexion supprimée")
            )
        )
    })
    public RouterFunction<ServerResponse> matchingRoutes(MatchingHandler handler) {
        return RouterFunctions.route()
            .POST("/api/users",                              handler::createUser)
            .GET( "/api/users/{id}",                         handler::getProfile)
            .PUT( "/api/users/{id}",                         handler::updateProfile)
            .GET( "/api/users/{id}/interests",               handler::getInterests)
            .POST("/api/users/{id}/interests",               handler::addInterest)
            .DELETE("/api/users/{id}/interests/{tag}",       handler::removeInterest)
            .GET( "/api/users/{id}/recommendations",         handler::getRecommendations)
            .GET( "/api/users/{id}/discovery",               handler::getDiscovery)
            .POST(  "/api/connections",                      handler::createConnection)
            .GET(   "/api/connections",                      handler::getConnections)
            .GET(   "/api/connections/pending",              handler::getPendingRequests)
            .PUT(   "/api/connections/{id}/status",          handler::updateConnectionStatus)
            .DELETE("/api/connections/{id}",                 handler::deleteConnection)
            .build();
    }
}
