package com.rodami.campuslink.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
<<<<<<< HEAD
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger / OpenAPI 3.
 * UI accessible sur : http://localhost:8080/swagger-ui.html
 * JSON spec     sur : http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI campusLinkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusLink API")
                        .description("""
                            **API réactive** (Spring WebFlux) du système de mise en relation contextuelle CampusLink.
                            
                            ## Modules disponibles
                            - 🔗 **Mise en Relation** — profils, intérêts, connexions, flux PULL/PUSH
                            - 📅 **Gestion des Événements** — CRUD événements, inscriptions, priorité BDE
                            
                            ## Authentification
                            Toutes les routes protégées nécessitent un **Bearer JWT** dans le header `Authorization`.
                            ```
                            Authorization: Bearer <votre_token>
                            ```
                            
                            ## Rôles
                            | Rôle | Périmètre |
                            |------|-----------|
                            | `USER` | Profil, connexions, inscriptions |
                            | `BDE` | + Créer/modifier/publier des événements |
                            | `ADMIN` | + Gouvernance, règles figées |
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe RODAMI")
                                .email("rodami@campuslink.dev"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Développement local"),
                        new Server().url("http://backend:8080").description("Docker interne")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT généré par Firebase Auth ou Spring Security")));
=======
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI campusLinkOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("CampusLink API — Module Profils")
                .description("Documentation des endpoints pour la gestion des profils (Module 1)")
                .version("1.0.0"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
            .name("Bearer Authentication")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");
>>>>>>> Mich
    }
}
