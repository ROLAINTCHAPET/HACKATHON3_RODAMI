package com.rodami.campuslink.governance.repository;

import com.rodami.campuslink.governance.entity.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EventRepository extends ReactiveCrudRepository<Event, Long> {
}
