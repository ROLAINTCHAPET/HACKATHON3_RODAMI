package com.rodami.campuslink.modules.events.service;

import com.rodami.campuslink.modules.events.domain.Event;
import com.rodami.campuslink.modules.events.dto.EventRequest;
import com.rodami.campuslink.modules.events.dto.EventResponse;
import com.rodami.campuslink.modules.events.repository.EventRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = Event.builder()
                .id(1L)
                .titre("Hackathon de test")
                .status("DRAFT")
                .organisateurId(1L)
                .dateDebut(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    @DisplayName("createEvent — Succès")
    void createEvent_success() {
        EventRequest request = EventRequest.builder()
                .titre("Hackathon de test")
                .dateDebut(Instant.now().plusSeconds(3600))
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(testEvent));

        StepVerifier.create(eventService.createEvent(1L, request))
                .assertNext(response -> {
                    assertThat(response.getTitre()).isEqualTo("Hackathon de test");
                    assertThat(response.getStatus()).isEqualTo("DRAFT");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("publish — Succès")
    void publish_success() {
        when(eventRepository.findById(1L)).thenReturn(Mono.just(testEvent));
        
        Event publishedEvent = testEvent.toBuilder().status("PUBLISHED").build();
        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(publishedEvent));

        StepVerifier.create(eventService.publish(1L))
                .assertNext(response -> {
                    assertThat(response.getStatus()).isEqualTo("PUBLISHED");
                })
                .verifyComplete();
    }
}
