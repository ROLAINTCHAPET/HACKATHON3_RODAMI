package com.rodami.campuslink.modules.events.scheduler;

import com.rodami.campuslink.modules.events.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * TWIST 05 : Gestion de la péremption des données.
 * Un événement dont la date est passée ne doit plus polluer les flux "PUBLISHED".
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventCleanupScheduler {

    private final EventService eventService;

    /**
     * Toutes les 30 minutes, on archive les événements dont la date de début est passée.
     * On utilise la date de début car beaucoup d'événements n'ont pas de date de fin saisie.
     */
    @Scheduled(cron = "0 0/30 * * * *")
    public void cleanupObsoleteEvents() {
        eventService.processPastEvents()
                .subscribe();
    }
}
