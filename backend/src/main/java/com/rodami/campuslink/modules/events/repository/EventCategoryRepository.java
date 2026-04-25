package com.rodami.campuslink.modules.events.repository;

import com.rodami.campuslink.modules.events.domain.EventCategory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EventCategoryRepository extends ReactiveCrudRepository<EventCategory, Long> {

    Mono<EventCategory> findByNom(String nom);

    /** Toutes les catégories triées par priorité BDE décroissante */
    Flux<EventCategory> findAllByOrderByPrioriteDesc();
}
