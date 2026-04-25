package com.rodami.campuslink.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DevAuthHandler {

    private final JwtUtil jwtUtil;
    private final com.rodami.campuslink.modules.matching.repository.UserRepository userRepository;

    public Mono<ServerResponse> generateToken(ServerRequest request) {
        return request.bodyToMono(Map.class)
            .flatMap(body -> {
                String email = (String) body.getOrDefault("email", "admin@campuslink.fr");
                String role = (String) body.getOrDefault("role", "USER");
                
                return userRepository.findByEmail(email)
                    .flatMap(user -> {
                        String token = jwtUtil.generateToken(user.getId(), email, role);
                        return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(Map.of(
                                "token", token,
                                "userId", user.getId(),
                                "email", email,
                                "role", role
                            ));
                    })
                    .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .bodyValue(Map.of("error", "Utilisateur non trouvé en base. Créez-le d'abord via POST /api/users")));
            })
            .switchIfEmpty(ServerResponse.badRequest().build());
    }
}
