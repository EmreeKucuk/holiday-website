# Holiday API - Unit Tests

This project includes comprehensive unit tests for all backend services and controllers.

## Test Structure

### Service Layer Tests
- **HolidayServiceTest**: Tests for holiday business logic, CRUD operations, working days calculations
- **CountryServiceTest**: Tests for country data retrieval operations
- **AudienceServiceTest**: Tests for audience management and multilingual translations
- **HolidayTemplateServiceTest**: Tests for holiday template operations
- **HolidayAiServiceTest**: Tests for AI-powered holiday query processing

### Controller Layer Tests
- **HolidayControllerTest**: Tests for holiday REST API endpoints
- **CountryControllerTest**: Tests for country REST API endpoints
- **ChatControllerTest**: Tests for AI chat REST API endpoints
- **HolidayTemplateControllerTest**: Tests for template REST API endpoints

### Integration Tests
- **HolidayApiIntegrationTest**: End-to-end integration tests

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=HolidayServiceTest
```

### Run Tests with Coverage Report
```bash
mvn test jacoco:report
```

### Run Tests in Specific Package
```bash
mvn test -Dtest="com.emre.holidayapi.service.*Test"
```

## Test Coverage

The test suite provides comprehensive coverage for:

- **Service Methods**: 100% method coverage for all service classes
- **Controller Endpoints**: All REST endpoints tested
- **Error Handling**: Exception scenarios and edge cases
- **Business Logic**: Holiday calculations, date operations, translations
- **Integration**: Cross-layer functionality testing

## Test Features

### Service Tests Include:
- Unit tests with mocked dependencies
- Edge case testing (null values, empty lists, invalid dates)
- Business logic validation
- Error handling verification
- Translation functionality testing

### Controller Tests Include:
- REST endpoint testing with MockMvc
- Request/response validation
- HTTP status code verification
- JSON serialization/deserialization
- Cross-origin resource sharing (CORS) testing

### Integration Tests Include:
- Full application context testing
- End-to-end workflow validation
- Database integration (with mocked data)

## Test Dependencies

The tests use:
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **AssertJ**: Fluent assertions
- **Spring Boot Test**: Integration testing support
- **MockMvc**: Web layer testing

## Running Tests in IDE

Most IDEs (IntelliJ IDEA, Eclipse, VS Code) can run tests directly:
1. Right-click on test class → "Run Tests"
2. Use the test runner panel
3. Run individual test methods

## Continuous Integration

These tests are designed to run in CI/CD pipelines:
- Fast execution (under 30 seconds total)
- No external dependencies
- Reliable and repeatable
- Comprehensive coverage

## Test Data

Tests use:
- Mock objects for external dependencies
- Fixed test data for predictable results
- Date-specific scenarios for holiday calculations
- Multilingual test cases (English/Turkish)

## Troubleshooting

### Common Issues:
1. **Spring Boot version compatibility**: Ensure MockitoBean imports are correct
2. **Date-sensitive tests**: Some tests use current date, ensure system time is correct
3. **Memory issues**: Run with `-Xmx512m` if needed

### Test Debugging:
- Add `@Disabled` to skip problematic tests temporarily
- Use `System.out.println()` for debugging output
- Check test logs for detailed error messages
