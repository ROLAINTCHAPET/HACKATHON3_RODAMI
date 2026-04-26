package com.rodami.campuslink.modules.events.service;

import com.rodami.campuslink.common.exception.ResourceNotFoundException;
import com.rodami.campuslink.profile.entity.EventRegistration;
import com.rodami.campuslink.modules.events.dto.RegistrationResponse;
import com.rodami.campuslink.profile.repository.EventRegistrationRepository;
import com.rodami.campuslink.modules.events.repository.EventRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final com.rodami.campuslink.profile.repository.ConnectionRepository connectionRepository;
    private final com.rodami.campuslink.profile.service.ProfileService profileService;
    private final com.rodami.campuslink.modules.matching.service.RecommendationService recommendationService;

    // ----------------------------------------------------------------
    // S'inscrire à un événement
    // ----------------------------------------------------------------
    @Transactional
    public Mono<RegistrationResponse> register(Long eventId, Long userId) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Événement", eventId)))
                .flatMap(event -> {
                    if (!"PUBLISHED".equals(event.getStatus())) {
                        return Mono.error(new IllegalArgumentException(
                                "Impossible de s'inscrire à un événement non publié"));
                    }
                    return registrationRepository.existsByEventIdAndUserId(eventId, userId);
                })
                .flatMap(alreadyRegistered -> {
                    if (alreadyRegistered) {
                        return Mono.error(new IllegalArgumentException("Vous êtes déjà inscrit à cet événement"));
                    }
                    // Vérifier la capacité max
                    return eventRepository.countParticipants(eventId)
                            .flatMap(count -> eventRepository.findById(eventId)
                                    .flatMap(event -> {
                                        if (event.getMaxParticipants() != null
                                                && count >= event.getMaxParticipants()) {
                                            return Mono.error(new IllegalArgumentException(
                                                    "L'événement est complet (max " + event.getMaxParticipants() + " participants)"));
                                        }
                                        EventRegistration reg = new EventRegistration();
                                        reg.setEventId(eventId);
                                        reg.setUserId(userId);
                                        return registrationRepository.save(reg);
                                    }));
                })
                .flatMap(this::enrichRegistration)
                .doOnSuccess(r -> log.info("[EVENTS] Inscription: userId={} → eventId={}", userId, eventId));
    }

    /** TWIST 08 : Inscription via token public */
    @Transactional
    public Mono<RegistrationResponse> registerByShareToken(java.util.UUID shareToken, Long userId) {
        return eventRepository.findByShareToken(shareToken)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Événement public", shareToken.toString())))
                .flatMap(event -> register(event.getId(), userId));
    }

    // ----------------------------------------------------------------
    // Se désinscrire
    // ----------------------------------------------------------------
    @Transactional
    public Mono<Void> unregister(Long eventId, Long userId) {
        return registrationRepository.existsByEventIdAndUserId(eventId, userId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new IllegalArgumentException("Vous n'êtes pas inscrit à cet événement"));
                    }
                    return registrationRepository.deleteByEventIdAndUserId(eventId, userId);
                })
                .doOnSuccess(v -> log.info("[EVENTS] Désinscription: userId={} ← eventId={}", userId, eventId));
    }

    // ----------------------------------------------------------------
    // Participants d'un événement
    // ----------------------------------------------------------------
    public Flux<RegistrationResponse> getParticipants(Long eventId) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Événement", eventId)))
                .thenMany(registrationRepository.findByEventId(eventId))
                .flatMap(this::enrichRegistration);
    }

    // ----------------------------------------------------------------
    // Événements auxquels un utilisateur est inscrit
    // ----------------------------------------------------------------
    public Flux<RegistrationResponse> getUserRegistrations(Long userId) {
        return registrationRepository.findByUserId(userId)
                .flatMap(this::enrichRegistration);
    }

    /**
     * IDs des co-participants à un événement (pour suggérer des connexions).
     * Utilisé par le module Matching dans la traçabilité source_event_id.
     */
    public Flux<Long> getCoParticipantIds(Long eventId, Long excludeUserId) {
        return registrationRepository.findUserIdsByEventId(eventId)
                .filter(uid -> !uid.equals(excludeUserId));
    }

    // ----------------------------------------------------------------
    // Enrichissement des données de la réponse
    // ----------------------------------------------------------------
    private Mono<RegistrationResponse> enrichRegistration(EventRegistration reg) {
        Mono<String> eventTitreMono = eventRepository.findById(reg.getEventId())
                .map(e -> e.getTitre())
                .defaultIfEmpty("—");

        Mono<String> userNomMono = userRepository.findById(reg.getUserId())
                .map(u -> (u.getPrenom() != null ? u.getPrenom() : "") + " " + (u.getNom() != null ? u.getNom() : ""))
                .defaultIfEmpty("Inconnu");

        return Mono.zip(eventTitreMono, userNomMono)
                .map(t -> RegistrationResponse.builder()
                        .id(reg.getId())
                        .eventId(reg.getEventId())
                        .eventTitre(t.getT1())
                        .userId(reg.getUserId())
                        .userNom(t.getT2())
                        .registeredAt(reg.getRegisteredAt())
                        .build());
    }

    /**
     * TWIST 09 : Confirmer la présence réelle à un événement.
     * Cette action transforme un "clic" (inscription) en "interaction réelle".
     */
    @Transactional
    public Mono<Void> confirmAttendance(Long eventId, Long userId) {
        log.info("[TWIST 09] Confirmation présence: userId={} → eventId={}", userId, eventId);
        
        return registrationRepository.confirmAttendance(eventId, userId)
                .doOnNext(rows -> log.info("[TWIST 09] Présence mise à jour: {} ligne(s) modifiée(s) pour userId={}", rows, userId))
                .doOnSuccess(v -> log.debug("[TWIST 09] Signal de confirmation terminé"))
                .then(
                    // TWIST 09 : AUTO-CONNECT (On crée du lien par l'action)
                    registrationRepository.findAttendedUserIdsByEventId(eventId)
                        .doOnNext(otherId -> log.debug("[TWIST 09] Co-participant trouvé: {}", otherId))
                        .filter(otherId -> !otherId.equals(userId))
                        .flatMap(otherId -> 
                            connectionRepository.findBetweenUsers(userId, otherId)
                                .doOnNext(c -> log.debug("[TWIST 09] Connexion existante trouvée avec {}", otherId))
                                .flatMap(conn -> {
                                    // Déjà connectés : on booste le score de réalité
                                    double newScore = Math.min(1.0, (conn.getRealityScore() != null ? conn.getRealityScore() : 0.1) + 0.4);
                                    return connectionRepository.updateRealityScore(userId, otherId, newScore)
                                            .doOnSuccess(v -> log.debug("[TWIST 09] Score réalité mis à jour pour {}", otherId));
                                })
                                .switchIfEmpty(Mono.defer(() -> {
                                    log.debug("[TWIST 09] Création auto-connexion avec {}", otherId);
                                    return connectionRepository.save(com.rodami.campuslink.profile.entity.Connection.builder()
                                        .requesterId(userId)
                                        .receiverId(otherId)
                                        .status("PENDING")
                                        .realityScore(0.8)
                                        .interactionCount(1)
                                        .sourceEventId(eventId)
                                        .createdAt(java.time.Instant.now())
                                        .updatedAt(java.time.Instant.now())
                                        .build())
                                        .doOnSuccess(c -> log.debug("[TWIST 09] Auto-connexion sauvegardée"))
                                        .then();
                                }))
                        )
                        .then(Mono.defer(() -> {
                            log.debug("[TWIST 09] Invalidation du cache pour {}", userId);
                            return recommendationService.invalidateCache(userId);
                        }))
                        .then()
                )
                .then(
                    // TWIST 09 : On enregistre l'intérêt seulement si la présence est CONFIRMÉE
                    eventRepository.findById(eventId)
                        .doOnNext(e -> log.debug("[TWIST 09] Événement trouvé pour intérêt implicite: {}", e.getTitre()))
                        .flatMap(event -> {
                            String tag = event.getTitre(); 
                            return profileService.recordImplicitInterest(userId, tag, "Event", eventId)
                                    .doOnSuccess(v -> log.debug("[TWIST 09] Intérêt implicite enregistré"));
                        })
                )
                .onErrorResume(e -> {
                    log.error("[TWIST 09] CRASH dans confirmAttendance: {}", e.getMessage(), e);
                    return Mono.error(e);
                });
    }
}
