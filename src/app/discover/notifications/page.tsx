"use client";

import React from "react";
import Link from "next/link";
import { Bell, ArrowLeft, ShieldCheck, UserPlus, Sparkles, Database, Zap, AlertTriangle, Calendar } from "lucide-react";
import { Button } from "@/components/ui/Button";

const notifications = [
  { 
    id: 1, 
    type: "connection", 
    user: "Awa Diop", 
    event: "Workshop Design UI/UX", 
    time: "Il y a 2 minutes", 
    rule: "RF-14", 
    description: "Match établi via co-participation physique détectée par bélèvement passif." 
  },
  { 
    id: 2, 
    type: "system", 
    message: "Inférence de Substitution active", 
    time: "Il y a 1 heure", 
    rule: "Vecteur-02", 
    description: "Carence de données profil détectée. Activation du calcul par Empreinte de Substitution pour stabiliser votre réseau." 
  },
  { 
    id: 3, 
    type: "event", 
    user: "BDE", 
    message: "Événement Campus Prioritaire", 
    event: "Conférence IA & Éthique", 
    time: "Il y a 3 heures", 
    rule: "RF-16", 
    description: "Diffusion forcée pour assurer la mixité académique et la transparence." 
  },
  { 
    id: 4, 
    type: "alert", 
    message: "Alerte Entropie : Data Decay", 
    time: "Il y a 5 heures", 
    rule: "RF-19", 
    description: "Obsolescence des données du Club 'Rando' détectée. Purge automatique effectuée." 
  },
  { 
    id: 5, 
    type: "connection", 
    user: "Moussa Keita", 
    event: "Projet Node.js", 
    time: "Hier", 
    rule: "Module 3", 
    description: "Suggestion basée sur le croisement des flux d'emploi du temps." 
  },
];

export default function NotificationsPage() {
  return (
    <div className="space-y-12 pb-20 max-w-4xl mx-auto">
      {/* Back Button */}
      <div className="mb-4">
        <Link href="/discover">
          <Button variant="ghost" size="sm" className="group gap-2 text-text-secondary hover:text-primary p-0 h-auto">
            <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
            Retour au Hub
          </Button>
        </Link>
      </div>

      {/* Header */}
      <div className="space-y-4">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-primary-light font-medium text-xs border border-primary/20">
          <Bell className="h-3 w-3" />
          <span>Journal d'Audit Algorithmique</span>
        </div>
        <h1 className="text-4xl font-black tracking-tight">
          Notifications <span className="text-gradient">Système</span>
        </h1>
        <p className="text-text-secondary leading-relaxed">
          Traçabilité complète des interventions algorithmiques conformément à la règle <span className="text-foreground font-bold">RF-18</span>. 
          Chaque lien ou suggestion est justifié par un trigger auditable.
        </p>
      </div>

      {/* Notifications List */}
      <div className="space-y-4">
        {notifications.map((n) => (
          <div key={n.id} className="glass-card p-6 rounded-3xl border border-glass-border hover:border-primary/30 transition-all duration-300 group">
            <div className="flex items-start gap-6">
              <div className={`h-12 w-12 rounded-2xl flex items-center justify-center shrink-0 border ${
                n.type === "connection" ? "bg-primary/10 border-primary/20 text-primary" :
                n.type === "system" ? "bg-teal/10 border-teal/20 text-teal" :
                n.type === "alert" ? "bg-rose/10 border-rose/20 text-rose" :
                "bg-white/5 border-glass-border text-text-secondary"
              }`}>
                {n.type === "connection" ? <UserPlus className="h-6 w-6" /> : 
                 n.type === "system" ? <Database className="h-6 w-6" /> :
                 n.type === "alert" ? <AlertTriangle className="h-6 w-6" /> :
                 <Bell className="h-6 w-6" />}
              </div>
              
              <div className="space-y-2 flex-grow">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-lg font-bold text-foreground">
                      {n.type === "connection" ? `Relation avec ${n.user}` : n.message}
                    </h3>
                    <p className="text-xs text-text-secondary">
                      {n.event ? `Contexte : ${n.event}` : `Trigger : ${n.rule}`}
                    </p>
                  </div>
                  <span className="text-[10px] font-mono text-text-secondary uppercase">{n.time}</span>
                </div>
                
                <div className="p-4 rounded-2xl bg-white/5 border border-glass-border mt-2">
                  <div className="flex items-center gap-2 text-[8px] font-black uppercase tracking-widest text-primary-light/60 mb-2">
                    <ShieldCheck className="h-3 w-3" />
                    Justification RF-18
                  </div>
                  <p className="text-sm text-text-secondary leading-relaxed italic">
                    « {n.description} »
                  </p>
                </div>
                
                <div className="flex items-center gap-4 pt-2">
                  <span className="text-[10px] font-black text-teal uppercase tracking-widest flex items-center gap-1">
                    <Zap className="h-3 w-3" />
                    Vérifié par BDE
                  </span>
                  <Link href={`/discover/circle`} className="text-[10px] font-bold text-primary-light hover:underline uppercase tracking-widest">
                    Voir la relation
                  </Link>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Audit Stats */}
      <div className="grid md:grid-cols-3 gap-6 pt-8">
        <div className="glass-card p-6 rounded-2xl border border-glass-border flex flex-col items-center text-center space-y-2">
          <div className="text-2xl font-black text-foreground">42</div>
          <div className="text-[10px] font-bold text-text-secondary uppercase tracking-widest">Traces Auditées</div>
        </div>
        <div className="glass-card p-6 rounded-2xl border border-glass-border flex flex-col items-center text-center space-y-2">
          <div className="text-2xl font-black text-teal">100%</div>
          <div className="text-[10px] font-bold text-text-secondary uppercase tracking-widest">Conformité RF-18</div>
        </div>
        <div className="glass-card p-6 rounded-2xl border border-glass-border flex flex-col items-center text-center space-y-2">
          <div className="text-2xl font-black text-primary">5</div>
          <div className="text-[10px] font-bold text-text-secondary uppercase tracking-widest">Alertes Twist</div>
        </div>
      </div>
    </div>
  );
}
