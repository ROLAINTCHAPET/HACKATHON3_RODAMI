package com.rodami.campuslink.modules.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/** Demande de connexion entre deux utilisateurs */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequest {

    @NotNull(message = "L'id du destinataire est obligatoire")
    private Long targetUserId;

    /** Événement déclencheur (optionnel — traçabilité) */
    private Long sourceEventId;
}
