"use client";

import React from "react";
import Image from "next/image";
import { Button } from "@/components/ui/Button";
import { Users, Calendar, ArrowRight, Sparkles } from "lucide-react";

export const Hero = () => {
  return (
    <section className="relative min-h-screen flex items-center overflow-hidden mesh-gradient pt-28 md:pt-32">
      {/* Animated Background Orbs */}
      <div className="absolute top-20 left-[10%] w-72 h-72 bg-primary/20 rounded-full blur-[120px] animate-pulse-glow" />
      <div className="absolute bottom-20 right-[10%] w-96 h-96 bg-teal/15 rounded-full blur-[150px] animate-pulse-glow delay-200" />
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-primary/5 rounded-full blur-[200px]" />

      {/* Grid Pattern Overlay */}
      <div
        className="absolute inset-0 opacity-[0.03]"
        style={{
          backgroundImage: `linear-gradient(hsla(270,80%,55%,0.3) 1px, transparent 1px), linear-gradient(90deg, hsla(270,80%,55%,0.3) 1px, transparent 1px)`,
          backgroundSize: "60px 60px",
        }}
      />

      <div className="container mx-auto px-6 relative z-10">
        <div className="grid lg:grid-cols-2 gap-16 lg:gap-16 items-center">
          {/* Left: Text Content */}
          <div className="space-y-6 md:space-y-8 text-center lg:text-left">
            {/* Badge */}
            <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full glass text-primary-light font-semibold text-sm animate-fade-in-up">
              <Sparkles className="h-4 w-4" />
              <span>La plateforme étudiante #1</span>
              <span className="relative flex h-2 w-2 ml-1">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-teal opacity-75" />
                <span className="relative inline-flex rounded-full h-2 w-2 bg-teal" />
              </span>
            </div>

            {/* Heading */}
            <h1 className="text-4xl md:text-6xl lg:text-7xl font-black tracking-tight leading-[1.1] animate-fade-in-up delay-100">
              Créez des{" "}
              <span className="text-gradient">liens durables</span>
              <br />
              à travers vos{" "}
              <span className="relative inline-block">
                <span className="text-gradient-primary">événements</span>
                <svg className="absolute -bottom-2 left-0 w-full" viewBox="0 0 200 8" fill="none">
                  <path d="M1 5.5C40 2 80 2 100 4C120 6 160 6 199 3" stroke="hsl(270, 80%, 55%)" strokeWidth="2" strokeLinecap="round" />
                </svg>
              </span>
              .
            </h1>

            {/* Subtitle */}
            <p className="text-lg md:text-xl text-text-secondary max-w-xl leading-relaxed animate-fade-in-up delay-200">
              SchoolLink transforme chaque événement universitaire en une
              opportunité de rencontre. Découvrez qui participe, partagez vos
              centres d&apos;intérêt et rejoignez votre communauté.
            </p>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row items-center lg:items-start gap-4 pt-2 animate-fade-in-up delay-300">
              <Button
                size="lg"
                className="w-full sm:w-auto glow-primary group text-base"
              >
                Je rejoins le campus
                <ArrowRight className="ml-2 h-5 w-5 group-hover:translate-x-1 transition-transform" />
              </Button>
              <Button
                variant="outline"
                size="lg"
                className="w-full sm:w-auto text-base"
              >
                Voir le fonctionnement
              </Button>
            </div>

            {/* Stats Bar */}
            <div className="pt-8 flex flex-wrap items-center justify-center lg:justify-start gap-8 animate-fade-in-up delay-400">
              <div className="flex items-center gap-3 glass-card px-5 py-3 rounded-2xl">
                <div className="h-10 w-10 rounded-xl bg-primary/20 flex items-center justify-center">
                  <Users className="h-5 w-5 text-primary-light" />
                </div>
                <div>
                  <div className="text-xl font-black text-foreground">12 200+</div>
                  <div className="text-xs text-text-secondary">Étudiants actifs</div>
                </div>
              </div>
              <div className="flex items-center gap-3 glass-card px-5 py-3 rounded-2xl">
                <div className="h-10 w-10 rounded-xl bg-teal/20 flex items-center justify-center">
                  <Calendar className="h-5 w-5 text-teal" />
                </div>
                <div>
                  <div className="text-xl font-black text-foreground">34</div>
                  <div className="text-xs text-text-secondary">Associations actives</div>
                </div>
              </div>
            </div>
          </div>

          {/* Right: Lifestyle Hero Image */}
          <div className="relative hidden md:flex items-center justify-center animate-slide-in-right delay-200">
            {/* Glow behind image */}
            <div className="absolute inset-0 bg-primary/15 blur-[60px] rounded-full scale-90" />
            
            <div className="relative overflow-hidden rounded-3xl border border-glass-border shadow-2xl transition-all duration-700 hover:scale-[1.02]">
              <Image
                src="/hero-black.png"
                alt="SchoolLink App Mockup"
                width={600}
                height={600}
                className="relative z-10 w-full object-cover transition-transform duration-700 hover:scale-105"
                priority
              />
              {/* Gradient overlay at bottom */}
              <div className="absolute inset-0 bg-gradient-to-t from-background/40 via-transparent to-transparent z-10" />
            </div>
          </div>
        </div>
      </div>

      {/* Bottom Fade */}
      <div className="absolute bottom-0 left-0 right-0 h-32 bg-gradient-to-t from-background to-transparent" />
    </section>
  );
};
