package com.rodami.campuslink.modules.matching.repository;

import com.rodami.campuslink.modules.matching.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByEmail(String email);

    Mono<User> findByFirebaseUid(String firebaseUid);

    Mono<Boolean> existsByEmail(String email);
}
