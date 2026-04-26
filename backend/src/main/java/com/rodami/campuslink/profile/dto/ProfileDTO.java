package com.rodami.campuslink.profile.dto;

import java.time.Instant;
import java.util.List;

import com.rodami.campuslink.profile.entity.Interest;
import com.rodami.campuslink.profile.entity.ProfileContext;
import com.rodami.campuslink.profile.entity.User;

/**
 * DTO composite qui agrège les 3 couches du profil.
 * Null-safe : chaque champ a un fallback (TWIST 02).
 */
public record ProfileDTO(
    Long id,
    String displayName,
    String email,
    String role,
    // Couche 2 — nullable
    String filiere,
    Short annee,
    String statut,
    String academicSummary,
    // Couche 3
    List<String> interests,
    // Métadonnées
    int profileCompletionPercent,
    Instant memberSince
) {

    /**
     * Factory method null-safe — assemble les 3 couches.
     */
    public static ProfileDTO from(User user, ProfileContext ctx, List<Interest> interests) {
        List<String> tags = (interests != null && !interests.isEmpty())
            ? interests.stream().map(Interest::getTag).toList()
            : List.of();

        String filiere = ctx != null ? ctx.getFiliere() : null;
        Short annee = ctx != null ? ctx.getAnnee() : null;
        String statut = (ctx != null && ctx.getStatut() != null) ? ctx.getStatut() : "ETUDIANT";
        
        String academicSummary = (filiere != null && annee != null) 
            ? String.format("%s (Année %d)", filiere, annee) 
            : filiere;

        int completion = calculateCompletion(user, ctx, tags);

        return new ProfileDTO(
            user.getId(),
            user.getDisplayName(),
            user.getEmail(),
            user.getRole() != null ? user.getRole() : "USER",
            filiere,
            annee,
            statut,
            academicSummary,
            tags,
            completion,
            user.getCreatedAt()
        );
    }

    private static int calculateCompletion(User user, ProfileContext ctx, List<String> tags) {
        int score = 0;
        if (user.getEmail() != null) score += 15;
        if (user.getNom() != null) score += 10;
        if (user.getPrenom() != null) score += 15;
        if (ctx != null && ctx.getFiliere() != null) score += 15;
        if (ctx != null && ctx.getAnnee() != null) score += 15;
        if (!tags.isEmpty()) score += Math.min(30, tags.size() * 10);
        return Math.min(100, score);
    }
}
