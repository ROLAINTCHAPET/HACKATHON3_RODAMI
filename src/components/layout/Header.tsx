"use client";

import React, { useState, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import { Button } from "@/components/ui/Button";
import { usePathname, useRouter } from "next/navigation";
import { Menu, X, ChevronRight, Bell, Sparkles, UserPlus, LogOut, ShieldCheck } from "lucide-react";

export const Header = () => {
  const pathname = usePathname();
  const router = useRouter();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isNotifOpen, setIsNotifOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userRole, setUserRole] = useState<string | null>(null);

  const isDashboard = pathname.startsWith("/discover");

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener("scroll", handleScroll);
    
    // Check auth status
    const userId = localStorage.getItem("userId");
    const role = localStorage.getItem("userRole");
    setIsLoggedIn(!!userId);
    setUserRole(role);

    return () => window.removeEventListener("scroll", handleScroll);
  }, [pathname]);

  const handleLogout = () => {
    localStorage.removeItem("userId");
    setIsLoggedIn(false);
    router.push("/");
  };

  const notifications = [
    { id: 1, type: "connection", user: "Awa Diop", event: "Workshop Design", time: "2m" },
    { id: 2, type: "system", message: "Inférence Twist-02 active", event: "Calcul Shadow Data", time: "1h" },
    { id: 3, type: "event", user: "BDE", message: "Nouvel événement prioritaire (RF-16)", event: "Conférence Energie", time: "3h" },
  ];

  return (
    <header
      className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${
        scrolled || isDashboard ? "bg-background/80 backdrop-blur-md border-b border-glass-border py-4" : "bg-transparent py-6"
      }`}
    >
      <div className="container mx-auto px-6 flex items-center justify-between">
        <Link href="/" className="flex items-center gap-3 group">
          <div className="relative">
            <Image
              src="/icon.png"
              alt="SchoolLink"
              width={42}
              height={42}
              className="rounded-xl relative z-10"
            />
            <div className="absolute inset-0 rounded-xl bg-primary/30 blur-lg opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
          </div>
          <span className="text-2xl font-bold tracking-tight">
            <span className="text-foreground">School</span>
            <span className="text-gradient-primary">Link</span>
          </span>
        </Link>

        {/* Desktop Navigation - Hidden if logged in or on Dashboard */}
        {!isLoggedIn && !isDashboard && (
          <nav className="hidden lg:flex items-center gap-8">
            {[
              { href: "/discover", label: "Découvrir" },
              { href: "#features", label: "Fonctionnalités" },
              { href: "#community", label: "Communauté" },
            ].map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className="relative text-sm font-medium text-text-secondary hover:text-foreground transition-colors duration-300 group"
              >
                {link.label}
                <span className="absolute -bottom-1 left-0 w-0 h-0.5 bg-primary rounded-full group-hover:w-full transition-all duration-300" />
              </Link>
            ))}
          </nav>
        )}

        <div className="hidden lg:flex items-center gap-4 pl-4 border-l border-glass-border ml-2">
          {isLoggedIn ? (
            <>
              {/* Notification Bell - Only shown when logged in */}
              <div className="relative mr-4">
                <button 
                  onClick={() => setIsNotifOpen(!isNotifOpen)}
                  className="p-2 rounded-xl hover:bg-primary/10 text-text-secondary hover:text-primary transition-all relative group"
                >
                  <Bell className="h-5 w-5 group-hover:animate-bounce" />
                  <span className="absolute top-1.5 right-1.5 h-2 w-2 bg-primary rounded-full animate-pulse border-2 border-background" />
                </button>

                {isNotifOpen && (
                  <div className="absolute top-full right-0 mt-4 w-80 glass border border-glass-border rounded-2xl p-4 shadow-2xl animate-in fade-in slide-in-from-top-2 z-[60] transition-all duration-300 hover:bg-background hover:backdrop-blur-none">
                    <div className="flex items-center justify-between mb-4 border-b border-glass-border pb-2">
                      <h3 className="text-xs font-black uppercase tracking-widest text-text-secondary">Notifications d'Audit</h3>
                      <span className="text-[10px] text-primary-light font-black">RF-18 ACTIF</span>
                    </div>
                    <div className="space-y-3">
                      {notifications.map((n) => (
                        <div key={n.id} className="p-3 rounded-xl bg-white/5 border border-glass-border hover:bg-white/10 transition-colors cursor-pointer group">
                          <div className="flex items-start gap-3">
                            <div className="h-8 w-8 rounded-full bg-primary/20 flex items-center justify-center shrink-0">
                              {n.type === "connection" ? <UserPlus className="h-4 w-4 text-primary" /> : <Sparkles className="h-4 w-4 text-primary" />}
                            </div>
                            <div className="space-y-1">
                              <p className="text-xs text-foreground leading-tight">
                                {n.type === "connection" ? (
                                  <>Mise en relation : <span className="font-bold text-primary-light">{n.user}</span> via co-participation à <span className="italic">"{n.event}"</span></>
                                ) : (
                                  <>{n.message} : <span className="text-text-secondary">{n.event}</span></>
                                )}
                              </p>
                              <div className="flex items-center justify-between">
                                <span className="text-[9px] text-text-secondary uppercase font-bold">{n.time} ago</span>
                                <span className="text-[8px] text-teal font-black uppercase opacity-0 group-hover:opacity-100 transition-opacity">Vérifié ✓</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                    <Link href="/discover/notifications" onClick={() => setIsNotifOpen(false)}>
                      <button className="w-full mt-4 py-2 rounded-xl bg-primary/5 hover:bg-primary/10 border border-primary/20 text-[10px] font-black uppercase tracking-widest transition-colors">
                        Voir tout le log RF-18
                      </button>
                    </Link>
                  </div>
                )}
              </div>

              {/* Admin Link */}
              {userRole === "ADMIN" && (
                <Link href="/admin/governance">
                  <Button
                    variant="ghost" 
                    size="sm"
                    className="text-text-secondary hover:text-primary gap-2 px-4 hover:bg-primary/10"
                  >
                    <ShieldCheck className="h-4 w-4" />
                    Gouvernance
                  </Button>
                </Link>
              )}

              {/* Logout Button */}
              <Button
                variant="ghost" 
                size="sm"
                onClick={handleLogout}
                className="text-text-secondary hover:text-error gap-2 px-4 hover:bg-error/10"
              >
                <LogOut className="h-4 w-4" />
                Déconnexion
              </Button>
            </>
          ) : (
            <>
              <Link href="/login">
                <Button variant="ghost" size="sm" className="text-text-secondary hover:text-foreground px-4">
                  Connexion
                </Button>
              </Link>
              <Link href="/register">
                <Button
                  variant="primary"
                  size="sm"
                  className="glow-primary px-8 rounded-xl h-11"
                >
                  S'inscrire
                </Button>
              </Link>
            </>
          )}
        </div>

        {/* Mobile Menu Button */}
        <button
          className="md:hidden p-2 text-foreground hover:text-primary transition-colors"
          onClick={() => setIsMenuOpen(!isMenuOpen)}
        >
          {isMenuOpen ? <X size={28} /> : <Menu size={28} />}
        </button>
      </div>

      {/* Mobile Navigation */}
      {isMenuOpen && (
        <div className="md:hidden glass border-t border-glass-border px-4 py-6 space-y-6">
          {!isLoggedIn && (
            <nav className="flex flex-col gap-4">
              {[
                { label: "Découvrir", href: "/discover" },
                { label: "Fonctionnalités", href: "#features" },
                { label: "Communauté", href: "#community" },
              ].map((link) => (
                <Link
                  key={link.label}
                  href={link.href}
                  className="text-lg font-medium text-text-secondary hover:text-foreground transition-colors"
                  onClick={() => setIsMenuOpen(false)}
                >
                  {link.label}
                </Link>
              ))}
            </nav>
          )}
          {isLoggedIn && (
            <div className="pt-2">
              <h3 className="text-xs font-black uppercase tracking-widest text-text-secondary mb-4">Navigation Dashboard</h3>
              <nav className="flex flex-col gap-4">
                {[
                  { label: "Flux Global", href: "/discover" },
                  { label: "Mon Cercle", href: "/discover/circle" },
                  { label: "Événements", href: "/discover/events" },
                  { label: "Paramètres", href: "/discover/settings" },
                ].map((link) => (
                  <Link
                    key={link.label}
                    href={link.href}
                    className={`text-lg font-medium transition-colors ${
                      pathname === link.href ? "text-primary font-bold" : "text-text-secondary hover:text-foreground"
                    }`}
                    onClick={() => setIsMenuOpen(false)}
                  >
                    {link.label}
                  </Link>
                ))}
              </nav>
              <div className="mt-6 pt-6 border-t border-glass-border">
                <Button 
                  variant="outline" 
                  className="w-full justify-center gap-2 text-error border-error/30 hover:bg-error/10"
                  onClick={() => {
                    handleLogout();
                    setIsMenuOpen(false);
                  }}
                >
                  <LogOut className="h-5 w-5" />
                  Déconnexion
                </Button>
              </div>
            </div>
          )}
          {!isLoggedIn && (
            <div className="flex flex-col gap-3">
              <Link href="/login" onClick={() => setIsMenuOpen(false)}>
                <Button variant="outline" className="w-full">
                  Connexion
                </Button>
              </Link>
              <Link href="/register" onClick={() => setIsMenuOpen(false)}>
                <Button variant="primary" className="w-full">
                  S'inscrire
                </Button>
              </Link>
            </div>
          )}
        </div>
      )}
    </header>
  );
};
