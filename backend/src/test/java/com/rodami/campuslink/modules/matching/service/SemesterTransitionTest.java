package com.rodami.campuslink.modules.matching.service;

import com.rodami.campuslink.profile.entity.User;
import com.rodami.campuslink.profile.repository.InterestRepository;
import com.rodami.campuslink.profile.repository.ProfileContextRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import com.rodami.campuslink.profile.service.ColdStartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SemesterTransitionTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileContextRepository profileContextRepository;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ColdStartService coldStartService;

    @Mock
    private SemesterService semesterService;

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveListOperations<String, String> listOperations;

    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationService, "pullMaxResults", 20);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        // Default mock for safety net
        when(coldStartService.getPopularTags(anyInt())).thenReturn(Flux.empty());
    }

    @Test
    void getRecommendations_afterSemesterChange_shouldInvalidateOldCache() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(profileContextRepository.findByUserId(userId)).thenReturn(Mono.empty());
        
        // --- STEP 1: Semester 1 ---
        when(semesterService.getCurrentSemesterId()).thenReturn(Mono.just("1"));
        String cacheKeyS1 = "pull:recommendations:S1:" + userId;
        
        // Simulation cache S1 présent
        when(listOperations.range(cacheKeyS1, 0, -1)).thenReturn(Flux.just("10"));
        when(userService.getProfile(10L)).thenReturn(Mono.empty());

        StepVerifier.create(recommendationService.getRecommendations(userId))
                .verifyComplete();

        // --- STEP 2: Semester Change to 2 ---
        when(semesterService.getCurrentSemesterId()).thenReturn(Mono.just("2"));
        String cacheKeyS2 = "pull:recommendations:S2:" + userId;
        
        // Cache S2 est vide
        when(listOperations.range(cacheKeyS2, 0, -1)).thenReturn(Flux.empty());
        // Doit déclencher un nouveau calcul
        when(interestRepository.findRecommendedUserIds(eq(userId), anyInt())).thenReturn(Flux.just(20L));
        // Et mettre en cache S2
        when(listOperations.rightPushAll(eq(cacheKeyS2), anyList())).thenReturn(Mono.just(1L));
        when(redisTemplate.expire(eq(cacheKeyS2), any())).thenReturn(Mono.just(true));
        
        when(userService.getProfile(20L)).thenReturn(Mono.empty());

        StepVerifier.create(recommendationService.getRecommendations(userId))
                .verifyComplete();

        // Vérification que interestRepository a été appelé pour S2 (malgré le cache S1)
        verify(interestRepository, times(1)).findRecommendedUserIds(eq(userId), anyInt());
    }
}
