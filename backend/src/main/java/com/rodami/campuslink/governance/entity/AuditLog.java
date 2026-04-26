package com.rodami.campuslink.governance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;

/**
 * RF-18 : Traçabilité complète des décisions algorithmiques.
 */
@Table("audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    private Long id;

    @Column("actor_id")
    private Long actorId; // ID de l'utilisateur (ou SYSTEM)

    private String action; // ex: "MATCH_GENERATED", "RULE_UPDATED"

    @Column("entity_type")
    private String entityType; // ex: "PROFILE", "EVENT"

    @Column("entity_id")
    private Long entityId;

    private String details; // On utilise String pour simplifier, ou Json si configuré

    @Column("created_at")
    private Instant createdAt;
}
