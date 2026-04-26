import React from "react";
import Image from "next/image";
import { Sparkles, Link as LinkIcon, Zap, ArrowRight } from "lucide-react";

const features = [
  {
    icon: Zap,
    title: "Onboarding sans friction",
    description:
      "Rejoignez la plateforme en moins de 2 minutes. Seuls vos centres d'intérêt comptent pour commencer.",
    colorClass: "bg-primary/20 text-primary-light",
    iconColorClass: "text-primary-light",
    number: "01",
  },
  {
    icon: Sparkles,
    title: "Connexion contextuelle",
    description:
      "Le système vous propose des connexions basées sur les événements auxquels vous participez.",
    colorClass: "bg-teal/20 text-teal",
    iconColorClass: "text-teal",
    number: "02",
  },
  {
    icon: LinkIcon,
    title: "Liens durables",
    description:
      "Les interactions éphémères se transforment en relations durables grâce au suivi post-événement.",
    colorClass: "bg-rose/20 text-rose",
    iconColorClass: "text-rose",
    number: "03",
  },
];

export const Features = () => {
  return (
    <section id="features" className="relative py-28 overflow-hidden">
      {/* Background  */}
      <div className="absolute top-0 right-0 w-96 h-96 bg-primary/5 rounded-full blur-[150px]" />
      <div className="absolute bottom-0 left-0 w-80 h-80 bg-teal/5 rounded-full blur-[120px]" />

      <div className="container mx-auto px-4 relative z-10">
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-20 space-y-6">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full glass text-teal font-semibold text-sm">
            <Zap className="h-4 w-4" />
            Simple & Efficace
          </div>
          <h2 className="text-4xl md:text-5xl font-black tracking-tight">
            Comment ça{" "}
            <span className="text-gradient">marche ?</span>
          </h2>
          <p className="text-lg text-text-secondary max-w-2xl mx-auto">
            Une expérience pensée pour l&apos;étudiant d&apos;aujourd&apos;hui :
            rapide, efficace et humaine.
          </p>
        </div>

        {/* Feature Cards */}
        <div className="grid md:grid-cols-3 gap-6 lg:gap-8">
          {features.map((feature, index) => (
            <div
              key={feature.title}
              className="group glass-card p-8 rounded-2xl relative overflow-hidden"
              style={{ animationDelay: `${index * 0.15}s` }}
            >
              {/* Number */}
              <div className="absolute top-6 right-6 text-6xl font-black text-white/[0.03] select-none">
                {feature.number}
              </div>

              {/* Icon */}
              <div
                className={`h-14 w-14 rounded-2xl ${feature.colorClass} flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-300`}
              >
                <feature.icon className={`h-7 w-7 ${feature.iconColorClass}`} />
              </div>

              {/* Content */}
              <h3 className="text-xl font-bold mb-3 text-foreground">
                {feature.title}
              </h3>
              <p className="text-text-secondary leading-relaxed text-sm">
                {feature.description}
              </p>

              {/* Learn more */}
              <div className="mt-6 flex items-center gap-2 text-primary-light text-sm font-semibold opacity-0 group-hover:opacity-100 translate-y-2 group-hover:translate-y-0 transition-all duration-300">
                En savoir plus
                <ArrowRight className="h-4 w-4" />
              </div>

              {/* Bottom glow on hover */}
              <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-2/3 h-1 bg-gradient-to-r from-transparent via-primary/40 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
            </div>
          ))}
        </div>

        {/* Connecting visual: Image showcase */}
        <div className="mt-24 relative">
          <div className="grid md:grid-cols-2 gap-6 items-center">
            {/* Image */}
            <div className="relative group hidden md:block">
              <div className="absolute inset-0 bg-primary/10 rounded-3xl blur-[40px] opacity-0 group-hover:opacity-100 transition-opacity duration-700" />
              <div className="relative overflow-hidden rounded-3xl border border-glass-border">
                <Image
                  src="/networking-black.png"
                  alt="Étudiants en réseau sur SchoolLink"
                  width={600}
                  height={400}
                  className="w-full object-cover transition-transform duration-700 group-hover:scale-105"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-background/80 via-transparent to-transparent" />
                <div className="absolute bottom-6 left-6 right-6">
                  <div className="glass-card px-5 py-4 rounded-xl">
                    <div className="text-sm font-bold text-foreground">
                      Campus Connect Event
                    </div>
                    <div className="text-xs text-text-secondary mt-1">
                      +150 étudiants connectés lors du dernier meetup
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Text */}
            <div className="space-y-6 md:pl-8">
              <h3 className="text-3xl md:text-4xl font-black">
                Des rencontres{" "}
                <span className="text-gradient-primary">authentiques</span>
              </h3>
              <p className="text-text-secondary leading-relaxed">
                Fini l&apos;ère des réseaux sociaux impersonnels. SchoolLink
                vous connecte avec des étudiants qui partagent vos passions,
                vos cours et vos événements. Chaque interaction a un{" "}
                <span className="text-primary-light font-semibold">contexte réel</span>.
              </p>
              <div className="flex flex-wrap gap-3">
                {["Événements", "Cours", "Clubs", "Projets"].map((tag) => (
                  <span
                    key={tag}
                    className="glass-card px-4 py-2 rounded-full text-sm font-medium text-text-secondary"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
