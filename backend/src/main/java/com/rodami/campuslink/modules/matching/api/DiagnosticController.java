package com.rodami.campuslink.modules.matching.api;

import com.rodami.campuslink.profile.repository.ConnectionRepository;
import com.rodami.campuslink.profile.repository.EventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * DiagnosticController : Outil de monitoring de la santé sociale.
 * TWIST 09 : Mesure les interactions réelles.
 * TWIST 10 : Rend la vie sociale visible SANS PROFILER (statistiques anonymes).
 */
@RestController
@RequestMapping("/api/diagnostics")
@RequiredArgsConstructor
public class DiagnosticController {

    private final ConnectionRepository connectionRepository;
    private final EventRegistrationRepository registrationRepository;

    @GetMapping("/twist09")
    public Mono<Map<String, Object>> getTwist09Diagnostics() {
        return Mono.zip(
            registrationRepository.countByIsAttended(true),
            connectionRepository.countByRealityScoreGreaterThan(0.5),
            connectionRepository.countBySourceEventIdIsNotNull()
        ).map(tuple -> Map.of(
            "description", "Diagnostic TWIST 09 — Réalité sociale",
            "total_real_interactions", tuple.getT1(),
            "strengthened_connections", tuple.getT2(),
            "auto_connections_created", tuple.getT3(),
            "success_status", tuple.getT1() > 0 ? "CONNECTÉ (Succès)" : "EN ATTENTE D'INTERACTION"
        ));
    }

    @GetMapping("/debug/connections")
    public reactor.core.publisher.Flux<com.rodami.campuslink.profile.entity.Connection> debugConnections() {
        return connectionRepository.findAll();
    }
}
