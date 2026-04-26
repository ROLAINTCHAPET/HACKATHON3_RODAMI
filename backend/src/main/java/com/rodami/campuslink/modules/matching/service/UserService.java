package com.rodami.campuslink.modules.matching.service;

import com.rodami.campuslink.common.exception.ResourceNotFoundException;
import com.rodami.campuslink.profile.entity.Interest;
import com.rodami.campuslink.profile.entity.ProfileContext;
import com.rodami.campuslink.profile.entity.User;
import com.rodami.campuslink.modules.matching.dto.InterestRequest;
import com.rodami.campuslink.modules.matching.dto.UserProfile;
import com.rodami.campuslink.modules.matching.dto.UserRequest;
import com.rodami.campuslink.profile.repository.ConnectionRepository;
import com.rodami.campuslink.profile.repository.InterestRepository;
import com.rodami.campuslink.profile.repository.ProfileContextRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileContextRepository profileContextRepository;
    private final InterestRepository interestRepository;
    private final ConnectionRepository connectionRepository;

    // ----------------------------------------------------------------
    // Récupération d'un profil complet (User + ProfileContext + Interests)
    // ----------------------------------------------------------------
    public Mono<UserProfile> getProfile(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur", userId)))
                .flatMap(user -> Mono.zip(
                        profileContextRepository.findByUserId(userId)
                                .defaultIfEmpty(new ProfileContext()),
                        interestRepository.findByUserId(userId).collectList(),
                        connectionRepository.countAcceptedConnectionsByUserId(userId)
                                .defaultIfEmpty(0L)
                ).map(tuple -> buildProfile(user, tuple.getT1(), tuple.getT2(), tuple.getT3())));
    }

    // ----------------------------------------------------------------
    // Création d'un utilisateur avec profil et intérêts
    // ----------------------------------------------------------------
    @Transactional
    public Mono<UserProfile> createUser(UserRequest request) {
        String firebaseUid = (request.getFirebaseUid() == null || request.getFirebaseUid().isBlank()) ? null : request.getFirebaseUid();

        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email déjà utilisé : " + request.getEmail()));
                    }
                    if (firebaseUid != null) {
                        return userRepository.existsByFirebaseUid(firebaseUid)
                                .flatMap(existsUid -> {
                                    if (existsUid) {
                                        return Mono.error(new IllegalArgumentException("Firebase UID déjà utilisé : " + firebaseUid));
                                    }
                                    return Mono.just(false);
                                });
                    }
                    return Mono.just(false);
                })
                .then(Mono.defer(() -> {
                    User user = User.builder()
                            .nom(request.getNom())
                            .prenom(request.getPrenom())
                            .email(request.getEmail())
                            .firebaseUid(firebaseUid)
                            .role("USER")
                            .build();
                    return userRepository.save(user);
                }))
                .flatMap(savedUser -> {
                    ProfileContext ctx = ProfileContext.builder()
                            .userId(savedUser.getId())
                            .filiere(request.getFiliere())
                            .annee(request.getAnnee())
                            .statut(request.getStatut() != null ? request.getStatut() : "ETUDIANT")
                            .build();

                    return profileContextRepository.save(ctx)
                            .then(Mono.defer(() -> {
                                if (request.getInterests() != null && !request.getInterests().isEmpty()) {
                                    return Flux.fromIterable(request.getInterests())
                                            .map(ir -> Interest.builder()
                                                    .userId(savedUser.getId())
                                                    .tag(ir.getTag().toLowerCase().trim())
                                                    .category(ir.getCategory())
                                                    .build())
                                            .flatMap(interestRepository::save)
                                            .collectList();
                                }
                                return Mono.just(List.<Interest>of());
                            }))
                            .map(interests -> buildProfile(savedUser, ctx, interests, 0L));
                });
    }

    /**
     * TWIST 08 : Récupère un utilisateur par email ou en crée un nouveau avec le rôle GUEST.
     * Utilisé pour l'adoption sans friction via liens publics.
     */
    @Transactional
    public Mono<User> getOrCreateGuestUser(String email, String prenom) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("[TWIST 08] Création d'un profil fantôme (GUEST) pour email={}", email);
                    User guest = User.builder()
                            .email(email)
                            .prenom(prenom)
                            .role("GUEST")
                            .build();
                    return userRepository.save(guest);
                }));
    }

    // ----------------------------------------------------------------
    // Mise à jour du profil
    // ----------------------------------------------------------------
    @Transactional
    public Mono<UserProfile> updateProfile(Long userId, UserRequest request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur", userId)))
                .flatMap(user -> {
                    user.setNom(request.getNom());
                    user.setPrenom(request.getPrenom());
                    return userRepository.save(user);
                })
                .flatMap(user -> profileContextRepository.findByUserId(userId)
                        .defaultIfEmpty(ProfileContext.builder().userId(userId).build())
                        .flatMap(ctx -> {
                            ctx.setUserId(userId);
                            if (request.getFiliere() != null) ctx.setFiliere(request.getFiliere());
                            if (request.getAnnee() != null)   ctx.setAnnee(request.getAnnee());
                            if (request.getStatut() != null)  ctx.setStatut(request.getStatut());
                            return profileContextRepository.save(ctx);
                        })
                        .then(getProfile(userId))
                );
    }

    // ----------------------------------------------------------------
    // Gestion des intérêts
    // ----------------------------------------------------------------
    public Mono<Void> addInterest(Long userId, InterestRequest request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur", userId)))
                .flatMap(u -> {
                    Interest interest = Interest.builder()
                            .userId(userId)
                            .tag(request.getTag().toLowerCase().trim())
                            .category(request.getCategory())
                            .build();
                    return interestRepository.save(interest);
                })
                .then();
    }

    public Mono<Void> removeInterest(Long userId, String tag) {
        return interestRepository.deleteByUserIdAndTag(userId, tag.toLowerCase().trim());
    }

    public Flux<Interest> getInterests(Long userId) {
        return interestRepository.findByUserId(userId);
    }

    // ----------------------------------------------------------------
    // Utilitaire — construction du DTO UserProfile (null-safe TWIST 02)
    // ----------------------------------------------------------------
    private UserProfile buildProfile(User user, ProfileContext ctx, List<Interest> interests, Long connectionCount) {
        return UserProfile.builder()
                .id(user.getId())
                .nom(user.getNom() != null ? user.getNom() : "")
                .prenom(user.getPrenom() != null ? user.getPrenom() : "")
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole() : "USER")
                .filiere(ctx != null ? ctx.getFiliere() : null)
                .annee(ctx != null ? ctx.getAnnee() : null)
                .statut(ctx != null && ctx.getStatut() != null ? ctx.getStatut() : "ETUDIANT")
                .interests(interests != null ? interests.stream().map(Interest::getTag).toList() : List.of())
                .connectionCount(connectionCount != null ? connectionCount : 0L)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
