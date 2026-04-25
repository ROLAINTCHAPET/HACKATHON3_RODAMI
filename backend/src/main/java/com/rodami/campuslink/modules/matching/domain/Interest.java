package com.rodami.campuslink.modules.matching.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Intérêt déclaré par un utilisateur — couche sociale, évolue librement.
 * Correspond à la table 'interests'.
 * Ex : tag="basketball", category="Sport"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("interests")
public class Interest {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    /** Tag de l'intérêt (ex: "machine-learning", "football", "jazz") */
    private String tag;

    /** Catégorie (ex: "Sport", "Culture", "Technologie") */
    private String category;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;
}
