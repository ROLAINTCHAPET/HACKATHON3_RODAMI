package com.rodami.campuslink.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!jwtUtil.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String subject = jwtUtil.getSubject(token);
        String role    = jwtUtil.getRole(token);

        var authority = new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER"));
        var auth      = new UsernamePasswordAuthenticationToken(subject, null, List.of(authority));

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
    }
}
