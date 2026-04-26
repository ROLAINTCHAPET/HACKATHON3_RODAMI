"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Image from "next/image";
import { Button } from "@/components/ui/Button";
import { ProfileControllerService } from "@/lib/services/ProfileControllerService";
import { InterestCatalogDTO } from "@/lib/models/InterestCatalogDTO";
import { Loader2, Sparkles, CheckCircle2, Search, ArrowRight, ArrowLeft, Hash, X, Zap, Shield } from "lucide-react";

import { useAuth } from "@/components/auth/AuthProvider";

const fallbacks: Record<string, InterestCatalogDTO[]> = {
  "Campus & Vie": [
    { tag: "Sport", emoji: "⚽" },
    { tag: "Musique", emoji: "🎸" },
    { tag: "Art", emoji: "🎨" },
    { tag: "Gaming", emoji: "🎮" },
  ],
  "Académique": [
    { tag: "Code", emoji: "💻" },
    { tag: "Science", emoji: "🔬" },
    { tag: "Design", emoji: "✨" },
  ],
  "Social & Nightlife": [
    { tag: "Soirées", emoji: "🍹" },
    { tag: "Cinéma", emoji: "🎬" },
    { tag: "Voyage", emoji: "🌍" },
  ]
};

export default function OnboardingPage() {
  const { user } = useAuth();
  const [step, setStep] = useState(1);
  const [filiere, setFiliere] = useState("");
  const [annee, setAnnee] = useState(1);
  const [catalog, setCatalog] = useState<Record<string, InterestCatalogDTO[]>>({});
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const router = useRouter();

  useEffect(() => {
    const fetchCatalog = async () => {
      if (!user) return; // Wait for user to be ready
      
      setIsLoading(true);
      try {
        const data = await ProfileControllerService.getInterestCatalog();
        setCatalog(data);
      } catch (err) {
        console.error("Failed to fetch interest catalog:", err);
      } finally {
        setIsLoading(false);
      }
    };
    fetchCatalog();
  }, [user]);

  const displayCatalog = Object.keys(catalog).length > 0 ? catalog : fallbacks;

  const toggleInterest = (tag: string) => {
    setSelectedTags(prev => 
      prev.includes(tag) ? prev.filter(t => t !== tag) : [...prev, tag]
    );
  };

  const handleSubmit = async () => {
    // Requirements: filiere must be set (done in step 1), interests are now optional
    setIsSaving(true);
    try {
      await ProfileControllerService.setupProfile({
        interests: selectedTags,
        filiere: filiere || "Général",
        annee: annee
      });
      router.push("/discover");
    } catch (err) {
      console.error("Failed to save profile:", err);
      // Even if it fails, try to redirect to let them into the dashboard
      router.push("/discover");
    } finally {
      setIsSaving(false);
    }
  };

  const filieres = [
    "Informatique", "Génie Civil", "Mathématiques", "Physique", 
    "Biologie", "Arts & Design", "Droit", "Économie", "Médecine"
  ];

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex flex-col items-center justify-center p-6 bg-[radial-gradient(circle_at_50%_50%,rgba(59,130,246,0.1),transparent_50%)]">
        <div className="relative mb-8">
            <Loader2 className="h-16 w-16 text-primary animate-spin" />
            <div className="absolute inset-0 bg-primary/20 blur-2xl rounded-full animate-pulse" />
        </div>
        <p className="text-text-secondary animate-pulse text-sm uppercase tracking-[0.3em] font-black">
          Génération de votre <span className="text-primary font-black">Empreinte Campus</span>...
        </p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background text-foreground relative overflow-hidden flex flex-col items-center justify-center selection:bg-primary/30">
      {/* Mesh Background Effects */}
      <div className="fixed inset-0 z-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-[10%] -right-[10%] w-[60%] h-[60%] bg-primary/10 blur-[140px] rounded-full animate-pulse-slow" />
        <div className="absolute -bottom-[20%] -left-[10%] w-[70%] h-[70%] bg-teal/5 blur-[160px] rounded-full animate-pulse-slow delay-700" />
        <div className="absolute top-[20%] left-[30%] w-[30%] h-[40%] bg-purple-500/5 blur-[120px] rounded-full" />
        <div className="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-[0.04] contrast-150 brightness-100" />
      </div>

      {/* Floating Elements (Visual Polish) */}
      <div className="fixed inset-0 z-0 pointer-events-none overflow-hidden hidden lg:block">
        <div className="absolute top-[15%] left-[10%] h-32 w-32 glass rounded-3xl rotate-12 border border-white/10 flex items-center justify-center animate-bounce-slow opacity-30">
            <Sparkles className="h-10 w-10 text-primary" />
        </div>
        <div className="absolute bottom-[20%] right-[10%] h-40 w-40 glass rounded-full border border-white/10 flex items-center justify-center animate-pulse-slow opacity-20">
            <Hash className="h-12 w-12 text-teal" />
        </div>
      </div>

      {/* Back Button */}
      <div className="absolute top-6 left-4 md:fixed md:top-8 md:left-8 z-50">
        <Button 
          variant="ghost" 
          size="sm" 
          onClick={() => router.push("/login")}
          className="group gap-2 md:gap-3 text-text-secondary hover:text-foreground glass-card px-4 py-2 md:px-5 md:py-3 rounded-xl md:rounded-2xl border-white/5"
        >
          <ArrowLeft className="h-4 w-4 transition-transform group-hover:-translate-x-1" />
          <span className="text-[9px] md:text-[10px] font-black uppercase tracking-widest">Sortie Sécurisée</span>
        </Button>
      </div>

      <div className="max-w-5xl w-full px-6 py-12 relative z-10 space-y-12">
        {/* Progress Header */}
        <div className="flex items-center justify-between gap-6">
          <div className="flex-1 h-1.5 rounded-full bg-white/5 overflow-hidden backdrop-blur-md border border-white/5">
            <div 
              className="h-full bg-gradient-to-r from-primary via-indigo-400 to-teal transition-all duration-1000 ease-out" 
              style={{ width: `${(step / 2) * 100}%` }}
            />
          </div>
          <span className="text-[10px] font-black uppercase tracking-[0.2em] text-text-secondary whitespace-nowrap glass px-4 py-2 rounded-xl border border-white/5">
            Phase <span className="text-primary font-black">0{step}</span> / 02
          </span>
        </div>

        {step === 1 ? (
          <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
            <div className="space-y-4 text-center">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-primary-light font-black text-[10px] uppercase tracking-widest border border-primary/20">
                <Sparkles className="h-3 w-3" />
                Votre Identité Campus
              </div>
              <h1 className="text-4xl md:text-6xl font-black tracking-tight leading-tight">
                Quel est votre <span className="text-gradient">cursus</span> ?
              </h1>
              <p className="text-text-secondary max-w-xl mx-auto">
                Ces informations nous aideront à vous proposer des événements pertinents pour votre filière et votre année.
              </p>
            </div>

            <div className="glass-card p-8 md:p-12 rounded-[2.5rem] border border-glass-border space-y-8">
              <div className="grid md:grid-cols-2 gap-8">
                <div className="space-y-3">
                  <label className="text-[11px] font-black uppercase tracking-widest text-text-secondary ml-1">Filière / Spécialité</label>
                  <select 
                    value={filiere}
                    onChange={(e) => setFiliere(e.target.value)}
                    className="w-full h-14 px-4 bg-white/5 border border-glass-border rounded-2xl text-foreground focus:ring-2 focus:ring-primary/40 focus:border-primary/40 outline-none transition-all"
                  >
                    <option value="" disabled className="bg-background">Choisir une filière</option>
                    {filieres.map(f => (
                      <option key={f} value={f} className="bg-background">{f}</option>
                    ))}
                  </select>
                </div>
                <div className="space-y-3">
                  <label className="text-[11px] font-black uppercase tracking-widest text-text-secondary ml-1">Année d'étude</label>
                  <div className="flex gap-2">
                    {[1, 2, 3, 4, 5].map(y => (
                      <button
                        key={y}
                        onClick={() => setAnnee(y)}
                        className={`flex-1 h-14 rounded-2xl border font-bold transition-all ${
                          annee === y 
                            ? "bg-primary border-primary text-white shadow-lg shadow-primary/20" 
                            : "glass border-glass-border text-text-secondary hover:border-primary/30"
                        }`}
                      >
                        L{y > 3 ? y-3 : y}{y > 3 ? "M" : ""}
                      </button>
                    ))}
                  </div>
                </div>
              </div>

              <Button 
                onClick={() => {
                  const role = localStorage.getItem("userRole");
                  if (role === "BDE" || role === "ADMIN") {
                    handleSubmit();
                  } else {
                    setStep(2);
                  }
                }}
                disabled={!filiere}
                className="w-full h-16 rounded-2xl text-lg font-bold glow-primary group"
              >
                {localStorage.getItem("userRole") === "BDE" ? "Finaliser mon Profil BDE" : "Continuer vers les intérêts"}
                <ArrowRight className="h-5 w-5 ml-2 transition-transform group-hover:translate-x-1" />
              </Button>
            </div>
          </div>
        ) : localStorage.getItem("userRole") !== "BDE" ? (
          <div className="space-y-8 animate-in fade-in slide-in-from-right-8 duration-500">
            {/* Phase 02 Header */}
            <div className="space-y-4 text-center">
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full glass text-teal font-black text-[10px] uppercase tracking-widest border border-teal/20">
                <Hash className="h-3 w-3" />
                Intérêts & Passions
              </div>
              <h2 className="text-4xl md:text-6xl font-black tracking-tight leading-tight">
                Qu'est-ce qui vous <span className="text-gradient">anime</span> ?
              </h2>
               <p className="text-text-secondary max-w-xl mx-auto">
                Choisissez vos intérêts pour alimenter votre flux PULL. 
                <span className="block text-[10px] uppercase font-black tracking-widest mt-2 text-primary-light/60">(Étape Optionnelle • Recommandée)</span>
              </p>
            </div>

            <div className="flex flex-col items-center gap-6 mb-12">
              <div className={`w-full max-w-2xl p-6 rounded-[2rem] glass transition-all duration-500 border ${selectedTags.length >= 3 ? "border-teal/40 bg-teal/5 shadow-lg shadow-teal/5" : "border-glass-border"}`}>
                <div className="flex items-center justify-between mb-4 px-2">
                  <div className="text-[10px] uppercase font-black tracking-widest text-text-secondary">
                    Votre sélection (Optionnelle)
                  </div>
                  <div className={`text-xs font-black ${selectedTags.length >= 3 ? "text-teal" : "text-primary-light/40"}`}>
                    {selectedTags.length} sélectionné(s)
                  </div>
                </div>
                
                <div className="flex flex-wrap gap-2 min-h-[40px]">
                  {selectedTags.length === 0 ? (
                    <p className="text-xs text-text-secondary italic ml-2 opacity-50">Aucun intérêt sélectionné...</p>
                  ) : (
                    selectedTags.map(tag => (
                      <button
                        key={tag}
                        onClick={() => toggleInterest(tag)}
                        className="px-4 py-2 rounded-xl bg-primary/20 border border-primary/30 text-primary-light text-[10px] font-black uppercase tracking-tight flex items-center gap-2 animate-in zoom-in-95"
                      >
                        {tag}
                        <span className="text-lg leading-none">&times;</span>
                      </button>
                    ))
                  )}
                </div>
              </div>

              <div className="flex flex-col sm:flex-row items-center gap-4">
                <Button 
                  onClick={handleSubmit} 
                  disabled={isSaving}
                  className="h-20 px-12 rounded-[2rem] text-xl font-bold glow-primary shadow-2xl transition-all hover:scale-105 active:scale-95 whitespace-nowrap"
                >
                  {isSaving ? (
                    <div className="flex items-center gap-3">
                      <Loader2 className="h-6 w-6 animate-spin text-white" />
                      <span>Synchronisation...</span>
                    </div>
                  ) : (
                    <div className="flex items-center gap-3">
                      <span>{selectedTags.length > 0 ? "Finaliser mon Profil" : "Continuer sans intérêts"}</span>
                      <ArrowRight className="h-6 w-6" />
                    </div>
                  )}
                </Button>
                
                {selectedTags.length === 0 && !isSaving && (
                  <button 
                    onClick={handleSubmit}
                    className="text-[10px] font-black uppercase tracking-[0.2em] text-text-secondary hover:text-primary transition-colors px-6 py-4"
                  >
                    Passer cette étape →
                  </button>
                )}
              </div>
            </div>

            {/* Interest Sections */}
            <div className="space-y-14 max-h-[55vh] overflow-y-auto pr-6 custom-scrollbar pb-10">
              {Object.entries(displayCatalog).map(([category, interests]) => (
                <div key={category} className="space-y-6 animate-in slide-in-from-bottom-4 duration-500">
                  <div className="flex items-center gap-4">
                    <h3 className="text-sm font-black uppercase tracking-[0.4em] text-text-secondary whitespace-nowrap">
                      {category}
                    </h3>
                    <div className="h-[2px] flex-1 bg-gradient-to-r from-white/10 to-transparent" />
                  </div>
                  
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    {interests.map((interest) => (
                      <button
                        key={interest.tag}
                        onClick={() => toggleInterest(interest.tag!)}
                        className={`p-6 rounded-3xl border text-left transition-all duration-500 flex flex-col gap-4 relative overflow-hidden group ${
                          selectedTags.includes(interest.tag!)
                            ? "bg-primary/20 border-primary shadow-lg shadow-primary/10"
                            : "glass border-white/5 hover:border-primary/40 hover:bg-white/[0.05]"
                        }`}
                      >
                        <div className={`text-4xl transition-transform duration-500 ${selectedTags.includes(interest.tag!) ? "scale-125 rotate-6" : "group-hover:scale-110"}`}>
                          {interest.emoji || "✨"}
                        </div>
                        <div className="space-y-1">
                          <span className={`text-[12px] font-black uppercase tracking-widest block transition-colors ${selectedTags.includes(interest.tag!) ? "text-primary-light" : "text-foreground"}`}>
                            {interest.tag}
                          </span>
                        </div>
                        
                        {selectedTags.includes(interest.tag!) && (
                          <div className="absolute top-4 right-4 h-2 w-2 rounded-full bg-primary animate-ping" />
                        )}
                        <div className={`absolute bottom-0 left-0 h-1 bg-primary transition-all duration-500 ${selectedTags.includes(interest.tag!) ? "w-full" : "w-0"}`} />
                      </button>
                    ))}
                  </div>
                </div>
              ))}
            </div>
            
            <button 
              onClick={() => setStep(1)}
              className="w-full text-center text-text-secondary text-xs hover:text-foreground transition-colors"
            >
              ← Retour au cursus
            </button>
          </div>
        ) : null}
      </div>
    </div>
  );
}
