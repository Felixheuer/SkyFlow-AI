'use client';

import { useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Chat } from '@/components/chat';
import { BookingsTable } from '@/components/bookings-table';
import { ThemeToggle } from '@/components/theme-toggle';
import { Button } from '@/components/ui/button';
import {
  ArrowRight,
  BarChart3,
  BellRing,
  Clock,
  Layers,
  Plane,
  Shield,
  Sparkles,
} from 'lucide-react';

const navItems = ['Solutions', 'Platform', 'Security', 'Docs'];

const statHighlights = [
  { value: '1.2s', label: 'Avg. response', subLabel: 'live AI replies' },
  { value: '240+', label: 'Bookings automated', subLabel: 'per day' },
  { value: '98%', label: 'Customer CSAT', subLabel: 'global teams' },
];

const spotlightHighlights = [
  {
    title: 'Context-aware automations',
    description: 'Hand off seat changes, cancellations, and loyalty perks without leaving the chat.',
    icon: BarChart3,
  },
  {
    title: 'Proactive disruption alerts',
    description: 'Detect delays and reroute passengers with live inventory and policy guardrails.',
    icon: BellRing,
  },
  {
    title: 'Unified audit trail',
    description: 'Every message, approval, and itinerary change is recorded for compliance.',
    icon: Layers,
  },
];

const featureCards = [
  {
    icon: Sparkles,
    title: 'Conversational precision',
    description: 'Domain-tuned AI understands intents, validates inputs, and orchestrates complex bookings.',
  },
  {
    icon: Shield,
    title: 'Trust-grade security',
    description: 'Role-based access, SSO, and encryption keep traveller data and PNRs protected.',
  },
  {
    icon: Clock,
    title: 'Ops autopilot',
    description: 'Automate reminders, change requests, and confirmations across every channel.',
  },
];

export default function Home() {
  const [chatId] = useState(() => nanoid());
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    // Hydration guard to prevent SSR mismatch
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setMounted(true);
  }, []);

  const handleBookingUpdate = () => {
    setRefreshTrigger((prev) => prev + 1);
  };

  if (!mounted) {
    return null;
  }

  return (
    <div className="app-shell relative min-h-screen overflow-hidden">
      <div className="pattern-grid" aria-hidden />
      <div className="background-spotlight" aria-hidden />

      <header className="sticky top-0 z-50 border-b border-border/40 bg-background/85 dark:bg-background/70 backdrop-blur-2xl">
        <div className="container mx-auto px-4 lg:px-8">
          <div className="flex items-center justify-between gap-4 py-4">
            <div className="flex items-center gap-3">
              <div className="relative group">
                <div className="absolute inset-0 bg-primary/30 rounded-xl blur-2xl opacity-60 group-hover:opacity-100 transition" />
                <div className="relative w-11 h-11 rounded-xl gradient-primary flex items-center justify-center shadow-glow">
                  <Plane className="h-5 w-5 text-primary-foreground" />
                </div>
              </div>
              <div>
                <p className="text-xs uppercase tracking-[0.4em] text-muted-foreground">SkyFlow</p>
                <p className="text-xl font-semibold">Booking Intelligence</p>
              </div>
            </div>

            <nav className="hidden md:flex items-center gap-1.5">
              {navItems.map((item, index) => (
                <button
                  key={item}
                  className={`nav-link ${index === 0 ? 'nav-link-active' : ''}`}
                >
                  {item}
                </button>
              ))}
            </nav>

            <div className="flex items-center gap-2">
              <span className="pill pill-muted hidden lg:inline-flex items-center gap-2">
                <span className="relative flex h-2 w-2">
                  <span className="absolute inset-0 rounded-full bg-success opacity-60 animate-ping" />
                  <span className="relative inline-flex h-2 w-2 rounded-full bg-success" />
                </span>
                99.9% uptime
              </span>
              <ThemeToggle />
            </div>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 lg:px-8 py-8 lg:py-12 relative z-10 space-y-10 lg:space-y-12">
        <section className="grid gap-6 lg:grid-cols-[1.3fr_1fr]">
          <div className="surface-panel p-6 md:p-8 space-y-6">
            <div className="flex flex-wrap items-center gap-2">
              <span className="pill pill-primary">SkyFlow 2.0</span>
              <span className="pill pill-muted">Now in public preview</span>
            </div>
            <div className="space-y-4">
              <h2 className="text-3xl md:text-4xl lg:text-5xl font-semibold tracking-tight">
                Modern booking ops with a conversational cockpit.
              </h2>
              <p className="text-base md:text-lg text-muted-foreground max-w-2xl">
                Give travellers instant answers, give agents full context, and orchestrate every itinerary
                change from a single AI-native workspace.
              </p>
            </div>
            <div className="flex flex-wrap gap-3">
              <Button className="btn-primary gap-2">
                Start a conversation
                <ArrowRight className="h-4 w-4" />
              </Button>
              <Button variant="outline" className="btn-secondary">
                Explore automations
              </Button>
            </div>
            <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
              {statHighlights.map((stat) => (
                <div key={stat.label} className="stat-card">
                  <p className="stat-value">{stat.value}</p>
                  <p className="stat-label">{stat.label}</p>
                  <p className="stat-sub text-muted-foreground">{stat.subLabel}</p>
                </div>
              ))}
            </div>
          </div>

          <div className="surface-panel surface-accent p-6 md:p-8 flex flex-col gap-5">
            <div>
              <p className="text-xs uppercase tracking-[0.4em] text-primary-foreground/70">Live status</p>
              <h3 className="text-2xl font-semibold">Ops cockpit</h3>
              <p className="text-sm text-primary-foreground/80 leading-relaxed mt-1">
                Real-time orchestration, escalations, and approvals streamed into a single control center.
              </p>
            </div>
            <div className="space-y-4">
              {spotlightHighlights.map((item) => (
                <div
                  key={item.title}
                  className="rounded-2xl bg-primary-foreground/10 p-4 border border-primary-foreground/25 backdrop-blur-sm"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-2xl bg-primary-foreground/15 text-primary-foreground flex items-center justify-center">
                      <item.icon className="h-4 w-4" />
                    </div>
                    <h4 className="font-medium">{item.title}</h4>
                  </div>
                  <p className="text-sm text-primary-foreground/80 mt-2 leading-relaxed">
                    {item.description}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="grid grid-cols-1 xl:grid-cols-5 gap-6">
          <div
            className="xl:col-span-2 h-[520px] sm:h-[600px] lg:h-[700px] xl:h-[760px] animate-scale-in"
            style={{ animationDelay: '100ms' }}
          >
            <Chat chatId={chatId} onBookingUpdate={handleBookingUpdate} />
          </div>
          <div
            className="xl:col-span-3 h-[520px] sm:h-[600px] lg:h-[700px] xl:h-[760px] animate-scale-in"
            style={{ animationDelay: '180ms' }}
          >
            <BookingsTable refreshTrigger={refreshTrigger} />
          </div>
        </section>

        <section className="grid grid-cols-1 md:grid-cols-3 gap-4 lg:gap-6">
          {featureCards.map((feature, index) => (
            <div
              key={feature.title}
              className="surface-panel p-5 lg:p-6 space-y-3 animate-scale-in"
              style={{ animationDelay: `${300 + index * 80}ms` }}
            >
              <div className="w-12 h-12 rounded-2xl bg-primary/10 dark:bg-primary/20 flex items-center justify-center">
                <feature.icon className="h-5 w-5 text-primary" />
              </div>
              <div>
                <h3 className="text-lg font-semibold">{feature.title}</h3>
                <p className="text-sm text-muted-foreground leading-relaxed">{feature.description}</p>
              </div>
            </div>
          ))}
        </section>

        <section className="surface-panel p-6 md:p-8 flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="space-y-3 w-full md:w-auto">
            <div className="flex flex-wrap items-center gap-2">
              <span className="pill pill-primary">Customer-ready in days</span>
              <span className="pill pill-muted">Works with your stack</span>
            </div>
            <h3 className="text-2xl font-semibold">Every booking workflow, one assistant</h3>
            <p className="text-sm md:text-base text-muted-foreground max-w-2xl">
              Connect Sabre, Amadeus, Salesforce, and in-house tools without new tabs or training. SkyFlow keeps
              humans in the loop while automating the busywork.
            </p>
          </div>
          <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
            <Button className="btn-primary w-full sm:w-auto">Launch interactive demo</Button>
            <Button variant="outline" className="btn-secondary w-full sm:w-auto">
              Talk to an expert
            </Button>
          </div>
        </section>
      </main>

      <footer className="relative z-10 pb-10 px-4 lg:px-8">
        <div className="container mx-auto surface-panel px-5 py-6 md:px-8 md:py-7 flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <Plane className="h-4 w-4" />
            <span>© {new Date().getFullYear()} SkyFlow. All rights reserved.</span>
          </div>
          <div className="flex items-center gap-4 text-sm text-muted-foreground">
            <button className="hover:text-foreground transition-colors">Privacy</button>
            <button className="hover:text-foreground transition-colors">Security</button>
            <button className="hover:text-foreground transition-colors">Contact</button>
          </div>
        </div>
      </footer>
    </div>
  );
}
