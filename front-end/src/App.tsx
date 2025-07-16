import { useState, useEffect } from 'react';
import { Calendar, Globe, Clock, Search, MapPin, ChevronRight, Loader2, CheckCircle, XCircle } from 'lucide-react';
import Swal from "sweetalert2";
import withReactContent from "sweetalert2-react-content";

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
  const [activeSection, setActiveSection] = useState('range');
  const [loading, setLoading] = useState(false);
  const [holidays, setHolidays] = useState<Holiday[]>([]);
  const [countries, setCountries] = useState<Country[]>([]);
  const [audiences, setAudiences] = useState<Audience[]>([]);
  const [todayResult, setTodayResult] = useState<{ isHoliday: boolean; holidays: Holiday[] } | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Form states
  const [dateRange, setDateRange] = useState({ start: '2025-01-01', end: '2025-01-31' });
  const [selectedCountry, setSelectedCountry] = useState('TR');
  const [selectedAudience, setSelectedAudience] = useState('');
  const [sortBy, setSortBy] = useState('date'); // 'date', 'name', 'type'
  const [sortOrder, setSortOrder] = useState('asc'); // 'asc', 'desc'
  const [chatHistory, setChatHistory] = useState<{ user: string; ai: string }[]>([]);

  const sections = [
    { id: 'range', label: 'Date Range', icon: Calendar },
    { id: 'today', label: 'Today\'s Holidays', icon: Clock },
    // { id: 'country', label: 'By Country', icon: Globe },
    { id: 'search', label: 'Search Countries', icon: Search },
    { id: 'chat', label: 'AI Chat', icon: MapPin },
  ];

  const handleDateRangeSearch = async () => {
    if (!dateRange.start || !dateRange.end) return;
    setLoading(true);
    setError(null);
    try {
      let url = `http://localhost:8080/api/holidays/range?start=${dateRange.start}&end=${dateRange.end}&country=${selectedCountry}`;
      if (selectedAudience) {
        url += `&audience=${selectedAudience}`;
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
      setError('Failed to fetch holidays for the specified date range');
    } finally {
      setLoading(false);
    }
  };

  const handleTodayCheck = async () => {
    setLoading(true);
    setError(null);
    try {
      let url = `http://localhost:8080/api/holidays/today?country=${selectedCountry}`;
      if (selectedAudience) {
        url += `&audience=${selectedAudience}`;
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

  const fetchAudiences = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/holidays/audiences');
      if (!response.ok) throw new Error('Failed to fetch audiences');
      const data = await response.json();
      setAudiences(data);
    } catch (err) {
      console.error('Failed to fetch audiences:', err);
    }
  };

  useEffect(() => {
    fetchAudiences();
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
                  : '<p>No holidays found for this country.</p>'
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
                : '<p>No holidays found with current filters.</p>';
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
    const response = await fetch('http://localhost:8080/api/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message })
    });
    const data = await response.json();
    setChatHistory([...chatHistory, { user: message, ai: data.reply }]);
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
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Audience (Optional)
                      </label>
                      <select
                        value={selectedAudience}
                        onChange={(e) => setSelectedAudience(e.target.value)}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      >
                        <option value="">All Audiences</option>
                        {audiences.map((audience) => (
                          <option key={audience.code} value={audience.code}>
                            {audience.audienceName}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Sort By
                        </label>
                        <select
                          value={sortBy}
                          onChange={(e) => setSortBy(e.target.value)}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        >
                          <option value="date">Date</option>
                          <option value="name">Name</option>
                          <option value="type">Type</option>
                        </select>
                      </div>
                      
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Order
                        </label>
                        <select
                          value={sortOrder}
                          onChange={(e) => setSortOrder(e.target.value)}
                          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        >
                          <option value="asc">Ascending</option>
                          <option value="desc">Descending</option>
                        </select>
                      </div>
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
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center space-x-3">
                      <MapPin className="w-5 h-5 text-blue-600" />
                      <h3 className="text-lg font-semibold text-gray-900">Available Countries</h3>
                    </div>
                    
                    <div className="flex items-center space-x-3">
                      <select
                        value={sortBy}
                        onChange={(e) => setSortBy(e.target.value)}
                        className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                      >
                        <option value="name">Sort by Name</option>
                        <option value="code">Sort by Code</option>
                      </select>
                      
                      <select
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value)}
                        className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
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
                  <div className="flex items-center justify-between mb-4">
                    <div className="flex items-center space-x-3">
                      <Calendar className="w-5 h-5 text-blue-600" />
                      <h3 className="text-lg font-semibold text-gray-900">
                        {activeSection === 'range' ? 'Holidays in Date Range' : 'Country Holidays'}
                      </h3>
                    </div>
                    
                    <div className="flex items-center space-x-3">
                      <select
                        value={sortBy}
                        onChange={(e) => setSortBy(e.target.value)}
                        className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                      >
                        <option value="date">Sort by Date</option>
                        <option value="name">Sort by Name</option>
                        <option value="type">Sort by Type</option>
                      </select>
                      
                      <select
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value)}
                        className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                      >
                        <option value="asc">{sortBy === 'date' ? 'Earliest First' : 'A-Z'}</option>
                        <option value="desc">{sortBy === 'date' ? 'Latest First' : 'Z-A'}</option>
                      </select>
                    </div>
                  </div>
                  
                  <div className="space-y-3">
                    {sortHolidays(holidays).map((holiday, index) => (
                      <div key={index} className="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors duration-200">
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <h4 className="font-medium text-gray-900 mb-1">{holiday.name}</h4>
                            <p className="text-sm text-gray-600 mb-2">{formatDate(holiday.date)}</p>
                            <div className="flex items-center flex-wrap gap-4 text-xs text-gray-500">
                              <span className="flex items-center space-x-1">
                                <span>Type:</span>
                                <span className="font-medium">{holiday.type}</span>
                              </span>
                              <span className="flex items-center space-x-1">
                                <span>Fixed:</span>
                                <span className="font-medium">{holiday.fixed ? 'Yes' : 'No'}</span>
                              </span>
                              {holiday.audiences && holiday.audiences.length > 0 && (
                                <span className="flex items-center space-x-1">
                                  <span>Audiences:</span>
                                  <span className="font-medium">{holiday.audiences.join(', ')}</span>
                                </span>
                              )}
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