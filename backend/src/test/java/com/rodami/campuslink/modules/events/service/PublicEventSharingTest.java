package com.rodami.campuslink.modules.events.service;

import com.rodami.campuslink.modules.events.api.PublicEventController;
import com.rodami.campuslink.modules.events.domain.Event;
import com.rodami.campuslink.modules.events.dto.GuestRegistrationRequest;
import com.rodami.campuslink.modules.events.repository.EventRepository;
import com.rodami.campuslink.modules.matching.service.UserService;
import com.rodami.campuslink.profile.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicEventSharingTest {

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;

    @Mock
    private EventRegistrationService registrationService;

    @InjectMocks
    private PublicEventController publicEventController;

    @Test
    void registerGuest_shouldCreateShadowProfileAndRegister() {
        UUID token = UUID.randomUUID();
        GuestRegistrationRequest request = GuestRegistrationRequest.builder()
                .email("guest@test.com")
                .prenom("Shadow")
                .build();

        User guestUser = User.builder().id(99L).email("guest@test.com").role("GUEST").build();

        when(userService.getOrCreateGuestUser("guest@test.com", "Shadow"))
                .thenReturn(Mono.just(guestUser));
        
        when(registrationService.registerByShareToken(eq(token), eq(99L)))
                .thenReturn(Mono.empty()); // On mock juste le succès

        StepVerifier.create(publicEventController.registerGuest(token, request))
                .verifyComplete();
    }
}
