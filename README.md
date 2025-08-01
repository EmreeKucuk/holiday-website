# Holiday API Website

A comprehensive full-stack web application for exploring holidays around the world. Built with Spring Boot backend and React frontend, this application provides extensive holiday data with advanced filtering, sorting, audience targeting, AI-powered chat assistance, and developer tools.

## 🌟 Features

### Frontend Features
- **Date Range Search**: Find holidays within specific date ranges with advanced filtering
- **Today's Holidays**: Quick check for current day holidays by country
- **Working Days Calculator**: Calculate working days between dates excluding holidays
- **Country Search**: Browse available countries and their holidays with interactive popups
- **AI Chat Assistant**: Intelligent holiday-related Q&A powered by local LLM (Ollama)
- **API Explorer**: Interactive API testing interface with real-time response monitoring
- **Advanced Sorting**: Sort results by date, name, or type with ascending/descending order
- **Audience Filtering**: Filter holidays by target audience (General Public, Government, etc.)
- **Multi-language Support**: Full internationalization (English/Turkish) with context-aware translations
- **Dark/Light Theme**: Automatic theme switching with smooth transitions
- **Interactive Popups**: Detailed holiday information with search and filtering capabilities
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **Real-time Updates**: Dynamic data loading with proper loading states and error handling

### Backend Features
- **RESTful API**: Well-structured REST endpoints with comprehensive documentation
- **AI Integration**: Spring AI with Ollama for intelligent holiday assistance
- **Database Integration**: Oracle database with JPA/Hibernate and optimized queries
- **CORS Support**: Configured for cross-origin requests with security considerations
- **Audience Management**: Holiday-audience relationship mapping with translations
- **Working Days Logic**: Smart business day calculation excluding weekends and holidays
- **Internationalization**: Multi-language support for holiday names and descriptions
- **Error Handling**: Comprehensive error responses with proper HTTP status codes
- **Unit Testing**: Complete test coverage with JUnit 5, Mockito, and integration tests (100% controller and service coverage)
- **Debug Endpoints**: Development-friendly debugging and monitoring tools

## 🛠️ Tech Stack

### Frontend
- **React 18** with TypeScript
- **Vite** - Fast build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework with dark mode support
- **Lucide React** - Modern icon library
- **SweetAlert2** - Beautiful popup modals and notifications

### Backend
- **Spring Boot 3.5.3**
- **Spring Data JPA** - Database abstraction layer with query optimization
- **Spring AI** - AI integration framework with Ollama support
- **Oracle Database** - Primary data storage with advanced indexing
- **JUnit 5 & Mockito** - Comprehensive unit and integration testing with standalone MockMvc
- **AssertJ** - Fluent assertion library for better test readability
- **H2 Database** - In-memory database for testing (test scope)
- **Jackson JSR310** - LocalDate serialization support for JSON responses
- **Maven** - Dependency management and build tool
- **Java 17** - Programming language

### AI & Infrastructure
- **Ollama** - Local LLM runtime (supports Mistral, Llama models)
- **Docker** (optional) - Containerization for easy deployment
- **Git** - Version control with comprehensive branching strategy

## 📁 Project Structure

```
holiday-website/
├── front-end/                 # React frontend application
│   ├── src/
│   │   ├── App.tsx            # Main application with all features
│   │   ├── index.css          # Global styles with dark mode
│   │   ├── main.tsx           # Application entry point
│   │   ├── ThemeContext.tsx   # Dark/light theme management
│   │   ├── ThemeToggle.tsx    # Theme switching component
│   │   ├── TranslationContext.tsx # Internationalization context
│   │   ├── translations.ts    # Translation definitions (EN/TR)
│   │   └── vite-env.d.ts      # TypeScript environment definitions
│   ├── package.json           # Frontend dependencies
│   ├── vite.config.ts         # Vite configuration
│   ├── tailwind.config.js     # Tailwind CSS with dark mode
│   ├── postcss.config.js      # PostCSS configuration
│   └── eslint.config.js       # ESLint configuration
│
├── holidayapi/                # Spring Boot backend
│   ├── src/main/java/com/emre/holidayapi/
│   │   ├── controller/        # REST controllers
│   │   │   ├── HolidayController.java      # Holiday endpoints
│   │   │   ├── CountryController.java      # Country endpoints
│   │   │   ├── ChatController.java         # AI chat endpoints
│   │   │   ├── HolidayTemplateController.java # Template endpoints
│   │   │   └── HomeController.java         # Root and debug endpoints
│   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── HolidayDto.java            # Holiday data transfer
│   │   │   └── AudienceDto.java           # Audience data transfer
│   │   ├── model/            # JPA entities
│   │   │   ├── HolidayDefinition.java     # Holiday entity
│   │   │   ├── HolidayTemplate.java       # Template entity
│   │   │   ├── CountryHoliday.java        # Country-holiday mapping
│   │   │   ├── Country.java               # Country entity
│   │   │   ├── Audience.java              # Audience entity
│   │   │   └── Translation.java           # Translation entity
│   │   ├── repository/       # JPA repositories
│   │   │   ├── HolidayDefinitionRepository.java
│   │   │   ├── CountryRepository.java
│   │   │   ├── AudienceRepository.java
│   │   │   └── TranslationRepository.java
│   │   ├── service/          # Business logic layer
│   │   │   ├── HolidayService.java        # Holiday business logic
│   │   │   ├── CountryService.java        # Country business logic
│   │   │   ├── AudienceService.java       # Audience business logic
│   │   │   ├── HolidayAiService.java      # Basic AI service
│   │   │   └── IntelligentHolidayAiService.java # Advanced AI service
│   │   └── config/           # Configuration classes
│   │       ├── CorsConfig.java            # CORS configuration
│   │       └── AiConfig.java              # AI/Ollama configuration
│   ├── src/main/resources/
│   │   ├── application.properties         # Main configuration
│   │   ├── application-example.properties # Example configuration
│   │   └── application-env.properties     # Environment-specific config
│   ├── src/test/java/        # Unit and integration tests
│   │   └── com/emre/holidayapi/
│   │       ├── HolidayapiApplicationTests.java # Integration tests
│   │       ├── controller/    # Controller unit tests (standalone MockMvc)
│   │       │   ├── HolidayControllerTest.java
│   │       │   ├── CountryControllerTest.java
│   │       │   ├── ChatControllerTest.java
│   │       │   └── HolidayTemplateControllerTest.java
│   │       ├── service/       # Service unit tests (100% coverage)
│   │       │   ├── HolidayServiceTest.java
│   │       │   ├── CountryServiceTest.java
│   │       │   ├── AudienceServiceTest.java
│   │       │   ├── HolidayTemplateServiceTest.java
│   │       │   └── HolidayAiServiceTest.java
│   │       ├── integration/   # Integration tests
│   │       │   └── HolidayApiIntegrationTest.java
│   │       └── suite/         # Test suite documentation
│   │           └── AllTestSuite.java
│   ├── pom.xml               # Maven dependencies with Spring AI
│   ├── add_countries.sql     # Database setup script
│   ├── add_audiences.sql     # Audience data script
│   └── HELP.md              # Spring Boot help documentation
│
├── sample-data.sql           # Sample database data
├── TESTING.md               # Comprehensive testing documentation and troubleshooting guide
├── AI_UPGRADE_GUIDE.md       # AI setup and configuration guide
├── tsconfig.json             # TypeScript configuration
├── tsconfig.app.json         # App-specific TypeScript config
├── tsconfig.node.json        # Node-specific TypeScript config
└── README.md                 # This comprehensive documentation
```

## 🚀 Getting Started

### Prerequisites
- **Java 17** or higher
- **Node.js 18** or higher
- **Oracle Database** (or compatible database)
- **Maven 3.6** or higher
- **Ollama** (for AI chat functionality) - [Installation Guide](https://ollama.ai)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/EmreeKucuk/holiday-website.git
   cd holiday-website
   ```

2. **Configure Database**
   - Copy the example configuration file:
   ```bash
   cp holidayapi/src/main/resources/application-example.properties holidayapi/src/main/resources/application.properties
   ```
   - Update `holidayapi/src/main/resources/application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # AI Configuration (optional)
   spring.ai.ollama.base-url=http://localhost:11434
   spring.ai.ollama.chat.options.model=llama3.1:8b
   ```
   - **Note**: The `application.properties` file is gitignored for security. Never commit database credentials!

3. **Set up AI Chat (Optional)**
   - Install Ollama: Follow the guide at [ollama.ai](https://ollama.ai)
   - Pull a model: `ollama pull llama3.1:8b` or `ollama pull mistral:7b`
   - Start Ollama service: `ollama serve`
   - See `AI_UPGRADE_GUIDE.md` for detailed setup instructions

4. **Build and Run Backend**
   ```bash
   cd holidayapi
   mvn clean compile
   mvn package -DskipTests
   java -jar target/holidayapi-0.0.1-SNAPSHOT.jar
   ```

   The backend will start on `http://localhost:8080`

5. **Run Tests (Recommended)**
   ```bash
   mvn test  # Run unit tests (all should pass)
   mvn verify # Run integration tests
   ```
   
   **Test Coverage**: The project includes comprehensive unit and integration tests:
   - **Controller Tests**: Standalone MockMvc tests for all REST endpoints
   - **Service Tests**: 100% method coverage with Mockito mocks
   - **Integration Tests**: Full application context testing with @AutoConfigureMockMvc
   - **AI Service Tests**: Complete coverage of holiday query processing logic
   
   See `TESTING.md` for detailed testing documentation and troubleshooting.

### Frontend Setup

1. **Install Dependencies**
   ```bash
   cd front-end
   npm install
   ```

2. **Start Development Server**
   ```bash
   npm run dev
   ```

   The frontend will start on `http://localhost:5173` (or next available port)

## 📚 API Documentation

### Base URL
```
http://localhost:8080
```

### Main Endpoints

#### Holiday Endpoints
- `GET /api/holidays/range` - Get holidays within date range
  - Parameters: `start`, `end`, `country`, `audience` (optional), `language`
- `GET /api/holidays/today` - Get today's holidays
  - Parameters: `country`, `audience` (optional), `language`
- `GET /api/holidays/working-days` - Calculate working days between dates
  - Parameters: `start`, `end`, `country`, `includeEndDate`, `language`
  - **Logic**: Excludes weekends (Saturday/Sunday) and official holidays
  - **Business Rules**: Sophisticated calculation considering country-specific holidays
  - **Response**: Detailed breakdown including total days, working days, holiday count, and holiday list
- `GET /api/holidays/country/{countryCode}` - Get holidays by country
  - Parameters: `language`
- `GET /api/holidays/types` - Get available holiday types
- `GET /api/holidays/audiences` - Get available audiences
- `GET /api/holidays/audiences/translated` - Get translated audiences
  - Parameters: `language`

#### Country Endpoints
- `GET /api/countries` - Get all available countries

#### AI Chat Endpoints
- `POST /api/chat` - AI-powered holiday assistance
  - Body: `{ "message": "your question", "country": "TR", "language": "en" }`

#### Debug Endpoints
- `GET /` - API status and available endpoints
- `GET /api/test` - Health check endpoint

#### Example Requests
```bash
# Get holidays for Turkey in January 2025
curl "http://localhost:8080/api/holidays/range?start=2025-01-01&end=2025-01-31&country=TR&language=en"

# Calculate working days
curl "http://localhost:8080/api/holidays/working-days?start=2025-01-01&end=2025-01-31&country=TR&includeEndDate=true"

# AI Chat
curl -X POST "http://localhost:8080/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Tell me about holidays in Turkey", "country": "TR", "language": "en"}'

# Get today's holidays for Turkey
curl "http://localhost:8080/api/holidays/today?country=TR&language=en"

# Get all countries
curl "http://localhost:8080/api/countries"
```

### Response Format
```json
{
  "name": "New Year's Day",
  "date": "2025-01-01",
  "countryCode": "TR",
  "fixed": false,
  "global": false,
  "counties": null,
  "launchYear": null,
  "type": "official",
  "audiences": ["General Public", "Government"]
}
```

### Working Days Response
```json
{
  "totalDays": 31,
  "workingDays": 21,
  "holidayDays": 2,
  "holidays": [...], // Array of holidays found in the date range
  "includeEndDate": true,
  "startDate": "2025-01-01",
  "endDate": "2025-01-31",
  "countryCode": "TR"
}
```

### AI Chat Response
```json
{
  "reply": "Turkey has several important holidays in 2025, including New Year's Day on January 1st, National Sovereignty and Children's Day on April 23rd, and Victory Day on August 30th..."
}
```

## 🎯 Usage Guide

### Date Range Search
1. Navigate to the "Date Range" tab
2. Select start and end dates using the date pickers
3. Choose a country from the dropdown (auto-populated)
4. Optionally select an audience filter (General Public, Government, etc.)
5. Choose sorting preferences (by date, name, or type) and order (ascending/descending)
6. Click "Search Holidays" to view results

### Working Days Calculator
1. Go to the "Working Days Calculator" tab
2. Select start and end dates for your calculation period
3. Choose the target country
4. Toggle "Include End Date" based on your calculation needs
5. Click "Calculate Working Days" to see:
   - Total calendar days
   - Working days (excluding weekends and holidays)
   - Number of holidays in the period
   - List of holidays found

### Today's Holidays
1. Select "Today's Holidays" tab
2. Choose a country from the dropdown
3. Optionally filter by audience
4. Click "Check Today" to see current day holidays

### AI Chat Assistant
1. Navigate to the "AI Chat" tab
2. Type your holiday-related question in the chat input
3. The AI can help with:
   - Holiday information for specific countries
   - Cultural significance of holidays
   - Planning around holidays
   - Historical context of celebrations
4. Responses are provided in your selected language (English/Turkish)

### Country Search
1. Go to "Search Countries" tab
2. Click "Load Countries" to see available countries
3. Use sorting controls to organize the list alphabetically
4. Click on any country to see its holidays in an interactive popup with:
   - Search functionality within holidays
   - Filter by holiday type
   - Detailed holiday information

### API Explorer
1. Access the "API Explorer" tab for developer tools
2. Test API endpoints directly from the interface
3. View response times and debug information
4. Monitor API health and performance

## 🧪 Testing

### Test Architecture
The project features a comprehensive testing strategy with **100% service layer coverage**:

#### **Controller Tests** (Standalone MockMvc)
- **HolidayControllerTest**: Tests for all holiday endpoints including working days calculation
- **CountryControllerTest**: Tests for country listing and retrieval
- **ChatControllerTest**: Tests for AI chat functionality with error handling
- **HolidayTemplateControllerTest**: Tests for holiday template management

#### **Service Tests** (100% Method Coverage)
- **HolidayServiceTest**: Complete business logic testing including date calculations
- **CountryServiceTest**: Country data retrieval testing
- **AudienceServiceTest**: Audience management and translation testing  
- **HolidayTemplateServiceTest**: Template operations testing
- **HolidayAiServiceTest**: Comprehensive AI query processing with 15+ test scenarios

#### **Integration Tests**
- **HolidayApiIntegrationTest**: End-to-end API testing with full Spring context
- CORS configuration testing
- Cross-layer functionality validation

### Key Testing Features
- **Standalone MockMvc**: Avoids Spring context loading issues in controller tests
- **Mockito Integration**: Clean mocking of all external dependencies
- **AssertJ Assertions**: Fluent and readable test assertions
- **Jackson JSR310**: Proper LocalDate serialization testing
- **Error Scenario Coverage**: Comprehensive edge case and exception testing
- **AI Service Mocking**: Complete testing of AI query processing logic

### Running Tests
```bash
# Run all unit tests
mvn test

# Run integration tests
mvn verify

# Run specific test class
mvn test -Dtest=HolidayServiceTest

# Run tests with coverage report
mvn test jacoco:report
```

### Test Documentation
See `TESTING.md` for:
- Detailed testing guide
- Troubleshooting common test failures
- Best practices for writing new tests
- MockMvc configuration examples

## 🔧 Configuration

### Frontend Configuration
- **API Base URL**: Update in `App.tsx` if backend runs on different port
- **Theme System**: Dark/light mode automatically syncs with user preferences
- **Language Support**: Add new languages in `translations.ts` and `TranslationContext.tsx`
- **Styling**: Modify `tailwind.config.js` for design customizations
- **Build Settings**: Configure in `vite.config.ts`

### Backend Configuration
- **Database**: Update `application.properties` with your database credentials
- **AI Models**: Configure Ollama model in `application.properties`:
  ```properties
  spring.ai.ollama.chat.options.model=llama3.1:8b
  # or
  spring.ai.ollama.chat.options.model=mistral:7b
  ```
- **CORS**: Modify `CorsConfig.java` for different frontend URLs
- **Port**: Change server port in `application.properties`
- **Logging**: Adjust logging levels for different components

### AI Configuration
- **Model Selection**: Choose between different Ollama models based on your hardware
- **Performance Tuning**: Adjust timeout settings and response parameters
- **Fallback**: The system gracefully handles AI service unavailability

## 🐛 Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure `CorsConfig.java` includes your frontend URL
   - Check that backend is running on correct port (8080)
   - Verify no proxy or firewall is blocking requests

2. **Database Connection**
   - Verify database credentials in `application.properties`
   - Ensure Oracle database is running and accessible
   - Check network connectivity to database server
   - Validate database schema and tables exist

3. **AI Chat Not Working**
   - Ensure Ollama is installed and running (`ollama serve`)
   - Verify the model is downloaded (`ollama list`)
   - Check Ollama is accessible at `http://localhost:11434`
   - Review model configuration in `application.properties`

4. **Build Failures**
   - Check Java version (requires Java 17+)
   - Verify Maven dependencies are properly downloaded
   - Clear Maven cache if needed: `mvn clean install`
   - Check for any missing environment variables

5. **Frontend Not Loading Data**
   - Confirm backend is running on `http://localhost:8080`
   - Check browser console for API errors and network issues
   - Verify network connectivity between frontend and backend
   - Test API endpoints directly using curl or API Explorer

6. **Test Coverage Issues**
   - Check that all test classes use proper annotations (`@ExtendWith(MockitoExtension.class)`)
   - Verify MockMvc is configured with standalone setup: `MockMvcBuilders.standaloneSetup(controller).build()`
   - Ensure Jackson JSR310 module is registered for LocalDate serialization in tests
   - See `TESTING.md` for comprehensive troubleshooting guide

7. **Integration Test Failures**
   - Ensure `@AutoConfigureMockMvc` annotation is present for integration tests
   - Check that Spring Boot test slices are properly configured
   - Verify test database configuration in `application-test.properties`

8. **AI Service Unavailable**
   - The application gracefully handles AI service failures
   - Chat functionality will return appropriate error messages if Ollama is not running
   - All other features work independently of AI services

### Performance Optimization

- **Database**: Ensure proper indexing on date and country fields
- **AI Responses**: Use appropriate model size for your hardware
- **Frontend**: Enable gzip compression for production builds
- **Caching**: Consider implementing Redis for frequently accessed data

## 🤝 Contributing

We welcome contributions! Here's how to get started:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Make your changes** with proper testing
4. **Run tests** (`mvn test` for backend, `npm test` for frontend)
5. **Update documentation** if needed
6. **Commit your changes** (`git commit -m 'Add some amazing feature'`)
7. **Push to the branch** (`git push origin feature/amazing-feature`)
8. **Open a Pull Request** with detailed description

### Development Guidelines
- Follow existing code style and patterns
- **Add unit tests for new functionality** (maintain 100% service coverage)
- **Use standalone MockMvc for controller tests** to avoid Spring context issues
- **Mock all external dependencies** in unit tests using Mockito
- Update README if adding new features
- Ensure backward compatibility
- Test with multiple languages and themes
- **Run full test suite before submitting**: `mvn test && mvn verify`

### Areas for Contribution
- Additional holiday data sources
- New language translations
- Performance optimizations
- UI/UX improvements
- Additional AI model support
- Mobile app development
- **Test coverage improvements** (repository layer, edge cases)
- **Performance testing and benchmarking**

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Emre Küçük**
- GitHub: [@EmreeKucuk](https://github.com/EmreeKucuk)

## 🙏 Acknowledgments

- **Spring Boot team** for the excellent framework and Spring AI integration
- **React team** for the powerful frontend library and TypeScript support
- **Tailwind CSS** for the utility-first CSS framework with dark mode
- **Oracle** for the robust database technology
- **Ollama team** for making local LLM deployment accessible
- **Open source community** for various libraries and tools used
- **Contributors** who have helped improve this project

---

## 📈 Current Features Status

### ✅ Completed Features
- [x] Comprehensive holiday database with multiple countries
- [x] Date range and working days calculation
- [x] Multi-language support (English/Turkish)
- [x] Dark/light theme with automatic detection
- [x] AI-powered holiday assistance with local LLM
- [x] Interactive API explorer for developers
- [x] **Complete unit and integration test coverage (100% service layer)**
- [x] **Standalone MockMvc test architecture for robust controller testing**
- [x] **Comprehensive AI service testing with multiple query scenarios**
- [x] **Advanced working days calculation with holiday exclusions**
- [x] Responsive design for all device sizes
- [x] Advanced sorting and filtering capabilities
- [x] Real-time error handling and user feedback
- [x] **Detailed testing documentation and troubleshooting guide**

### 🚧 Future Enhancements
- [ ] User authentication and personalized holiday lists
- [ ] Holiday creation and editing interface for admins
- [ ] Calendar view with holiday visualization
- [ ] Export functionality (PDF, Excel, iCal)
- [ ] Holiday notifications and reminders system
- [ ] Advanced search with multiple criteria
- [ ] Holiday statistics and analytics dashboard
- [ ] API rate limiting and caching layer
- [ ] Docker containerization for easy deployment
- [ ] Mobile app (React Native or Flutter)
- [ ] Integration with external holiday APIs
- [ ] Social features (sharing, comments, ratings)
- [ ] Offline mode with service workers
- [ ] Advanced AI features (holiday predictions, recommendations)

---

## 🎯 Technical Achievements

- **100% Service Test Coverage**: Comprehensive unit tests for all service layer methods
- **Standalone MockMvc Architecture**: Robust controller testing without Spring context overhead
- **AI Integration**: Successfully integrated local LLM with Spring AI and comprehensive AI service tests
- **Internationalization**: Full i18n support with context-aware translations
- **Performance**: Optimized database queries and frontend rendering
- **Accessibility**: WCAG-compliant design with keyboard navigation
- **Developer Experience**: Interactive API explorer, comprehensive documentation, and complete test suite
- **Test Documentation**: Detailed testing guide in `TESTING.md` with troubleshooting scenarios
- **Error Handling**: Graceful degradation and comprehensive error responses
- **Working Days Logic**: Sophisticated business day calculation with holiday exclusions

---

**Made with ☕ by Emre Küçük**
