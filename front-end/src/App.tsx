import React, { useState } from 'react';
import { Calendar, Globe, Clock, Search, MapPin, ChevronRight, Loader2, CheckCircle, XCircle } from 'lucide-react';

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

function App() {
  const [activeSection, setActiveSection] = useState('range');
  const [loading, setLoading] = useState(false);
  const [holidays, setHolidays] = useState<Holiday[]>([]);
  const [countries, setCountries] = useState<Country[]>([]);
  const [todayResult, setTodayResult] = useState<{ isHoliday: boolean; holidays: Holiday[] } | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Form states
  const [dateRange, setDateRange] = useState({ start: '', end: '' });
  const [selectedCountry, setSelectedCountry] = useState('US');
  const [specificDate, setSpecificDate] = useState('');

  const sections = [
    { id: 'range', label: 'Date Range', icon: Calendar },
    { id: 'today', label: 'Today\'s Holidays', icon: Clock },
    { id: 'country', label: 'By Country', icon: Globe },
    { id: 'search', label: 'Search Countries', icon: Search },
  ];

  const handleDateRangeSearch = async () => {
    if (!dateRange.start || !dateRange.end) return;
    
    setLoading(true);
    setError(null);
    
    try {
      // Simulated API call - replace with actual API
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock data
      const mockHolidays: Holiday[] = [
        {
          name: "New Year's Day",
          date: "2024-01-01",
          countryCode: "US",
          fixed: true,
          global: true,
          counties: null,
          launchYear: null,
          type: "Public"
        },
        {
          name: "Independence Day",
          date: "2024-07-04",
          countryCode: "US",
          fixed: true,
          global: true,
          counties: null,
          launchYear: 1776,
          type: "Public"
        },
        {
          name: "Christmas Day",
          date: "2024-12-25",
          countryCode: "US",
          fixed: true,
          global: true,
          counties: null,
          launchYear: null,
          type: "Public"
        }
      ];
      
      setHolidays(mockHolidays);
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
      // Simulated API call
      await new Promise(resolve => setTimeout(resolve, 800));
      
      const today = new Date().toISOString().split('T')[0];
      const isNewYear = today.includes('01-01');
      
      setTodayResult({
        isHoliday: isNewYear,
        holidays: isNewYear ? [{
          name: "New Year's Day",
          date: today,
          countryCode: selectedCountry,
          fixed: true,
          global: true,
          counties: null,
          launchYear: null,
          type: "Public"
        }] : []
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
      // Simulated API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      const mockCountryHolidays: Holiday[] = [
        {
          name: "Memorial Day",
          date: "2024-05-27",
          countryCode: selectedCountry,
          fixed: false,
          global: true,
          counties: null,
          launchYear: 1971,
          type: "Public"
        },
        {
          name: "Labor Day",
          date: "2024-09-02",
          countryCode: selectedCountry,
          fixed: false,
          global: true,
          counties: null,
          launchYear: 1894,
          type: "Public"
        }
      ];
      
      setHolidays(mockCountryHolidays);
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
      // Simulated API call
      await new Promise(resolve => setTimeout(resolve, 600));
      
      const mockCountries: Country[] = [
        { countryCode: 'US', name: 'United States' },
        { countryCode: 'GB', name: 'United Kingdom' },
        { countryCode: 'CA', name: 'Canada' },
        { countryCode: 'AU', name: 'Australia' },
        { countryCode: 'DE', name: 'Germany' },
        { countryCode: 'FR', name: 'France' },
        { countryCode: 'JP', name: 'Japan' },
        { countryCode: 'IN', name: 'India' },
      ];
      
      setCountries(mockCountries);
    } catch (err) {
      setError('Failed to fetch countries');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
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
                        <option value="US">United States</option>
                        <option value="GB">United Kingdom</option>
                        <option value="CA">Canada</option>
                        <option value="AU">Australia</option>
                        <option value="DE">Germany</option>
                        <option value="FR">France</option>
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
                        <option value="US">United States</option>
                        <option value="GB">United Kingdom</option>
                        <option value="CA">Canada</option>
                        <option value="AU">Australia</option>
                        <option value="DE">Germany</option>
                        <option value="FR">France</option>
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
                        <option value="US">United States</option>
                        <option value="GB">United Kingdom</option>
                        <option value="CA">Canada</option>
                        <option value="AU">Australia</option>
                        <option value="DE">Germany</option>
                        <option value="FR">France</option>
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
                        onClick={() => setSelectedCountry(country.countryCode)}
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