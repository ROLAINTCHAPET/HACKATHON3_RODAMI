package com.rodami.campuslink.profile.repository;

import com.rodami.campuslink.profile.dto.ConnectionHistoryDTO;
import com.rodami.campuslink.profile.entity.Connection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConnectionRepository extends ReactiveCrudRepository<Connection, Long> {
    
    @Query("SELECT " +
           "  CASE WHEN c.requester_id = :userId THEN c.receiver_id ELSE c.requester_id END as otherUserId, " +
           "  u.prenom || ' ' || u.nom as otherUserName, " +
           "  c.updated_at as connectedAt, " +
           "  c.status " +
           "FROM connections c " +
           "JOIN users u ON u.id = (CASE WHEN c.requester_id = :userId THEN c.receiver_id ELSE c.requester_id END) " +
           "WHERE (c.requester_id = :userId OR c.receiver_id = :userId) AND c.status = 'ACCEPTED' " +
           "ORDER BY c.updated_at DESC LIMIT 5")
    Flux<ConnectionHistoryDTO> findRecentConnectionsByUserId(Long userId);

    Mono<Long> countBySourceEventIdAndStatus(Long sourceEventId, String status);
}
