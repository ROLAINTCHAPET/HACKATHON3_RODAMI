package com.rodami.campuslink.modules.events.api;

import com.rodami.campuslink.modules.events.dto.EventResponse;
import com.rodami.campuslink.modules.events.service.EventService;
import com.rodami.campuslink.modules.events.service.EventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * TWIST 08 : Contrôleur pour l'accès public (sans authentification).
 * Permet de consulter les détails d'un événement via son token de partage.
 */
@RestController
@RequestMapping("/api/public/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;
    private final com.rodami.campuslink.modules.matching.service.UserService userService;
    private final EventRegistrationService registrationService;

    /**
     * Récupère les détails d'un événement via son token secret.
     * Accessible sans être connecté.
     */
    @GetMapping("/{token}")
    public Mono<EventResponse> getByToken(@PathVariable UUID token) {
        return eventService.getByShareToken(token);
    }

    /**
     * Inscription d'un invité (GUEST) à un événement.
     * Crée un profil fantôme si l'email n'existe pas déjà.
     */
    @PostMapping("/{token}/register")
    public Mono<com.rodami.campuslink.modules.events.dto.RegistrationResponse> registerGuest(
            @PathVariable UUID token,
            @RequestBody com.rodami.campuslink.modules.events.dto.GuestRegistrationRequest request) {
        
        return userService.getOrCreateGuestUser(request.getEmail(), request.getPrenom())
                .flatMap(user -> registrationService.registerByShareToken(token, user.getId()));
    }
}
