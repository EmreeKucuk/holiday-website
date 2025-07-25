// translations.ts - Translation definitions for the entire application

export interface Translation {
  // Header and Navigation
  title: string;
  subtitle: string;
  languageSelector: string;
  
  // Hero Section
  mainHeadline: string;
  mainDescription: string;

  // Filter Section
  filters: string;
  selectCountry: string;
  selectAudience: string;
  allCountries: string;
  allAudiences: string;

  // Date Range Section
  searchByDateRange: string;
  startDate: string;
  endDate: string;
  searchButton: string;

  // Today's Holidays
  todaysHolidays: string;
  checkTodayButton: string;

    // Working Days Calculator
    workingDaysCalculator: string;
    totalDays: string;
    workingDays: string;
    holidayDays: string;
    calculateButton: string;
    workingDaysResults: string;
    includeEndDate: string;
    excludeEndDate: string;  // Results sections
  dateRangeResults: string;
  countryHolidays: string;
  
  // Sorting
  sortBy: string;
  name: string;
  date: string;
  type: string;
  
  // Sort options
  sortByDate: string;
  sortByName: string;
  sortByType: string;
  sortByCode: string;
  ascending: string;
  descending: string;

  // Holiday Types
  holidayTypes: {
    official: string;
    religious: string;
    cultural: string;
    observance: string;
    national: string;
    public: string;
  };

  // Audiences
  audiences: {
    'General Public': string;
    'Government': string;
    'Religious': string;
    'Educational': string;
    'Workers': string;
    'general': string;
    'government': string;
    'religious': string;
    'educational': string;
    'workers': string;
    'all': string;
    'blue_collar': string;
  };

  // Countries
  countries: {
    TR: string;
    US: string;
    GB: string;
    DE: string;
    FR: string;
    IT: string;
    ES: string;
    CA: string;
    AU: string;
    JP: string;
    KR: string;
    // Add more countries as needed
  };

  // Results and Messages
  resultsTitle: string;
  noHolidaysFound: string;
  loading: string;
  error: string;
  apiError: string;

  // Holiday Details
  audiences_label: string;
  date_label: string;
  type_label: string;
  country_label: string;

  // Buttons and Actions
  clear: string;
  reset: string;
  search: string;
  close: string;

  // Today status
  todayIsHoliday: string;
  noHolidaysToday: string;
  since: string;

  // Time periods
  periods: {
    today: string;
    thisWeek: string;
    thisMonth: string;
    thisYear: string;
  };

  // AI Chat
  aiChat: {
    title: string;
    welcomeTitle: string;
    welcomeMessage: string;
    countryContext: string;
    inputPlaceholder: string;
    sendButton: string;
    thinking: string;
    assistant: string;
    tryAsking: string;
    enterToSend: string;
    shiftEnterNewLine: string;
    suggestions: {
      todayHoliday: string;
      holidayRange: string;
      yearlyHolidays: string;
      audienceHolidays: string;
      religiousHolidays: string;
      holidayDuration: string;
      specificHoliday: string;
      holidayStatistics: string;
      workingDays: string;
      monthlyHolidays: string;
      vacationOptimization: string;
    };
  };

  // Theme
  theme: {
    toggleTooltip: string;
    darkMode: string;
    lightMode: string;
  };
}

export const translations: Record<string, Translation> = {
  en: {
    // Header and Navigation
    title: "Holiday Calendar",
    subtitle: "Discover holidays and celebrations around the world",
    languageSelector: "Language",
    
    // Hero Section
    mainHeadline: "Discover Holidays Around the World",
    mainDescription: "Access comprehensive holiday data for any country, check specific dates, and explore global celebrations",

    // Filter Section
    filters: "Filters",
    selectCountry: "Select Country",
    selectAudience: "Select Audience",
    allCountries: "All Countries",
    allAudiences: "All Audiences",

    // Date Range Section
    searchByDateRange: "Search by Date Range",
    startDate: "Start Date",
    endDate: "End Date",
    searchButton: "Search",

    // Today's Holidays
    todaysHolidays: "Today's Holidays",
    checkTodayButton: "Check Today's Holidays",

    // Working Days Calculator
    workingDaysCalculator: "Working Days Calculator",
    totalDays: "Total Days",
    workingDays: "Working Days",
    holidayDays: "Holiday Days",
    calculateButton: "Calculate",
    workingDaysResults: "Working Days Analysis",
    includeEndDate: "Include End Date",
    excludeEndDate: "Exclude End Date",

    // Results sections
    dateRangeResults: "Holidays in Date Range",
    countryHolidays: "Country Holidays",
    
    // Sorting
    sortBy: "Sort by",
    name: "Name",
    date: "Date",
    type: "Type",
    
    // Sort options
    sortByDate: "Sort by Date",
    sortByName: "Sort by Name",
    sortByType: "Sort by Type",
    sortByCode: "Sort by Code",
    ascending: "Ascending",
    descending: "Descending",

    // Holiday Types
    holidayTypes: {
      official: "Official Holiday",
      religious: "Religious Holiday",
      cultural: "Cultural Holiday",
      observance: "Observance",
      national: "National Holiday",
      public: "Public Holiday",
    },

    // Audiences
    audiences: {
      'General Public': "General Public",
      'Government': "Government",
      'Religious': "Religious",
      'Educational': "Educational",
      'Workers': "Workers",
      'general': "General Public",
      'government': "Government",
      'religious': "Religious",
      'educational': "Educational",
      'workers': "Workers",
      'all': "All Employees",
      'blue_collar': "Blue Collar Workers",
    },

    // Countries
    countries: {
      TR: "Turkey",
      US: "United States",
      GB: "United Kingdom",
      DE: "Germany",
      FR: "France",
      IT: "Italy",
      ES: "Spain",
      CA: "Canada",
      AU: "Australia",
      JP: "Japan",
      KR: "South Korea",
    },

    // Results and Messages
    resultsTitle: "Holiday Results",
    noHolidaysFound: "No holidays found for the selected criteria.",
    loading: "Loading holidays...",
    error: "An error occurred",
    apiError: "Failed to fetch holidays. Please try again.",

    // Today status
    todayIsHoliday: "Yes, today is a holiday!",
    noHolidaysToday: "No holidays today",
    since: "Since:",

    // Holiday Details
    audiences_label: "Audiences",
    date_label: "Date",
    type_label: "Type",
    country_label: "Country",

    // Buttons and Actions
    clear: "Clear",
    reset: "Reset",
    search: "Search",
    close: "Close",

    // Time periods
    periods: {
      today: "Today",
      thisWeek: "This Week",
      thisMonth: "This Month",
      thisYear: "This Year",
    },

    // AI Chat
    aiChat: {
      title: "AI Holiday Assistant",
      welcomeTitle: "Welcome to Holiday Assistant!",
      welcomeMessage: "Ask me about holidays, working days, or anything related to your holiday calendar.",
      countryContext: "Country Context",
      inputPlaceholder: "Ask about holidays, working days, or anything related...",
      sendButton: "Send",
      thinking: "Thinking...",
      assistant: "Holiday Assistant",
      tryAsking: "Try asking:",
      enterToSend: "Press Enter to send",
      shiftEnterNewLine: "Shift + Enter for new line",
      suggestions: {
        todayHoliday: "Is there a holiday today?",
        holidayRange: "What holidays are between 01/01/2025 - 31/01/2025?",
        yearlyHolidays: "How many holidays this year?",
        audienceHolidays: "Show me holidays for students only",
        religiousHolidays: "Show me religious holidays",
        holidayDuration: "How long does Ramadan last?",
        specificHoliday: "When is Christmas 2025?",
        holidayStatistics: "Which month has the most holidays?",
        workingDays: "How many working days between 01/01/2025 and 31/01/2025?",
        monthlyHolidays: "Show me all holidays in June 2025",
        vacationOptimization: "Find the best time to take vacation to maximize my time off with 5 vacation days"
      }
    },

    // Theme
    theme: {
      toggleTooltip: "Toggle theme",
      darkMode: "Dark mode",
      lightMode: "Light mode"
    },
  },

  tr: {
    // Header and Navigation
    title: "Tatil Takvimi",
    subtitle: "Dünya çapındaki tatilleri ve kutlamaları keşfedin",
    languageSelector: "Dil",
    
    // Hero Section
    mainHeadline: "Dünya Çapındaki Tatilleri Keşfedin",
    mainDescription: "Herhangi bir ülke için kapsamlı tatil verilerine erişin, belirli tarihleri kontrol edin ve küresel kutlamaları keşfedin",

    // Filter Section
    filters: "Filtreler",
    selectCountry: "Ülke Seçin",
    selectAudience: "Hedef Kitle Seçin",
    allCountries: "Tüm Ülkeler",
    allAudiences: "Tüm Hedef Kitleler",

    // Date Range Section
    searchByDateRange: "Tarih Aralığında Ara",
    startDate: "Başlangıç Tarihi",
    endDate: "Bitiş Tarihi",
    searchButton: "Ara",

    // Today's Holidays
    todaysHolidays: "Bugünün Tatilleri",
    checkTodayButton: "Bugünün Tatillerini Kontrol Et",

    // Working Days Calculator
    workingDaysCalculator: "Çalışma Günü Hesaplayıcısı",
    totalDays: "Toplam Gün",
    workingDays: "Çalışma Günleri",
    holidayDays: "Tatil Günleri",
    calculateButton: "Hesapla",
    workingDaysResults: "Çalışma Günü Analizi",
    includeEndDate: "Son Günü Dahil Et",
    excludeEndDate: "Son Günü Dahil Etme",

    // Results sections
    dateRangeResults: "Tarih Aralığındaki Tatiller",
    countryHolidays: "Ülke Tatilleri",
    
    // Sorting
    sortBy: "Sırala",
    name: "İsim",
    date: "Tarih",
    type: "Tür",
    
    // Sort options
    sortByDate: "Tarihe Göre Sırala",
    sortByName: "İsme Göre Sırala",
    sortByType: "Türe Göre Sırala",
    sortByCode: "Koda Göre Sırala",
    ascending: "Artan",
    descending: "Azalan",

    // Holiday Types
    holidayTypes: {
      official: "Resmi Tatil",
      religious: "Dini Tatil",
      cultural: "Kültürel Tatil",
      observance: "Anma Günü",
      national: "Ulusal Tatil",
      public: "Kamu Tatili",
    },

    // Audiences
    audiences: {
      'General Public': "Genel Halk",
      'Government': "Devlet",
      'Religious': "Dini",
      'Educational': "Eğitim",
      'Workers': "İşçiler",
      'general': "Genel Halk",
      'government': "Devlet",
      'religious': "Dini",
      'educational': "Eğitim",
      'workers': "İşçiler",
      'all': "Tüm Çalışanlar",
      'blue_collar': "Mavi Yakalı Çalışanlar",
    },

    // Countries
    countries: {
      TR: "Türkiye",
      US: "Amerika Birleşik Devletleri",
      GB: "Birleşik Krallık",
      DE: "Almanya",
      FR: "Fransa",
      IT: "İtalya",
      ES: "İspanya",
      CA: "Kanada",
      AU: "Avustralya",
      JP: "Japonya",
      KR: "Güney Kore",
    },

    // Results and Messages
    resultsTitle: "Tatil Sonuçları",
    noHolidaysFound: "Seçilen kriterlere uygun tatil bulunamadı.",
    loading: "Tatiller yükleniyor...",
    error: "Bir hata oluştu",
    apiError: "Tatiller getirilemedi. Lütfen tekrar deneyin.",

    // Today status
    todayIsHoliday: "Evet, bugün tatil günü!",
    noHolidaysToday: "Bugün tatil yok",
    since: "Başlangıç:",

    // Holiday Details
    audiences_label: "Hedef Kitleler",
    date_label: "Tarih",
    type_label: "Tür",
    country_label: "Ülke",

    // Buttons and Actions
    clear: "Temizle",
    reset: "Sıfırla",
    search: "Ara",
    close: "Kapat",

    // Time periods
    periods: {
      today: "Bugün",
      thisWeek: "Bu Hafta",
      thisMonth: "Bu Ay",
      thisYear: "Bu Yıl",
    },

    // AI Chat
    aiChat: {
      title: "AI Tatil Asistanı",
      welcomeTitle: "Tatil Asistanına Hoş Geldiniz!",
      welcomeMessage: "Bana tatiller, çalışma günleri veya tatil takviminizle ilgili herhangi bir şey sorabilirsiniz.",
      countryContext: "Ülke Bağlamı",
      inputPlaceholder: "Tatiller, çalışma günleri veya ilgili herhangi bir şey hakkında sorun...",
      sendButton: "Gönder",
      thinking: "Düşünüyor...",
      assistant: "Tatil Asistanı",
      tryAsking: "Şunları sormayı deneyin:",
      enterToSend: "Göndermek için Enter'a basın",
      shiftEnterNewLine: "Yeni satır için Shift + Enter",
      suggestions: {
        todayHoliday: "Bugün tatil var mı?",
        holidayRange: "01/01/2025 - 31/01/2025 arasında hangi tatiller var?",
        yearlyHolidays: "Bu yıl kaç tatil var?",
        audienceHolidays: "Sadece öğrenciler için tatilleri göster",
        religiousHolidays: "Dini tatilleri göster",
        holidayDuration: "Ramazan Bayramı kaç gün sürer?",
        specificHoliday: "2025'te Noel ne zaman?",
        holidayStatistics: "En çok tatil hangi ayda?",
        workingDays: "01/01/2025 ile 31/01/2025 arasında kaç iş günü var?",
        monthlyHolidays: "Haziran 2025'teki tüm tatilleri göster",
        vacationOptimization: "5 izin günüyle tatil süresini maksimize etmek için en iyi zamanı bul"
      }
    },

    // Theme
    theme: {
      toggleTooltip: "Tema değiştir",
      darkMode: "Karanlık mod",
      lightMode: "Aydınlık mod"
    },
  },
};

export const getTranslation = (language: string): Translation => {
  return translations[language] || translations.en;
};
