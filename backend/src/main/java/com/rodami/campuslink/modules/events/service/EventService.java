package com.rodami.campuslink.modules.events.service;

import com.rodami.campuslink.common.exception.ResourceNotFoundException;
import com.rodami.campuslink.modules.events.domain.Event;
import com.rodami.campuslink.modules.events.domain.EventCategory;
import com.rodami.campuslink.modules.events.dto.EventRequest;
import com.rodami.campuslink.modules.events.dto.EventResponse;
import com.rodami.campuslink.modules.events.repository.EventCategoryRepository;
import com.rodami.campuslink.modules.events.repository.EventRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // ----------------------------------------------------------------
    // Lecture
    // ----------------------------------------------------------------

    /** Tous les événements publiés, triés par priorité BDE puis date */
    public Flux<EventResponse> getAllPublished() {
        return eventRepository.findAllPublished(Instant.now())
                .flatMap(this::enrichEvent);
    }

    /** Événements à venir uniquement */
    public Flux<EventResponse> getUpcoming() {
        return eventRepository.findUpcoming(Instant.now())
                .flatMap(this::enrichEvent);
    }

    /** Événements d'une catégorie */
    public Flux<EventResponse> getByCategory(Long categoryId) {
        return eventRepository.findByCategoryId(categoryId, Instant.now())
                .flatMap(this::enrichEvent);
    }

    /** Recherche par mot-clé (titre / description) */
    public Flux<EventResponse> search(String keyword) {
        return eventRepository.searchByKeyword(keyword)
                .flatMap(this::enrichEvent);
    }

    /** Événements créés par un organisateur */
    public Flux<EventResponse> getByOrganisateur(Long organisateurId) {
        return eventRepository.findByOrganisateurId(organisateurId)
                .flatMap(this::enrichEvent);
    }

    /** Détail d'un événement */
    public Mono<EventResponse> getById(Long eventId) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Événement", eventId)))
                .flatMap(this::enrichEvent);
    }

    /** Détail d'un événement par son token public (TWIST 08) */
    public Mono<EventResponse> getByShareToken(java.util.UUID shareToken) {
        return eventRepository.findByShareToken(shareToken)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Événement public", shareToken.toString())))
                .flatMap(this::enrichEvent);
    }

    // ----------------------------------------------------------------
    // Création (BDE/Admin uniquement — géré par SecurityConfig)
    // ----------------------------------------------------------------
    @Transactional
    public Mono<EventResponse> createEvent(Long organisateurId, EventRequest request) {
        return validateCategory(request.getCategoryId())
                .then(userRepository.findById(organisateurId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Organisateur", organisateurId))))
                .flatMap(org -> {
                    Event event = Event.builder()
                            .titre(request.getTitre())
                            .description(request.getDescription())
                            .dateDebut(request.getDateDebut())
                            .dateFin(request.getDateFin())
                            .lieu(request.getLieu())
                            .categoryId(request.getCategoryId())
                            .organisateurId(organisateurId)
                            .status("DRAFT")     // commence toujours en DRAFT
                            .maxParticipants(request.getMaxParticipants())
                            .build();
                    return eventRepository.save(event);
                })
                .flatMap(this::enrichEvent)
                .doOnSuccess(e -> log.info("[EVENTS] Événement créé id={} titre='{}'", e.getId(), e.getTitre()));
    }

    // ----------------------------------------------------------------
    // Modification
    // ----------------------------------------------------------------
    @Transactional
    public Mono<EventResponse> updateEvent(Long eventId, Long requesterId, EventRequest request) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Événement", eventId)))
                .flatMap(event -> {
                    event.setTitre(request.getTitre());
                    event.setDescription(request.getDescription());
                    event.setDateDebut(request.getDateDebut());
                    event.setDateFin(request.getDateFin());
                    event.setLieu(request.getLieu());
                    event.setCategoryId(request.getCategoryId());
                    event.setMaxParticipants(request.getMaxParticipants());
                    return eventRepository.save(event);
                })
                .flatMap(this::enrichEvent);
    }

    // ----------------------------------------------------------------
    // Cycle de vie : publier / annuler
    // ----------------------------------------------------------------
    @Transactional
    public Mono<EventResponse> publish(Long eventId) {
        return changeStatus(eventId, "PUBLISHED");
    }

    @Transactional
    public Mono<EventResponse> cancel(Long eventId) {
        return changeStatus(eventId, "CANCELLED");
    }

    private Mono<EventResponse> changeStatus(Long eventId, String newStatus) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Événement", eventId)))
                .flatMap(event -> {
                    // TWIST 05 : Validation stricte pour la publication
                    if ("PUBLISHED".equals(newStatus)) {
                        validateEventCompleteness(event);
                    }
                    event.setStatus(newStatus);
                    return eventRepository.save(event);
                })
                .flatMap(this::enrichEvent)
                .doOnSuccess(e -> log.info("[EVENTS] Statut changé id={} → {}", e.getId(), newStatus));
    }

    /**
     * TWIST 05 : Nettoyage automatique des événements obsolètes.
     * Appelé par le scheduler.
     */
    public Mono<Long> processPastEvents() {
        return eventRepository.markPastEvents(Instant.now())
                .count()
                .doOnSuccess(count -> {
                    if (count > 0) log.info("[TWIST 05] {} événements obsolètes archivés (status=PAST).", count);
                });
    }

    private void validateEventCompleteness(Event event) {
        if (event.getDescription() == null || event.getDescription().isBlank()) {
            throw new IllegalArgumentException("TWIST 05 : Impossible de publier un événement sans description.");
        }
        if (event.getLieu() == null || event.getLieu().isBlank()) {
            throw new IllegalArgumentException("TWIST 05 : Impossible de publier un événement sans lieu.");
        }
        if (event.getCategoryId() == null) {
            throw new IllegalArgumentException("TWIST 05 : Impossible de publier un événement sans catégorie.");
        }
    }

    // ----------------------------------------------------------------
    // Suppression
    // ----------------------------------------------------------------
    @Transactional
    public Mono<Void> deleteEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Événement", eventId)))
                .flatMap(eventRepository::delete);
    }

    // ----------------------------------------------------------------
    // Catégories
    // ----------------------------------------------------------------
    public Flux<EventCategory> getAllCategories() {
        return categoryRepository.findAllByOrderByPrioriteDesc();
    }

    // ----------------------------------------------------------------
    // Utilitaire — enrichissement de la réponse
    // ----------------------------------------------------------------
    public Flux<EventResponse> getFlashEvents() {
        return eventRepository.findAll()
                .filter(e -> e.getIsFlash() != null && e.getIsFlash())
                .flatMap(this::enrichEvent);
    }

    private Mono<EventResponse> enrichEvent(Event event) {
        Mono<String> categoryNomMono = event.getCategoryId() != null
                ? categoryRepository.findById(event.getCategoryId())
                        .map(EventCategory::getNom)
                        .defaultIfEmpty("—")
                : Mono.just("—");

        Mono<Short> categoryPrioriteMono = event.getCategoryId() != null
                ? categoryRepository.findById(event.getCategoryId())
                        .map(EventCategory::getPriorite)
                        .defaultIfEmpty((short) 0)
                : Mono.just((short) 0);

        Mono<String> organisateurNomMono = userRepository.findById(event.getOrganisateurId())
                .map(u -> (u.getPrenom() != null ? u.getPrenom() : "") + " " + (u.getNom() != null ? u.getNom() : ""))
                .defaultIfEmpty("Inconnu");

        Mono<Long> countMono = eventRepository.countParticipants(event.getId())
                .defaultIfEmpty(0L);

        return Mono.zip(categoryNomMono, categoryPrioriteMono, organisateurNomMono, countMono)
                .map(t -> EventResponse.builder()
                        .id(event.getId())
                        .titre(event.getTitre())
                        .description(event.getDescription())
                        .dateDebut(event.getDateDebut())
                        .dateFin(event.getDateFin())
                        .lieu(event.getLieu())
                        .categoryId(event.getCategoryId())
                        .categoryNom(t.getT1())
                        .categoryPriorite(t.getT2())
                        .organisateurId(event.getOrganisateurId())
                        .organisateurNom(t.getT3())
                        .status(event.getStatus())
                        .maxParticipants(event.getMaxParticipants())
                        .shareToken(event.getShareToken())
                        .participantCount(t.getT4())
                        .createdAt(event.getCreatedAt())
                        .updatedAt(event.getUpdatedAt())
                        .build());
    }

    private Mono<Void> validateCategory(Long categoryId) {
        if (categoryId == null) return Mono.empty();
        return categoryRepository.findById(categoryId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Catégorie introuvable : " + categoryId)))
                .then();
    }
}
