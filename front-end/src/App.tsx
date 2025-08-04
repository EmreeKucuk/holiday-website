import { useState, useEffect, useRef } from 'react';
import { Calendar, Globe, Clock, Search, MapPin, ChevronRight, Loader2, CheckCircle, XCircle, Code } from 'lucide-react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";
import { useTranslation } from './TranslationContext';
import ThemeToggle from './ThemeToggle';
import ApiExplorer from './ApiExplorer';

const MySwal = withReactContent(Swal);

interface Holiday {
  name: string;
  date: string;
  countryCode: string;
  fixed: boolean;
  global: boolean;
  counties: string[] | null;
  launchYear: number | null;
  type: string;
  audiences?: string[]; // Add audience information
}

interface Country {
  countryCode: string;
  name: string;
}

interface Audience {
  code: string;
  audienceName: string;
}

function App() {
  const { language, t, setLanguage, translateAudience, translateHolidayType, translateCountry } = useTranslation();
  const [activeSection, setActiveSection] = useState('range');
  const [loading, setLoading] = useState(false);
  const [holidays, setHolidays] = useState<Holiday[]>([]);
  const [countries, setCountries] = useState<Country[]>([]);
  const [audiences, setAudiences] = useState<Audience[]>([]);
  const [todayResult, setTodayResult] = useState<{ isHoliday: boolean; holidays: Holiday[] } | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Available languages
  const languages = [
    { code: 'en', name: 'English' },
    { code: 'tr', name: 'Türkçe' }
  ];

  // Form states
  const [dateRange, setDateRange] = useState({ start: '2025-01-01', end: '2025-01-31' });
  const [selectedCountry, setSelectedCountry] = useState('TR');
  const [selectedAudience, setSelectedAudience] = useState('');
  const [sortBy, setSortBy] = useState('date'); // 'date', 'name', 'type'
  const [sortOrder, setSortOrder] = useState('asc'); // 'asc', 'desc'
  const [chatHistory, setChatHistory] = useState<{ user: string; ai: string; timestamp: Date }[]>([]);
  const [currentMessage, setCurrentMessage] = useState('');
  const chatContainerRef = useRef<HTMLDivElement>(null);
  const [includeEndDate, setIncludeEndDate] = useState(true); // For working days calculator
  const [showHolidaysInCalculator, setShowHolidaysInCalculator] = useState(true); // Show/hide holidays list in working days calculator
  const [holidaysLoadedForCurrentCalculation, setHolidaysLoadedForCurrentCalculation] = useState(false); // Track if holidays were loaded for current calculation
  const [workingDaysResult, setWorkingDaysResult] = useState<{
    totalDays: number;
    workingDays: number;
    holidayDays: number;
    holidays: Holiday[];
  } | null>(null);

  const sections = [
    { id: 'range', label: t.searchByDateRange, icon: Calendar },
    { id: 'today', label: t.todaysHolidays, icon: Clock },
    { id: 'workingdays', label: t.workingDaysCalculator || 'Working Days Calculator', icon: Search },
    { id: 'search', label: 'Search Countries', icon: Search },
    { id: 'chat', label: t.aiChat.title, icon: MapPin },
    { id: 'api-explorer', label: 'API Explorer', icon: Code },
  ];

  const handleDateRangeSearch = async () => {
    if (!dateRange.start || !dateRange.end) return;
    setLoading(true);
    setError(null);
    try {
      let url = `http://localhost:8080/api/holidays/range?start=${dateRange.start}&end=${dateRange.end}&country=${selectedCountry}&language=${language}`;
      if (selectedAudience) {
        url += `&audience=${encodeURIComponent(selectedAudience)}`;
      }
      console.log('Date Range Search - URL:', url);
      console.log('Date Range Search - Start:', dateRange.start, 'End:', dateRange.end, 'Country:', selectedCountry);
      const response = await fetch(url);
      if (!response.ok) throw new Error('Failed to fetch holidays');
      const data = await response.json();
      console.log('Date Range Search - Response:', data);
      setHolidays(data);
    } catch (err) {
      console.error('Date Range Search - Error:', err);
      setError(t.apiError);
    } finally {
      setLoading(false);
    }
  };

  const handleTodayCheck = async () => {
    setLoading(true);
    setError(null);
    try {
      let url = `http://localhost:8080/api/holidays/today?country=${selectedCountry}&language=${language}`;
      if (selectedAudience) {
        url += `&audience=${encodeURIComponent(selectedAudience)}`;
      }
      const response = await fetch(url);
      if (!response.ok) throw new Error('Failed to fetch today\'s holidays');
      const data = await response.json();
      setTodayResult({
        isHoliday: data.length > 0,
        holidays: data
      });
    } catch (err) {
      setError('Failed to check today\'s holidays');
    } finally {
      setLoading(false);
    }
  };

  const handleWorkingDaysCalculation = async () => {
    if (!dateRange.start || !dateRange.end) return;
    setLoading(true);
    setError(null);
    setHolidaysLoadedForCurrentCalculation(false); // Reset holidays loaded flag
    try {
      // First, get the working days calculation
      let url = `http://localhost:8080/api/holidays/working-days?start=${dateRange.start}&end=${dateRange.end}&country=${selectedCountry}&language=${language}&includeEndDate=${includeEndDate}`;
      if (selectedAudience) {
        url += `&audience=${encodeURIComponent(selectedAudience)}`;
      }
      console.log('Working Days Calculation - URL:', url);
      console.log('Selected Audience:', selectedAudience);
      console.log('Working Days Calculation Parameters:', {
        start: dateRange.start,
        end: dateRange.end,
        country: selectedCountry,
        language: language,
        includeEndDate: includeEndDate,
        audience: selectedAudience
      });
      const response = await fetch(url);
      if (!response.ok) throw new Error('Failed to calculate working days');
      const data = await response.json();
      console.log('Working Days Calculation - Response:', data);
      console.log('Working Days Calculation - Audience filter applied:', selectedAudience || 'All audiences');
      
      // If user wants to see holidays, fetch them separately for efficiency
      let holidaysData = [];
      if (showHolidaysInCalculator && data.holidayDays > 0) {
        try {
          let holidaysUrl = `http://localhost:8080/api/holidays/range?start=${dateRange.start}&end=${dateRange.end}&country=${selectedCountry}&language=${language}`;
          if (selectedAudience) {
            holidaysUrl += `&audience=${encodeURIComponent(selectedAudience)}`;
          }
          console.log('Fetching holidays for display - URL:', holidaysUrl);
          console.log('Holidays fetch parameters:', {
            start: dateRange.start,
            end: dateRange.end,
            country: selectedCountry,
            language: language,
            audience: selectedAudience
          });
          const holidaysResponse = await fetch(holidaysUrl);
          if (holidaysResponse.ok) {
            holidaysData = await holidaysResponse.json();
            console.log('Holidays fetched for display:', holidaysData);
            setHolidaysLoadedForCurrentCalculation(true);
          }
        } catch (holidaysErr) {
          console.error('Failed to fetch holidays for display:', holidaysErr);
          // Don't fail the entire operation if holidays can't be fetched
        }
      }
      
      // Combine the working days result with holidays if fetched
      setWorkingDaysResult({
        ...data,
        holidays: holidaysData
      });
    } catch (err) {
      console.error('Working Days Calculation - Error:', err);
      setError(t.apiError);
    } finally {
      setLoading(false);
    }
  };

  const loadHolidaysForWorkingDays = async () => {
    if (!workingDaysResult || !dateRange.start || !dateRange.end || workingDaysResult.holidayDays === 0 || holidaysLoadedForCurrentCalculation) return;
    
    setLoading(true);
    try {
      let holidaysUrl = `http://localhost:8080/api/holidays/range?start=${dateRange.start}&end=${dateRange.end}&country=${selectedCountry}&language=${language}`;
      if (selectedAudience) {
        holidaysUrl += `&audience=${encodeURIComponent(selectedAudience)}`;
      }
      console.log('Loading holidays for working days display - URL:', holidaysUrl);
      console.log('Load holidays parameters:', {
        start: dateRange.start,
        end: dateRange.end,
        country: selectedCountry,
        language: language,
        audience: selectedAudience
      });
      const holidaysResponse = await fetch(holidaysUrl);
      if (holidaysResponse.ok) {
        const holidaysData = await holidaysResponse.json();
        console.log('Holidays loaded for working days display:', holidaysData);
        setWorkingDaysResult(prev => prev ? { ...prev, holidays: holidaysData } : null);
        setHolidaysLoadedForCurrentCalculation(true);
      }
    } catch (holidaysErr) {
      console.error('Failed to load holidays for working days display:', holidaysErr);
      setError('Failed to load holidays list');
    } finally {
      setLoading(false);
    }
  };

  const handleCountryHolidays = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await fetch(`http://localhost:8080/api/holidays/country/${selectedCountry}?language=${language}`);
      if (!response.ok) throw new Error('Failed to fetch holidays');
      const data = await response.json();
      setHolidays(data);
    } catch (err) {
      setError(t.apiError);
    } finally {
      setLoading(false);
    }
  };

  const handleCountrySearch = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch('http://localhost:8080/api/countries');
      if (!response.ok) throw new Error('Failed to fetch countries');
      const data = await response.json();
      setCountries(data);
    } catch (err) {
      setError('Failed to fetch countries');
    } finally {
      setLoading(false);
    }
  };

  const fetchAudiences = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/holidays/audiences/translated?language=${language}`);
      if (!response.ok) throw new Error('Failed to fetch audiences');
      const data = await response.json();
      setAudiences(data.map((aud: {code: string, name: string}) => ({ 
        code: aud.code, 
        audienceName: aud.name 
      })));
    } catch (err) {
      console.error('Failed to fetch audiences:', err);
    }
  };

  useEffect(() => {
    fetchAudiences();
  }, [language]);

  useEffect(() => {
    handleCountrySearch();
  }, []);

  // Auto-scroll chat to bottom when new messages are added
  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }
  }, [chatHistory]);

  // Reset working days calculation when audience changes
  useEffect(() => {
    if (workingDaysResult) {
      // Clear the current result to force recalculation with new audience
      setWorkingDaysResult(null);
      setHolidaysLoadedForCurrentCalculation(false);
      console.log('Audience changed, clearing working days result to force recalculation');
    }
  }, [selectedAudience]);

  const formatDate = (dateString: string) => {
    const locale = language === 'tr' ? 'tr-TR' : 'en-US';
    return new Date(dateString).toLocaleDateString(locale, {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const handleCountryClick = async (countryCode: string) => {
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/holidays/country/${countryCode}`);
      if (!response.ok) throw new Error("Failed to fetch holidays");
      const holidays = await response.json();

      // Fetch types if needed
      const typesRes = await fetch("http://localhost:8080/api/holidays/types");
      const types = await typesRes.json();

      let filtered = holidays;

      const showPopup = (filterType = "", search = "") => {
        filtered = holidays.filter(
          (h: any) =>
            (!filterType || h.type === filterType) &&
            (!search || h.name.toLowerCase().includes(search.toLowerCase()))
        );

        MySwal.fire({
          title: `Holidays in ${countryCode}`,
          html: `
            <div>
              <input id="search-input" type="text" placeholder="Search holidays" class="swal2-input" />
              <select id="type-select" class="swal2-select">
                <option value="">All Types</option>
                ${types.map((t: string) => `<option value="${t}">${t}</option>`).join('')}
              </select>
              <div id="holidays-list" style="max-height: 300px; overflow-y: auto; margin-top: 10px;">
                ${filtered.length > 0 
                  ? filtered.map((h: any) => `
                      <div style="padding: 8px; border-bottom: 1px solid #eee;">
                        <b>${h.name}</b> - ${h.date} (${h.type})
                      </div>
                    `).join('')
                  : `<p>${t.noHolidaysFound}</p>`
                }
              </div>
            </div>
          `,
          width: 600,
          showCloseButton: true,
          showConfirmButton: false,
          didOpen: () => {
            const searchInput = document.getElementById('search-input') as HTMLInputElement;
            const typeSelect = document.getElementById('type-select') as HTMLSelectElement;
            const holidaysList = document.getElementById('holidays-list') as HTMLDivElement;

            const updateList = () => {
              const searchValue = searchInput.value.toLowerCase();
              const typeValue = typeSelect.value;
              
              const filteredHolidays = holidays.filter(
                (h: any) =>
                  (!typeValue || h.type === typeValue) &&
                  (!searchValue || h.name.toLowerCase().includes(searchValue))
              );

              holidaysList.innerHTML = filteredHolidays.length > 0 
                ? filteredHolidays.map((h: any) => `
                    <div style="padding: 8px; border-bottom: 1px solid #eee;">
                      <b>${h.name}</b> - ${h.date} (${h.type})
                    </div>
                  `).join('')
                : `<p>${t.noHolidaysFound}</p>`;
            };

            searchInput.addEventListener('input', updateList);
            typeSelect.addEventListener('change', updateList);
          }
        });
      };

      showPopup();
    } catch (err) {
      Swal.fire("Error", "Failed to fetch holidays for this country", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleChat = async (message: string) => {
    if (!message.trim()) return;
    
    setLoading(true);
    const userMessage = { user: message, ai: '', timestamp: new Date() };
    setChatHistory(prev => [...prev, userMessage]);
    setCurrentMessage('');
    
    try {
      console.log('Sending chat request:', { message, country: selectedCountry, language });
      
      // Add timeout to the fetch request
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 120000); // 120 seconds timeout

      const response = await fetch('http://localhost:8080/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          message,
          country: selectedCountry,
          language: language 
        }),
        signal: controller.signal
      });
      
      clearTimeout(timeoutId);
      console.log('Response status:', response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('AI response received:', data);
      
      setChatHistory(prev => {
        const updated = [...prev];
        updated[updated.length - 1] = { ...updated[updated.length - 1], ai: data.reply };
        return updated;
      });
    } catch (error) {
      console.error('Chat error:', error);
      let errorMessage = 'Sorry, I encountered an error. Please try again.';
      
      if (error instanceof Error) {
        if (error.name === 'AbortError') {
          errorMessage = 'Request timed out. The AI is taking too long to respond. Please try a shorter message.';
        } else if (error.message.includes('Failed to fetch')) {
          errorMessage = 'Cannot connect to the server. Please make sure the backend is running.';
        }
      }
      
      setChatHistory(prev => {
        const updated = [...prev];
        updated[updated.length - 1] = { ...updated[updated.length - 1], ai: errorMessage };
        return updated;
      });
    } finally {
      setLoading(false);
    }
  };

  const sortHolidays = (holidays: Holiday[]) => {
    const sorted = [...holidays].sort((a, b) => {
      let compareResult = 0;
      
      switch (sortBy) {
        case 'name':
          compareResult = a.name.localeCompare(b.name);
          break;
        case 'date':
          compareResult = new Date(a.date).getTime() - new Date(b.date).getTime();
          break;
        case 'type':
          compareResult = a.type.localeCompare(b.type);
          break;
        default:
          compareResult = new Date(a.date).getTime() - new Date(b.date).getTime();
      }
      
      return sortOrder === 'desc' ? -compareResult : compareResult;
    });
    

    return sorted;
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-a50 via-white to-primary-a40 dark:from-surface-a0 dark:via-surface-a10 dark:to-surface-a20 transition-colors duration-300">
      {/* Header */}
      <header className="bg-primary-a30/80 dark:bg-surface-a10/80 backdrop-blur-lg border-b border-primary-a20 dark:border-surface-a30 sticky top-0 z-50 transition-colors duration-300">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-r from-primary-a0 to-primary-a10 rounded-lg flex items-center justify-center">
                <Calendar className="w-6 h-6 text-black dark:text-white" />
              </div>
              <div>
                <h1 className="text-xl font-bold text-black dark:bg-gradient-to-r dark:from-primary-a30 dark:to-primary-a10 dark:bg-clip-text dark:text-transparent">
                  {t.title}
                </h1>
                <p className="text-sm text-black/90 dark:text-white">{t.subtitle}</p>
              </div>
            </div>
            
            <nav className="hidden md:flex space-x-1">
              {sections.map((section) => {
                const Icon = section.icon;
                return (
                  <button
                    key={section.id}
                    onClick={() => setActiveSection(section.id)}
                    className={`flex items-center space-x-2 px-4 py-2 rounded-lg transition-all duration-200 ${
                      activeSection === section.id
                        ? 'bg-primary-a40 dark:bg-surface-a30 text-black dark:text-white shadow-sm'
                        : 'text-black dark:text-white hover:bg-primary-a50 dark:hover:bg-surface-a20'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span className="text-sm font-medium">{section.label}</span>
                  </button>
                );
              })}
            </nav>
            
            {/* Language Selector */}
            <div className="flex items-center space-x-3">
              <label className="text-sm font-medium text-black dark:text-white">{t.languageSelector}:</label>
              <select
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
                className="px-3 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent text-sm bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
              >
                {languages.map((lang) => (
                  <option key={lang.code} value={lang.code}>
                    {lang.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Hero Section */}
        <div className="text-center mb-12">
          <h2 className="text-4xl font-bold text-black dark:text-white mb-4">
            {t.mainHeadline}
          </h2>
          <p className="text-xl text-black dark:text-white max-w-2xl mx-auto">
            {t.mainDescription}
          </p>
        </div>

        {/* Mobile Navigation */}
        <div className="md:hidden mb-8">
          <div className="flex overflow-x-auto space-x-2 pb-2">
            {sections.map((section) => {
              const Icon = section.icon;
              return (
                <button
                  key={section.id}
                  onClick={() => setActiveSection(section.id)}
                  className={`flex items-center space-x-2 px-4 py-2 rounded-lg whitespace-nowrap transition-all duration-200 ${
                    activeSection === section.id
                      ? 'bg-primary-a40 dark:bg-surface-a30 text-black dark:text-white shadow-sm'
                      : 'text-black dark:text-white bg-primary-a50 dark:bg-surface-a20 hover:bg-primary-a40 dark:hover:bg-surface-a30'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  <span className="text-sm font-medium">{section.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Content Sections */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Input Section */}
          <div className="lg:col-span-1">
            <div className="bg-primary-a50 dark:bg-surface-a10 rounded-xl shadow-lg p-6 sticky top-24 transition-colors duration-300">
              {activeSection === 'range' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <Calendar className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-black dark:text-white">{t.searchByDateRange}</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        {t.startDate}
                      </label>
                      <input
                        type="date"
                        value={dateRange.start}
                        onChange={(e) => setDateRange(prev => ({ ...prev, start: e.target.value }))}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        {t.endDate}
                      </label>
                      <input
                        type="date"
                        value={dateRange.end}
                        onChange={(e) => setDateRange(prev => ({ ...prev, end: e.target.value }))}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        Country
                      </label>
                      <select
                        value={selectedCountry}
                        onChange={(e) => setSelectedCountry(e.target.value)}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        {countries.map((country) => (
                          <option key={country.countryCode} value={country.countryCode}>
                            {country.name}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        {t.selectAudience}
                      </label>
                      <select
                        value={selectedAudience}
                        onChange={(e) => setSelectedAudience(e.target.value)}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        <option value="">{t.allAudiences}</option>
                        {audiences.map((audience) => (
                          <option key={audience.code} value={audience.code}>
                            {translateAudience(audience.audienceName)}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-black dark:text-white mb-2">
                          {t.sortBy}
                        </label>
                        <select
                          value={sortBy}
                          onChange={(e) => setSortBy(e.target.value)}
                          className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                        >
                          <option value="date">{t.date}</option>
                          <option value="name">{t.name}</option>
                          <option value="type">{t.type}</option>
                        </select>
                      </div>
                      
                      <div>
                        <label className="block text-sm font-medium text-black dark:text-white mb-2">
                          Order
                        </label>
                        <select
                          value={sortOrder}
                          onChange={(e) => setSortOrder(e.target.value)}
                          className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                        >
                          <option value="asc">{t.ascending}</option>
                          <option value="desc">{t.descending}</option>
                        </select>
                      </div>
                    </div>
                    
                    <button
                      onClick={handleDateRangeSearch}
                      disabled={loading || !dateRange.start || !dateRange.end}
                      className="w-full bg-gradient-to-r from-blue-500 to-purple-600 text-white px-6 py-3 rounded-lg font-medium hover:from-blue-600 hover:to-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
                    >
                      {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
                      <span>{loading ? t.loading : t.searchButton}</span>
                    </button>
                  </div>
                </div>
              )}

              {activeSection === 'workingdays' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <Calendar className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-black dark:text-white">{t.workingDaysCalculator}</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        {t.startDate}
                      </label>
                      <input
                        type="date"
                        value={dateRange.start}
                        onChange={(e) => setDateRange(prev => ({ ...prev, start: e.target.value }))}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        {t.endDate}
                      </label>
                      <input
                        type="date"
                        value={dateRange.end}
                        onChange={(e) => setDateRange(prev => ({ ...prev, end: e.target.value }))}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        Country
                      </label>
                      <select
                        value={selectedCountry}
                        onChange={(e) => setSelectedCountry(e.target.value)}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        {countries.map((country) => (
                          <option key={country.countryCode} value={country.countryCode}>
                            {translateCountry(country.countryCode)}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        {t.selectAudience}
                      </label>
                      <select
                        value={selectedAudience}
                        onChange={(e) => setSelectedAudience(e.target.value)}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        <option value="">{t.allAudiences}</option>
                        {audiences.map((audience) => (
                          <option key={audience.code} value={audience.code}>
                            {translateAudience(audience.audienceName)}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div className="flex items-center space-x-4">
                      <label className="block text-sm font-medium text-black dark:text-white">
                        End Date Calculation:
                      </label>
                      <div className="flex items-center space-x-4">
                        <label className="flex items-center">
                          <input
                            type="radio"
                            name="endDateOption"
                            checked={includeEndDate}
                            onChange={() => setIncludeEndDate(true)}
                            className="mr-2 text-primary-a0"
                          />
                          <span className="text-sm text-black dark:text-white">{t.includeEndDate}</span>
                        </label>
                        <label className="flex items-center">
                          <input
                            type="radio"
                            name="endDateOption"
                            checked={!includeEndDate}
                            onChange={() => setIncludeEndDate(false)}
                            className="mr-2 text-primary-a0"
                          />
                          <span className="text-sm text-black dark:text-white">{t.excludeEndDate}</span>
                        </label>
                      </div>
                    </div>
                    
                    <div className="flex items-center space-x-4">
                      <label className="block text-sm font-medium text-black dark:text-white">
                        Show Holidays List:
                      </label>
                      <div className="flex items-center space-x-4">
                        <label className="flex items-center">
                          <input
                            type="radio"
                            name="showHolidaysOption"
                            checked={showHolidaysInCalculator}
                            onChange={() => {
                              setShowHolidaysInCalculator(true);
                              // If we already have working days result but no holidays loaded yet, load them
                              if (workingDaysResult && !holidaysLoadedForCurrentCalculation && workingDaysResult.holidayDays > 0) {
                                loadHolidaysForWorkingDays();
                              }
                            }}
                            className="mr-2 text-primary-a0"
                          />
                          <span className="text-sm text-black dark:text-white">Show Holidays</span>
                        </label>
                        <label className="flex items-center">
                          <input
                            type="radio"
                            name="showHolidaysOption"
                            checked={!showHolidaysInCalculator}
                            onChange={() => setShowHolidaysInCalculator(false)}
                            className="mr-2 text-primary-a0"
                          />
                          <span className="text-sm text-black dark:text-white">Hide Holidays</span>
                        </label>
                      </div>
                    </div>
                    
                    {!workingDaysResult && selectedAudience && (
                      <div className="bg-blue-100 dark:bg-blue-900/30 p-3 rounded-lg border border-blue-300 dark:border-blue-700">
                        <p className="text-sm text-blue-800 dark:text-blue-300">
                          ℹ️ Audience "{translateAudience(audiences.find(a => a.code === selectedAudience)?.audienceName || selectedAudience)}" selected. Click "Calculate" to get results for this audience.
                        </p>
                      </div>
                    )}
                    
                    <button
                      onClick={handleWorkingDaysCalculation}
                      disabled={loading || !dateRange.start || !dateRange.end}
                      className="w-full bg-gradient-to-r from-green-500 to-blue-600 text-white px-6 py-3 rounded-lg font-medium hover:from-green-600 hover:to-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
                    >
                      {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Calendar className="w-4 h-4" />}
                      <span>{loading ? t.loading : t.calculateButton}</span>
                    </button>
                  </div>
                </div>
              )}

              {activeSection === 'today' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <Clock className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-black dark:text-white">Today's Holidays</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        Country
                      </label>
                      <select
                        value={selectedCountry}
                        onChange={(e) => setSelectedCountry(e.target.value)}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        {countries.map((country) => (
                          <option key={country.countryCode} value={country.countryCode}>
                            {country.name}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <button
                      onClick={handleTodayCheck}
                      disabled={loading}
                      className="w-full bg-gradient-to-r from-green-500 to-teal-600 text-white px-6 py-3 rounded-lg font-medium hover:from-green-600 hover:to-teal-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
                    >
                      {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Clock className="w-4 h-4" />}
                      <span>{loading ? t.loading : t.checkTodayButton}</span>
                    </button>
                  </div>
                </div>
              )}

              {activeSection === 'country' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <Globe className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-black dark:text-white">{t.filters}</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        {t.selectCountry}
                      </label>
                      <select
                        value={selectedCountry}
                        onChange={(e) => setSelectedCountry(e.target.value)}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        {countries.map((country) => (
                          <option key={country.countryCode} value={country.countryCode}>
                            {country.name}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <button
                      onClick={handleCountryHolidays}
                      disabled={loading}
                      className="w-full bg-gradient-to-r from-purple-500 to-pink-600 text-white px-6 py-3 rounded-lg font-medium hover:from-purple-600 hover:to-pink-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
                    >
                      {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Globe className="w-4 h-4" />}
                      <span>{loading ? 'Loading...' : 'Get Holidays'}</span>
                    </button>
                  </div>
                </div>
              )}

              {activeSection === 'search' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <Search className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-black dark:text-white">Available Countries</h3>
                  </div>
                  
                  <button
                    onClick={handleCountrySearch}
                    disabled={loading}
                    className="w-full bg-gradient-to-r from-orange-500 to-red-600 text-white px-6 py-3 rounded-lg font-medium hover:from-orange-600 hover:to-red-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
                  >
                    {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
                    <span>{loading ? 'Loading...' : 'Load Countries'}</span>
                  </button>
                </div>
              )}

              {activeSection === 'chat' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <MapPin className="w-5 h-5 text-primary-black dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-black dark:text-white">{t.aiChat.title}</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-black dark:text-white mb-2">
                        {t.aiChat.countryContext}
                      </label>
                      <select
                        value={selectedCountry}
                        onChange={(e) => setSelectedCountry(e.target.value)}
                        className="w-full px-4 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        {countries.map((country) => (
                          <option key={country.countryCode} value={country.countryCode}>
                            {country.name}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div className="text-sm text-black dark:text-white bg-primary-a40 dark:bg-surface-a30 p-3 rounded-lg">
                      <p className="font-medium mb-2">{t.aiChat.tryAsking}</p>
                      <ul className="space-y-1 text-sm">
                        <li>• "{t.aiChat.suggestions.todayHoliday}"</li>
                        <li>• "{t.aiChat.suggestions.vacationOptimization}"</li>
                        <li>• "{t.aiChat.suggestions.holidayRange}"</li>
                        <li>• "{t.aiChat.suggestions.religiousHolidays}"</li>
                        <li>• "{t.aiChat.suggestions.holidayDuration}"</li>
                        <li>• "{t.aiChat.suggestions.specificHoliday}"</li>
                        <li>• "{t.aiChat.suggestions.workingDays}"</li>
                        <li>• "{t.aiChat.suggestions.holidayStatistics}"</li>
                        <li>• "{t.aiChat.suggestions.yearlyHolidays}"</li>
                        <li>• "{t.aiChat.suggestions.monthlyHolidays}"</li>
                        <li>• "{t.aiChat.suggestions.audienceHolidays}"</li>
                      </ul>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Results Section */}
          <div className="lg:col-span-2">
            <div className="bg-primary-a50 dark:bg-surface-a10 rounded-xl shadow-lg p-6 transition-colors duration-300">
              {error && (
                <div className="mb-6 p-4 bg-red-400/20 dark:bg-red-900/20 border border-red-300 dark:border-red-800 rounded-lg flex items-center space-x-3">
                  <XCircle className="w-5 h-5 text-red-700 dark:text-red-400" />
                  <span className="text-red-800 dark:text-red-300">{error}</span>
                </div>
              )}

              {activeSection === 'today' && todayResult && (
                <div className="space-y-4">
                  <div className="flex items-center space-x-3 mb-4">
                    <Clock className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-black dark:text-white">Today's Result</h3>
                  </div>
                  
                  <div className={`p-4 rounded-lg border-2 ${
                    todayResult.isHoliday 
                      ? 'bg-green-100 dark:bg-green-900/30 border-green-300 dark:border-green-700' 
                      : 'bg-primary-a50 dark:bg-surface-a30 border-primary-a30 dark:border-surface-a40'
                  }`}>
                    <div className="flex items-center space-x-3">
                      {todayResult.isHoliday ? (
                        <CheckCircle className="w-6 h-6 text-green-600 dark:text-green-400" />
                      ) : (
                        <XCircle className="w-6 h-6 text-gray-500 dark:text-gray-400" />
                      )}
                      <div>
                        <p className="font-medium text-black dark:text-white">
                          {todayResult.isHoliday ? t.todayIsHoliday : t.noHolidaysToday}
                        </p>
                        <p className="text-sm text-black dark:text-white">
                          {formatDate(new Date().toISOString().split('T')[0])}
                        </p>
                      </div>
                    </div>
                    
                    {todayResult.holidays.length > 0 && (
                      <div className="mt-4 space-y-2">
                        {todayResult.holidays.map((holiday, index) => (
                          <div key={index} className="bg-white dark:bg-surface-a40 p-3 rounded-lg">
                            <h4 className="font-medium text-black dark:text-white">{holiday.name}</h4>
                            <p className="text-sm text-black dark:text-white">{holiday.type} Holiday</p>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              )}

              {activeSection === 'workingdays' && workingDaysResult && (
                <div className="space-y-4">
                  <div className="flex items-center space-x-3 mb-4">
                    <Calendar className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-a60 dark:text-white">{t.workingDaysResults}</h3>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                    <div className="bg-blue-100 dark:bg-blue-900/30 p-4 rounded-lg border border-blue-300 dark:border-blue-700">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-blue-700 dark:text-blue-300">{t.totalDays}</p>
                          <p className="text-2xl font-bold text-blue-900 dark:text-blue-100">{workingDaysResult.totalDays}</p>
                        </div>
                        <Calendar className="w-8 h-8 text-blue-500 dark:text-blue-400" />
                      </div>
                    </div>
                    
                    <div className="bg-green-100 dark:bg-green-900/30 p-4 rounded-lg border border-green-300 dark:border-green-700">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-green-700 dark:text-green-300">{t.workingDays}</p>
                          <p className="text-2xl font-bold text-green-900 dark:text-green-100">{workingDaysResult.workingDays}</p>
                        </div>
                        <CheckCircle className="w-8 h-8 text-green-500 dark:text-green-400" />
                      </div>
                    </div>
                    
                    <div className="bg-red-100 dark:bg-red-900/30 p-4 rounded-lg border border-red-300 dark:border-red-700">
                      <div className="flex items-center justify-between">
                        <div>
                          <p className="text-sm font-medium text-red-700 dark:text-red-300">{t.holidayDays}</p>
                          <p className="text-2xl font-bold text-red-900 dark:text-red-100">{workingDaysResult.holidayDays}</p>
                        </div>
                        <XCircle className="w-8 h-8 text-red-500 dark:text-red-400" />
                      </div>
                    </div>
                  </div>
                  
                  {showHolidaysInCalculator && (
                    <div>
                      <h4 className="text-md font-semibold text-white dark:text-white mb-3">Holidays in Date Range:</h4>
                      {loading ? (
                        <div className="flex items-center justify-center py-4">
                          <Loader2 className="w-5 h-5 animate-spin text-primary-a0 dark:text-primary-a30 mr-2" />
                          <span className="text-black dark:text-white">Loading holidays...</span>
                        </div>
                      ) : workingDaysResult.holidays && workingDaysResult.holidays.length > 0 ? (
                        <div className="space-y-2">
                          {workingDaysResult.holidays.map((holiday, index) => (
                            <div key={index} className="p-3 bg-primary-a50 dark:bg-surface-a40 rounded-lg border border-primary-a30 dark:border-surface-a40">
                              <div className="flex items-center justify-between">
                                <div>
                                  <h5 className="font-medium text-black dark:text-white">{holiday.name}</h5>
                                  <p className="text-sm text-black dark:text-white">{formatDate(holiday.date)}</p>
                                </div>
                                <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                                  {translateHolidayType(holiday.type)}
                                </span>
                              </div>
                            </div>
                          ))}
                        </div>
                      ) : workingDaysResult.holidayDays > 0 ? (
                        <div className="text-center py-4">
                          <p className="text-black dark:text-white">Click "Show Holidays" and then recalculate to see the holidays list.</p>
                        </div>
                      ) : (
                        <div className="text-center py-4">
                          <p className="text-black dark:text-white">No holidays found in the selected date range.</p>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              )}

              {activeSection === 'search' && countries.length > 0 && (
                <div className="space-y-4">
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center space-x-3">
                      <MapPin className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                      <h3 className="text-lg font-semibold text-black dark:text-white">Available Countries</h3>
                    </div>
                    
                    <div className="flex items-center space-x-3">
                      <select
                        value={sortBy}
                        onChange={(e) => setSortBy(e.target.value)}
                        className="px-3 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent text-sm bg-white dark:bg-surface-a20 text-black dark:text-white"
                      >
                        <option value="name">{t.sortByName}</option>
                        <option value="code">{t.sortByCode}</option>
                      </select>
                      
                      <select
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value)}
                        className="px-3 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent text-sm bg-white dark:bg-surface-a20 text-black dark:text-white"
                      >
                        <option value="asc">A-Z</option>
                        <option value="desc">Z-A</option>
                      </select>
                    </div>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    {countries
                      .sort((a, b) => {
                        const compareValue = sortBy === 'name' ? a.name.localeCompare(b.name) : a.countryCode.localeCompare(b.countryCode);
                        return sortOrder === 'desc' ? -compareValue : compareValue;
                      })
                      .map((country) => (
                      <div
                        key={country.countryCode}
                        className="p-4 border border-gray-200 dark:border-surface-a40 rounded-lg hover:bg-gray-50 dark:hover:bg-surface-a30 transition-colors duration-200 cursor-pointer bg-white dark:bg-surface-a40"
                        onClick={() => handleCountryClick(country.countryCode)}
                      >
                        <div className="flex items-center justify-between">
                          <div>
                            <h4 className="font-medium text-black dark:text-white">{country.name}</h4>
                            <p className="text-sm text-black dark:text-white">{country.countryCode}</p>
                          </div>
                          <ChevronRight className="w-4 h-4 text-black dark:text-white" />
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {(activeSection === 'range' || activeSection === 'country') && holidays.length > 0 && (
                <div className="space-y-4">
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center space-x-3">
                      <Calendar className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                      <h3 className="text-lg font-semibold text-black dark:text-white">
                        {activeSection === 'range' ? t.dateRangeResults : t.countryHolidays}
                      </h3>
                    </div>
                    
                    <div className="flex items-center space-x-3">
                      <select
                        value={sortBy}
                        onChange={(e) => setSortBy(e.target.value)}
                        className="px-3 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent text-sm bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        <option value="date">{t.sortByDate}</option>
                        <option value="name">{t.sortByName}</option>
                        <option value="type">{t.sortByType}</option>
                      </select>
                      
                      <select
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value)}
                        className="px-3 py-2 border border-primary-a20 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent text-sm bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                      >
                        <option value="asc">{sortBy === 'date' ? 'Earliest First' : 'A-Z'}</option>
                        <option value="desc">{sortBy === 'date' ? 'Latest First' : 'Z-A'}</option>
                      </select>
                    </div>
                  </div>
                  
                  <div className="space-y-3">
                    {sortHolidays(holidays).map((holiday, index) => (
                      <div key={index} className="p-4 border border-gray-200 dark:border-surface-a40 rounded-lg hover:bg-gray-50 dark:hover:bg-surface-a30 transition-colors duration-200 bg-white dark:bg-surface-a40">
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <h4 className="font-medium text-black dark:text-white mb-1">{holiday.name}</h4>
                            <p className="text-sm text-black dark:text-white mb-2">{formatDate(holiday.date)}</p>
                            <div className="flex items-center flex-wrap gap-4 text-xs text-black dark:text-white">
                              <span className="flex items-center space-x-1">
                                <span>Type:</span>
                                <span className="font-medium">{holiday.type}</span>
                              </span>
                              <span className="flex items-center space-x-1">
                                <span>{t.type_label}:</span>
                                <span className="font-medium">{translateHolidayType(holiday.type)}</span>
                              </span>
                              {holiday.audiences && holiday.audiences.length > 0 && (
                                <span className="flex items-center space-x-1">
                                  <span>{t.audiences_label}:</span>
                                  <span className="font-medium">{holiday.audiences.map(aud => translateAudience(aud)).join(', ')}</span>
                                </span>
                              )}
                              {holiday.launchYear && (
                                <span className="flex items-center space-x-1">
                                  <span>{t.since}</span>
                                  <span className="font-medium">{holiday.launchYear}</span>
                                </span>
                              )}
                            </div>
                          </div>
                          <div className="ml-4">
                            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                              {translateCountry(holiday.countryCode)}
                            </span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {activeSection === 'chat' && (
                <div className="flex flex-col h-[600px]">
                  {/* Chat Header */}
                  <div className="flex items-center space-x-3 mb-4 pb-4 border-b border-primary-a40 dark:border-surface-a30">
                    <MapPin className="w-5 h-5 text-primary-a0 dark:text-primary-a30" />
                    <h3 className="text-lg font-semibold text-black dark:text-white">{t.aiChat.title}</h3>
                    <span className="text-sm text-black/80 dark:text-surface-a50">({translateCountry(selectedCountry)})</span>
                  </div>
                  
                  {/* Chat Messages */}
                  <div ref={chatContainerRef} className="flex-1 overflow-y-auto space-y-4 mb-4 px-2">
                    {chatHistory.length === 0 ? (
                      <div className="text-center py-8">
                        <div className="w-16 h-16 bg-primary-a30 dark:bg-surface-a30 rounded-full flex items-center justify-center mx-auto mb-4">
                          <MapPin className="w-8 h-8 text-primary-a60 dark:text-primary-a30" />
                        </div>
                        <h4 className="text-lg font-medium text-black dark:text-white mb-2">{t.aiChat.welcomeTitle}</h4>
                        <p className="text-black/90 dark:text-surface-a50 max-w-md mx-auto">
                          {t.aiChat.welcomeMessage}
                        </p>
                      </div>
                    ) : (
                      chatHistory.map((chat, index) => (
                        <div key={index} className="space-y-3">
                          {/* User Message */}
                          <div className="flex justify-end">
                            <div className="max-w-xs lg:max-w-md">
                              <div className="bg-blue-600 text-white rounded-lg rounded-br-none px-4 py-2">
                                <p className="text-sm">{chat.user}</p>
                              </div>
                              <div className="text-xs text-black dark:text-white mt-1 text-right">
                                {chat.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                              </div>
                            </div>
                          </div>
                          
                          {/* AI Response */}
                          {chat.ai && (
                            <div className="flex justify-start">
                              <div className="max-w-xs lg:max-w-md">
                                <div className="bg-gray-100 dark:bg-surface-a40 text-gray-900 dark:text-white rounded-lg rounded-bl-none px-4 py-2">
                                  <p className="text-sm whitespace-pre-wrap">{chat.ai}</p>
                                </div>
                                <div className="text-xs text-black dark:text-white mt-1">
                                  {t.aiChat.assistant}
                                </div>
                              </div>
                            </div>
                          )}
                          
                          {/* Loading indicator for the last message if AI hasn't responded */}
                          {index === chatHistory.length - 1 && !chat.ai && loading && (
                            <div className="flex justify-start">
                              <div className="max-w-xs lg:max-w-md">
                                <div className="bg-gray-100 dark:bg-surface-a40 text-black dark:text-white rounded-lg rounded-bl-none px-4 py-2">
                                  <div className="flex items-center space-x-2">
                                    <Loader2 className="w-4 h-4 animate-spin text-blue-600" />
                                    <span className="text-sm text-black dark:text-white">{t.aiChat.thinking}</span>
                                  </div>
                                </div>
                              </div>
                            </div>
                          )}
                        </div>
                      ))
                    )}
                  </div>
                  
                  {/* Chat Input */}
                  <div className="border-t border-gray-200 pt-4">
                    <div className="flex space-x-2">
                      <input
                        type="text"
                        value={currentMessage}
                        onChange={(e) => setCurrentMessage(e.target.value)}
                        onKeyPress={(e) => {
                          if (e.key === 'Enter' && !e.shiftKey) {
                            e.preventDefault();
                            handleChat(currentMessage);
                          }
                        }}
                        placeholder={t.aiChat.inputPlaceholder}
                        className="flex-1 px-4 py-2 border border-primary-a40 dark:border-surface-a40 rounded-lg focus:ring-2 focus:ring-primary-a0 focus:border-transparent bg-white dark:bg-surface-a20 text-gray-900 dark:text-white"
                        disabled={loading}
                      />
                      <button
                        onClick={() => handleChat(currentMessage)}
                        disabled={loading || !currentMessage.trim()}
                        className="bg-primary-a0 dark:bg-primary-a20 text-white px-4 py-2 rounded-lg hover:bg-primary-a10 dark:hover:bg-primary-a30 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200 flex items-center space-x-2"
                      >
                        {loading ? (
                          <Loader2 className="w-4 h-4 animate-spin" />
                        ) : (
                          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                          </svg>
                        )}
                      </button>
                    </div>
                    <div className="text-xs text-white/70 dark:text-surface-a50 mt-2">
                      {t.aiChat.enterToSend} • {t.aiChat.shiftEnterNewLine}
                    </div>
                  </div>
                </div>
              )}

              {activeSection === 'api-explorer' && (
                <div className="w-full">
                  <ApiExplorer />
                </div>
              )}

              {/* Show "no holidays found" when search was performed but returned empty results */}
              {!loading && !error && (activeSection === 'range' || activeSection === 'country') && holidays.length === 0 && (
                <div className="text-center py-12">
                  <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Calendar className="w-8 h-8 text-yellow-600" />
                  </div>
                  <h3 className="text-lg font-medium text-black dark:text-white mb-2">{t.noHolidaysFound}</h3>
                  <p className="text-black dark:text-white">
                    Try different search criteria or date range.
                  </p>
                </div>
              )}

              {!loading && !error && holidays.length === 0 && !todayResult && countries.length === 0 && (
                <div className="text-center py-12">
                  <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Calendar className="w-8 h-8 text-gray-400" />
                  </div>
                  <h3 className="text-lg font-medium text-black dark:text-white mb-2">No Results Yet</h3>
                  <p className="text-black dark:text-white">
                    Select a search method and click the button to get started
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>

      {/* Theme Toggle */}
      <ThemeToggle />
    </div>
  );
}

export default App;