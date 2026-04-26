import React from "react";
import { Button } from "@/components/ui/Button";
import { ArrowRight, Rocket } from "lucide-react";

export const CTASection = () => {
  return (
    <section className="py-24 container mx-auto px-4">
      <div className="relative rounded-[2rem] overflow-hidden">
        {/* Gradient Background */}
        <div className="absolute inset-0 bg-gradient-to-br from-primary via-purple-600 to-primary" />

        {/* Mesh overlay */}
        <div
          className="absolute inset-0 opacity-10"
          style={{
            backgroundImage: `radial-gradient(circle at 20% 50%, hsla(172, 70%, 42%, 0.4) 0%, transparent 50%), radial-gradient(circle at 80% 50%, hsla(330, 80%, 65%, 0.3) 0%, transparent 50%)`,
          }}
        />

        {/* Grid Pattern */}
        <div
          className="absolute inset-0 opacity-[0.06]"
          style={{
            backgroundImage: `linear-gradient(white 1px, transparent 1px), linear-gradient(90deg, white 1px, transparent 1px)`,
            backgroundSize: "40px 40px",
          }}
        />

        {/* Decorative blurs */}
        <div className="absolute top-0 left-0 -translate-x-1/3 -translate-y-1/3 w-80 h-80 bg-white/10 rounded-full blur-[80px]" />
        <div className="absolute bottom-0 right-0 translate-x-1/3 translate-y-1/3 w-96 h-96 bg-teal/15 rounded-full blur-[100px]" />

        {/* Content */}
        <div className="relative z-10 p-10 md:p-20 text-center">
          <div className="max-w-3xl mx-auto space-y-8">
            {/* Icon */}
            <div className="inline-flex items-center justify-center h-16 w-16 rounded-2xl bg-white/10 backdrop-blur-sm border border-white/20 mb-4">
              <Rocket className="h-8 w-8 text-white" />
            </div>

            <h2 className="text-3xl md:text-6xl font-black leading-tight text-white">
              Prêt à transformer votre vie étudiante ?
            </h2>
            <p className="text-lg md:text-xl text-white/70 max-w-2xl mx-auto">
              Rejoignez SchoolLink aujourd'hui et ne manquez plus aucune
              opportunité de rencontre sur votre campus.
            </p>
            <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4">
              <Button
                variant="secondary"
                size="lg"
                className="w-full sm:w-auto bg-white text-primary hover:bg-white/90 shadow-xl shadow-black/10 font-bold group"
              >
                Commencer maintenant
                <ArrowRight className="ml-2 h-5 w-5 group-hover:translate-x-1 transition-transform" />
              </Button>
              <Button
                variant="outline"
                size="lg"
                className="w-full sm:w-auto border-white/30 text-white hover:bg-white/10"
              >
                Nous contacter
              </Button>
            </div>

            {/* Trust badges */}
            <div className="pt-8 flex flex-wrap items-center justify-center gap-6 text-white/40 text-sm">
              <span className="flex items-center gap-2">
                <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
                Gratuit pour les étudiants
              </span>
              <span className="flex items-center gap-2">
                <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
                Données sécurisées
              </span>
              <span className="flex items-center gap-2">
                <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
                Inscription en 2 min
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
