package com.rodami.campuslink.config;

import com.rodami.campuslink.profile.entity.Interest;
import com.rodami.campuslink.profile.entity.ProfileContext;
import com.rodami.campuslink.profile.entity.User;
import com.rodami.campuslink.profile.repository.InterestRepository;
import com.rodami.campuslink.profile.repository.ProfileContextRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;

/**
 * Données de seed pour le développement et les démos.
 * Crée des profils avec différents niveaux de complétion (TWIST 02).
 *
 * Activé uniquement en profil "dev" ou "demo".
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final UserRepository userRepository;
    private final ProfileContextRepository contextRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile({"dev", "demo"})
    public CommandLineRunner seedData() {
        return args -> {
            // Vérifier si les données existent déjà
            userRepository.count().subscribe(count -> {
                if (count > 0) {
                    log.info("Données de seed déjà présentes ({} utilisateurs)", count);
                    return;
                }

                log.info("Initialisation des données de seed...");

                // ===== Profil 100% complet — Marie Dupont =====
                User marie = User.builder()
                    .nom("Dupont").prenom("Marie")
                    .email("marie.dupont@campus.fr")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role("USER")
                    .createdAt(Instant.now()).updatedAt(Instant.now())
                    .build();

                userRepository.save(marie).subscribe(saved -> {
                    contextRepository.save(ProfileContext.builder()
                        .userId(saved.getId())
                        .filiere("Informatique").annee((short) 3)
                        .statut("ETUDIANT").updatedAt(Instant.now())
                        .build()).subscribe();

                    interestRepository.saveAll(List.of(
                        Interest.builder().userId(saved.getId()).tag("Programmation").category("Tech").createdAt(Instant.now()).build(),
                        Interest.builder().userId(saved.getId()).tag("Intelligence Artificielle").category("Tech").createdAt(Instant.now()).build(),
                        Interest.builder().userId(saved.getId()).tag("Yoga").category("Sport").createdAt(Instant.now()).build(),
                        Interest.builder().userId(saved.getId()).tag("Cinéma").category("Culture").createdAt(Instant.now()).build()
                    )).subscribe();

                    log.info("  ✅ Marie Dupont — profil 100%");
                });

                // ===== Profil 60% — Ahmed Ben Salah (pas de filière) =====
                User ahmed = User.builder()
                    .nom("Ben Salah").prenom("Ahmed")
                    .email("ahmed.bensalah@campus.fr")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role("USER")
                    .createdAt(Instant.now()).updatedAt(Instant.now())
                    .build();

                userRepository.save(ahmed).subscribe(saved -> {
                    interestRepository.saveAll(List.of(
                        Interest.builder().userId(saved.getId()).tag("Football").category("Sport").createdAt(Instant.now()).build(),
                        Interest.builder().userId(saved.getId()).tag("Gaming").category("Social").createdAt(Instant.now()).build(),
                        Interest.builder().userId(saved.getId()).tag("Cybersécurité").category("Tech").createdAt(Instant.now()).build()
                    )).subscribe();
                    log.info("  ✅ Ahmed Ben Salah — profil 60% (pas de contexte académique)");
                });

                // ===== Profil 30% — Léa Martin (seulement identité) =====
                User lea = User.builder()
                    .nom("Martin").prenom("Léa")
                    .email("lea.martin@campus.fr")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role("USER")
                    .createdAt(Instant.now()).updatedAt(Instant.now())
                    .build();

                userRepository.save(lea).subscribe(saved -> {
                    log.info("  ✅ Léa Martin — profil 30% (aucun intérêt, aucun contexte)");
                });

                // ===== Admin BDE =====
                User bde = User.builder()
                    .nom("Responsable").prenom("BDE")
                    .email("bde@campus.fr")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role("BDE")
                    .createdAt(Instant.now()).updatedAt(Instant.now())
                    .build();

                userRepository.save(bde).subscribe(saved -> {
                    contextRepository.save(ProfileContext.builder()
                        .userId(saved.getId())
                        .filiere("Management").annee((short) 4)
                        .statut("ETUDIANT").updatedAt(Instant.now())
                        .build()).subscribe();

                    interestRepository.saveAll(List.of(
                        Interest.builder().userId(saved.getId()).tag("Soirées").category("Social").createdAt(Instant.now()).build(),
                        Interest.builder().userId(saved.getId()).tag("Bénévolat").category("Social").createdAt(Instant.now()).build(),
                        Interest.builder().userId(saved.getId()).tag("Musique").category("Culture").createdAt(Instant.now()).build()
                    )).subscribe();

                    log.info("  ✅ BDE Admin — profil complet avec rôle BDE");
                });

                // ===== Admin système =====
                User admin = User.builder()
                    .nom("Système").prenom("Admin")
                    .email("admin@campus.fr")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .createdAt(Instant.now()).updatedAt(Instant.now())
                    .build();

                userRepository.save(admin).subscribe(saved -> {
                    log.info("  ✅ Admin Système — rôle ADMIN");
                });

                log.info("Seed terminé — 5 profils créés avec variété de complétion");
            });
        };
    }
}
