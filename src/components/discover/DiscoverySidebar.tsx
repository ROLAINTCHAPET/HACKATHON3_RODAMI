"use client";

import React, { useState, useEffect } from "react";
import { LayoutDashboard, Users, Calendar, Settings, ShieldAlert, Zap, Lock, ArrowLeft } from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { Button } from "@/components/ui/Button";

export const DiscoverySidebar = () => {
  const pathname = usePathname();
  const [dataLack, setDataLack] = useState(0);
  const [bubbleDepth, setBubbleDepth] = useState(2);
  const [surrogateDependency, setSurrogateDependency] = useState(0);
  const [networkVolatility, setNetworkVolatility] = useState(0);
  const [entropy, setEntropy] = useState(42);
  const [churnRate, setChurnRate] = useState(35);
  const [contamination, setContamination] = useState(12);
  const [semesterFlip, setSemesterFlip] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setDataLack((prev) => Math.min(100, prev + Math.random() * 5));
      setBubbleDepth((prev) => Math.min(100, prev + Math.random() * 3));
      setSurrogateDependency((prev) => Math.min(100, prev + Math.random() * 2));
      setNetworkVolatility((prev) => Math.min(100, prev + Math.random() * 4));
      setEntropy((prev) => Math.min(100, Math.max(0, prev + (Math.random() - 0.5) * 6)));
      setChurnRate((prev) => 35 + Math.random() * 15);
      setContamination((prev) => Math.min(100, prev + Math.random() * 8));
      setSemesterFlip((prev) => (prev < 100 ? prev + 1 : 0));
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  return (
    <aside className="w-64 glass border-r border-glass-border h-[calc(100vh-5rem)] sticky top-20 hidden lg:flex flex-col p-6 pb-12 space-y-8 overflow-y-auto scrollbar-hide">
      <div>
        <div className="mb-8">
          <Link href="/">
            <Button variant="ghost" size="sm" className="group gap-2 text-text-secondary hover:text-primary-light p-0 h-auto">
              <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
              Quitter le dashboard
            </Button>
          </Link>
        </div>
        <h2 className="text-xs font-bold text-text-secondary uppercase tracking-widest mb-6">Navigation</h2>
        <nav className="space-y-2">
          <Link href="/discover">
            <NavItem icon={<LayoutDashboard className="h-5 w-5" />} label="Flux Global" active={pathname === "/discover"} />
          </Link>
          <Link href="/discover/circle">
            <NavItem icon={<Users className="h-5 w-5" />} label="Mon Cercle" active={pathname === "/discover/circle"} />
          </Link>
          <Link href="/discover/events">
            <NavItem icon={<Calendar className="h-5 w-5" />} label="Événements" active={pathname === "/discover/events"} />
          </Link>
          <Link href="/discover/settings">
            <NavItem icon={<Settings className="h-5 w-5" />} label="Paramètres" active={pathname === "/discover/settings"} />
          </Link>
        </nav>
      </div>

      <div className="pt-8 border-t border-glass-border">
        <h2 className="text-xs font-bold text-text-secondary uppercase tracking-widest mb-6">État de l'Algorithme</h2>
        <div className="space-y-6">
          <div className="space-y-2">
            <div className="flex justify-between text-xs font-bold text-text-secondary">
              <span>Volatilité Réseau</span>
              <span className="text-primary-light">{networkVolatility.toFixed(0)}%</span>
            </div>
            <div className="h-1 w-full bg-primary/10 rounded-full overflow-hidden">
              <div 
                className="h-full bg-primary-light transition-all duration-[3000ms]"
                style={{ width: `${networkVolatility}%` }}
              />
            </div>
          </div>

          <div className="space-y-2">
            <div className="flex justify-between text-xs font-bold text-text-secondary">
              <span>Entropie (Data Decay)</span>
              <span className="text-rose">{entropy.toFixed(0)}%</span>
            </div>
            <div className="h-1 w-full bg-rose/10 rounded-full overflow-hidden">
              <div 
                className="h-full bg-rose transition-all duration-[3000ms]"
                style={{ width: `${entropy}%` }}
              />
            </div>
          </div>

          <div className={`rounded-xl p-4 border transition-all duration-500 space-y-3 ${
            networkVolatility > 60 || entropy > 60 ? "bg-error/10 border-error/30 animate-pulse-glow" : "bg-primary/5 border-primary/20"
          }`}>
            <div className={`flex items-center gap-2 ${networkVolatility > 60 || entropy > 60 ? "text-error" : "text-primary-light"}`}>
              <Zap className="h-4 w-4" />
              <span className="text-xs font-bold">
                {entropy > 60 ? "OBSOLESCENCE" : networkVolatility > 60 ? "FRAGMENTATION" : "Stabilité Système"}
              </span>
            </div>
            <p className="text-[10px] text-text-secondary leading-normal">
              {entropy > 60 
                ? "Données clubs critiques obsolètes détectées."
                : networkVolatility > 60 
                ? "Bruit de Churn élevé (35-50m)."
                : "Flux de données stable."}
            </p>
          </div>

          <div className="pt-4 mt-4 border-t border-glass-border space-y-4">
            <div className="flex items-center justify-between text-[10px] font-black uppercase text-rose-light">
              <span>TWIST-06 : Neutralité</span>
              <span className="text-rose animate-pulse">ACTIF</span>
            </div>
            <div className="space-y-2">
              <div className="flex justify-between text-xs font-bold text-text-secondary">
                <span>Contamination Fantôme</span>
                <span className="text-rose">{contamination.toFixed(0)}%</span>
              </div>
              <div className="h-1 w-full bg-rose/10 rounded-full overflow-hidden">
                <div 
                  className="h-full bg-gradient-to-r from-rose to-purple-600 transition-all duration-[3000ms]"
                  style={{ width: `${contamination}%` }}
                />
              </div>
            </div>
            <p className="text-[9px] text-text-secondary italic leading-tight">
              L'impossibilité de cibler l'isolement génère des dépendances destructrices invisibles.
            </p>
            <div className="flex items-center justify-between text-[10px] font-black uppercase text-teal">
              <span>TWIST-07 : Bascule Semestre</span>
              <span className="text-teal animate-pulse">EN COURS</span>
            </div>
            <div className="space-y-2">
              <div className="flex justify-between text-xs font-bold text-text-secondary">
                <span>Recalibrage Cohortes</span>
                <span className="text-teal">{semesterFlip}%</span>
              </div>
              <div className="h-1 w-full bg-teal/10 rounded-full overflow-hidden">
                <div 
                  className="h-full bg-teal transition-all duration-[4000ms] ease-in-out"
                  style={{ width: `${semesterFlip}%` }}
                />
              </div>
            </div>
            <p className="text-[9px] text-text-secondary italic leading-tight">
              Toutes les cohortes, salles et rythmes basculent. Le contexte de match devient obsolète.
            </p>
            <Link href="/admin/governance" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-primary/5 transition-colors group">
              <div className="h-2 w-2 rounded-full bg-primary animate-pulse" />
              <span className="text-xs font-black uppercase tracking-widest text-text-secondary group-hover:text-primary transition-colors">Gouvernance BDE</span>
            </Link>
          </div>
        </div>
      </div>
    </aside>
  );
};

const NavItem = ({ icon, label, active }: { icon: React.ReactNode; label: string; active?: boolean }) => (
  <div className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all cursor-pointer group ${
    active ? "glass bg-primary/10 text-primary-light border-primary/20" : "text-text-secondary hover:text-foreground hover:bg-white/5"
  }`}>
    {icon}
    <span className="text-sm font-semibold">{label}</span>
  </div>
);
