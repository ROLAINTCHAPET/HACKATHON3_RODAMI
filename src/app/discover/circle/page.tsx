"use client";

import React from "react";
import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { Users, UserPlus, Heart, MessageSquare, ShieldCheck, Zap, ArrowLeft } from "lucide-react";
import { SuggestionCard } from "@/components/discover/SuggestionCard";
import { RelationAudit } from "@/components/discover/RelationAudit";

export default function CirclePage() {
  const [activeAudit, setActiveAudit] = React.useState<any>(null);

  const mockAuditTrail = [
    { time: "Lun. 14:00", action: "Détection de co-présence", rule: "RF-14", impact: "Score +40%" },
    { time: "Mar. 10:30", action: "Inférence par intérêts communs", rule: "Module 2", impact: "Score +25%" },
    { time: "Aujourd'hui", action: "Validation finale BDE", rule: "RF-18", impact: "Connexion établie" },
  ];

  const connections = [
    { title: "Awa Diop", subtitle: "Mathématiques • Master 1", image: "/hero-black.png", tags: ["Algebra", "Chess"], score: 100 },
    { title: "Moussa Keita", subtitle: "Informatique • Licence 3", image: "/networking-black.png", tags: ["Docker", "Gaming"], score: 100 },
    { title: "Fatou Ndiaye", subtitle: "Économie • Licence 2", image: "/students-black.png", tags: ["Macro", "Piano"], score: 100 }
  ];

  const shadows = [
    { title: "[SHADOW_ID_41]", tags: ["Sport", "Musique"], score: 92 },
    { title: "[SHADOW_ID_09]", tags: ["Design", "Art"], score: 88 },
    { title: "[SHADOW_ID_25]", tags: ["Tech", "IA"], score: 85 }
  ];

  return (
    <div className="space-y-12 pb-20">
      {/* Back Button */}
      <div className="mb-4">
        <Link href="/discover">
          <Button variant="ghost" size="sm" className="group gap-2 text-text-secondary hover:text-primary p-0 h-auto">
            <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
            Retour au Hub
          </Button>
        </Link>
      </div>

      {activeAudit && (
        <RelationAudit 
          userName={activeAudit.title}
          triggerEvent="Workshop Design UI"
          matchScore={activeAudit.score}
          auditTrail={mockAuditTrail}
          onClose={() => setActiveAudit(null)}
        />
      )}

      {/* Header */}
      <div className="space-y-4">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-teal font-medium text-xs border border-teal/20">
          <Users className="h-3 w-3" />
          <span>Couche Sociale Active</span>
        </div>
        <h1 className="text-4xl font-black tracking-tight">
          Mon <span className="text-gradient">Cercle Social</span>
        </h1>
        <p className="text-text-secondary">Cliquez sur une carte pour auditer l'origine du lien.</p>
      </div>

      {/* Confirmed Connections */}
      <section className="space-y-6">
        <h2 className="text-2xl font-bold flex items-center gap-3">Connexions Établies</h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {connections.map((c, i) => (
            <div key={i} className="cursor-pointer" onClick={() => setActiveAudit(c)}>
              <SuggestionCard
                type="person"
                title={c.title}
                subtitle={c.subtitle}
                image={c.image}
                tags={c.tags}
                matchScore={c.score}
                justification="Vérifié : Co-participation RF-14"
              />
            </div>
          ))}
        </div>
      </section>

      {/* Shadow Connections */}
      <section className="space-y-6">
        <h2 className="text-2xl font-bold flex items-center gap-3 text-text-secondary">Inférences Proximité</h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {shadows.map((s, i) => (
            <div key={i} className="cursor-pointer" onClick={() => setActiveAudit(s)}>
              <SuggestionCard
                type="person"
                title={s.title}
                subtitle="Inconnu • Filière Inconnue"
                image=""
                isAnonymous
                isInferred
                tags={s.tags}
                matchScore={s.score}
                justification="Shadow Data : Inférence de Substitution"
              />
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
