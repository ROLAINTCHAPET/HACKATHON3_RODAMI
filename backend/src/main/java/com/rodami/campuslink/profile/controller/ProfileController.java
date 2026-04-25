package com.rodami.campuslink.profile.controller;

import com.rodami.campuslink.profile.dto.*;
import com.rodami.campuslink.profile.entity.Interest;
import com.rodami.campuslink.profile.entity.InterestCatalog;
import com.rodami.campuslink.profile.repository.InterestCatalogRepository;
import com.rodami.campuslink.profile.service.ColdStartService;
import com.rodami.campuslink.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour la gestion des profils.
 *
 * Endpoints :
 * - POST   /api/profiles/onboard              → Onboarding (inscription + profil)
 * - GET    /api/profiles/me                    → Mon profil
 * - GET    /api/profiles/{id}                  → Profil d'un utilisateur
 * - PUT    /api/profiles/{id}                  → Mise à jour partielle
 * - GET    /api/profiles/{id}/interests        → Intérêts d'un utilisateur
 * - POST   /api/profiles/{id}/interests        → Ajouter des intérêts
 * - DELETE /api/profiles/{id}/interests/{tag}  → Retirer un intérêt
 * - GET    /api/profiles/{id}/completion       → % de complétion
 * - GET    /api/profiles/cold-start/suggestions → Suggestions cold start
 * - GET    /api/interests/catalog              → Catalogue d'intérêts (public)
 */
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ColdStartService coldStartService;
    private final InterestCatalogRepository catalogRepository;

    // ===== ONBOARDING (RF-01) =====

    /**
     * Inscription + création de profil en un seul flow.
     * Public — pas besoin d'être authentifié.
     * Retourne un JWT pour une connexion immédiate.
     */
    @PostMapping("/api/profiles/onboard")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> onboard(@Valid @RequestBody OnboardingRequest request) {
        return profileService.onboard(request);
    }

    /**
     * Ajuster le profil (après inscription).
     * Authentifié — extrait le userId du JWT.
     */
    @PostMapping("/api/profiles/setup")
    public Mono<ProfileDTO> setupProfile(
            @RequestBody ProfileSetupRequest request,
            Authentication auth) {
        return profileService.getProfileByEmail(auth.getName())
            .flatMap(profile -> profileService.setupProfile(profile.id(), request));
    }

    // ===== PROFIL =====

    /**
     * Mon profil — utilise le JWT pour identifier l'utilisateur.
     */
    @GetMapping("/api/profiles/me")
    public Mono<ProfileDTO> getMyProfile(Authentication auth) {
        String email = auth.getName(); // subject du JWT = email
        return profileService.getProfileByEmail(email);
    }

    /**
     * Historique de l'activité (RF-05).
     */
    @GetMapping("/api/profiles/me/history")
    public Mono<ActivityHistoryDTO> getMyHistory(Authentication auth) {
        return profileService.getProfileByEmail(auth.getName())
            .flatMap(profile -> profileService.getActivityHistory(profile.id()));
    }

    /**
     * Profil d'un utilisateur par ID.
     */
    @GetMapping("/api/profiles/{id}")
    public Mono<ProfileDTO> getProfile(@PathVariable Long id) {
        return profileService.getProfile(id);
    }

    /**
     * Mise à jour partielle du profil (enrichissement progressif RF-04).
     */
    @PutMapping("/api/profiles/{id}")
    public Mono<ProfileDTO> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request) {
        return profileService.updateProfile(id, request);
    }

    // ===== INTÉRÊTS =====

    /**
     * Liste des intérêts d'un utilisateur.
     * ✅ TWIST 02 : retourne un Flux vide si aucun intérêt, pas d'erreur.
     */
    @GetMapping("/api/profiles/{id}/interests")
    public Flux<Interest> getUserInterests(@PathVariable Long id) {
        return profileService.getUserInterests(id);
    }

    /**
     * Ajouter des intérêts à un utilisateur.
     */
    @PostMapping("/api/profiles/{id}/interests")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<List<Interest>> addInterests(
            @PathVariable Long id,
            @RequestBody List<String> tags) {
        return profileService.addInterests(id, tags);
    }

    /**
     * Retirer un intérêt spécifique.
     */
    @DeleteMapping("/api/profiles/{id}/interests/{tag}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeInterest(
            @PathVariable Long id,
            @PathVariable String tag) {
        return profileService.removeInterest(id, tag);
    }

    // ===== COMPLÉTION =====

    /**
     * Pourcentage de complétion du profil.
     */
    @GetMapping("/api/profiles/{id}/completion")
    public Mono<Map<String, Integer>> getCompletion(@PathVariable Long id) {
        return profileService.getCompletionPercent(id)
            .map(percent -> Map.of("completionPercent", percent));
    }

    // ===== COLD START (TWIST 01) =====

    /**
     * Suggestions cold start pour un nouvel utilisateur.
     * Retourne des tags pertinents même sans historique.
     */
    @GetMapping("/api/profiles/cold-start/suggestions")
    public Mono<Map<String, Object>> getColdStartSuggestions(Authentication auth) {
        String email = auth.getName();
        return profileService.getProfileByEmail(email)
            .flatMap(profile -> coldStartService.getFallbackSuggestions(profile.id())
                .map(suggestions -> Map.<String, Object>of(
                    "userId", profile.id(),
                    "suggestedTags", suggestions,
                    "hasInterests", !profile.interests().isEmpty(),
                    "profileCompletion", profile.profileCompletionPercent()
                )));
    }

    // ===== CATALOGUE D'INTÉRÊTS (public) =====

    /**
     * Retourne le catalogue complet des intérêts prédéfinis, groupés par catégorie.
     * Utilisé par le frontend pour la grille de sélection lors de l'onboarding.
     */
    @GetMapping("/api/interests/catalog")
    public Mono<Map<String, List<InterestCatalogDTO>>> getInterestCatalog() {
        return catalogRepository.findAllByOrderByDisplayOrderAsc()
            .map(cat -> new InterestCatalogDTO(cat.getId(), cat.getTag(), cat.getCategory(), cat.getEmoji()))
            .collectList()
            .map(items -> items.stream()
                .collect(Collectors.groupingBy(InterestCatalogDTO::category)));
    }
}
