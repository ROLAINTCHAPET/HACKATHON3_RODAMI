package com.rodami.campuslink.governance.controller;

import com.rodami.campuslink.governance.dto.EventImpactDTO;
import com.rodami.campuslink.governance.entity.Association;
import com.rodami.campuslink.governance.entity.AuditLog;
import com.rodami.campuslink.governance.entity.GovernanceRule;
import com.rodami.campuslink.governance.service.GovernanceService;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/governance")
@RequiredArgsConstructor
public class GovernanceController {

    private final GovernanceService governanceService;
    private final UserRepository userRepository;

    /**
     * RF-16/17 : Mettre à jour une règle de priorité.
     */
    @PutMapping("/rules/{key}")
    public Mono<GovernanceRule> updateRule(
            @PathVariable String key,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace("ROLE_", ""))
                .findFirst().orElse("USER");

        return userRepository.findByEmail(auth.getName())
                .flatMap(user -> governanceService.updateRule(key, body.get("value"), role, user.getId()));
    }

    /**
     * RF-19 : Valider une association.
     */
    @PutMapping("/associations/{id}/validate")
    public Mono<Association> validateAssociation(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        
        return userRepository.findByEmail(auth.getName())
                .flatMap(user -> governanceService.validateAssociation(id, body.get("status"), user.getId()));
    }

    /**
     * RF-20 : Tableau de bord d'impact.
     */
    @GetMapping("/impact/events/{id}")
    public Mono<EventImpactDTO> getEventImpact(@PathVariable Long id) {
        return governanceService.getEventImpact(id);
    }

    /**
     * RF-18 : Consulter les logs d'audit pour une entité.
     */
    @GetMapping("/audit")
    public Mono<Void> getAuditLogs() {
        // Implémentation simplifiée pour l'instant
        return Mono.empty();
    }
}
