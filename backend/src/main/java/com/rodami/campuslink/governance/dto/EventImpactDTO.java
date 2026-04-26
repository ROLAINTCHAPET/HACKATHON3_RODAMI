package com.rodami.campuslink.governance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RF-20 : Mesure de l'impact social d'un événement.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventImpactDTO {
    private Long eventId;
    private String titre;
    private int participantCount;
    private int connectionsCreatedCount; // Nombre de connexions acceptées liées à cet event
    private double successRate; // (connections / participants) * 100
}
