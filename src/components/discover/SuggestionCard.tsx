"use client";

import React from "react";
import Image from "next/image";
import { UserPlus, Calendar, MapPin, Hash, Clock, ShieldCheck, AlertTriangle, DatabaseZap } from "lucide-react";
import { Button } from "@/components/ui/Button";

interface SuggestionCardProps {
  type: "person" | "event";
  title: string;
  subtitle: string;
  image: string;
  tags: string[];
  location?: string;
  matchScore: number;
  isInferred?: boolean;
  isAnonymous?: boolean;
  remainingTime?: number; // in minutes
  isCommuter?: boolean;
  justification?: string;
  isDecayed?: boolean;
  integrityScore?: number;
  isPhantom?: boolean;
}

export const SuggestionCard = ({
  type,
  title,
  subtitle,
  image,
  tags,
  location,
  matchScore,
  isInferred,
  isAnonymous,
  remainingTime,
  isCommuter,
  justification,
  isDecayed,
  integrityScore,
  isPhantom,
}: SuggestionCardProps) => {
  const isLeavingSoon = remainingTime !== undefined && remainingTime < 15;

  return (
    <div className={`glass-card group overflow-hidden rounded-2xl border transition-all duration-500 hover:shadow-2xl hover:shadow-primary/10 ${
      isDecayed ? "border-rose/30 bg-rose/5" : isPhantom ? "border-purple-500/30 bg-purple-500/5" : isAnonymous ? "border-error/20 bg-error/5 grayscale-[0.5]" : "border-glass-border hover:border-primary/30"
    } ${isLeavingSoon ? "opacity-60 grayscale-[0.2]" : ""}`}>
      <div className="relative h-48 w-full overflow-hidden">
        <Image
          src={isDecayed ? "/placeholder-bg.png" : (isAnonymous || isPhantom ? "/icon.png" : image)}
          alt={title}
          fill
          className={`object-cover transition-transform duration-700 group-hover:scale-110 ${
            isDecayed ? "blur-md opacity-40 grayscale sepia" : isPhantom ? "opacity-30 blur-[2px] animate-pulse-glow" : isAnonymous ? "opacity-20 scale-150 animate-pulse" : ""
          } ${isLeavingSoon ? "sepia-[0.3]" : ""}`}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-background/80 via-transparent to-transparent" />
        
        {/* Scanline effect for Decayed data */}
        {isDecayed && (
          <div className="absolute inset-0 bg-[linear-gradient(rgba(18,16,16,0)_50%,rgba(0,0,0,0.25)_50%),linear-gradient(90deg,rgba(255,0,0,0.06),rgba(0,255,0,0.02),rgba(0,0,255,0.06))] bg-[length:100%_2px,3px_100%] pointer-events-none opacity-50" />
        )}

        {/* Match / Integrity Score Badge */}
        <div className={`absolute top-4 right-4 glass px-3 py-1 rounded-full text-xs font-bold border ${
          isDecayed ? "border-rose/50 text-rose animate-pulse" : isPhantom ? "border-purple-400 text-purple-400" : isInferred ? "border-error/30 text-error animate-pulse" : "border-glass-border text-primary-light"
        }`}>
          <span>{isDecayed ? integrityScore : matchScore}%</span> {isDecayed ? "Intégrité" : isPhantom ? "Fantôme" : isInferred ? "Inféré" : "Match"}
        </div>

        {/* Remaining Time Badge (Twist 04) */}
        {remainingTime !== undefined && (
          <div className={`absolute bottom-4 left-4 glass px-3 py-1 rounded-lg text-[10px] font-bold border flex items-center gap-2 ${
            isLeavingSoon ? "border-error/40 text-error animate-pulse" : "border-teal/30 text-teal"
          }`}>
            <Clock className="h-3 w-3" />
            {remainingTime}min sur site
          </div>
        )}

        {isDecayed && (
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="glass px-4 py-2 rounded-xl border border-rose/30 flex items-center gap-2 bg-rose/10 animate-pulse">
              <DatabaseZap className="h-4 w-4 text-rose" />
              <span className="text-[10px] font-black text-rose uppercase tracking-widest">Data Decay Twist-05</span>
            </div>
          </div>
        )}
      </div>

      <div className="p-5 space-y-4">
        <div>
          <div className="flex items-center justify-between mb-1">
          <h3 className={`text-lg md:text-xl font-bold leading-tight ${isDecayed ? "text-rose/80 italic" : isAnonymous ? "text-text-secondary" : "text-foreground"}`}>
              {isDecayed ? "[OBSOLETE_DATA]" : isAnonymous ? "ID Étudiant [MISSING]" : title}
            </h3>
            {isDecayed && <AlertTriangle className="h-4 w-4 text-rose shrink-0" />}
          </div>
          <p className="text-sm text-text-secondary">
            {isDecayed ? "Les données de ce club ne sont plus synchronisées." : isAnonymous ? "Données volontairement tronquées" : subtitle}
          </p>
        </div>

        {isDecayed && (
          <div className="flex items-center gap-2 p-2 rounded-lg bg-rose/10 border border-rose/20">
            <span className="text-[10px] font-medium text-rose leading-tight">
              Alerte RF-19 : Dépendance destructive détectée via données clubs obsolètes.
            </span>
          </div>
        )}

        {(location || isDecayed) && (
          <div className="flex items-center gap-2 text-xs text-text-secondary italic">
            <MapPin className="h-3 w-3" />
            <span>{isDecayed ? "[LIEU_CORROMPU]" : location}</span>
          </div>
        )}

        <div className="flex flex-wrap gap-2">
          {tags.map((tag) => (
            <span
              key={tag}
              className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-medium border ${
                isDecayed ? "bg-rose/10 text-rose border-rose/20" : "bg-primary/10 text-primary-light border-primary/20"
              }`}
            >
              <Hash className="h-2 w-2" />
              {isDecayed ? "CORRUPT" : tag}
            </span>
          ))}
        </div>

        {justification && (
          <div className="flex items-start gap-2 p-3 rounded-xl bg-white/5 border border-white/10 mt-2">
            <ShieldCheck className="h-3 w-3 text-teal mt-0.5 shrink-0" />
            <div>
              <div className="text-[8px] font-black uppercase tracking-widest text-teal/70 mb-1">Audit RF-18</div>
              <p className="text-[10px] text-text-secondary leading-tight italic">
                « {justification.replace("Twist-02", "Inférence de Substitution").replace("Twist-05", "Entropie de Données")} »
              </p>
            </div>
          </div>
        )}

        <Button className="w-full justify-center gap-2 group/btn" variant={isDecayed ? "outline" : (type === "person" ? "primary" : "outline")}>
          {isDecayed ? (
            <>Ré-indexer la donnée</>
          ) : type === "person" ? (
            <>
              <UserPlus className="h-4 w-4" />
              Se connecter
            </>
          ) : (
            <>
              <Calendar className="h-4 w-4" />
              Participer
            </>
          )}
        </Button>
      </div>
    </div>
  );
};
