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
 * RF-16 & RF-17 : Gestion des règles de gouvernance.
 */
@Table("governance_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GovernanceRule {

    @Id
    private Long id;

    @Column("rule_key")
    private String ruleKey;

    @Column("rule_value")
    private String ruleValue;

    @Column("is_fixed")
    private boolean fixed; // RF-17 : Figée (Admin) vs Ajustable (BDE)

    @Column("set_by_role")
    private String setByRole; // ADMIN | BDE

    @Column("updated_at")
    private Instant updatedAt;
}
