package com.rodami.campuslink.profile.service;

import com.rodami.campuslink.common.exception.ResourceNotFoundException;
import com.rodami.campuslink.profile.dto.*;
import com.rodami.campuslink.profile.entity.Interest;
import com.rodami.campuslink.profile.entity.ProfileContext;
import com.rodami.campuslink.profile.entity.User;
import com.rodami.campuslink.profile.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

/**
 * Service principal de gestion des profils.
 *
 * Respecte strictement :
 * - RF-01 : Onboarding < 2 minutes (inscription + profil en un flow)
 * - RF-02 : Profil structuré en 3 couches
 * - RF-04 : Enrichissement progressif par l'usage
 * - TWIST 01 : Cold start — contenu pertinent même sans historique
 * - TWIST 02 : 60% des profils vides — JAMAIS supposer qu'une donnée existe
 *
 * Règle absolue :
 * ❌ user.getInterests().get(0)  → plante si vide
 * ✅ user.getInterests().isEmpty() ? valeurParDéfaut : valeur
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileContextRepository profileContextRepository;
    private final InterestRepository interestRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final ConnectionRepository connectionRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    // ===== ACTIVITÉ & HISTORIQUE (RF-05) =====

    /**
     * Récupère l'historique récent de l'utilisateur (événements + connexions).
     */
    public Mono<ActivityHistoryDTO> getActivityHistory(Long userId) {
        return Mono.zip(
            eventRegistrationRepository.findRecentHistoryByUserId(userId).collectList().defaultIfEmpty(List.of()),
            connectionRepository.findRecentConnectionsByUserId(userId).collectList().defaultIfEmpty(List.of())
        ).map(tuple -> new ActivityHistoryDTO(tuple.getT1(), tuple.getT2()));
    }

    // ===== ONBOARDING & SETUP =====

    /**
     * Crée un utilisateur complet avec ses intérêts en un seul appel.
     * Retourne un AuthResponse avec le JWT pour une connexion immédiate.
     */
    public Mono<AuthResponse> onboard(OnboardingRequest request) {
        return userRepository.existsByEmail(request.email())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new IllegalArgumentException(
                        "Un compte avec cet email existe déjà"));
                }
                return createUserWithProfile(request);
            });
    }

    /**
     * Ajuste le profil d'un utilisateur existant (intérêts + contexte académique).
     */
    public Mono<ProfileDTO> setupProfile(Long userId, ProfileSetupRequest request) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur", userId)))
            .flatMap(user -> {
                // 1. Contexte académique
                Mono<ProfileContext> contextMono = Mono.empty();
                if (request.filiere() != null || request.annee() != null) {
                    contextMono = profileContextRepository.findByUserId(userId)
                        .defaultIfEmpty(ProfileContext.builder()
                            .userId(userId)
                            .statut("ETUDIANT")
                            .build())
                        .flatMap(ctx -> {
                            if (request.filiere() != null) ctx.setFiliere(request.filiere());
                            if (request.annee() != null) ctx.setAnnee(request.annee());
                            ctx.setUpdatedAt(Instant.now());
                            return profileContextRepository.save(ctx);
                        });
                }

                // 2. Intérêts
                Mono<Void> interestsMono = Mono.empty();
                if (request.interests() != null && !request.interests().isEmpty()) {
                    List<Interest> interests = request.interests().stream()
                        .map(tag -> Interest.builder()
                            .userId(userId)
                            .tag(tag.trim())
                            .createdAt(Instant.now())
                            .build())
                        .toList();
                    interestsMono = interestRepository.saveAll(interests).then();
                }

                return Mono.when(contextMono, interestsMono)
                    .then(getProfile(userId));
            });
    }

    private Mono<AuthResponse> createUserWithProfile(OnboardingRequest request) {
        // 1. Créer l'utilisateur (couche 1 — identité stable)
        User user = User.builder()
            .nom(request.nom())
            .prenom(request.prenom())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .role("USER")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        return userRepository.save(user)
            .flatMap(savedUser -> {
                // 2. Créer le contexte académique si fourni (couche 2)
                Mono<ProfileContext> contextMono;
                if (request.filiere() != null || request.annee() != null) {
                    ProfileContext ctx = ProfileContext.builder()
                        .userId(savedUser.getId())
                        .filiere(request.filiere())
                        .annee(request.annee())
                        .statut("ETUDIANT")
                        .updatedAt(Instant.now())
                        .build();
                    contextMono = profileContextRepository.save(ctx);
                } else {
                    contextMono = Mono.empty();
                }

                // 3. Créer les intérêts (couche 3)
                // ✅ TWIST 02 : vérification défensive même si la validation a déjà vérifié
                List<Interest> interests = (request.interests() != null && !request.interests().isEmpty())
                    ? request.interests().stream()
                        .map(tag -> Interest.builder()
                            .userId(savedUser.getId())
                            .tag(tag.trim())
                            .createdAt(Instant.now())
                            .build())
                        .toList()
                    : List.of();

                Flux<Interest> interestFlux = interestRepository.saveAll(interests);

                // Attendre que tout soit sauvegardé puis retourner l'AuthResponse
                return contextMono
                    .then(interestFlux.collectList())
                    .then(authService.buildAuthResponse(savedUser));
            });
    }

    // ===== GET PROFILE — Agrège les 3 couches (RF-02 + TWIST 02) =====

    /**
     * Récupère le profil complet d'un utilisateur en agrégeant les 3 couches.
     * Ne crash JAMAIS même si les couches 2 et 3 sont vides (TWIST 02).
     */
    public Mono<ProfileDTO> getProfile(Long userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur", userId)))
            .flatMap(this::assembleProfile);
    }

    /**
     * Récupère le profil par email (utilisé pour /me).
     */
    public Mono<ProfileDTO> getProfileByEmail(String email) {
        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                "Utilisateur avec email " + email + " introuvable")))
            .flatMap(this::assembleProfile);
    }

    /**
     * Assemble les 3 couches du profil en un ProfileDTO.
     * ✅ TWIST 02 : defaultIfEmpty garantit qu'on ne crash jamais.
     */
    private Mono<ProfileDTO> assembleProfile(User user) {
        return Mono.zip(
            // Couche 2 — contexte académique (peut être vide)
            profileContextRepository.findByUserId(user.getId())
                .defaultIfEmpty(ProfileContext.builder()
                    .statut("ETUDIANT")
                    .build()),
            // Couche 3 — intérêts (peut être vide)
            interestRepository.findByUserId(user.getId())
                .collectList()
                .defaultIfEmpty(List.of())
        ).map(tuple -> ProfileDTO.from(user, tuple.getT1(), tuple.getT2()));
    }

    // ===== UPDATE PROFILE (RF-04) — Enrichissement progressif =====

    /**
     * Met à jour partiellement le profil.
     * Seuls les champs non-null sont mis à jour.
     */
    public Mono<ProfileDTO> updateProfile(Long userId, UpdateProfileRequest request) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur", userId)))
            .flatMap(user -> {
                // Mise à jour du contexte académique (couche 2)
                Mono<Void> updateContext = updateAcademicContext(userId, request);

                // Ajout d'intérêts
                Mono<Void> addInterests = Mono.empty();
                if (request.interestsToAdd() != null && !request.interestsToAdd().isEmpty()) {
                    List<Interest> newInterests = request.interestsToAdd().stream()
                        .map(tag -> Interest.builder()
                            .userId(userId)
                            .tag(tag.trim())
                            .createdAt(Instant.now())
                            .build())
                        .toList();
                    addInterests = interestRepository.saveAll(newInterests).then();
                }

                // Suppression d'intérêts
                Mono<Void> removeInterests = Mono.empty();
                if (request.interestsToRemove() != null && !request.interestsToRemove().isEmpty()) {
                    removeInterests = Flux.fromIterable(request.interestsToRemove())
                        .flatMap(tag -> interestRepository.deleteByUserIdAndTag(userId, tag))
                        .then();
                }

                return Mono.when(updateContext, addInterests, removeInterests)
                    .then(getProfile(userId));
            });
    }

    private Mono<Void> updateAcademicContext(Long userId, UpdateProfileRequest request) {
        if (request.filiere() == null && request.annee() == null && request.statut() == null) {
            return Mono.empty();
        }

        return profileContextRepository.findByUserId(userId)
            .defaultIfEmpty(ProfileContext.builder()
                .userId(userId)
                .statut("ETUDIANT")
                .updatedAt(Instant.now())
                .build())
            .flatMap(ctx -> {
                if (request.filiere() != null) ctx.setFiliere(request.filiere());
                if (request.annee() != null) ctx.setAnnee(request.annee());
                if (request.statut() != null) ctx.setStatut(request.statut());
                ctx.setUpdatedAt(Instant.now());
                return profileContextRepository.save(ctx).then();
            });
    }

    // ===== ENRICHISSEMENT IMPLICITE (RF-04) =====

    /**
     * Ajoute un intérêt de manière implicite (par l'usage, sans formulaire).
     * Appelé par d'autres modules quand l'utilisateur interagit avec un contenu tagué.
     * ✅ TWIST 09 : Track le sourceEventId pour permettre le cleanup systémique.
     */
    public Mono<Void> recordImplicitInterest(Long userId, String tag, String category, Long sourceEventId) {
        return interestRepository.findByUserId(userId)
            .collectList()
            .flatMap(existingInterests -> {
                boolean alreadyExistsForThisSource = existingInterests != null && !existingInterests.isEmpty()
                    && existingInterests.stream().anyMatch(i -> tag.equals(i.getTag()) && 
                        (sourceEventId == null ? i.getSourceEventId() == null : sourceEventId.equals(i.getSourceEventId())));

                if (alreadyExistsForThisSource) {
                    return Mono.empty();
                }

                Interest newInterest = Interest.builder()
                    .userId(userId)
                    .tag(tag)
                    .category(category)
                    .sourceEventId(sourceEventId)
                    .createdAt(Instant.now())
                    .build();

                return interestRepository.save(newInterest).then();
            });
    }

    // ===== INTÉRÊTS =====

    /**
     * Retourne les intérêts d'un utilisateur.
     * ✅ TWIST 02 : retourne une liste vide si aucun intérêt, jamais null.
     */
    public Flux<Interest> getUserInterests(Long userId) {
        return interestRepository.findByUserId(userId);
    }

    /**
     * Ajoute des intérêts à un utilisateur.
     */
    public Mono<List<Interest>> addInterests(Long userId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Mono.just(List.of());
        }

        List<Interest> interests = tags.stream()
            .map(tag -> Interest.builder()
                .userId(userId)
                .tag(tag.trim())
                .createdAt(Instant.now())
                .build())
            .toList();

        return interestRepository.saveAll(interests).collectList();
    }

    /**
     * Supprime un intérêt spécifique.
     */
    public Mono<Void> removeInterest(Long userId, String tag) {
        return interestRepository.deleteByUserIdAndTag(userId, tag);
    }

    // ===== COMPLÉTION =====

    /**
     * Retourne uniquement le pourcentage de complétion du profil.
     */
    public Mono<Integer> getCompletionPercent(Long userId) {
        return getProfile(userId)
            .map(ProfileDTO::profileCompletionPercent);
    }
}
