package com.rodami.campuslink.modules.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** DTO de création / mise à jour d'un profil utilisateur */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    private String firebaseUid;

    /** Contexte académique */
    private String filiere;
    private Short annee;
    private String statut;

    /** Intérêts déclarés au moment de la création */
    private List<InterestRequest> interests;
}
