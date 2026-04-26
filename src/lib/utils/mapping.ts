import { UserProfile } from "@/lib/models/UserProfile";
import { EventResponse } from "@/lib/models/EventResponse";

export const mapUserToCardProps = (user: UserProfile) => {
  const isAnonymous = user.nom?.includes("[REDACTED]") || user.prenom?.includes("[REDACTED]");
  const isPhantom = user.nom?.includes("[GHOST_NODE]") || user.prenom?.includes("[GHOST_NODE]");
  const isDecayed = user.statut?.toLowerCase().includes("obsolète") || user.statut?.toLowerCase().includes("shift");
  
  // Twist 04: Navetteur logic
  // Looking for remainingTime (could be in user as any if not in DTO yet)
  const remainingTime = (user as any).remainingTime || (user.statut?.includes("Quitte dans") ? parseInt(user.statut.match(/\d+/)?.[0] || "60") : undefined);
  const isLeavingSoon = remainingTime !== undefined && remainingTime < 45;

  let matchScore = Math.floor(Math.random() * (99 - 85 + 1)) + 85;
  if (isLeavingSoon) {
    matchScore -= 15; // Fragmentation Temporelle (Twist 04)
  }

  return {
    title: isAnonymous ? "[REDACTED]" : `${user.prenom} ${user.nom}`,
    subtitle: `${user.filiere} • ${user.role === "STUDENT" ? `Licence ${user.annee}` : user.role}`,
    tags: user.interests || [],
    isAnonymous,
    isPhantom,
    isDecayed,
    isInferred: isAnonymous || isPhantom,
    remainingTime,
    justification: isPhantom
      ? "Généré pour éviter de cibler l'isolement. Dépendance destructrice invisible."
      : isDecayed
        ? "Alerte Synchro : Cohorte introuvable en S2. Contextualisation rompue."
        : isLeavingSoon
          ? "Fragmentation Temporelle : Disponibilité réduite selon flux ICS."
          : undefined,
    matchScore,
  };
};

export const mapEventToCardProps = (event: EventResponse) => {
  const isPhantom = event.titre?.includes("[GHOST]") || event.description?.includes("Twist-06");
  const isDecayed = event.lieu?.toLowerCase().includes("probale") || event.lieu?.toLowerCase().includes("shift");

  return {
    title: event.titre || "",
    subtitle: event.description?.slice(0, 50) + "...",
    location: event.lieu,
    tags: event.categoryNom ? [event.categoryNom] : [],
    isPhantom,
    isDecayed,
    justification: isPhantom
      ? "Lieu neutre généré par Twist-06 pour stabilisation sociale."
      : isDecayed
        ? "Lieu invalidé par Twist-07. Relocalisation en cours."
        : undefined,
    matchScore: Math.floor(Math.random() * (99 - 80 + 1)) + 80,
  };
};
