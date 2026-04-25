package com.rodami.campuslink.modules.matching.repository;

import com.rodami.campuslink.modules.matching.domain.ProfileContext;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProfileContextRepository extends ReactiveCrudRepository<ProfileContext, Long> {

    Mono<ProfileContext> findByUserId(Long userId);

    Mono<Void> deleteByUserId(Long userId);
}
