# Holiday API Website

A full-stack web application for exploring holidays around the world. Built with Spring Boot backend and React frontend, this application provides comprehensive holiday data with advanced filtering, sorting, and audience targeting capabilities.

## 🌟 Features

### Frontend Features
- **Date Range Search**: Find holidays within specific date ranges
- **Today's Holidays**: Quick check for current day holidays
- **Country Search**: Browse available countries and their holidays
- **Advanced Sorting**: Sort results by date, name, or type
- **Audience Filtering**: Filter holidays by target audience
- **Interactive Popups**: Detailed holiday information with search and filtering
- **Responsive Design**: Works seamlessly on desktop and mobile devices
- **Real-time Updates**: Dynamic data loading with loading states

### Backend Features
- **RESTful API**: Well-structured REST endpoints
- **Database Integration**: Oracle database with JPA/Hibernate
- **CORS Support**: Configured for cross-origin requests
- **Audience Management**: Holiday-audience relationship mapping
- **Error Handling**: Comprehensive error responses
- **Debug Endpoints**: Development-friendly debugging tools

## 🛠️ Tech Stack

### Frontend
- **React 18** with TypeScript
- **Vite** - Fast build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **Lucide React** - Icon library
- **SweetAlert2** - Beautiful popup modals

### Backend
- **Spring Boot 3.5.3**
- **Spring Data JPA** - Database abstraction layer
- **Oracle Database** - Primary data storage
- **Maven** - Dependency management and build tool
- **Java 20** - Programming language

## 📁 Project Structure

```
holiday-website/
├── front-end/                 # React frontend application
│   ├── src/
│   │   ├── App.tsx            # Main application component
│   │   ├── index.css          # Global styles
│   │   └── main.tsx           # Application entry point
│   ├── package.json           # Frontend dependencies
│   ├── vite.config.ts         # Vite configuration
│   └── tailwind.config.js     # Tailwind CSS configuration
│
├── holidayapi/                # Spring Boot backend
│   ├── src/main/java/com/emre/holidayapi/
│   │   ├── controller/        # REST controllers
│   │   │   ├── HolidayController.java
│   │   │   ├── CountryController.java
│   │   │   └── HomeController.java
│   │   ├── dto/              # Data Transfer Objects
│   │   │   └── HolidayDto.java
│   │   ├── model/            # JPA entities
│   │   │   ├── HolidayDefinition.java
│   │   │   ├── HolidayTemplate.java
│   │   │   ├── Country.java
│   │   │   ├── Audience.java
│   │   │   └── Translation.java
│   │   ├── repository/       # JPA repositories
│   │   │   ├── HolidayDefinitionRepository.java
│   │   │   ├── CountryRepository.java
│   │   │   └── AudienceRepository.java
│   │   ├── service/          # Business logic layer
│   │   │   ├── HolidayService.java
│   │   │   ├── CountryService.java
│   │   │   └── AudienceService.java
│   │   └── config/           # Configuration classes
│   │       └── CorsConfig.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml               # Maven dependencies
│
└── README.md                 # Project documentation
```

## 🚀 Getting Started

### Prerequisites
- **Java 20** or higher
- **Node.js 18** or higher
- **Oracle Database** (or compatible database)
- **Maven 3.6** or higher

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/EmreeKucuk/holiday-website.git
   cd holiday-website
   ```

2. **Configure Database**
   - Update `holidayapi/src/main/resources/application.properties` with your database credentials
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and Run Backend**
   ```bash
   cd holidayapi
   mvn clean compile
   mvn package -DskipTests
   java -jar target/holidayapi-0.0.1-SNAPSHOT.jar
   ```

   The backend will start on `http://localhost:8080`

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
  - Parameters: `start`, `end`, `country`, `audience` (optional)
- `GET /api/holidays/today` - Get today's holidays
  - Parameters: `country`, `audience` (optional)
- `GET /api/holidays/country/{countryCode}` - Get holidays by country
- `GET /api/holidays/types` - Get available holiday types
- `GET /api/holidays/audiences` - Get available audiences

#### Country Endpoints
- `GET /api/countries` - Get all available countries

#### Example Requests
```bash
# Get holidays for Turkey in January 2025
curl "http://localhost:8080/api/holidays/range?start=2025-01-01&end=2025-01-31&country=TR"

# Get today's holidays for Turkey
curl "http://localhost:8080/api/holidays/today?country=TR"

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

## 🎯 Usage Guide

### Date Range Search
1. Navigate to the "Date Range" tab
2. Select start and end dates
3. Choose a country from the dropdown
4. Optionally select an audience filter
5. Choose sorting preferences (by date, name, or type)
6. Click "Search Holidays"

### Country Search
1. Go to "Search Countries" tab
2. Click "Load Countries" to see available countries
3. Use sorting controls to organize the list
4. Click on any country to see its holidays in a popup

### Today's Holidays
1. Select "Today's Holidays" tab
2. Choose a country
3. Click "Check Today" to see if there are any holidays

## 🔧 Configuration

### Frontend Configuration
- **API Base URL**: Update in `App.tsx` if backend runs on different port
- **Styling**: Modify `tailwind.config.js` for design customizations
- **Build Settings**: Configure in `vite.config.ts`

### Backend Configuration
- **Database**: Update `application.properties`
- **CORS**: Modify `CorsConfig.java` for different frontend URLs
- **Port**: Change server port in `application.properties`

## 🐛 Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure `CorsConfig.java` includes your frontend URL
   - Check that backend is running on correct port

2. **Database Connection**
   - Verify database credentials in `application.properties`
   - Ensure Oracle database is running and accessible

3. **Build Failures**
   - Check Java version (requires Java 20+)
   - Verify Maven dependencies are properly downloaded

4. **Frontend Not Loading Data**
   - Confirm backend is running on `http://localhost:8080`
   - Check browser console for API errors
   - Verify network connectivity between frontend and backend

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Emre Küçük**
- GitHub: [@EmreeKucuk](https://github.com/EmreeKucuk)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the frontend library
- Tailwind CSS for the utility-first CSS framework
- Oracle for the database technology

---

## 📈 Future Enhancements

- [ ] User authentication and authorization
- [ ] Holiday creation and editing interface
- [ ] Multi-language support for holiday names
- [ ] Holiday calendar view
- [ ] Export functionality (PDF, Excel)
- [ ] Holiday notifications and reminders
- [ ] Advanced search with multiple criteria
- [ ] Holiday statistics and analytics
- [ ] API rate limiting and caching
- [ ] Docker containerization

---

**Made with ❤️ by Emre Küçük**
