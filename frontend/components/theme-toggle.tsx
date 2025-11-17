'use client';

import * as React from 'react';
import { Moon, Sun } from 'lucide-react';
import { useTheme } from 'next-themes';
import { Button } from '@/components/ui/button';

export function ThemeToggle() {
  const { resolvedTheme, setTheme } = useTheme();
  const [mounted, setMounted] = React.useState(false);

  React.useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <Button variant="ghost" size="icon" className="h-9 w-9 md:h-10 md:w-10 glass border-border/30">
        <Sun className="h-4 w-4 md:h-5 md:w-5" />
      </Button>
    );
  }

  const isDark = resolvedTheme === 'dark';

  return (
    <Button
      variant="ghost"
      size="icon"
      onClick={() => setTheme(isDark ? 'light' : 'dark')}
      className="h-9 w-9 md:h-10 md:w-10 relative overflow-hidden group glass border-border/30 hover:bg-accent transition-all duration-300"
    >
      <Sun className={`h-4 w-4 md:h-5 md:w-5 absolute transition-all duration-300 ${
        isDark ? 'rotate-90 scale-0 opacity-0' : 'rotate-0 scale-100 opacity-100'
      }`} />
      <Moon className={`h-4 w-4 md:h-5 md:w-5 absolute transition-all duration-300 ${
        isDark ? 'rotate-0 scale-100 opacity-100' : '-rotate-90 scale-0 opacity-0'
      }`} />
      <span className="sr-only">Toggle theme</span>
    </Button>
  );
}

