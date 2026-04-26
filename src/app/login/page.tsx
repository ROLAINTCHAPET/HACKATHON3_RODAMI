"use client";

import React, { useState } from "react";
import Link from "next/link";
import Image from "next/image";
import { Button } from "@/components/ui/Button";
import { Eye, EyeOff, Mail, Lock, ArrowLeft, ArrowRight } from "lucide-react";

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4 relative overflow-hidden">
      {/* Background Glows */}
      <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/20 blur-[120px] rounded-full animate-pulse" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-teal/10 blur-[120px] rounded-full animate-pulse" />

      {/* Back Button */}
      <Link href="/" className="absolute top-8 left-8">
        <Button variant="ghost" size="sm" className="group gap-2 text-text-secondary hover:text-foreground">
          <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
          Retour au campus
        </Button>
      </Link>

      <div className="w-full max-w-md space-y-8 relative z-10 transition-all duration-500 animate-in fade-in zoom-in slide-in-from-bottom-4">
        {/* Logo & Header */}
        <div className="text-center space-y-4">
          <div className="inline-flex items-center justify-center p-3 rounded-2xl glass border border-glass-border shadow-xl mb-2">
            <Image src="/icon.png" alt="SchoolLink" width={48} height={48} className="rounded-xl shadow-2xl" />
          </div>
          <h1 className="text-4xl font-black tracking-tight">
            Bon <span className="text-gradient">Retour</span>
          </h1>
          <p className="text-text-secondary">Accédez à votre campus personnalisé.</p>
        </div>

        {/* Form */}
        <div className="glass-card p-10 rounded-[2.5rem] border border-glass-border shadow-2xl space-y-6">
          <div className="space-y-5">
            {/* Email Field */}
            <div className="space-y-2">
              <label className="text-[11px] font-black uppercase tracking-widest text-text-secondary ml-1">Email Universitaire</label>
              <div className="relative group">
                <div className="absolute inset-y-0 left-4 flex items-center text-text-secondary group-focus-within:text-primary transition-colors">
                  <Mail className="h-5 w-5" />
                </div>
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="nom.prenom@univ-campus.fr"
                  className="w-full h-14 pl-12 pr-4 bg-white/5 border border-glass-border rounded-2xl text-foreground placeholder:text-text-secondary/50 focus:outline-none focus:ring-2 focus:ring-primary/40 focus:border-primary/40 transition-all"
                />
              </div>
            </div>

            {/* Password Field */}
            <div className="space-y-2">
              <div className="flex justify-between items-center ml-1">
                <label className="text-[11px] font-black uppercase tracking-widest text-text-secondary">Mot de passe</label>
                <Link href="#" className="text-[10px] font-bold text-primary-light hover:underline">Oublié ?</Link>
              </div>
              <div className="relative group">
                <div className="absolute inset-y-0 left-4 flex items-center text-text-secondary group-focus-within:text-primary transition-colors">
                  <Lock className="h-5 w-5" />
                </div>
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                  className="w-full h-14 pl-12 pr-12 bg-white/5 border border-glass-border rounded-2xl text-foreground placeholder:text-text-secondary/50 focus:outline-none focus:ring-2 focus:ring-primary/40 focus:border-primary/40 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-4 flex items-center text-text-secondary hover:text-foreground transition-colors"
                >
                  {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                </button>
              </div>
            </div>
          </div>

          <Button variant="primary" className="w-full h-14 rounded-2xl text-lg font-bold glow-primary group shadow-2xl shadow-primary/20">
            Se connecter
            <ArrowRight className="h-5 w-5 ml-2 transition-transform group-hover:translate-x-1" />
          </Button>

          <div className="pt-2 text-center text-sm text-text-secondary">
            Pas encore de compte ?{" "}
            <Link href="/register" className="text-primary-light font-bold hover:underline">
              S'inscrire gratuitement
            </Link>
          </div>
        </div>

        {/* Info Box */}
        <div className="p-4 bg-primary/5 border border-primary/20 rounded-2xl flex items-start gap-3">
          <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center shrink-0 mt-0.5">
            <Lock className="h-4 w-4 text-primary-light" />
          </div>
          <div className="text-[11px] leading-relaxed text-text-secondary">
            <span className="font-black text-foreground">Sécurité École :</span> Connexion sécurisée via le portail SSO de votre établissement. Vos données restent archivées selon la Politique Vie Privée Active.
          </div>
        </div>
      </div>
    </div>
  );
}
