package com.rodami.campuslink.modules.matching.service;

import com.rodami.campuslink.governance.service.GovernanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * TWIST 07 : Service de gestion du cycle académique.
 * Permet de piloter la bascule globale de semestre.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SemesterService {

    private final GovernanceService governanceService;
    
    private static final String RULE_KEY = "academic.current_semester";
    private static final String DEFAULT_SEMESTER = "1";

    /**
     * Récupère le semestre actuel.
     * Utilise un cache mémoire court pour éviter de surcharger la DB à chaque recommandation.
     */
    public Mono<String> getCurrentSemesterId() {
        return governanceService.getRuleValue(RULE_KEY, DEFAULT_SEMESTER)
                .cache(Duration.ofMinutes(1)); // Cache court de 1 min
    }
}
