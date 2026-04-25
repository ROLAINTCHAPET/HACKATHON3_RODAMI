package com.rodami.campuslink.modules.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/** Vue d'une connexion retournée par l'API */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionResponse {

    private Long id;
    private Long userId1;
    private Long userId2;

    /** Nom complet du correspondant (selon la perspective de l'appelant) */
    private String partnerName;

    private String status;
    private Long sourceEventId;
    private Instant createdAt;
    private Instant updatedAt;
}
