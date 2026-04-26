package com.rodami.campuslink.profile.service;

import com.rodami.campuslink.common.exception.ResourceNotFoundException;
import com.rodami.campuslink.profile.dto.*;
import com.rodami.campuslink.profile.entity.Interest;
import com.rodami.campuslink.profile.entity.ProfileContext;
import com.rodami.campuslink.profile.entity.User;
import com.rodami.campuslink.profile.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private ProfileContextRepository profileContextRepository;
    @Mock private InterestRepository interestRepository;
    @Mock private EventRegistrationRepository eventRegistrationRepository;
    @Mock private ConnectionRepository connectionRepository;
    @Mock private AuthService authService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileService profileService;

    private User testUser;
    private ProfileContext testContext;
    private List<Interest> testInterests;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .nom("Dupont").prenom("Marie")
            .email("marie.dupont@campus.fr")
            .passwordHash("hashed")
            .role("USER")
            .createdAt(Instant.now()).updatedAt(Instant.now())
            .build();

        testContext = ProfileContext.builder()
            .id(1L).userId(1L)
            .filiere("Informatique").annee((short) 3)
            .statut("ETUDIANT").updatedAt(Instant.now())
            .build();

        testInterests = List.of(
            Interest.builder().id(1L).userId(1L).tag("Tech").category("Tech").createdAt(Instant.now()).build()
        );
    }

    @Test
    @DisplayName("setupProfile — Ajuste le profil (intérêts et contexte) avec succès")
    void setupProfile_success() {
        ProfileSetupRequest request = new ProfileSetupRequest(List.of("Tech"), "Informatique", (short) 3);
        
        when(userRepository.findById(1L)).thenReturn(Mono.just(testUser));
        when(profileContextRepository.findByUserId(1L)).thenReturn(Mono.empty(), Mono.just(testContext));
        when(profileContextRepository.save(any(ProfileContext.class))).thenReturn(Mono.just(testContext));
        when(interestRepository.saveAll(anyList())).thenReturn(Flux.fromIterable(testInterests));
        
        // Mock pour getProfile à la fin
        when(interestRepository.findByUserId(1L)).thenReturn(Flux.fromIterable(testInterests));

        StepVerifier.create(profileService.setupProfile(1L, request))
            .assertNext(profile -> {
                assertThat(profile.filiere()).isEqualTo("Informatique");
                assertThat(profile.interests()).contains("Tech");
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("recordImplicitInterest — RF-04 : Ajoute un intérêt sans doublon lors d'une action utilisateur")
    void recordImplicitInterest_success() {
        when(interestRepository.findByUserId(1L)).thenReturn(Flux.empty());
        when(interestRepository.save(any(Interest.class))).thenReturn(Mono.just(testInterests.get(0)));

        StepVerifier.create(profileService.recordImplicitInterest(1L, "Sport", "Culture"))
            .verifyComplete();
    }

    @Test
    @DisplayName("getActivityHistory — Agrège l'historique des événements et connexions (RF-05)")
    void getActivityHistory_success() {
        EventHistoryDTO event = new EventHistoryDTO(1L, "Gala Campus", Instant.now(), "PUBLISHED");
        ConnectionHistoryDTO conn = new ConnectionHistoryDTO(2L, "Ahmed Ben Salah", Instant.now(), "ACCEPTED");

        when(eventRegistrationRepository.findRecentHistoryByUserId(1L)).thenReturn(Flux.just(event));
        when(connectionRepository.findRecentConnectionsByUserId(1L)).thenReturn(Flux.just(conn));

        StepVerifier.create(profileService.getActivityHistory(1L))
            .assertNext(history -> {
                assertThat(history.recentEvents()).hasSize(1);
                assertThat(history.recentEvents().get(0).title()).isEqualTo("Gala Campus");
                assertThat(history.recentConnections()).hasSize(1);
                assertThat(history.recentConnections().get(0).otherUserName()).isEqualTo("Ahmed Ben Salah");
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("getProfile — TWIST 02 : Retourne un profil même si incomplet")
    void getProfile_empty_twist02() {
        when(userRepository.findById(1L)).thenReturn(Mono.just(testUser));
        when(profileContextRepository.findByUserId(1L)).thenReturn(Mono.empty());
        when(interestRepository.findByUserId(1L)).thenReturn(Flux.empty());

        StepVerifier.create(profileService.getProfile(1L))
            .assertNext(profile -> {
                assertThat(profile.displayName()).isEqualTo("Marie Dupont");
                assertThat(profile.filiere()).isNull(); // Couche 2 vide
                assertThat(profile.interests()).isEmpty(); // Couche 3 vide
                assertThat(profile.profileCompletionPercent()).isLessThan(50);
            })
            .verifyComplete();
    }
}
