"use client";

import React, { useState, useEffect } from "react";
import { ShieldCheck, EyeOff, Ban, AlertTriangle } from "lucide-react";

export const PrivacyCensorship = () => {
  const [activeAlert, setActiveAlert] = useState<string | null>(null);

  const censoredFields = [
    { label: "Personnalité", status: "INTERDIT", icon: Ban },
    { label: "Hobbies", status: "CENSURÉ", icon: EyeOff },
    { label: "État Émotionnel", status: "REJETÉ", icon: ShieldCheck },
  ];

  useEffect(() => {
    const alerts = [
      "Service Vie Privée : Tentative d'accès bloquée",
      "Conformité Réglementaire : Données Sensitive non-disponibles",
      "Protection Mentale : Algorithme restreint"
    ];
    const timer = setInterval(() => {
      setActiveAlert(alerts[Math.floor(Math.random() * alerts.length)]);
      setTimeout(() => setActiveAlert(null), 3000);
    }, 8000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-3 p-3 bg-teal/5 border border-teal/20 rounded-xl">
        <ShieldCheck className="h-5 w-5 text-teal shrink-0" />
        <div>
          <div className="text-[10px] font-black text-teal uppercase tracking-widest leading-none mb-1">Protection Institutionnelle</div>
          <p className="text-[11px] text-text-secondary leading-tight">Le Service Vie Privée bloque la collecte proactive de données psychologiques.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-2">
        {censoredFields.map((field, i) => (
          <div key={i} className="flex items-center justify-between p-2 rounded-lg bg-background/50 border border-glass-border">
            <div className="flex items-center gap-2">
              <field.icon className="h-3 w-3 text-text-secondary" />
              <span className="text-[11px] font-medium text-text-secondary">{field.label}</span>
            </div>
            <span className="text-[9px] font-black px-2 py-0.5 rounded bg-error/10 text-error border border-error/20">
              {field.status}
            </span>
          </div>
        ))}
      </div>

      {activeAlert && (
        <div className="flex items-center gap-2 p-2 rounded-lg bg-error/5 border border-error/20 animate-pulse">
          <AlertTriangle className="h-3 w-3 text-error" />
          <span className="text-[9px] font-bold text-error uppercase">{activeAlert}</span>
        </div>
      )}
    </div>
  );
};
