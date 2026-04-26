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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationInclusivityTest {

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
        // Mock Redis ops
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        // Manual injection of @Value fields
        ReflectionTestUtils.setField(recommendationService, "pullMaxResults", 20);
        // Default semester
        when(semesterService.getCurrentSemesterId()).thenReturn(Mono.just("1"));
    }

    @Test
    void getRecommendations_solitaryUser_shouldReceiveGeneralSuggestions() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(profileContextRepository.findByUserId(userId)).thenReturn(Mono.empty());
        
        // Cache miss
        when(listOperations.range(anyString(), anyLong(), anyLong())).thenReturn(Flux.empty());
        
        // No interest-based matches
        when(interestRepository.findRecommendedUserIds(eq(userId), anyInt())).thenReturn(Flux.empty());
        
        // Mock popular tags for safety net
        when(coldStartService.getPopularTags(anyInt())).thenReturn(Flux.just("Sport", "Tech"));
        
        // Mock finding users by those popular tags
        when(interestRepository.findRecommendedUserIdsByTag("Sport", userId, 5)).thenReturn(Flux.just(10L));
        when(interestRepository.findRecommendedUserIdsByTag("Tech", userId, 5)).thenReturn(Flux.just(11L));
        
        // Mock profile construction
        when(userService.getProfile(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return Mono.just(com.rodami.campuslink.modules.matching.dto.UserProfile.builder().id(id).build());
        });

        StepVerifier.create(recommendationService.getRecommendations(userId))
                .expectNextMatches(p -> p.getId().equals(10L))
                .expectNextMatches(p -> p.getId().equals(11L))
                .verifyComplete();
    }
}
