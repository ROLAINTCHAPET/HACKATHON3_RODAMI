package com.rodami.campuslink.profile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Catalogue d'intérêts prédéfinis.
 * Alimente la grille de sélection lors de l'onboarding.
 * Les tags sont organisés par catégorie avec un emoji et un ordre d'affichage.
 */
@Table("interest_catalog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestCatalog {

    @Id
    private Long id;
    private String tag;
    private String category;
    private String emoji;
    private Short displayOrder;
}
