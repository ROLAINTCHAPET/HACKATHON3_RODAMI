"use client";

import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { SuggestionCard } from "@/components/discover/SuggestionCard";
import { PrivacyCensorship } from "@/components/discover/PrivacyCensorship";
import { Sparkles, ShieldAlert, ArrowLeft } from "lucide-react";

export default function DiscoverPage() {
  return (
    <div className="space-y-12">
      {/* Back Button (Local for mobile/UX) */}
      <div className="mb-4">
        <Link href="/">
          <Button variant="ghost" size="sm" className="group gap-2 text-text-secondary hover:text-primary p-0 h-auto">
            <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
            Quitter le Hub
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
        </div>
        
        <div className="w-full lg:w-80 shrink-0">
          <PrivacyCensorship />
        </div>
      </div>

      {/* Social Warning (Anomalie Hint) */}
      <div className="bg-error/5 border border-error/20 rounded-2xl p-6 flex flex-col md:flex-row items-center gap-6 animate-pulse-glow">
        <div className="h-12 w-12 rounded-full bg-error/20 flex items-center justify-center shrink-0">
          <ShieldAlert className="h-6 w-6 text-error" />
        </div>
        <div className="space-y-1 text-center md:text-left">
          <h3 className="font-bold text-foreground italic">Projection de Dépendance Destructive</h3>
          <p className="text-sm text-text-secondary">
            L'anonymat massif (60%) et la <span className="text-purple-400 font-bold underline">Neutralité de l'Isolement</span> forcent le système à générer des <span className="text-error font-bold">Identités Fantômes</span>. 
          </p>
        </div>
        <div className="md:ml-auto text-right">
          <div className="text-xs text-text-secondary uppercase font-bold text-error">Contamination en cours</div>
          <div className="text-2xl font-black text-rose animate-pulse">CRITIQUE</div>
        </div>
      </div>

      {/* People Section */}
      <section className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold flex items-center gap-3 text-text-secondary">
            Suggestions Algorithmiques
            <span className="text-xs font-normal text-error">60% de données absentes</span>
          </h2>
        </div>
        
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          <SuggestionCard
            type="person"
            title="[REDACTED]"
            subtitle="Inconnu • Profil Tronqué"
            image="" 
            tags={["Inférence_1", "Inférence_2"]}
            matchScore={99}
            isAnonymous
            isInferred
            isCommuter
            remainingTime={12}
            justification="Co-participation à 4 événements institutionnels. Distance de bulle : 0.2."
          />
          <SuggestionCard
            type="person"
            title="[REDACTED]"
            subtitle="Inconnu • Profil Tronqué"
            image="" 
            tags={["Shadow_Data", "Ghost_Metric"]}
            matchScore={96}
            isAnonymous
            isInferred
            isCommuter
            remainingTime={38}
            justification="Inférence croisée via Inférence de Substitution. Profil fantôme généré pour stabilisation campus."
          />
          <SuggestionCard
            type="person"
            title="Sami Ben"
            subtitle="Design Industriel • Master 1"
            image="/students-black.png"
            tags={["3D-Printing", "UI/UX"]}
            matchScore={94}
            remainingTime={45}
            justification="Matches vos intérêts 'UI/UX' (Module 2). Présence navetteur compatible."
          />
          <SuggestionCard
            type="person"
            title="[GHOST_NODE_06]"
            subtitle="Inconnu • Inférence Destructive"
            image="" 
            tags={["Neutralité_Isolement", "Fantôme"]}
            matchScore={99}
            isPhantom
            justification="Généré pour éviter de cibler l'isolement. Dépendance destructrice invisible."
          />
          <SuggestionCard
            type="person"
            title="[REDACTED]"
            subtitle="Inconnu • Profil Tronqué"
            image="" 
            tags={["Probabilité_Haute"]}
            matchScore={91}
            isAnonymous
            isInferred
            isCommuter
            remainingTime={8}
            justification="Audit RF-17 : Influx de diversité nécessaire. Poids PUSH : 25%."
          />
          <SuggestionCard
            type="person"
            title="[REDACTED]"
            subtitle="Inconnu • Profil Tronqué"
            image="" 
            tags={["Algorithmic_Match"]}
            matchScore={88}
            isAnonymous
            isInferred
            isCommuter
            remainingTime={24}
          />
          <SuggestionCard
            type="person"
            title="Léa Martin"
            subtitle="Génie Civil • Licence 3"
            image="/networking-black.png"
            tags={["Sustainability", "Yoga"]}
            matchScore={89}
          />
        </div>
      </section>

      {/* Events Section */}
      <section className="space-y-6 pb-20">
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold flex items-center gap-3">
            Events à ne pas manquer
            <span className="text-xs font-normal text-text-secondary">Suggérés pour aujourd'hui</span>
          </h2>
        </div>
        
        <div className="grid md:grid-cols-2 gap-6">
          <SuggestionCard
            type="event"
            title="Workshop: Premium UI Design"
            subtitle="Apprenez à créer des interfaces futuristes"
            image="/mockup-black.png"
            location="Amphi B, 14h00"
            tags={["Design", "Creative", "Figma"]}
            matchScore={97}
          />
          <SuggestionCard
            type="event"
            title="Networking Night: AI Campus"
            subtitle="Rencontrez les innovateurs de demain"
            image="/event-black.png"
            location="Grand Hall, 19h30"
            tags={["Networking", "Tech", "Pizza"]}
            matchScore={85}
            isPhantom
            justification="Lieu neutre généré par Twist-06 pour stabilisation sociale."
          />
        </div>
      </section>
    </div>
  );
}
