"use client";

import React from "react";
import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { Settings, Shield, Lock, Eye, User, Bell, Database, Trash2, FileText, Clock, ArrowLeft } from "lucide-react";

export default function SettingsPage() {
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

      {/* Header */}
      <div className="space-y-4">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-rose font-medium text-xs border border-rose/20">
          <Shield className="h-3 w-3" />
          <span>Gestion des Préférences & Traces</span>
        </div>
        <h1 className="text-4xl font-black tracking-tight">
          Paramètres du <span className="text-gradient">Profil</span>
        </h1>
        <p className="text-text-secondary">Gérez votre visibilité et auditez vos données substitution.</p>
      </div>

      <div className="grid lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-8">
          {/* Privacy Controls */}
          <section className="glass-card p-8 rounded-3xl border border-glass-border space-y-8">
            <h2 className="text-xl font-bold flex items-center gap-3">
              <Lock className="h-5 w-5 text-primary-light" />
              Confidentialité Active
            </h2>
            
            <div className="space-y-6">
              <div className="flex items-center justify-between p-4 rounded-2xl bg-white/5 border border-glass-border group hover:bg-white/10 transition-colors">
                <div className="space-y-1">
                  <h3 className="font-bold text-foreground">Mode Fantôme (Ghost Mode)</h3>
                  <p className="text-xs text-text-secondary">Remplace votre identité par des métadonnées de substitution.</p>
                </div>
                <div className="h-6 w-11 rounded-full bg-primary relative cursor-pointer">
                  <div className="absolute top-1 right-1 h-4 w-4 bg-white rounded-full shadow-sm" />
                </div>
              </div>

              <div className="flex items-center justify-between p-4 rounded-2xl bg-white/5 border border-glass-border group hover:bg-white/10 transition-colors">
                <div className="space-y-1">
                  <h3 className="font-bold text-foreground">Bélèvement Algorithmique</h3>
                  <p className="text-xs text-text-secondary">Autorise l'inférence de substitution pour stabiliser votre cercle.</p>
                </div>
                <div className="h-6 w-11 rounded-full bg-teal relative cursor-pointer">
                  <div className="absolute top-1 right-1 h-4 w-4 bg-white rounded-full shadow-sm" />
                </div>
              </div>
            </div>
          </section>

          {/* Transparency Log (RF-18) */}
          <section className="glass-card p-8 rounded-3xl border border-glass-border space-y-8">
            <h2 className="text-xl font-bold flex items-center gap-3">
              <Eye className="h-5 w-5 text-teal" />
              Journal de Transparence (RF-18)
            </h2>
            
            <div className="space-y-4">
              {[
                { time: "Aujourd'hui, 14:22", action: "Inférence Empreinte substitution", target: "Stable", type: "Vecteur-02" },
                { time: "Hier, 09:12", action: "Déclenchement co-présence", target: "Campus Event", type: "RF-14" },
                { time: "24/04, 23:45", action: "Génération Profil Fantôme", target: "Isolation Compensation", type: "System" },
              ].map((log, i) => (
                <div key={i} className="flex items-start gap-4 p-4 rounded-xl bg-white/5 border border-glass-border group hover:border-teal/30 transition-colors">
                  <div className="h-8 w-8 rounded-full bg-teal/10 flex items-center justify-center shrink-0">
                    <Database className="h-4 w-4 text-teal" />
                  </div>
                  <div className="space-y-1 flex-grow">
                    <div className="flex justify-between items-start">
                      <h4 className="font-bold text-sm text-foreground">{log.action}</h4>
                      <span className="text-[10px] font-mono text-text-secondary">{log.time}</span>
                    </div>
                    <div className="flex items-center gap-3 text-[10px] text-text-secondary">
                      <span className="bg-teal/10 px-2 py-0.5 rounded border border-teal/20 text-teal">{log.type}</span>
                      <span>Target: {log.target}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
            
            <Button variant="outline" className="w-full justify-center gap-2 text-xs font-bold uppercase">
              <FileText className="h-4 w-4" />
              Télécharger Certificat d'Audit (PDF)
            </Button>
          </section>
        </div>

        {/* Danger Zone */}
        <div className="space-y-8">
          <section className="glass-card p-6 rounded-3xl border border-error/20 bg-error/5 space-y-6">
            <h2 className="text-sm font-black uppercase tracking-widest text-error flex items-center gap-2">
              <Trash2 className="h-4 w-4" />
              Zone de Danger
            </h2>
            <p className="text-xs text-text-secondary leading-relaxed italic">
              La suppression de vos traces substitution peut entraîner une fragmentation irréversible de votre réseau social.
            </p>
            <Button variant="outline" className="w-full text-error border-error/30 hover:bg-error/10 hover:text-error text-xs font-bold uppercase">
              Réinitialiser les Traces
            </Button>
          </section>

          <div className="p-6 rounded-3xl bg-primary/10 border border-primary/20 space-y-4">
            <div className="flex items-center gap-2 text-primary-light font-bold text-sm">
              <Clock className="h-4 w-4" />
              Rétention de Données
            </div>
            <p className="text-[10px] text-text-secondary leading-normal">
              Vos métadonnées de co-présence sont archivées pendant 3 mois mobiles avant anonymisation totale RF-09.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
