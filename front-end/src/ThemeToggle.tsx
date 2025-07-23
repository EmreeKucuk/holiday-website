import React from 'react';
import { Sun, Moon } from 'lucide-react';
import { useTheme } from './ThemeContext';
import { useTranslation } from './TranslationContext';

const ThemeToggle: React.FC = () => {
  const { isDark, toggleTheme } = useTheme();
  const { t } = useTranslation();

  return (
    <button
      onClick={toggleTheme}
      className="fixed bottom-6 right-6 bg-primary-a50 dark:bg-surface-a20 border border-primary-a40 dark:border-surface-a30 rounded-full p-3 shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-110 z-50 group"
      title={t.theme.toggleTooltip}
      aria-label={isDark ? t.theme.lightMode : t.theme.darkMode}
    >
      <div className="relative w-6 h-6">
        <Sun 
          className={`absolute inset-0 w-6 h-6 text-yellow-500 transition-all duration-300 ${
            isDark ? 'opacity-0 rotate-90 scale-0' : 'opacity-100 rotate-0 scale-100'
          }`}
        />
        <Moon 
          className={`absolute inset-0 w-6 h-6 text-primary-a0 dark:text-primary-a30 transition-all duration-300 ${
            isDark ? 'opacity-100 rotate-0 scale-100' : 'opacity-0 -rotate-90 scale-0'
          }`}
        />
      </div>
      
      {/* Tooltip */}
      <div className="absolute bottom-full right-0 mb-2 px-3 py-1 bg-surface-a10 dark:bg-primary-a50 text-white dark:text-surface-a10 text-sm rounded-lg opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none whitespace-nowrap">
        {isDark ? t.theme.lightMode : t.theme.darkMode}
        <div className="absolute top-full right-3 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-surface-a10 dark:border-t-primary-a50"></div>
      </div>
    </button>
  );
};

export default ThemeToggle;
