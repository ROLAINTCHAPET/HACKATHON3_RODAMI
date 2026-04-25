package com.rodami.campuslink.profile.dto;

import java.time.Instant;

public record ConnectionHistoryDTO(
    Long otherUserId,
    String otherUserName,
    Instant connectedAt,
    String status
) {}
