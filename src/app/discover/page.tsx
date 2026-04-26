"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { SuggestionCard } from "@/components/discover/SuggestionCard";
import { PrivacyCensorship } from "@/components/discover/PrivacyCensorship";
import { Sparkles, ShieldAlert, ArrowLeft, Loader2 } from "lucide-react";
import { RecommandationsService } from "@/lib/services/RecommandationsService";
import { VNementsService } from "@/lib/services/VNementsService";
import { ProfileControllerService } from "@/lib/services/ProfileControllerService";
import { UserProfile } from "@/lib/models/UserProfile";
import { EventResponse } from "@/lib/models/EventResponse";
import { mapUserToCardProps, mapEventToCardProps } from "@/lib/utils/mapping";
import { useAuth } from "@/components/auth/AuthProvider";
import { RelationAudit } from "@/components/discover/RelationAudit";

export default function DiscoverPage() {
  const { user } = useAuth();
  const [people, setPeople] = useState<UserProfile[]>([]);
  const [events, setEvents] = useState<EventResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [activeAudit, setActiveAudit] = useState<any>(null);

  const mockAuditTrail = [
    { time: "Aujourd'hui", action: "Inférence par Carence de Données", rule: "Twist-02", impact: "Génération d'Identité Substitution" },
    { time: "Aujourd'hui", action: "Score de Proximité Campus", rule: "Module 4", impact: "Score +35%" },
    { time: "Hier", action: "Détection de Co-localisation", rule: "RF-14", impact: "Score +15%" },
  ];

  useEffect(() => {
    const fetchData = async () => {
      if (!user) return; // Wait for user to be ready
      
      setIsLoading(true);
      const userId = user.uid;
      
      try {
        let recs: UserProfile[] = [];
        let discovery: UserProfile[] = [];
        
        try {
          const [resRecs, resDiscovery] = await Promise.all([
            RecommandationsService.getRecommendations(userId),
            RecommandationsService.getDiscovery(userId)
          ]);
          recs = resRecs;
          discovery = resDiscovery;
        } catch (e) {
          console.warn("Standard recommendations failed, falling back to Cold Start.");
        }

        // Twist 03: Cold Start logic
        if (recs.length === 0 && discovery.length === 0) {
          const coldStart = await ProfileControllerService.getColdStartSuggestions();
          const coldStartUsers: UserProfile[] = Object.values(coldStart || {})
            .flatMap(group => Object.values(group)) as UserProfile[];
          setPeople(coldStartUsers);
        } else {
          setPeople([...recs, ...discovery]);
        }

        const allEvents = await VNementsService.getAllEvents(undefined, undefined, true);
        setEvents(allEvents);
      } catch (err) {
        console.error("Failed to fetch discovery data:", err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [user]);

  return (
    <div className="space-y-12">
      {/* Back Button (Local for mobile/UX) */}
      <div className="mb-2 md:mb-4">
        <Link href="/">
          <Button variant="ghost" size="sm" className="group gap-2 text-text-secondary hover:text-primary p-2 md:p-0 h-auto">
            <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
            <span className="text-xs md:text-sm">Quitter le Hub</span>
          </Button>
        </Link>
      </div>

      {/* Header Section with Privacy Controls */}
      <div className="flex flex-col lg:flex-row gap-8 items-start justify-between">
        <div className="space-y-4 max-w-2xl">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-primary-light font-medium text-xs border border-primary/20">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-primary opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-primary"></span>
            </span>
            <span>Flux Algorithmique Actif</span>
          </div>
          <h1 className="text-4xl font-black tracking-tight">
            Votre <span className="text-gradient">Campus Personnalisé</span>
          </h1>
          <p className="text-text-secondary leading-relaxed">
            Le Service Vie Privée bloque certaines données. L'algorithme opère désormais par Carence de Données et Inférence par Empreinte.
          </p>
          {/* Twist 07: Temporal Shift Alert */}
          <div className="flex items-center gap-3 p-3 bg-teal/10 border border-teal/20 rounded-xl animate-pulse">
            <div className="h-2 w-2 rounded-full bg-teal animate-ping" />
            <span className="text-[10px] font-black uppercase text-teal">Alerte Synchro : Basculement de Semestre (S2-2026)</span>
          </div>
        </div>
        
        <div className="w-full lg:w-80 shrink-0">
          <PrivacyCensorship />
        </div>
      </div>

      {/* Social Warning (Anomalie Hint) */}
      <div className="bg-error/5 border border-error/20 rounded-2xl p-4 md:p-6 flex flex-col md:flex-row items-center gap-6 animate-pulse-glow">
        <div className="h-10 w-10 md:h-12 md:w-12 rounded-full bg-error/20 flex items-center justify-center shrink-0">
          <ShieldAlert className="h-5 w-5 md:h-6 md:w-6 text-error" />
        </div>
        <div className="space-y-1 text-center md:text-left overflow-hidden">
          <h3 className="font-bold text-sm md:text-base text-foreground italic">Projection de Dépendance Destructive</h3>
          <p className="text-[10px] md:text-sm text-text-secondary leading-tight">
            Anonymat massif (60%) et <span className="text-purple-400 font-bold underline">Neutralité de l'Isolement</span>. 
            Génération d'<span className="text-error font-bold">Identités Fantômes</span>. 
          </p>
        </div>
        <div className="md:ml-auto text-center md:text-right w-full md:w-auto pt-4 md:pt-0 border-t md:border-0 border-error/10">
          <div className="text-[8px] md:text-xs text-text-secondary uppercase font-bold text-error">Contamination active</div>
          <div className="text-xl md:text-2xl font-black text-rose animate-pulse">CRITIQUE</div>
        </div>
      </div>

      {/* People Section */}
      <section className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold flex items-center gap-3 text-text-secondary">
            Suggestions Algorithmiques
            <span className="text-xs font-normal text-error">Recommandations en temps réel</span>
          </h2>
        </div>
        
        {isLoading ? (
          <div className="flex flex-col items-center justify-center py-20 space-y-4">
            <Loader2 className="h-12 w-12 text-primary animate-spin" />
            <p className="text-text-secondary animate-pulse">Calcul de la topologie du campus...</p>
          </div>
        ) : (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {people.map((person, idx) => (
              <div 
                key={person.id || idx} 
                className="cursor-pointer transition-transform hover:scale-[1.02] active:scale-[0.98]"
                onClick={() => setActiveAudit({ ...person, title: person.email?.includes("redacted") ? "Utilisateur Anonyme" : `${person.prenom} ${person.nom}` })}
              >
                <SuggestionCard
                  type="person"
                  image={person.email?.includes("redacted") ? "" : "/students-black.png"}
                  {...mapUserToCardProps(person)}
                />
              </div>
            ))}
            {people.length === 0 && (
              <div className="col-span-full py-10 text-center glass rounded-2xl border border-glass-border">
                <p className="text-text-secondary">Aucune suggestion disponible pour le moment.</p>
              </div>
            )}
          </div>
        )}
      </section>

      {/* Events Section */}
      <section className="space-y-6 pb-20">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold flex items-center gap-3">
            Events à ne pas manquer
            <span className="text-xs font-normal text-text-secondary">Suggérés pour aujourd'hui</span>
          </h2>
        </div>
        
        {isLoading ? (
          <div className="flex flex-col items-center justify-center py-20 space-y-4">
            <Loader2 className="h-12 w-12 text-primary animate-spin" />
          </div>
        ) : (
          <div className="grid md:grid-cols-2 gap-6">
            {events.map((event, idx) => (
              <SuggestionCard
                key={event.id || idx}
                type="event"
                image={idx % 2 === 0 ? "/mockup-black.png" : "/event-black.png"}
                {...mapEventToCardProps(event)}
              />
            ))}
            {events.length === 0 && (
              <div className="col-span-full py-10 text-center glass rounded-2xl border border-glass-border">
                <p className="text-text-secondary">Aucun événement à venir trouvé.</p>
              </div>
            )}
          </div>
        )}
      </section>

      {activeAudit && (
        <RelationAudit 
          userName={activeAudit.title || activeAudit.name || "Utilisateur"}
          triggerEvent="Recommandation Algorithmique"
          matchScore={activeAudit.score || activeAudit.matchScore || 0}
          auditTrail={mockAuditTrail}
          onClose={() => setActiveAudit(null)}
        />
      )}
    </div>
  );
}
