package com.rodami.campuslink.profile.repository;

import com.rodami.campuslink.profile.entity.InterestCatalog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface InterestCatalogRepository extends ReactiveCrudRepository<InterestCatalog, Long> {

    Flux<InterestCatalog> findAllByOrderByDisplayOrderAsc();

    Flux<InterestCatalog> findByCategoryOrderByDisplayOrderAsc(String category);
}
