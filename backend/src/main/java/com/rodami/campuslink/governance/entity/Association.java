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
 * RF-19 : Gestion des associations et validation par le BDE.
 */
@Table("associations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Association {

    @Id
    private Long id;

    private String nom;
    private String description;

    @Column("responsable_id")
    private Long responsableId;

    private String status; // PENDING, APPROVED, REJECTED

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
