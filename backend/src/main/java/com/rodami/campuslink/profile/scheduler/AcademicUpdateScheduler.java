package com.rodami.campuslink.profile.scheduler;

import com.rodami.campuslink.profile.repository.ProfileContextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Planificateur pour les tâches académiques.
 * RF-03 : Mise à jour automatique du contexte académique à chaque rentrée.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AcademicUpdateScheduler {

    private final DatabaseClient databaseClient;

    /**
     * Chaque 1er Septembre à 00:00, on incrémente l'année de tous les étudiants.
     * On limite à 8 ans maximum (durée max d'un cursus long).
     */
    @Scheduled(cron = "0 0 0 1 9 *")
    public void performBackToSchoolUpdate() {
        log.info("Rentrée universitaire : Début de la mise à jour automatique des années...");

        databaseClient.sql("UPDATE profile_contexts SET annee = annee + 1, updated_at = :now WHERE annee < 8")
            .bind("now", Instant.now())
            .fetch()
            .rowsUpdated()
            .doOnSuccess(count -> log.info("Mise à jour terminée : {} profils mis à jour pour la nouvelle année.", count))
            .doOnError(e -> log.error("Erreur lors de la mise à jour de rentrée : {}", e.getMessage()))
            .subscribe();
    }
}
