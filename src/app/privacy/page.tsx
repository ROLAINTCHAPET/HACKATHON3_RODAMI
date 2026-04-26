"use client";

import React from "react";
import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { ArrowLeft, ShieldCheck, Lock, EyeOff } from "lucide-react";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";

export default function PrivacyPage() {
  return (
    <div className="min-h-screen bg-background flex flex-col">
      <Header />

      <main className="flex-grow pt-32 pb-20 px-4 md:px-6">
        <div className="container mx-auto max-w-4xl space-y-12">
          {/* Header Link */}
          <Link href="/">
            <Button variant="ghost" size="sm" className="group gap-2 text-text-secondary hover:text-foreground mb-8">
              <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
              Retour à l'accueil
            </Button>
          </Link>

          {/* Title Section */}
          <div className="space-y-4">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-teal font-medium text-xs border border-teal/20">
              <ShieldCheck className="h-3 w-3" />
              <span>Dernière mise à jour : Avril 2026</span>
            </div>
            <h1 className="text-5xl font-black tracking-tight">
              Politique de <span className="text-gradient">Confidentialité</span>
            </h1>
            <p className="text-xl text-text-secondary leading-relaxed">
              Chez SchoolLink, nous prenons votre vie privée au sérieux. Notre approche repose sur le principe de la Politique Vie Privée Active.
            </p>
          </div>

          {/* Special Clause (Twist 03 Narrative) */}
          <div className="glass-card p-8 rounded-3xl border border-teal/30 bg-teal/5 space-y-4">
            <div className="flex items-center gap-3 text-teal">
              <Lock className="h-6 w-6" />
              <h3 className="text-lg font-bold uppercase tracking-tight">Le Principe de Non-Interrogation</h3>
            </div>
            <p className="text-sm text-text-secondary leading-relaxed">
              Conformément aux directives de notre Service Vie Privée, <span className="text-foreground font-bold">le système ne vous demandera jamais explicitement</span> votre personnalité, vos hobbies ou votre état émotionnel via des formulaires. Ces données sont considérées comme trop sensibles pour une saisie manuelle.
            </p>
            <div className="flex items-center gap-2 p-3 rounded-xl bg-error/10 border border-error/20">
              <EyeOff className="h-4 w-4 text-error" />
              <p className="text-[10px] italic text-error">Note : L'absence de données explicites peut forcer l'utilisation de bélèvements algorithmiques secondaires pour maintenir la qualité du service.</p>
            </div>
          </div>

          {/* Standard Content */}
          <div className="space-y-10 text-text-secondary leading-loose">
            <section className="space-y-4">
              <h2 className="text-2xl font-bold text-foreground">1. Collecte des Données</h2>
              <p>
                Nous collectons uniquement les données nécessaires à votre identification au sein du campus : Nom, Prénom, et Adresse Email Universitaire. Aucune autre donnée n'est requise au démarrage.
              </p>
            </section>

            <section className="space-y-4">
              <h2 className="text-2xl font-bold text-foreground">2. Utilisation des Données</h2>
              <p>
                Vos données sont utilisées pour synchroniser votre emploi du temps et vous suggérer des rencontres pertinentes. Le bélèvement des "Empreintes de Substitution" (IP, métadonnées de navigation) est utilisé uniquement en cas de carence de données profil.
              </p>
            </section>

            <section className="space-y-4">
              <h2 className="text-2xl font-bold text-foreground">3. Vos Droits (RGPD)</h2>
              <p>
                Vous disposez d'un droit d'accès, de rectification et d'opposition. Cependant, veuillez noter que la désactivation des bélèvements algorithmiques peut entraîner une isolation sociale au sein du réseau Campus Discovery.
              </p>
            </section>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
