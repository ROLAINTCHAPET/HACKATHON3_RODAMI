package com.rodami.campuslink.governance.service;

import com.rodami.campuslink.governance.dto.EventImpactDTO;
import com.rodami.campuslink.governance.entity.Association;
import com.rodami.campuslink.governance.entity.AuditLog;
import com.rodami.campuslink.governance.entity.GovernanceRule;
import com.rodami.campuslink.governance.repository.AssociationRepository;
import com.rodami.campuslink.governance.repository.AuditLogRepository;
import com.rodami.campuslink.governance.repository.EventRepository;
import com.rodami.campuslink.governance.repository.GovernanceRuleRepository;
import com.rodami.campuslink.profile.repository.ConnectionRepository;
import com.rodami.campuslink.profile.repository.EventRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class GovernanceService {

    private final GovernanceRuleRepository ruleRepository;
    private final AuditLogRepository auditLogRepository;
    private final AssociationRepository associationRepository;
    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final ConnectionRepository connectionRepository;

    /**
     * RF-16 : Récupère une priorité algorithmique.
     */
    public Mono<String> getRuleValue(String key, String defaultValue) {
        return ruleRepository.findByRuleKey(key)
                .map(GovernanceRule::getRuleValue)
                .defaultIfEmpty(defaultValue);
    }

    /**
     * RF-17 : Met à jour une règle avec vérification de sécurité.
     */
    public Mono<GovernanceRule> updateRule(String key, String value, String userRole, Long actorId) {
        return ruleRepository.findByRuleKey(key)
                .flatMap(rule -> {
                    // Si la règle est figée (fixed), seul l'ADMIN peut la modifier
                    if (rule.isFixed() && !"ADMIN".equals(userRole)) {
                        return Mono.error(new SecurityException("Cette règle est figée par l'administration et ne peut être modifiée par le BDE."));
                    }

                    String oldValue = rule.getRuleValue();
                    rule.setRuleValue(value);
                    rule.setUpdatedAt(Instant.now());
                    rule.setSetByRole(userRole);

                    return ruleRepository.save(rule)
                            .flatMap(savedRule -> logAction(
                                    actorId,
                                    "RULE_UPDATED",
                                    "GOVERNANCE_RULE",
                                    savedRule.getId(),
                                    String.format("Clé: %s, Ancienne: %s, Nouvelle: %s", key, oldValue, value)
                            ).thenReturn(savedRule));
                });
    }

    /**
     * RF-19 : Valider ou rejeter une association (par le BDE).
     */
    public Mono<Association> validateAssociation(Long associationId, String newStatus, Long actorId) {
        return associationRepository.findById(associationId)
                .flatMap(assoc -> {
                    String oldStatus = assoc.getStatus();
                    assoc.setStatus(newStatus);
                    assoc.setUpdatedAt(Instant.now());

                    return associationRepository.save(assoc)
                            .flatMap(savedAssoc -> logAction(
                                    actorId,
                                    "ASSOCIATION_VALIDATED",
                                    "ASSOCIATION",
                                    savedAssoc.getId(),
                                    String.format("Nom: %s, Ancien Statut: %s, Nouveau: %s", savedAssoc.getNom(), oldStatus, newStatus)
                            ).thenReturn(savedAssoc));
                });
    }

    /**
     * RF-20 : Calcule l'impact d'un événement sur la création de liens sociaux.
     */
    public Mono<EventImpactDTO> getEventImpact(Long eventId) {
        return eventRepository.findById(eventId)
                .flatMap(event -> Mono.zip(
                        registrationRepository.countByEventId(eventId).defaultIfEmpty(0L),
                        connectionRepository.countBySourceEventIdAndStatus(eventId, "ACCEPTED").defaultIfEmpty(0L)
                ).map(tuple -> {
                    long participants = tuple.getT1();
                    long connections = tuple.getT2();
                    double successRate = participants > 0 ? (double) connections / participants * 100 : 0;

                    return EventImpactDTO.builder()
                            .eventId(eventId)
                            .titre(event.getTitre())
                            .participantCount((int) participants)
                            .connectionsCreatedCount((int) connections)
                            .successRate(Math.round(successRate * 100.0) / 100.0)
                            .build();
                }));
    }

    /**
     * RF-18 : Enregistre une trace d'audit.
     */
    public Mono<AuditLog> logAction(Long actorId, String action, String entityType, Long entityId, String details) {
        AuditLog logEntry = AuditLog.builder()
                .actorId(actorId)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .createdAt(Instant.now())
                .build();
        return auditLogRepository.save(logEntry);
    }
}
