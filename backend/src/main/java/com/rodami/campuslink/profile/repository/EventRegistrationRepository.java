package com.rodami.campuslink.profile.repository;

import com.rodami.campuslink.profile.dto.EventHistoryDTO;
import com.rodami.campuslink.profile.entity.EventRegistration;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRegistrationRepository extends ReactiveCrudRepository<EventRegistration, Long> {
    
    @Query("SELECT er.event_id as eventId, e.titre as title, er.registered_at as registeredAt, e.status " +
           "FROM event_registrations er " +
           "JOIN events e ON er.event_id = e.id " +
           "WHERE er.user_id = :userId " +
           "ORDER BY er.registered_at DESC LIMIT 5")
    Flux<EventHistoryDTO> findRecentHistoryByUserId(Long userId);

    Mono<Long> countByEventId(Long eventId);
}
