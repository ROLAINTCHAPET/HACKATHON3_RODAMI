package com.rodami.campuslink.profile.dto;

import java.time.Instant;

public record EventHistoryDTO(
    Long eventId,
    String title,
    Instant registeredAt,
    String status
) {}
