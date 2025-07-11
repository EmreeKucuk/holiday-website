import React, { useState, useEffect } from 'react';
import { Calendar, Globe, Clock, Search, MapPin, ChevronRight, Loader2, CheckCircle, XCircle } from 'lucide-react';
import Swal from 'sweetalert2';

interface Holiday {
  name: string;
  date: string;
  countryCode: string;
  fixed: boolean;
  global: boolean;
  counties: string[] | null;
  launchYear: number | null;
  type: string;
}

interface Country {
  countryCode: string;
  name: string;
}

interface HolidayTemplate {
  id: number;
  code: string;
  defaultName: string;
  type: string;
}

function App() {
  const [activeSection, setActiveSection] = useState('range');
  const [loading, setLoading] = useState(false);
  const [holidays, setHolidays] = useState<Holiday[]>([]);
  const [countries, setCountries] = useState<Country[]>([]);
  const [todayResult, setTodayResult] = useState<{ isHoliday: boolean; holidays: Holiday[] } | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [holidayTemplates, setHolidayTemplates] = useState<HolidayTemplate[]>([]);
  const [loadingTemplates, setLoadingTemplates] = useState(false);

  // Form states
  const [dateRange, setDateRange] = useState({ start: '', end: '' });
  const [selectedCountry, setSelectedCountry] = useState('US');
  const [specificDate, setSpecificDate] = useState('');
  const [chatHistory, setChatHistory] = useState<{ user: string; ai: string }[]>([]);

  const sections = [
    { id: 'range', label: 'Date Range', icon: Calendar },
    { id: 'today', label: 'Today\'s Holidays', icon: Clock },
    { id: 'country', label: 'By Country', icon: Globe },
    { id: 'search', label: 'Search Countries', icon: Search },
    { id: 'chat', label: 'AI Chat', icon: MapPin },
  ];

  const handleDateRangeSearch = async () => {
    if (!dateRange.start || !dateRange.end) return;

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `http://localhost:8080/api/holidays/range?start=${dateRange.start}&end=${dateRange.end}`
      );
      if (!response.ok) throw new Error('Failed to fetch holidays');
      const data = await response.json();
      setHolidays(data);
    } catch (err) {
      setError('Failed to fetch holidays for the specified date range');
    } finally {
      setLoading(false);
    }
  };

  const handleTodayCheck = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(
        `http://localhost:8080/api/holidays/today?country=${selectedCountry}`
      );
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

  const handleCountryHolidays = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await fetch(`http://localhost:8080/api/holidays/country/${selectedCountry}`);
      if (!response.ok) throw new Error('Failed to fetch holidays');
      const data = await response.json();
      setHolidays(data);
    } catch (err) {
      setError('Failed to fetch holidays for the selected country');
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

  const fetchHolidayTemplates = async () => {
    setLoadingTemplates(true);
    try {
      const response = await fetch('http://localhost:8080/api/holiday-templates');
      if (!response.ok) throw new Error('Failed to fetch');
      const data = await response.json();
      setHolidayTemplates(data);
    } catch (err) {
      setHolidayTemplates([]);
    } finally {
      setLoadingTemplates(false);
    }
  };

  useEffect(() => {
    fetchHolidayTemplates();
  }, []);

  useEffect(() => {
    handleCountrySearch();
  }, []);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
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
      const holidays = await response.json();

      // Optionally, fetch audiences/types for filters
      // const audiences = await fetch(...);
      // const types = await fetch(...);

      Swal.fire({
        title: `Holidays in ${countryCode}`,
        html: `
          <div>
            <label>Type:</label>
            <select id="type-filter">
              <option value="">All</option>
              <option value="religious">Religious</option>
              <option value="official">Official</option>
              <!-- Add more types dynamically -->
            </select>
          </div>
          <div id="holiday-list">
            ${holidays.map((h: any) => `<div>${h.name} - ${h.date} (${h.type})</div>`).join('')}
          </div>
        `,
        showCloseButton: true,
        width: 600,
        didOpen: () => {
          // Add filter logic here if needed
        }
      });
    } finally {
      setLoading(false);
    }
  };

  const handleChat = async (message: string) => {
    const response = await fetch('http://localhost:8080/api/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message })
    });
    const data = await response.json();
    setChatHistory([...chatHistory, { user: message, ai: data.reply }]);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-lg border-b border-gray-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
                <Calendar className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                  Holiday API
                </h1>
                <p className="text-sm text-gray-600">Explore holidays worldwide</p>
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
                        ? 'bg-blue-100 text-blue-700 shadow-sm'
                        : 'text-gray-600 hover:bg-gray-100'
                    }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span className="text-sm font-medium">{section.label}</span>
                  </button>
                );
              })}
            </nav>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Hero Section */}
        <div className="text-center mb-12">
          <h2 className="text-4xl font-bold text-gray-900 mb-4">
            Discover Holidays Around the World
          </h2>
          <p className="text-xl text-gray-600 max-w-2xl mx-auto">
            Access comprehensive holiday data for any country, check specific dates, and explore global celebrations
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
                      ? 'bg-blue-100 text-blue-700 shadow-sm'
                      : 'text-gray-600 bg-white hover:bg-gray-50'
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
            <div className="bg-white rounded-xl shadow-lg p-6 sticky top-24">
              {activeSection === 'range' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <Calendar className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">Date Range Search</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Start Date
                      </label>
                      <input
                        type="date"
                        value={dateRange.start}
                        onChange={(e) => setDateRange(prev => ({ ...prev, start: e.target.value }))}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        End Date
                      </label>
                      <input
                        type="date"
                        value={dateRange.end}
                        onChange={(e) => setDateRange(prev => ({ ...prev, end: e.target.value }))}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      />
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Country
                      </label>
                      <select
                        value={selectedCountry}
                        onChange={(e) => setSelectedCountry(e.target.value)}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      >
                        {countries.map((country) => (
                          <option key={country.countryCode} value={country.countryCode}>
                            {country.name}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <button
                      onClick={handleDateRangeSearch}
                      disabled={loading || !dateRange.start || !dateRange.end}
                      className="w-full bg-gradient-to-r from-blue-500 to-purple-600 text-white px-6 py-3 rounded-lg font-medium hover:from-blue-600 hover:to-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
                    >
                      {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
                      <span>{loading ? 'Searching...' : 'Search Holidays'}</span>
                    </button>
                  </div>
                </div>
              )}

              {activeSection === 'today' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <Clock className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">Today's Holidays</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Country
                      </label>
                      <select
                        value={selectedCountry}
                        onChange={(e) => setSelectedCountry(e.target.value)}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
                      <span>{loading ? 'Checking...' : 'Check Today'}</span>
                    </button>
                  </div>
                </div>
              )}

              {activeSection === 'country' && (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3 mb-4">
                    <Globe className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">Browse by Country</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Select Country
                      </label>
                      <select
                        value={selectedCountry}
                        onChange={(e) => setSelectedCountry(e.target.value)}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
                    <Search className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">Available Countries</h3>
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
                    <MapPin className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">AI Chat</h3>
                  </div>
                  
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Your Message
                      </label>
                      <textarea
                        value={chatHistory[chatHistory.length - 1]?.user}
                        onChange={(e) => setChatHistory(prev => {
                          const newHistory = [...prev];
                          newHistory[newHistory.length - 1].user = e.target.value;
                          return newHistory;
                        })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        rows={3}
                      />
                    </div>
                    
                    <button
                      onClick={() => handleChat(chatHistory[chatHistory.length - 1]?.user)}
                      disabled={loading}
                      className="w-full bg-gradient-to-r from-indigo-500 to-blue-600 text-white px-6 py-3 rounded-lg font-medium hover:from-indigo-600 hover:to-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center space-x-2"
                    >
                      {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <MapPin className="w-4 h-4" />}
                      <span>{loading ? 'Sending...' : 'Send Message'}</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>

          {/* Results Section */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-xl shadow-lg p-6">
              {error && (
                <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-center space-x-3">
                  <XCircle className="w-5 h-5 text-red-600" />
                  <span className="text-red-700">{error}</span>
                </div>
              )}

              {activeSection === 'today' && todayResult && (
                <div className="space-y-4">
                  <div className="flex items-center space-x-3 mb-4">
                    <Clock className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">Today's Result</h3>
                  </div>
                  
                  <div className={`p-4 rounded-lg border-2 ${
                    todayResult.isHoliday 
                      ? 'bg-green-50 border-green-200' 
                      : 'bg-gray-50 border-gray-200'
                  }`}>
                    <div className="flex items-center space-x-3">
                      {todayResult.isHoliday ? (
                        <CheckCircle className="w-6 h-6 text-green-600" />
                      ) : (
                        <XCircle className="w-6 h-6 text-gray-400" />
                      )}
                      <div>
                        <p className="font-medium text-gray-900">
                          {todayResult.isHoliday ? 'Yes, today is a holiday!' : 'No holidays today'}
                        </p>
                        <p className="text-sm text-gray-600">
                          {formatDate(new Date().toISOString().split('T')[0])}
                        </p>
                      </div>
                    </div>
                    
                    {todayResult.holidays.length > 0 && (
                      <div className="mt-4 space-y-2">
                        {todayResult.holidays.map((holiday, index) => (
                          <div key={index} className="bg-white p-3 rounded-lg">
                            <h4 className="font-medium text-gray-900">{holiday.name}</h4>
                            <p className="text-sm text-gray-600">{holiday.type} Holiday</p>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              )}

              {activeSection === 'search' && countries.length > 0 && (
                <div className="space-y-4">
                  <div className="flex items-center space-x-3 mb-4">
                    <MapPin className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">Available Countries</h3>
                  </div>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    {countries.map((country) => (
                      <div
                        key={country.countryCode}
                        className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors duration-200 cursor-pointer"
                        onClick={() => handleCountryClick(country.countryCode)}
                      >
                        <div className="flex items-center justify-between">
                          <div>
                            <h4 className="font-medium text-gray-900">{country.name}</h4>
                            <p className="text-sm text-gray-600">{country.countryCode}</p>
                          </div>
                          <ChevronRight className="w-4 h-4 text-gray-400" />
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {(activeSection === 'range' || activeSection === 'country') && holidays.length > 0 && (
                <div className="space-y-4">
                  <div className="flex items-center space-x-3 mb-4">
                    <Calendar className="w-5 h-5 text-blue-600" />
                    <h3 className="text-lg font-semibold text-gray-900">
                      {activeSection === 'range' ? 'Holidays in Date Range' : 'Country Holidays'}
                    </h3>
                  </div>
                  
                  <div className="space-y-3">
                    {holidays.map((holiday, index) => (
                      <div key={index} className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors duration-200">
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <h4 className="font-medium text-gray-900 mb-1">{holiday.name}</h4>
                            <p className="text-sm text-gray-600 mb-2">{formatDate(holiday.date)}</p>
                            <div className="flex items-center space-x-4 text-xs text-gray-500">
                              <span className="flex items-center space-x-1">
                                <span>Type:</span>
                                <span className="font-medium">{holiday.type}</span>
                              </span>
                              <span className="flex items-center space-x-1">
                                <span>Fixed:</span>
                                <span className="font-medium">{holiday.fixed ? 'Yes' : 'No'}</span>
                              </span>
                              {holiday.launchYear && (
                                <span className="flex items-center space-x-1">
                                  <span>Since:</span>
                                  <span className="font-medium">{holiday.launchYear}</span>
                                </span>
                              )}
                            </div>
                          </div>
                          <div className="ml-4">
                            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                              {holiday.countryCode}
                            </span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {!loading && !error && holidays.length === 0 && !todayResult && countries.length === 0 && (
                <div className="text-center py-12">
                  <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <Calendar className="w-8 h-8 text-gray-400" />
                  </div>
                  <h3 className="text-lg font-medium text-gray-900 mb-2">No Results Yet</h3>
                  <p className="text-gray-600">
                    Select a search method and click the button to get started
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

export default App;