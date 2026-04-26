package com.rodami.campuslink.profile.repository;

import com.rodami.campuslink.profile.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByEmail(String email);

    Mono<User> findByFirebaseUid(String firebaseUid);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByFirebaseUid(String firebaseUid);
}
