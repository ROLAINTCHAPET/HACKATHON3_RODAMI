package com.rodami.campuslink.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodami.campuslink.modules.events.domain.Event;
import com.rodami.campuslink.modules.events.repository.EventRepository;
import com.rodami.campuslink.modules.matching.service.UserService;
import com.rodami.campuslink.modules.matching.dto.UserRequest;
import com.rodami.campuslink.profile.entity.User;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TWIST 09 : Pipeline d'ingestion automatique des données du Sujet 9.
 * Se déclenche au démarrage si la base est vide.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class Sujet9IngestionConfig {

    private final String DATA_PATH = "/home/tchoungs/Bureau/RODAMI/HACKATHON3_RODAMI/sujet9/";

    @Bean
    @Profile("!test")
    public CommandLineRunner ingestSujet9(
            UserRepository userRepository,
            UserService userService,
            EventRepository eventRepository,
            R2dbcEntityTemplate template,
            ObjectMapper objectMapper) {
        return args -> {
            userRepository.count()
                    .flatMap(count -> {
                        if (count <= 1) {
                            log.info("[TWIST 09] Démarrage du pipeline d'ingestion Sujet 9...");
                            return runPipeline(userService, eventRepository, template, objectMapper);
                        }
                        log.info("[TWIST 09] Base déjà peuplée ({} utilisateurs). Saut de l'ingestion.", count);
                        return Mono.empty();
                    })
                    .block(Duration.ofMinutes(10)); // On attend la fin pour voir les erreurs
        };
    }

    private Mono<Void> runPipeline(UserService userService, EventRepository eventRepository, R2dbcEntityTemplate template, ObjectMapper objectMapper) {
        return ingestStudents(userService)
                .then(ingestAssociationsAndEvents(template, eventRepository, objectMapper))
                .then(ingestIcs(template))
                .doOnSuccess(v -> log.info("[TWIST 09] Pipeline terminé avec succès !"))
                .doOnError(e -> log.error("[TWIST 09] Échec du pipeline", e))
                .then();
    }

    private Mono<Void> ingestStudents(UserService userService) {
        return Mono.fromCallable(() -> {
            FileSystemResource res = new FileSystemResource(DATA_PATH + "etudiants.csv");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
                return br.lines().skip(1).toList();
            }
        }).flatMapMany(Flux::fromIterable)
        .flatMap(line -> {
            String[] cols = line.split(",");
            if (cols.length < 6) return Mono.empty();
            
            String studentId = cols[0];
            String email = studentId.toLowerCase() + "@univ.cm";
            
            UserRequest req = new UserRequest();
            req.setEmail(email);
            req.setNom("Nom_" + studentId);
            req.setPrenom("Prenom_" + studentId);
            req.setFiliere(cols[1]);
            req.setAnnee(mapAnnee(cols[2]));
            req.setStatut("ETUDIANT");
            
            // Intérêts
            String[] tags = cols[5].split("\\|");
            req.setInterests(List.of()); // On gère manuellement après pour plus de contrôle
            
            return userService.createUser(req)
                    .flatMap(profile -> {
                        return Flux.fromArray(tags)
                                .filter(t -> !t.isBlank())
                                .flatMap(t -> userService.addInterest(profile.getId(), 
                                    new com.rodami.campuslink.modules.matching.dto.InterestRequest(t.trim(), "General")))
                                .then();
                    });
        }, 5) // Concurrence de 5 pour ne pas saturer le pool
        .then();
    }

    private Mono<Void> ingestAssociationsAndEvents(R2dbcEntityTemplate template, EventRepository eventRepository, ObjectMapper objectMapper) {
        return Mono.fromCallable(() -> {
            FileSystemResource res = new FileSystemResource(DATA_PATH + "associations_evenements.json");
            return objectMapper.readValue(res.getInputStream(), Sujet9Data.class);
        }).flatMap(data -> {
            // 1. Importer les associations
            Map<String, Long> assoc_id_map = new java.util.concurrent.ConcurrentHashMap<>();
            return Flux.fromIterable(data.getAssociations())
                    .flatMap(a -> {
                        return template.getDatabaseClient()
                            .sql("INSERT INTO associations (nom, description, responsable_id, status) " +
                                 "VALUES (:nom, :desc, 1, 'APPROVED') " +
                                 "ON CONFLICT (nom) DO UPDATE SET description = EXCLUDED.description " +
                                 "RETURNING id")
                            .bind("nom", a.getNom())
                            .bind("desc", "Filière cible: " + a.getFiliereCible())
                            .fetch().one()
                            .map(m -> (Long) m.get("id"))
                            .doOnNext(id -> assoc_id_map.put(a.getId(), id));
                    })
                    .then(Mono.defer(() -> {
                        log.info("[TWIST 09] Ingestion de {} événements...", data.getEvenements().size());
                        // 2. Importer les événements
                        return Flux.fromIterable(data.getEvenements())
                            .flatMap(e -> {
                                String time = e.getHeureDebut();
                                if (time.length() == 5) time += ":00"; // HH:mm -> HH:mm:ss
                                LocalDateTime start = LocalDateTime.parse(e.getDate() + "T" + time);
                                // On utilise du SQL brut pour gérer le cast ENUM de Postgres (::event_status)
                                return template.getDatabaseClient()
                                    .sql("INSERT INTO events (titre, description, date_debut, date_fin, lieu, organisateur_id, association_id, status, max_participants, share_token, is_flash) " +
                                         "VALUES (:titre, :desc, :start, :end, :lieu, 1, :assoc_id, 'PUBLISHED'::event_status, :max, :token, false)")
                                    .bind("titre", e.getTitre())
                                    .bind("desc", e.getDescription() != null ? e.getDescription() : "")
                                    .bind("start", start.toInstant(ZoneOffset.UTC))
                                    .bind("end", start.plusHours(e.getDureeHeures() != null ? e.getDureeHeures() : 1).toInstant(ZoneOffset.UTC))
                                    .bind("lieu", e.getLieu() != null ? e.getLieu() : "Campus")
                                    .bind("assoc_id", assoc_id_map.get(e.getOrganisateurId()))
                                    .bind("max", e.getPlacesMax() != null ? e.getPlacesMax() : 100)
                                    .bind("token", UUID.randomUUID())
                                    .fetch().rowsUpdated();
                            })
                            .then();
                    }));
        });
    }

    private Mono<Void> ingestIcs(R2dbcEntityTemplate template) {
        return Mono.fromCallable(() -> {
            FileSystemResource res = new FileSystemResource(DATA_PATH + "ETU0001.ics");
            if (!res.exists()) return "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                br.lines().forEach(l -> sb.append(l).append("\n"));
                return sb.toString();
            }
        }).flatMap(content -> {
            if (content.isEmpty()) return Mono.empty();
            Pattern p = Pattern.compile("BEGIN:VEVENT.*?SUMMARY:(.*?)\\n.*?DTSTART:(.*?)\\n.*?DTEND:(.*?)\\n.*?LOCATION:(.*?)\\n.*?END:VEVENT", Pattern.DOTALL);
            Matcher m = p.matcher(content);
            
            return Flux.generate(sink -> {
                if (m.find()) sink.next(m); else sink.complete();
            }).flatMap(matchObj -> {
                Matcher match = (Matcher) matchObj;
                String summary = match.group(1).trim();
                String startStr = match.group(2).trim();
                String endStr = match.group(3).trim();
                String loc = match.group(4).trim();
                
                Instant s = parseIcsDate(startStr);
                Instant e = parseIcsDate(endStr);
                
                return template.getDatabaseClient()
                        .sql("INSERT INTO academic_schedules (user_id, summary, start_time, end_time, location) VALUES (1, :sum, :start, :end, :loc)")
                        .bind("sum", summary)
                        .bind("start", s)
                        .bind("end", e)
                        .bind("loc", loc)
                        .fetch().rowsUpdated();
            }).then();
        });
    }

    private Short mapAnnee(String annee) {
        int res = switch (annee) {
            case "L1" -> 1;
            case "L2" -> 2;
            case "L3" -> 3;
            case "M1" -> 4;
            case "M2" -> 5;
            default -> 1;
        };
        return (short) res;
    }

    private Instant parseIcsDate(String val) {
        // Format ICS Basic: 20260106T150000(Z)
        String clean = val.trim();
        try {
            if (clean.endsWith("Z")) {
                // yyyyMMddTHHmmssZ -> yyyy-MM-ddTHH:mm:ssZ
                String iso = clean.substring(0, 4) + "-" + 
                             clean.substring(4, 6) + "-" + 
                             clean.substring(6, 8) + "T" + 
                             clean.substring(9, 11) + ":" + 
                             clean.substring(11, 13) + ":" + 
                             clean.substring(13, 15) + "Z";
                return Instant.parse(iso);
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
            return LocalDateTime.parse(clean, formatter).toInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            log.warn("[TWIST 09] Erreur de parsing date ICS '{}', repli sur maintenant.", clean);
            return Instant.now();
        }
    }

    @Data
    public static class Sujet9Data {
        private List<AssocJson> associations;
        private List<EventJson> evenements;
    }

    @Data
    public static class AssocJson {
        private String id;
        private String nom;
        @JsonProperty("filiere_cible")
        private String filiereCible;
    }

    @Data
    public static class EventJson {
        private String titre;
        @JsonProperty("organisateur_id")
        private String organisateurId;
        private String date;
        @JsonProperty("heure_debut")
        private String heureDebut;
        @JsonProperty("duree_heures")
        private Integer dureeHeures;
        private String lieu;
        @JsonProperty("places_max")
        private Integer placesMax;
        private String description;
        private String tags;
    }
}
