package com.rodami.campuslink.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${RENDER_EXTERNAL_URL:https://campuslink-backend.onrender.com}")
    private String productionUrl;

    @Bean
    public OpenAPI campusLinkOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusLink API")
                        .description("""
                            **API réactive** (Spring WebFlux) du système de mise en relation contextuelle CampusLink.
                            
                            ## Modules disponibles
                            - 👤 **Profils** — onboarding, 3 couches de profil, cold start
                            - 🔗 **Mise en Relation** — intérêts, connexions, flux PULL/PUSH
                            - 📅 **Gestion des Événements** — CRUD événements, inscriptions, priorité BDE
                            - 🏛️ **Gouvernance** — règles Admin/BDE, audit logs, impact social
                            
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
                        new Server().url(productionUrl).description("Production (Render)"),
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
    }
}
