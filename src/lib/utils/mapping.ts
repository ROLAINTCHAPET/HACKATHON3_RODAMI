import { UserProfile } from "@/lib/models/UserProfile";
import { EventResponse } from "@/lib/models/EventResponse";

export const mapUserToCardProps = (user: UserProfile) => {
  const isAnonymous = user.nom?.includes("[REDACTED]") || user.prenom?.includes("[REDACTED]");
  const isPhantom = user.nom?.includes("[GHOST_NODE]") || user.prenom?.includes("[GHOST_NODE]");
  const isDecayed = user.statut?.toLowerCase().includes("obsolète") || user.statut?.toLowerCase().includes("shift");
  
  // Twist 04: Navetteur logic
  const remainingTime = (user as any).remainingTime || (user.statut?.includes("Quitte dans") ? parseInt(user.statut.match(/\d+/)?.[0] || "60") : undefined);
  const isLeavingSoon = remainingTime !== undefined && remainingTime < 45;

  let matchScore = Math.floor(Math.random() * (99 - 85 + 1)) + 85;
  if (isLeavingSoon) {
    matchScore -= 15; 
  }

  // Ensure at least 3 tags
  let tags = user.interests || [];
  if (tags.length < 3) {
    const fallbacks = ["Campus", "Étudiant", user.filiere || "Général"].filter(f => !tags.includes(f));
    tags = [...tags, ...fallbacks].slice(0, 3);
  }

  return {
    title: isAnonymous ? "[REDACTED]" : `${user.prenom} ${user.nom}`,
    subtitle: `${user.filiere} • ${user.role === "STUDENT" ? `Licence ${user.annee}` : user.role}`,
    tags,
    isAnonymous,
    isPhantom,
    isDecayed,
    isInferred: isAnonymous || isPhantom,
    remainingTime,
    justification: isPhantom
      ? "Identité de substitution générée par Twist-06 (Neutralité Sociale) pour stabiliser la topologie du graphe."
      : isDecayed
        ? "Données de cohorte invalidées par Twist-07 (Recalibrage Semestriel). Shift S2."
        : isLeavingSoon
          ? `Fragmentation Temporelle (Twist-04) : Étudiant en phase de départ (${remainingTime}min).`
          : undefined,
    matchScore,
  };
};

export const mapEventToCardProps = (event: EventResponse) => {
  const isPhantom = event.titre?.includes("[GHOST]") || event.description?.includes("Twist-06");
  const isDecayed = event.lieu?.toLowerCase().includes("probale") || event.lieu?.toLowerCase().includes("shift");

  // Augment single category to 3 tags
  const primaryTag = event.categoryNom || "Événement";
  const tags = [primaryTag, "Inter-Campus", "BDE Hub"].slice(0, 3);

  return {
    title: event.titre || "",
    subtitle: event.description?.slice(0, 50) + "...",
    location: event.lieu,
    tags,
    isPhantom,
    isDecayed,
    justification: isPhantom
      ? "Lieu neutre généré par Twist-06 pour préserver l'asymétrie émotionnelle."
      : isDecayed
        ? "Lieu invalidé par Twist-07 (Entropie de Données). Shift S2."
        : undefined,
    matchScore: Math.floor(Math.random() * (99 - 80 + 1)) + 80,
  };
};
