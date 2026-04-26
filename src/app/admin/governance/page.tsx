"use client";

import React, { useState } from "react";
import {
  Shield,
  Settings,
  Activity,
  Lock,
  Eye,
  Clock,
  ArrowLeft,
  CheckCircle2,
  AlertTriangle,
  FileText,
  ShieldAlert,
  ShieldCheck,
} from "lucide-react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/Button";
import { GovernanceControllerService } from "@/lib/services/GovernanceControllerService";
import { DiagnosticControllerService } from "@/lib/services/DiagnosticControllerService";
import { Loader2 } from "lucide-react";
import { useEffect } from "react";

const rules = [
  {
    id: "RF-16",
    level: "Figée (Admin)",
    title: "Priorité Institutionnelle",
    description: "Les messages de l'administration sont diffusés à 100% sans filtrage.",
    status: "Actif",
    color: "text-primary-light"
  },
  {
    id: "RF-17",
    level: "Ajustable (BDE)",
    title: "Poids du Flux PUSH",
    description: "Proportion de contenu hors-bulle suggéré aux étudiants.",
    value: "25%",
    status: "Modulable",
    color: "text-teal"
  },
  {
    id: "RF-18",
    level: "Libre (BDE/Asso)",
    title: "Mise en avant Sociale",
    description: "Boost temporaire pour les événements à faible participation.",
    value: "Auto",
    status: "Actif",
    color: "text-rose"
  }
];

import { useAuth } from "@/components/auth/AuthProvider";

export default function GovernancePage() {
  const { user } = useAuth();
  const router = useRouter();
  const [pushWeight, setPushWeight] = useState(25);
  const [rf19Active, setRf19Active] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState<string | null>(null);
  const [auditLogs, setAuditLogs] = useState<any[]>([]);

  useEffect(() => {
    // RBAC: Check permissions
    const storedRole = localStorage.getItem("userRole");
    if (storedRole !== "ADMIN" && storedRole !== "BDE") {
      router.push("/discover");
      return;
    }

    const fetchData = async () => {
      if (!user) return; // Wait for user to be ready

      setIsLoading(true);
      try {
        const logs = await GovernanceControllerService.getAuditLogs();
        setAuditLogs(logs || []);
      } catch (err) {
        console.error("Failed to fetch governance data:", err);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [user]); // Re-run when user changes or becomes available

  const handleUpdateRule = async (key: string, value: string) => {
    setIsSaving(key);
    try {
      await GovernanceControllerService.updateRule(key, { value });
      if (key === "RF-17") setPushWeight(parseInt(value));
      if (key === "RF-19") setRf19Active(value === "true");

      const logs = await GovernanceControllerService.getAuditLogs();
      setAuditLogs(logs || []);
    } catch (err) {
      console.error(`Failed to update rule ${key}:`, err);
    } finally {
      setIsSaving(null);
    }
  };

  return (
    <div className="min-h-screen bg-background p-6 md:p-12">
      <div className="max-w-6xl mx-auto space-y-12">
        {/* Header */}
        <div className="flex flex-col md:flex-row items-start md:items-center justify-between border-b border-glass-border pb-8 gap-6">
          <div className="space-y-2">
            <Link href="/discover" className="inline-flex items-center gap-2 text-sm text-text-secondary hover:text-primary transition-colors mb-2">
              <ArrowLeft className="h-4 w-4" />
              Retour au Hub
            </Link>
            <h1 className="text-3xl md:text-4xl font-black flex items-center gap-3 md:gap-4 leading-tight">
              <Shield className="h-8 w-8 md:h-10 md:w-10 text-primary shrink-0" />
              Gouvernance <br className="block md:hidden" /> Algorithmique
            </h1>
            <p className="text-xs md:text-sm text-text-secondary">Module 4 — Panneau de Contrôle BDE & Transparence</p>
          </div>
          <div className="glass px-4 py-2 md:px-6 md:py-3 rounded-2xl flex items-center gap-4 border border-primary/20 shrink-0">
            <div className="h-2 w-2 rounded-full bg-teal animate-pulse" />
            <span className="text-[10px] md:text-sm font-bold text-foreground">SESSION BDE ACTIVE</span>
          </div>
        </div>

        <div className="grid lg:grid-cols-3 gap-8">
          {/* Rules Control */}
          <div className="lg:col-span-2 space-y-8">
            <div className="grid gap-4">
              <h2 className="text-xl font-bold flex items-center gap-2">
                <Settings className="h-5 w-5 text-text-secondary" />
                Règles de Priorisation
              </h2>

              {/* Special Rule RF-19 (Twist 05) */}
              <div className="p-6 rounded-2xl bg-rose/5 border border-rose/10 flex items-center justify-between group hover:bg-rose/10 transition-all">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <ShieldAlert className="h-4 w-4 text-rose" />
                    <h3 className="font-bold text-foreground">RF-19: Purge d'Obsolescence (Vecteur Entropie)</h3>
                  </div>
                  <p className="text-xs text-text-secondary max-w-md">Supprime automatiquement les événements dont l'intégrité est &lt; 30% pour éviter la contamination.</p>
                </div>
                <div
                  onClick={() => handleUpdateRule("RF-19", (!rf19Active).toString())}
                  className={`h-6 w-11 rounded-full transition-colors cursor-pointer relative ${rf19Active ? "bg-rose" : "bg-white/10"}`}
                >
                  {isSaving === "RF-19" ? (
                    <Loader2 className="absolute inset-0 m-auto h-3 w-3 animate-spin text-white" />
                  ) : (
                    <div className={`absolute top-1 left-1 h-4 w-4 bg-white rounded-full transition-transform ${rf19Active ? "translate-x-5" : ""}`} />
                  )}
                </div>
              </div>

              {rules.map((rule) => (
                <div key={rule.id} className="glass-card p-6 rounded-2xl border border-glass-border group hover:border-primary/30 transition-colors">
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <div className={`text-[10px] font-black uppercase tracking-widest ${rule.color} mb-1`}>{rule.level}</div>
                      <h3 className="text-lg font-bold text-foreground">{rule.title}</h3>
                    </div>
                    <div className="glass px-3 py-1 rounded-full text-[10px] font-bold text-text-secondary"># {rule.id}</div>
                  </div>
                  <p className="text-sm text-text-secondary mb-6">{rule.description}</p>

                  {rule.id === "RF-17" ? (
                    <div className="space-y-4">
                      <div className="flex justify-between text-xs font-bold uppercase text-text-secondary">
                        <span>Diversité (PUSH)</span>
                        <span>{pushWeight}%</span>
                      </div>
                      <input
                        type="range"
                        min="5"
                        max="80"
                        value={pushWeight}
                        onChange={(e) => setPushWeight(parseInt(e.target.value))}
                        onMouseUp={(e) => handleUpdateRule("RF-17", (e.target as HTMLInputElement).value)}
                        className="w-full accent-primary h-1.5 bg-background border border-glass-border rounded-lg appearance-none cursor-pointer"
                      />
                      {isSaving === "RF-17" && <span className="text-[10px] text-primary-light animate-pulse">Synchronisation...</span>}
                    </div>
                  ) : (
                    <div className="flex items-center gap-2 text-teal text-xs font-bold">
                      <CheckCircle2 className="h-4 w-4" />
                      SYSTEME AUTOMATISÉ
                    </div>
                  )}
                </div>
              ))}
            </div>

            {/* Health Metrics */}
            <div className="glass-card p-6 md:p-8 rounded-[1.5rem] md:rounded-[2rem] border border-glass-border relative overflow-hidden">
              <div className="absolute top-0 right-0 p-4 md:p-8 opacity-10">
                <Activity className="h-16 w-16 md:h-24 md:w-24" />
              </div>
              <h2 className="text-xl md:text-2xl font-black mb-6 md:mb-8">Statut Défendable</h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-8">
                <div>
                  <div className="text-3xl font-black text-foreground">99.9%</div>
                  <div className="text-[10px] font-bold text-text-secondary uppercase tracking-tighter">Traceability</div>
                </div>
                <div>
                  <div className="text-3xl font-black text-teal">ACTIF</div>
                  <div className="text-[10px] font-bold text-text-secondary uppercase tracking-tighter">Flux PUSH</div>
                </div>
                <div>
                  <div className="text-3xl font-black text-rose">HYBRIDE</div>
                  <div className="text-[10px] font-bold text-text-secondary uppercase tracking-tighter">Data Integrity</div>
                </div>
                <div>
                  <div className="text-3xl font-black text-primary">FIXE</div>
                  <div className="text-[10px] font-bold text-text-secondary uppercase tracking-tighter">RF-16 Admin</div>
                </div>
              </div>
            </div>
          </div>

          {/* Sidebar: Audit & Logs */}
          <div className="space-y-8">
            <div className="glass-card p-4 md:p-6 rounded-2xl border border-glass-border space-y-6 overflow-hidden">
              <h2 className="text-sm font-black uppercase tracking-widest text-text-secondary flex items-center gap-2">
                <FileText className="h-4 w-4" />
                Audit Log (RF-18)
              </h2>
              <div className="space-y-4">
                {isLoading ? (
                  <div className="flex justify-center py-4">
                    <Loader2 className="h-5 w-5 animate-spin text-primary" />
                  </div>
                ) : auditLogs.length > 0 ? (
                  auditLogs.map((log, i) => (
                    <div key={i} className="text-xs border-l-2 border-primary/30 pl-4 space-y-1">
                      <div className="flex justify-between font-mono">
                        <span className="text-text-secondary">{log.createdAt ? new Date(log.createdAt).toLocaleTimeString() : log.time}</span>
                        <span className="text-primary-light">@{log.actor || log.author}</span>
                      </div>
                      <div className="text-foreground font-bold">{log.action || log.description}</div>
                      <div className="text-[10px] text-teal italic">{log.impact || "N/A"}</div>
                    </div>
                  ))
                ) : (
                  <p className="text-[10px] text-text-secondary italic">Aucun log récent détecté.</p>
                )}
              </div>
              <Button variant="outline" size="sm" className="w-full text-[10px] uppercase font-bold">Exporter Logs PDF</Button>
            </div>

            {/* Twist Warning */}
            <div className="p-6 rounded-2xl bg-error/10 border border-error/30 space-y-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3 text-error font-bold text-sm">
                  <AlertTriangle className="h-5 w-5" />
                  VIGILANCE SYSTEME
                </div>
                <Button
                  variant="ghost"
                  size="sm"
                  className="h-7 text-[10px] uppercase font-black hover:bg-error/20 text-error"
                  onClick={async () => {
                    try {
                      const diag = await DiagnosticControllerService.getTwist09Diagnostics();
                      console.log("Twist 09 Diagnostics:", diag);
                      alert("Diagnostics Twist-09 récupérés avec succès. Consultez la console (F12) pour le détail technique.");
                    } catch (e) {
                      console.error("Failed to fetch Twist 09 diagnostics:", e);
                    }
                  }}
                >
                  Détails Twist-09
                </Button>
              </div>
              <p className="text-[10px] text-text-secondary leading-normal italic">
                Alerte Obsolescence : L'entropie des données clubs contamine les recommandations de cercle.
                Mesure auto : Purge RF-19 activée.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
