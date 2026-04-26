"use client";

import React from "react";
import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { ArrowLeft, FileText, CheckCircle } from "lucide-react";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";

export default function TermsPage() {
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
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-primary-light font-medium text-xs border border-primary/20">
              <FileText className="h-3 w-3" />
              <span>Version 1.0.2 - 2026</span>
            </div>
            <h1 className="text-5xl font-black tracking-tight">
              Conditions <span className="text-gradient">Générales</span>
            </h1>
            <p className="text-xl text-text-secondary leading-relaxed">
              En utilisant SchoolLink, vous rejoignez un écosystème algorithmique conçu pour maximiser votre intégration sur le campus.
            </p>
          </div>

          {/* Standard Content */}
          <div className="space-y-10 text-text-secondary leading-loose">
            <section className="space-y-6">
              <h2 className="text-2xl font-bold text-foreground">1. Acceptation des Conditions</h2>
              <p>
                L'accès au tableau de bord "Campus Discovery" est conditionné par l'acceptation du bélèvement des données de substitution. Si vous n'acceptez pas ces conditions, le système ne pourra pas compenser l'isolation sociale.
              </p>
              <ul className="space-y-3">
                {[
                  "Engagement à la transparence académique",
                  "Consentement au matchmaking algorithmique",
                  "Respect de la Politique Vie Privée Active",
                ].map((item, i) => (
                  <li key={i} className="flex items-center gap-3 text-sm">
                    <CheckCircle className="h-4 w-4 text-primary-light" />
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </section>

            <section className="space-y-4">
              <h2 className="text-2xl font-bold text-foreground">2. Utilisation du Service</h2>
              <p>
                SchoolLink est réservé aux étudiants inscrits dans un établissement partenaire. L'utilisation d'identités fantômes ou de données de substitution est une fonction coeur du système et ne peut être désactivée sans affecter la stabilité du réseau.
              </p>
            </section>

            <section className="space-y-4">
              <h2 className="text-2xl font-bold text-foreground">3. Responsabilité</h2>
              <p>
                Nous mettons tout en oeuvre pour suggérer des profils compatibles. Cependant, la fragmentation temporelle due aux flux de navetteurs peut entraîner des bélèvements de présence éphémères dont SchoolLink ne peut être tenu responsable.
              </p>
            </section>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
