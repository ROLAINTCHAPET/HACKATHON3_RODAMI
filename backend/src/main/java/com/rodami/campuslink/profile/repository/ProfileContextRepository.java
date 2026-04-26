package com.rodami.campuslink.profile.repository;

import com.rodami.campuslink.profile.entity.ProfileContext;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProfileContextRepository extends ReactiveCrudRepository<ProfileContext, Long> {

    Mono<ProfileContext> findByUserId(Long userId);
}
