package com.rodami.campuslink.modules.matching.service;

import com.rodami.campuslink.common.exception.ResourceNotFoundException;
import com.rodami.campuslink.modules.matching.domain.Interest;
import com.rodami.campuslink.modules.matching.domain.ProfileContext;
import com.rodami.campuslink.modules.matching.domain.User;
import com.rodami.campuslink.modules.matching.dto.InterestRequest;
import com.rodami.campuslink.modules.matching.dto.UserProfile;
import com.rodami.campuslink.modules.matching.dto.UserRequest;
import com.rodami.campuslink.modules.matching.repository.ConnectionRepository;
import com.rodami.campuslink.modules.matching.repository.InterestRepository;
import com.rodami.campuslink.modules.matching.repository.ProfileContextRepository;
import com.rodami.campuslink.modules.matching.repository.UserRepository;
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
                ).map(tuple -> buildProfile(user, tuple.getT1(), tuple.getT2(), tuple.getT3())));
    }

    // ----------------------------------------------------------------
    // Création d'un utilisateur avec profil et intérêts
    // ----------------------------------------------------------------
    @Transactional
    public Mono<UserProfile> createUser(UserRequest request) {
        return userRepository.existsByEmail(request.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Email déjà utilisé : " + request.getEmail()));
                    }
                    User user = User.builder()
                            .nom(request.getNom())
                            .prenom(request.getPrenom())
                            .email(request.getEmail())
                            .firebaseUid(request.getFirebaseUid())
                            .role("USER")
                            .build();
                    return userRepository.save(user);
                })
                .flatMap(savedUser -> {
                    ProfileContext ctx = ProfileContext.builder()
                            .userId(savedUser.getId())
                            .filiere(request.getFiliere())
                            .annee(request.getAnnee())
                            .statut(request.getStatut() != null ? request.getStatut() : "ETUDIANT")
                            .build();

                    Mono<ProfileContext> saveCtx = profileContextRepository.save(ctx);

                    Flux<Interest> saveInterests = Flux.empty();
                    if (request.getInterests() != null && !request.getInterests().isEmpty()) {
                        saveInterests = Flux.fromIterable(request.getInterests())
                                .map(ir -> Interest.builder()
                                        .userId(savedUser.getId())
                                        .tag(ir.getTag().toLowerCase().trim())
                                        .category(ir.getCategory())
                                        .build())
                                .flatMap(interestRepository::save);
                    }

                    return Mono.zip(
                            saveCtx,
                            saveInterests.collectList()
                    ).map(tuple -> buildProfile(savedUser, tuple.getT1(), tuple.getT2(), 0L));
                });
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
    // Utilitaire — construction du DTO UserProfile
    // ----------------------------------------------------------------
    private UserProfile buildProfile(User user, ProfileContext ctx, List<Interest> interests, Long connectionCount) {
        return UserProfile.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(user.getRole())
                .filiere(ctx.getFiliere())
                .annee(ctx.getAnnee())
                .statut(ctx.getStatut())
                .interests(interests.stream().map(Interest::getTag).toList())
                .connectionCount(connectionCount)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
