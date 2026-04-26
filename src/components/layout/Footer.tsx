import React from "react";
import Link from "next/link";
import Image from "next/image";
import { Globe, Mail, ExternalLink, Globe2 } from "lucide-react";

const footerLinks = {
  Produit: [
    { label: "Découvrir", href: "/discover" },
    { label: "Fonctionnement", href: "#features" },
    { label: "Événements", href: "#community" },
    { label: "Associations", href: "#" },
  ],
  Légal: [
    { label: "CGU", href: "/terms" },
    { label: "Confidentialité", href: "/privacy" },
    { label: "Mentions légales", href: "#" },
    { label: "Cookies", href: "#" },
  ],
  Contact: [
    { label: "Support", href: "#" },
    { label: "Devenir partenaire", href: "#" },
    { label: "BDE Campus", href: "#" },
    { label: "Presse", href: "#" },
  ],
};

const socialLinks = [
  { icon: Globe, href: "#", label: "Twitter" },
  { icon: Globe2, href: "#", label: "Instagram" },
  { icon: ExternalLink, href: "#", label: "LinkedIn" },
  { icon: Mail, href: "#", label: "Contact" },
];

export const Footer = () => {
  return (
    <footer className="relative w-full border-t border-glass-border bg-card/50 pt-16 pb-8">
      {/* Top glow */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-1/2 h-px bg-gradient-to-r from-transparent via-primary/40 to-transparent" />

      <div className="container mx-auto px-6">
        <div className="grid gap-12 sm:grid-cols-2 md:grid-cols-5">
          {/* Brand Column */}
          <div className="md:col-span-2 space-y-6">
            <Link href="/" className="flex items-center gap-2">
              <Image
                src="/icon.png"
                alt="SchoolLink"
                width={36}
                height={36}
                className="rounded-xl"
              />
              <span className="text-xl font-bold tracking-tight">
                <span className="text-foreground">School</span>
                <span className="text-gradient-primary">Link</span>
              </span>
            </Link>
            <p className="text-sm text-text-secondary max-w-xs leading-relaxed">
              Créer du lien au cœur du campus. SchoolLink, la plateforme de
              networking innovante pour les étudiants.
            </p>
            {/* Social Icons */}
            <div className="flex items-center gap-3">
              {socialLinks.map((social) => (
                <Link
                  key={social.label}
                  href={social.href}
                  className="h-9 w-9 rounded-lg glass flex items-center justify-center text-text-secondary hover:text-primary-light hover:border-primary/30 transition-all duration-300"
                  aria-label={social.label}
                >
                  <social.icon className="h-4 w-4" />
                </Link>
              ))}
            </div>
          </div>

          {/* Link Columns */}
          {Object.entries(footerLinks).map(([title, links]) => (
            <div key={title} className="space-y-4">
              <h4 className="text-xs font-bold uppercase tracking-widest text-foreground">
                {title}
              </h4>
              <ul className="space-y-3">
                {links.map((link) => (
                  <li key={link.label}>
                    <Link
                      href={link.href}
                      className="text-sm text-text-secondary hover:text-primary-light transition-colors duration-300"
                    >
                      {link.label}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        {/* Bottom Bar */}
        <div className="mt-16 border-t border-glass-border pt-8 flex flex-col md:flex-row justify-between items-center gap-4">
          <p className="text-xs text-text-secondary">
            © {new Date().getFullYear()} SchoolLink. Tous droits réservés.
          </p>
          <p className="text-xs text-text-secondary">
            Fait avec 💜 pour les étudiants
          </p>
        </div>
      </div>
    </footer>
  );
};
