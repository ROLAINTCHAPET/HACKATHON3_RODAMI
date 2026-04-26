package com.rodami.campuslink.modules.matching.service;

import com.rodami.campuslink.profile.entity.User;
import com.rodami.campuslink.profile.repository.InterestRepository;
import com.rodami.campuslink.profile.repository.ProfileContextRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveListOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileContextRepository profileContextRepository;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;
    
    @Mock
    private ReactiveListOperations<String, String> listOperations;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    @DisplayName("getRecommendations — Utilise le cache si présent")
    void getRecommendations_use_cache() {
        Long userId = 1L;
        String cacheKey = "pull:recommendations:" + userId;

        when(userRepository.findById(userId)).thenReturn(Mono.just(User.builder().id(userId).build()));
        when(profileContextRepository.findByUserId(userId)).thenReturn(Mono.empty());
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.range(cacheKey, 0, -1)).thenReturn(Flux.just("2", "3"));
        when(interestRepository.findRecommendedUserIds(anyLong(), anyInt())).thenReturn(Flux.empty());
        
        // Mock userService.getProfile for each ID
        when(userService.getProfile(2L)).thenReturn(Mono.empty()); // On s'en fiche du contenu ici
        when(userService.getProfile(3L)).thenReturn(Mono.empty());

        StepVerifier.create(recommendationService.getRecommendations(userId))
                .expectNextCount(0) // On a mocké Mono.empty() donc pas d'émission
                .verifyComplete();
    }
}
