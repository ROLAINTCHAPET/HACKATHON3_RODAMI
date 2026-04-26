import React from "react";
import Image from "next/image";
import { ShieldCheck, Star, Quote } from "lucide-react";

const stats = [
  {
    value: "34",
    label: "Associations",
    description: "Actives sur la plateforme",
    color: "text-primary-light",
  },
  {
    value: "500+",
    label: "Profils actifs",
    description: "En phase beta",
    color: "text-teal",
  },
  {
    value: "2min",
    label: "Onboarding",
    description: "Pour créer votre 1er lien",
    color: "text-yellow",
  },
  {
    value: "95%",
    label: "Satisfaction",
    description: "Des utilisateurs beta",
    color: "text-rose",
  },
];

const testimonials = [
  {
    name: "Sarah M.",
    role: "Étudiante en Marketing, L3",
    text: "Grâce à SchoolLink, j'ai rencontré mon groupe de projet et ma meilleure amie lors d'un même événement. La magie du contexte !",
    avatar: "S",
    color: "bg-primary/20 text-primary-light",
  },
  {
    name: "Karim B.",
    role: "Président BDE Engineering",
    text: "On a doublé la participation à nos événements depuis qu'on utilise SchoolLink. Les étudiants viennent car ils savent qu'ils vont rencontrer des gens.",
    avatar: "K",
    color: "bg-teal/20 text-teal",
  },
  {
    name: "Léa D.",
    role: "Étudiante en Informatique, M1",
    text: "L'onboarding est ultra rapide et l'algorithme de matching est bluffant. J'ai trouvé des partenaires pour mon hackathon en 10 minutes.",
    avatar: "L",
    color: "bg-rose/20 text-rose",
  },
];

export const SocialProof = () => {
  return (
    <section id="community" className="relative py-28 overflow-hidden">
      {/* Background effects */}
      <div className="absolute top-1/3 left-0 w-96 h-96 bg-teal/5 rounded-full blur-[150px]" />
      <div className="absolute bottom-0 right-0 w-80 h-80 bg-primary/5 rounded-full blur-[120px]" />

      <div className="container mx-auto px-4 relative z-10">
        {/* Section Header */}
        <div className="text-center max-w-3xl mx-auto mb-20 space-y-6">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full glass text-teal font-semibold text-sm">
            <ShieldCheck size={18} />
            Validé par le BDE
          </div>
          <h2 className="text-4xl md:text-5xl font-black tracking-tight">
            La confiance de{" "}
            <span className="text-gradient">tout un campus</span>.
          </h2>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-6 max-w-5xl mx-auto mb-24">
          {stats.map((stat) => (
            <div
              key={stat.label}
              className="glass-card p-6 rounded-2xl text-center space-y-2"
            >
              <div
                className={`text-4xl md:text-5xl font-black ${stat.color}`}
              >
                {stat.value}
              </div>
              <div className="text-base font-bold text-foreground">
                {stat.label}
              </div>
              <div className="text-xs text-text-secondary">
                {stat.description}
              </div>
            </div>
          ))}
        </div>

        {/* Campus Event Image Banner */}
        <div className="relative mb-24 group">
          <div className="absolute inset-0 bg-primary/10 rounded-3xl blur-[50px] opacity-0 group-hover:opacity-100 transition-opacity duration-700" />
          <div className="relative overflow-hidden rounded-3xl border border-glass-border">
            <Image
              src="/event-black.png"
              alt="Événement campus SchoolLink"
              width={1200}
              height={500}
              className="w-full h-64 md:h-96 object-cover transition-transform duration-700 group-hover:scale-[1.02]"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-background via-background/40 to-transparent" />
            <div className="absolute bottom-0 left-0 right-0 p-8 md:p-12">
              <div className="flex flex-wrap items-end justify-between gap-6">
                <div>
                  <h3 className="text-2xl md:text-3xl font-black text-foreground mb-2">
                    Événements Campus
                  </h3>
                  <p className="text-text-secondary text-sm md:text-base max-w-xl">
                    Chaque événement est une chance de créer des connexions
                    significatives avec des étudiants qui partagent vos passions.
                  </p>
                </div>
                <div className="glass-card px-6 py-4 rounded-xl flex items-center gap-3">
                  <div className="flex -space-x-2">
                    {["S", "K", "L", "A"].map((letter, i) => (
                      <div
                        key={i}
                        className="h-8 w-8 rounded-full bg-primary/30 border-2 border-background flex items-center justify-center text-xs font-bold text-primary-light"
                      >
                        {letter}
                      </div>
                    ))}
                  </div>
                  <span className="text-sm font-semibold text-foreground">
                    +248 ce mois
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Testimonials */}
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
          {testimonials.map((testimonial) => (
            <div
              key={testimonial.name}
              className="glass-card p-8 rounded-2xl relative overflow-hidden group"
            >
              {/* Quote icon */}
              <Quote className="absolute top-6 right-6 h-8 w-8 text-white/[0.04]" />

              {/* Stars */}
              <div className="flex gap-1 mb-4">
                {[1, 2, 3, 4, 5].map((star) => (
                  <Star
                    key={star}
                    className="h-4 w-4 fill-yellow text-yellow"
                  />
                ))}
              </div>

              {/* Text */}
              <p className="text-text-secondary text-sm leading-relaxed mb-6">
                &ldquo;{testimonial.text}&rdquo;
              </p>

              {/* Author */}
              <div className="flex items-center gap-3">
                <div
                  className={`h-10 w-10 rounded-full ${testimonial.color} flex items-center justify-center font-bold text-sm`}
                >
                  {testimonial.avatar}
                </div>
                <div>
                  <div className="text-sm font-bold text-foreground">
                    {testimonial.name}
                  </div>
                  <div className="text-xs text-text-secondary">
                    {testimonial.role}
                  </div>
                </div>
              </div>

              {/* Bottom accent */}
              <div className="absolute bottom-0 left-0 right-0 h-[2px] bg-gradient-to-r from-transparent via-primary/30 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
