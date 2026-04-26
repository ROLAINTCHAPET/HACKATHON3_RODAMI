"use client";

import React, { useState } from "react";
import Link from "next/link";
import Image from "next/image";
import { Button } from "@/components/ui/Button";
import { Eye, EyeOff, Mail, Lock, ArrowLeft, ArrowRight, User, Loader2 } from "lucide-react";
import { AuthService } from "@/lib/custom/AuthService";
import { useRouter } from "next/navigation";

export default function RegisterPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [userId, setUserId] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const router = useRouter();

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");

    try {
      const user = await AuthService.register({
        email,
        password
      });

      if (user) {
        localStorage.setItem("userId", user.uid);
        localStorage.setItem("userName", email.split('@')[0]);
        router.push("/discover");
      }
    } catch (err: any) {
      setError(err.message || "Une erreur est survenue lors de l'inscription.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4 relative overflow-hidden">
      {/* Background Glows */}
      <div className="absolute top-[-10%] right-[-10%] w-[40%] h-[40%] bg-primary/20 blur-[120px] rounded-full animate-pulse" />
      <div className="absolute bottom-[-10%] left-[-10%] w-[40%] h-[40%] bg-teal/10 blur-[120px] rounded-full animate-pulse" />

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
            Créer un <span className="text-gradient">Compte</span>
          </h1>
          <p className="text-text-secondary">Onboarding ultra-rapide pour rejoindre le flux du campus.</p>
        </div>

        {/* Form Body */}
        <form onSubmit={handleRegister} className="glass-card p-10 rounded-[2.5rem] border border-glass-border shadow-2xl space-y-8">
          {error && (
            <div className="p-4 bg-rose/10 border border-rose/20 rounded-xl text-rose text-xs font-bold animate-in fade-in slide-in-from-top-2">
              {error}
            </div>
          )}
          <div className="space-y-4">
            {/* ID Field */}
            <div className="space-y-2">
              <label className="text-[11px] font-black uppercase tracking-widest text-text-secondary ml-1">Identifiant Institutionnel</label>
              <div className="relative group">
                <User className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-text-secondary group-focus-within:text-primary transition-colors" />
                <input
                  type="text"
                  value={userId}
                  onChange={(e) => setUserId(e.target.value)}
                  placeholder="ID Unique (ex: JD2026)"
                  className="w-full h-14 pl-12 pr-4 bg-white/5 border border-glass-border rounded-2xl text-foreground placeholder:text-text-secondary/50 focus:outline-none focus:ring-2 focus:ring-primary/40 transition-all font-mono"
                />
              </div>
            </div>

            {/* Email Field */}
            <div className="space-y-2">
              <label className="text-[11px] font-black uppercase tracking-widest text-text-secondary ml-1">Email Universitaire</label>
              <div className="relative group">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-text-secondary group-focus-within:text-primary transition-colors" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="nom.prenom@univ-campus.fr"
                  required
                  className="w-full h-14 pl-12 pr-4 bg-white/5 border border-glass-border rounded-2xl text-foreground placeholder:text-text-secondary/50 focus:outline-none focus:ring-2 focus:ring-primary/40 transition-all"
                />
              </div>
            </div>

            {/* Password Field */}
            <div className="space-y-2">
              <label className="text-[11px] font-black uppercase tracking-widest text-text-secondary ml-1">Mot de passe</label>
              <div className="relative group">
                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-text-secondary group-focus-within:text-primary transition-colors" />
                <input
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                  required
                  className="w-full h-14 pl-12 pr-12 bg-white/5 border border-glass-border rounded-2xl text-foreground placeholder:text-text-secondary/50 focus:outline-none focus:ring-2 focus:ring-primary/40 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-text-secondary hover:text-foreground transition-colors"
                >
                  {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
                </button>
              </div>
            </div>
          </div>

          <Button 
            type="submit"
            disabled={isLoading || !userId || !email || !password}
            className="w-full h-14 rounded-2xl text-lg font-bold glow-primary group shadow-2xl shadow-primary/20 disabled:opacity-50"
          >
            {isLoading ? <Loader2 className="h-6 w-6 animate-spin mx-auto" /> : (
              <>
                Établir la Connexion
                <ArrowRight className="h-5 w-5 ml-2 transition-transform group-hover:translate-x-1" />
              </>
            )}
          </Button>

          <div className="pt-4 text-center text-sm text-text-secondary border-t border-glass-border">
            Vous avez déjà un compte ?{" "}
            <Link href="/login" className="text-primary-light font-bold hover:underline">
              Se connecter
            </Link>
          </div>
        </form>

        {/* Terms agreement */}
        <p className="text-[10px] text-center text-text-secondary leading-relaxed px-10">
          En vous inscrivant, vous acceptez le bélèvement de données passif nécessaire au fonctionnement de l'algorithme ainsi que nos{" "}
          <Link href="/terms" className="text-primary-light font-bold hover:underline">Conditions Générales</Link>.
        </p>
      </div>
    </div>
  );
}
