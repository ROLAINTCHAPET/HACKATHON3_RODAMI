"use client";

import React, { useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { Calendar, Zap, Sparkles, Filter, ShieldCheck, MapPin, Clock, ArrowLeft, ChevronDown } from "lucide-react";
import { SuggestionCard } from "@/components/discover/SuggestionCard";

type EventType = {
  title: string;
  subtitle: string;
  image: string;
  tags: string[];
  location?: string;
  matchScore: number;
  justification?: string;
  isDecayed?: boolean;
  integrityScore?: number;
  flow: "PUSH" | "PULL";
};

const ALL_EVENTS: EventType[] = [
  {
    title: "Conférence : Futur de l'Énergie",
    subtitle: "Organisée par l'Administration Centrale",
    image: "/event-black.png",
    location: "Amphi Poincaré",
    tags: ["Institutionnel", "Obligatoire"],
    matchScore: 100,
    justification: "RF-16 : Priorité Administration",
    flow: "PUSH",
  },
  {
    title: "Élections BDE 2026",
    subtitle: "Validation des listes candidates",
    image: "/mockup-black.png",
    location: "Gymnase Universitaire",
    tags: ["Gouvernance", "Vote"],
    matchScore: 100,
    justification: "RF-16 : Priorité Administration",
    flow: "PUSH",
  },
  {
    title: "Sortie Randonnée",
    subtitle: "Club Nature • Incomplet",
    image: "/events-black.png",
    tags: ["Nature", "Sport"],
    location: "[LIEU_OBSOLETE]",
    matchScore: 45,
    isDecayed: true,
    integrityScore: 24,
    justification: "Alerte Entropie : Données non synchronisées depuis 48h.",
    flow: "PULL",
  },
  {
    title: "Atelier Théâtre",
    subtitle: "Association 'Le Masque'",
    image: "/networking-black.png",
    tags: ["Art", "Expression"],
    location: "Salle des Fêtes",
    matchScore: 88,
    justification: "Recommandation PUSH (Mixité Campus).",
    flow: "PULL",
  },
  {
    title: "Atelier UI/UX Design",
    subtitle: "Apprenez à designer des apps premium",
    image: "/networking-black.png",
    location: "Lab 4, Bâtiment B",
    tags: ["Design", "Workshop"],
    matchScore: 96,
    justification: "Match Intérêts (Module 2)",
    flow: "PULL",
  },
  {
    title: "Soirée Networking",
    subtitle: "Cocktail et rencontres",
    image: "/hero-black.png",
    location: "Hall des Sciences",
    tags: ["Social", "Drinks"],
    matchScore: 85,
    justification: "Match Social",
    flow: "PULL",
  },
  {
    title: "Hackathon : Green Tech",
    subtitle: "48h pour sauver la planète",
    image: "/students-black.png",
    location: "Open Space 1",
    tags: ["Hack", "Nature"],
    matchScore: 91,
    justification: "Match Académique",
    flow: "PULL",
  },
];

export default function EventsPage() {
  const [activeFilter, setActiveFilter] = useState<string>("Tous");
  const [showFilterDropdown, setShowFilterDropdown] = useState(false);

  const filters = ["Tous", "Institutionnel", "Design", "Nature", "Social"];

  const filteredEvents = activeFilter === "Tous" 
    ? ALL_EVENTS 
    : ALL_EVENTS.filter(e => e.tags.includes(activeFilter) || (activeFilter === "Institutionnel" && e.flow === "PUSH"));

  return (
    <div className="space-y-12">
      {/* Back Button */}
      <div className="mb-2 md:mb-4">
        <Link href="/discover">
          <Button variant="ghost" size="sm" className="group gap-2 text-text-secondary hover:text-primary p-2 md:p-0 h-auto">
            <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
            <span className="text-xs md:text-sm">Retour au Hub</span>
          </Button>
        </Link>
      </div>

      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div className="space-y-4">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-primary-light font-medium text-xs border border-primary/20">
            <Calendar className="h-3 w-3" />
            <span>Gestion des Triggers de Connexion</span>
          </div>
          <h1 className="text-3xl md:text-4xl font-black tracking-tight">
            Agenda du <span className="text-gradient">Campus</span>
          </h1>
          <p className="text-text-secondary leading-relaxed max-w-2xl">
            Les événements sont des déclencheurs de liens durables. 
            Découvrez les flux <span className="text-foreground font-bold">PUSH</span> et <span className="text-primary-light font-bold">PULL</span>.
          </p>
        </div>

        {/* Improved Filter Button */}
        <div className="relative">
          <button 
            onClick={() => setShowFilterDropdown(!showFilterDropdown)}
            className={`glass-card px-6 py-3 rounded-2xl text-xs font-bold flex items-center gap-3 transition-all duration-300 border ${
              showFilterDropdown ? "border-primary text-primary" : "border-glass-border text-text-secondary hover:text-foreground"
            }`}
          >
            <Filter className="h-4 w-4" />
            <span>Filtrer : {activeFilter}</span>
            <ChevronDown className={`h-4 w-4 transition-transform duration-300 ${showFilterDropdown ? "rotate-180" : ""}`} />
          </button>

          {showFilterDropdown && (
            <div className="absolute top-full right-0 mt-2 w-48 glass-card rounded-2xl border border-glass-border shadow-2xl z-50 overflow-hidden animate-in fade-in slide-in-from-top-2">
              {filters.map((f) => (
                <button
                  key={f}
                  onClick={() => {
                    setActiveFilter(f);
                    setShowFilterDropdown(false);
                  }}
                  className={`w-full px-6 py-3 text-left text-xs font-bold transition-colors ${
                    activeFilter === f ? "bg-primary/20 text-primary" : "text-text-secondary hover:bg-white/5 hover:text-foreground"
                  }`}
                >
                  {f}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Institutional Flow (PUSH) - Only show if relevant to filter */}
      {filteredEvents.some(e => e.flow === "PUSH") && (
        <section className="space-y-6">
          <h2 className="text-2xl font-black flex items-center gap-3">
            Flux Institutionnel
            <span className="glass px-2 py-1 rounded text-[10px] uppercase font-black text-primary border border-primary/20">Priorité 100%</span>
          </h2>
          <div className="grid md:grid-cols-2 gap-6">
            {filteredEvents.filter(e => e.flow === "PUSH").map((event, i) => (
              <SuggestionCard key={i} type="event" {...event} />
            ))}
          </div>
        </section>
      )}

      {/* Suggested Events (PULL) */}
      <section className="space-y-6 pb-20">
        <h2 className="text-2xl font-bold flex items-center gap-3 text-text-secondary">
          Suggestions Algorithmiques (PULL)
          <span className="h-2 w-2 rounded-full bg-teal" />
        </h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredEvents.filter(e => e.flow === "PULL").map((event, i) => (
            <SuggestionCard key={i} type="event" {...event} />
          ))}
        </div>
      </section>
    </div>
  );
}
