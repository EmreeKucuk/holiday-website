// TranslationContext.tsx - React context for managing translations
import React, { createContext, useContext, useState, ReactNode } from 'react';
import { Translation, getTranslation } from './translations';

interface TranslationContextType {
  language: string;
  t: Translation;
  setLanguage: (language: string) => void;
  translateAudience: (audience: string) => string;
  translateHolidayType: (type: string) => string;
  translateCountry: (countryCode: string) => string;
}

const TranslationContext = createContext<TranslationContextType | undefined>(undefined);

interface TranslationProviderProps {
  children: ReactNode;
}

export const TranslationProvider: React.FC<TranslationProviderProps> = ({ children }) => {
  const [language, setLanguageState] = useState<string>('en');
  const t = getTranslation(language);

  const setLanguage = (newLanguage: string) => {
    setLanguageState(newLanguage);
  };

  const translateAudience = (audience: string): string => {
    const audienceKey = audience as keyof typeof t.audiences;
    return t.audiences[audienceKey] || audience;
  };

  const translateHolidayType = (type: string): string => {
    const typeKey = type as keyof typeof t.holidayTypes;
    return t.holidayTypes[typeKey] || type;
  };

  const translateCountry = (countryCode: string): string => {
    const countryKey = countryCode as keyof typeof t.countries;
    return t.countries[countryKey] || countryCode;
  };

  const value: TranslationContextType = {
    language,
    t,
    setLanguage,
    translateAudience,
    translateHolidayType,
    translateCountry,
  };

  return (
    <TranslationContext.Provider value={value}>
      {children}
    </TranslationContext.Provider>
  );
};

export const useTranslation = (): TranslationContextType => {
  const context = useContext(TranslationContext);
  if (!context) {
    throw new Error('useTranslation must be used within a TranslationProvider');
  }
  return context;
};
