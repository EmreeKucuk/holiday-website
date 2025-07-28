package com.emre.holidayapi.suite;

import org.junit.jupiter.api.Test;

/**
 * Test suite documentation class for the Holiday API.
 * 
 * This project includes comprehensive unit tests for:
 * 
 * SERVICE LAYER TESTS:
 * - HolidayServiceTest: Tests for holiday business logic, CRUD operations, date calculations
 * - CountryServiceTest: Tests for country data retrieval
 * - AudienceServiceTest: Tests for audience management and translations
 * - HolidayTemplateServiceTest: Tests for holiday template operations
 * - HolidayAiServiceTest: Tests for AI-powered holiday query processing
 * 
 * CONTROLLER LAYER TESTS:
 * - HolidayControllerTest: Tests for holiday REST endpoints
 * - CountryControllerTest: Tests for country REST endpoints
 * - ChatControllerTest: Tests for AI chat REST endpoints
 * - HolidayTemplateControllerTest: Tests for template REST endpoints
 * 
 * INTEGRATION TESTS:
 * - HolidayApiIntegrationTest: End-to-end integration tests
 * 
 * To run all tests:
 * mvn test
 * 
 * To run specific test class:
 * mvn test -Dtest=HolidayServiceTest
 * 
 * To run tests with coverage:
 * mvn test jacoco:report
 */
public class AllTestSuite {

    @Test
    void testSuiteDocumentation() {
        // This test serves as documentation
        // All actual tests are in their respective test classes
        System.out.println("Holiday API Test Suite includes comprehensive tests for:");
        System.out.println("- 5 Service classes");
        System.out.println("- 4 Controller classes");
        System.out.println("- Integration tests");
        System.out.println("Total test methods: 50+");
    }
}
