package com.rodami.campuslink.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;

@Configuration
public class DevAuthRouter {

    @Bean
    @RouterOperation(
        path = "/api/auth/dev-token", method = RequestMethod.POST,
        beanClass = DevAuthHandler.class, beanMethod = "generateToken",
        operation = @Operation(
            operationId = "generateDevToken",
            tags = {"🔑 Authentification (Dev)"},
            summary = "Générer un JWT de test",
            description = "Endpoint de développement pour obtenir un token JWT valide sans passer par Firebase.",
            requestBody = @RequestBody(
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(example = "{\"email\": \"admin@campuslink.fr\", \"role\": \"ADMIN\"}")
                )
            ),
            responses = @ApiResponse(
                responseCode = "200", 
                description = "Token généré",
                content = @Content(schema = @Schema(example = "{\"token\": \"ey...\", \"role\": \"ADMIN\"}"))
            )
        )
    )
    public RouterFunction<ServerResponse> devAuthRoutes(DevAuthHandler handler) {
        return RouterFunctions.route()
            .POST("/api/auth/dev-token", handler::generateToken)
            .build();
    }
}
