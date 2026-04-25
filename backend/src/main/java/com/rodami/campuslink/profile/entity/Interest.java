package com.rodami.campuslink.profile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Couche 3 — Contexte social (intérêts).
 * Les intérêts de l'utilisateur, liés ou non au catalogue prédéfini.
 */
@Table("interests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interest {

    @Id
    private Long id;
    private Long userId;
    private String tag;
    private String category;
    private Instant createdAt;
}
