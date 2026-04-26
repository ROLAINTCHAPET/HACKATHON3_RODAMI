package com.rodami.campuslink.profile.dto;

import java.util.List;

/**
 * Historique complet de l'activité utilisateur (RF-05).
 */
public record ActivityHistoryDTO(
    List<EventHistoryDTO> recentEvents,
    List<ConnectionHistoryDTO> recentConnections
) {}
