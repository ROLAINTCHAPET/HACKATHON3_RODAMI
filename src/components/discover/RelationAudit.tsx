"use client";

import React from "react";
import { Shield, Clock, FileText, CheckCircle2, Info, ArrowRight } from "lucide-react";

interface AuditStep {
  time: string;
  action: string;
  rule: string;
  impact: string;
}

interface RelationAuditProps {
  userName: string;
  triggerEvent: string;
  matchScore: number;
  auditTrail: AuditStep[];
  onClose: () => void;
}

export const RelationAudit = ({
  userName,
  triggerEvent,
  matchScore,
  auditTrail,
  onClose,
}: RelationAuditProps) => {
  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 md:p-6 bg-background/60 backdrop-blur-xl animate-in fade-in duration-300">
      <div className="glass-card w-full max-w-2xl rounded-[2rem] md:rounded-[2.5rem] border border-glass-border p-6 md:p-12 shadow-2xl relative overflow-hidden">
        {/* Background Mesh */}
        <div className="absolute -top-24 -right-24 h-64 w-64 bg-primary/10 blur-[100px] rounded-full" />
        
        <div className="relative space-y-8">
          {/* Header */}
          <div className="flex items-start justify-between">
            <div className="space-y-2">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/10 text-primary-light font-bold text-[10px] uppercase tracking-widest border border-primary/20">
                <Shield className="h-3 w-3" />
                Droit à la Transparence (RF-18)
              </div>
              <h2 className="text-3xl font-black">Audit de Relation : <span className="text-gradient">{userName}</span></h2>
              <p className="text-text-secondary text-sm">Origine algorithmique de votre connexion campus.</p>
            </div>
            <button 
              onClick={onClose}
              className="h-10 w-10 rounded-full glass border border-glass-border flex items-center justify-center hover:bg-white/10 transition-colors"
            >
              ✕
            </button>
          </div>

          {/* Core Info */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="glass bg-white/5 p-4 rounded-2xl border border-glass-border">
              <div className="text-[10px] font-black uppercase text-text-secondary mb-1">Événement Source</div>
              <div className="text-sm font-bold text-foreground">{triggerEvent}</div>
            </div>
            <div className="glass bg-white/5 p-4 rounded-2xl border border-glass-border">
              <div className="text-[10px] font-black uppercase text-text-secondary mb-1">Score d'Inférence</div>
              <div className="text-sm font-bold text-primary-light">{matchScore}% de fiabilité</div>
            </div>
          </div>

          {/* Audit Steps */}
          <div className="space-y-6">
            <h3 className="text-xs font-black uppercase tracking-widest text-text-secondary flex items-center gap-2">
              <FileText className="h-4 w-4" />
              Historique des Décisions Algorithmiques
            </h3>
            <div className="space-y-6 relative before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-px before:bg-glass-border">
              {auditTrail.map((step, i) => (
                <div key={i} className="relative pl-10">
                  <div className="absolute left-0 top-1 h-5 w-5 rounded-full bg-background border-2 border-primary flex items-center justify-center z-10">
                    <div className="h-1.5 w-1.5 rounded-full bg-primary" />
                  </div>
                  <div className="space-y-1">
                    <div className="flex items-center justify-between">
                      <span className="text-[10px] font-mono text-text-secondary">{step.time}</span>
                      <span className="text-[10px] font-black text-primary-light px-2 py-0.5 rounded-md bg-primary/10 border border-primary/20">
                        Règle {step.rule}
                      </span>
                    </div>
                    <p className="text-sm font-bold text-foreground">{step.action}</p>
                    <p className="text-[11px] text-text-secondary italic">Impact : {step.impact}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Verification Footer */}
          <div className="p-4 rounded-2xl bg-teal/5 border border-teal/20 flex items-center gap-4">
            <CheckCircle2 className="h-6 w-6 text-teal" />
            <p className="text-xs text-text-secondary leading-relaxed">
              Cette connexion a été auditée et validée conforme aux directives de la gouvernance BDE. 
              <span className="text-teal font-bold ml-1 cursor-pointer hover:underline">Contester cette relation ?</span>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
