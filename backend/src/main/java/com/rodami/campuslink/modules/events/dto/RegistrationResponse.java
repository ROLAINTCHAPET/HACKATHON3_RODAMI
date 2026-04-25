package com.rodami.campuslink.modules.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** Vue d'une inscription : qui s'est inscrit à quel événement */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    private Long id;
    private Long eventId;
    private String eventTitre;
    private Long userId;
    private String userNom;
    private Instant registeredAt;
}
