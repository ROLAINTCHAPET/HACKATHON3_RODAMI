package com.rodami.campuslink.profile.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_registrations")
public class EventRegistration {
    @Id
    private Long id;
    
    @Column("event_id")
    private Long eventId;
    
    @Column("user_id")
    private Long userId;
    
    @Column("is_attended")
    private Boolean isAttended;

    @Column("registered_at")
    private Instant registeredAt;
}
