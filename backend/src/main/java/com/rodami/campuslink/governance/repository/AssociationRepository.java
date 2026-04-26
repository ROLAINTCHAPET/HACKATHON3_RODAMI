package com.rodami.campuslink.governance.repository;

import com.rodami.campuslink.governance.entity.Association;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AssociationRepository extends ReactiveCrudRepository<Association, Long> {
    Flux<Association> findByStatus(String status);
}
