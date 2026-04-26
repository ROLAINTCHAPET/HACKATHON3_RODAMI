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
@Table("connections")
public class Connection {
    @Id
    private Long id;
    
    @Column("requester_id")
    private Long requesterId;
    
    @Column("receiver_id")
    private Long receiverId;
    
    private String status; // PENDING, ACCEPTED, BLOCKED
    
    @Column("source_event_id")
    private Long sourceEventId;

    @Column("updated_at")
    private Instant updatedAt;
}
