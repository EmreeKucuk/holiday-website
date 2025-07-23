package com.emre.holidayapi.service;

import com.emre.holidayapi.model.*;
import com.emre.holidayapi.repository.*;
import com.emre.holidayapi.dto.AudienceDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class HolidayAiService {

    private final ChatClient chatClient;
    private final HolidayService holidayService;
    private final AudienceService audienceService;
    private final CountryRepository countryRepository;
    private final TranslationRepository translationRepository;

    public HolidayAiService(ChatClient.Builder chatClientBuilder, 
                           HolidayService holidayService, 
                           AudienceService audienceService,
                           CountryRepository countryRepository,
                           TranslationRepository translationRepository) {
        this.chatClient = chatClientBuilder.build();
        this.holidayService = holidayService;
        this.audienceService = audienceService;
        this.countryRepository = countryRepository;
        this.translationRepository = translationRepository;
    }

    public String processHolidayQuery(String userMessage, String countryCode, String language) {
        try {
            String lowerMessage = userMessage.toLowerCase();
            
            // Check if asking about today's holiday
            if (lowerMessage.contains("today") && lowerMessage.contains("holiday")) {
                return handleTodayHolidayQuery(countryCode, language);
            }
            
            // Check if asking about holidays between dates
            if (containsDateRange(lowerMessage)) {
                return handleDateRangeQuery(userMessage, countryCode, language);
            }
            
            // Check for specific holiday name recognition
            if (containsHolidayNameQuery(lowerMessage)) {
                return handleHolidayNameQuery(userMessage, countryCode, language);
            }
            
            // Check for specific year queries
            if (containsSpecificYear(lowerMessage)) {
                return handleSpecificYearQuery(userMessage, countryCode, language);
            }
            
            // Check for holiday statistics queries
            if (containsStatisticsKeywords(lowerMessage)) {
                return handleStatisticsQuery(userMessage, countryCode, language);
            }
            
            // Check for holiday type queries (religious, official, etc.)
            if (containsHolidayTypeKeywords(lowerMessage)) {
                return handleHolidayTypeQuery(userMessage, countryCode, language);
            }
            
            // Check if asking about annual holidays
            if (lowerMessage.contains("year") && (lowerMessage.contains("holiday") || lowerMessage.contains("how many"))) {
                return handleAnnualHolidayQuery(countryCode, language);
            }
            
            // Check if asking about audience-specific holidays
            if (containsAudienceKeywords(lowerMessage)) {
                return handleAudienceSpecificQuery(userMessage, countryCode, language);
            }
            
            // For general queries, use AI with context
            return handleGeneralQuery(userMessage, countryCode, language);
            
        } catch (Exception e) {
            return getLocalizedErrorMessage(language);
        }
    }

    private String handleTodayHolidayQuery(String countryCode, String language) {
        LocalDate today = LocalDate.now();
        List<HolidayDefinition> todayHolidays = holidayService.getHolidaysByDate(today, countryCode);
        
        if (todayHolidays.isEmpty()) {
            return String.format("No, there are no holidays today (%s) in %s.", 
                today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode));
        } else {
            String holidayNames = todayHolidays.stream()
                .map(h -> getHolidayName(h.getTemplate(), language))
                .collect(Collectors.joining(", "));
            return String.format("Yes! Today (%s) is a holiday in %s: %s", 
                today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode), 
                holidayNames);
        }
    }

    private String handleDateRangeQuery(String userMessage, String countryCode, String language) {
        // Extract dates from message
        List<LocalDate> dates = extractDatesFromMessage(userMessage);
        if (dates.size() < 2) {
            return "I couldn't find valid date range in your message. Please use format like '01/01/2025 - 31/01/2025' or 'between 01/01/2025 and 31/01/2025'.";
        }
        
        LocalDate startDate = dates.get(0);
        LocalDate endDate = dates.get(1);
        
        // Check if asking about working days calculation
        if (userMessage.toLowerCase().contains("working") || userMessage.toLowerCase().contains("calculate")) {
            return handleWorkingDaysCalculation(startDate, endDate, countryCode, userMessage.toLowerCase().contains("weekend"));
        }
        
        // Get holidays in date range
        List<HolidayDefinition> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, countryCode);
        
        if (holidays.isEmpty()) {
            return String.format("There are no holidays between %s and %s in %s.", 
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode));
        } else {
            StringBuilder response = new StringBuilder();
            response.append(String.format("Here are the holidays between %s and %s in %s:\n\n", 
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode)));
            
            for (HolidayDefinition holiday : holidays) {
                response.append(String.format("• %s - %s\n", 
                    getHolidayName(holiday.getTemplate(), language),
                    holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
            
            return response.toString();
        }
    }

    private String handleWorkingDaysCalculation(LocalDate startDate, LocalDate endDate, String countryCode, boolean includeWeekends) {
        List<HolidayDefinition> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, countryCode);
        
        int totalDays = (int) startDate.until(endDate).getDays() + 1;
        int holidayDays = holidays.size();
        
        int weekendDays = 0;
        if (!includeWeekends) {
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                if (current.getDayOfWeek().getValue() >= 6) { // Saturday = 6, Sunday = 7
                    weekendDays++;
                }
                current = current.plusDays(1);
            }
        }
        
        int workingDays = totalDays - holidayDays - (includeWeekends ? 0 : weekendDays);
        
        return String.format("Between %s and %s in %s:\n" +
                "• Total days: %d\n" +
                "• Holiday days: %d\n" +
                "%s" +
                "• Working days: %d", 
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getCountryName(countryCode),
                totalDays,
                holidayDays,
                includeWeekends ? "" : String.format("• Weekend days: %d\n", weekendDays),
                workingDays);
    }

    private String handleAnnualHolidayQuery(String countryCode, String language) {
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        
        List<HolidayDefinition> yearlyHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
        
        return String.format("In %d, %s has %d holidays throughout the year.", 
            currentYear, getCountryName(countryCode), yearlyHolidays.size());
    }

    private String handleAudienceSpecificQuery(String userMessage, String countryCode, String language) {
        // Extract audience from message
        String audience = extractAudienceFromMessage(userMessage, language);
        if (audience == null) {
            return "I couldn't identify the specific audience you're asking about. Please mention a specific group like 'students', 'government employees', 'private sector', etc.";
        }
        
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        
        List<HolidayDefinition> audienceHolidays = holidayService.getHolidaysByDateRangeAndAudience(
            startOfYear, endOfYear, countryCode, audience);
        
        if (audienceHolidays.isEmpty()) {
            return String.format("I couldn't find any holidays specifically for %s in %s this year.", 
                audience, getCountryName(countryCode));
        }
        
        StringBuilder response = new StringBuilder();
        response.append(String.format("Holidays for %s in %s (%d):\n\n", 
            audience, getCountryName(countryCode), currentYear));
        
        for (HolidayDefinition holiday : audienceHolidays) {
            response.append(String.format("• %s - %s\n", 
                getHolidayName(holiday.getTemplate(), language),
                holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }
        
        return response.toString();
    }

    private String handleGeneralQuery(String userMessage, String countryCode, String language) {
        // Create context about the holiday system
        String context = createHolidayContext(countryCode, language);
        
        String systemPrompt = String.format("""
            You are an intelligent and friendly holiday assistant designed to help users with country-specific holiday-related questions for %s. 
            You are connected to a backend system that provides detailed information about public holidays, working days, and audience-specific holidays.

            Your job is to interpret the user's natural language input, and — based on provided context or API output — respond with clear, informative answers in %s.

            You support the following functionalities:

            1. ✅ **Today's Holidays**: If the user asks "Is there a holiday today?" or similar, respond with whether today is a holiday in the selected country and what holidays are being observed.

            2. ✅ **Date Range Queries**: If the user asks about holidays between two dates (e.g., "What holidays are there from 01/01/2025 to 15/01/2025?"), list all holidays within that range.

            3. ✅ **Working Day Calculations**: If the user asks "How many working days between 01/01/2025 and 31/01/2025?", return the number of working days, excluding holidays and optionally weekends.

            4. ✅ **Annual Holiday Count**: If the user asks "How many holidays are there this year in %s?", return the total number of holidays in the current year for that country.

            5. ✅ **Audience-Specific Holidays**: If the user includes audience types like "students", "government employees", "private sector" etc., return holidays that are only valid for that group.

            6. ✅ **General Questions**: If a question doesn't match the patterns above, try to answer it by using the provided country context and year statistics.

            7. 🔄 **Holiday Name Recognition**: If the user asks a question like "When is Ramazan Bayramı?" or "Tell me about 23 Nisan", extract the holiday name and return its date and description if available.

            8. 📅 **Holidays in a Specific Year**: If the user specifies a year like "2024" or asks "What are the holidays in 2024 in %s?", return a list of holidays for that year.

            9. 📊 **Holiday Statistics**: Support questions like:
               - "Which month has the most holidays?"
               - "What's the longest holiday this year?"
               - "How many holidays fall on weekends?"

            10. 🕌 **Holiday Type Classification**: If the user asks for "religious holidays" or "official public holidays", filter results based on the type of holiday.

            11. 📆 **Holiday Duration Analysis**: If a holiday spans multiple days or extends into a weekend, highlight its total duration (e.g., "Ramazan Bayramı lasts for 3 days").

            12. 🌍 **Language-Aware Responses**: Always respond in %s. Adjust your tone and phrasing accordingly.

            Current context:
            %s
            
            Instructions:
            - Be concise, but informative and polite
            - Use date format dd/MM/yyyy in your responses  
            - Always tailor your answer to the country and year provided
            - If you cannot find an exact answer, suggest users ask about:
              * Holidays today
              * Holidays between specific dates
              * Yearly holiday totals
              * Holidays for specific audiences (students, employees, etc.)
            - Act as a true holiday expert for %s
            """, getCountryName(countryCode), language, getCountryName(countryCode), getCountryName(countryCode), language, context, getCountryName(countryCode));
        
        PromptTemplate template = new PromptTemplate(systemPrompt + "\n\nUser question: {question}");
        Prompt prompt = template.create(Map.of("question", userMessage));
        
        return chatClient.prompt(prompt).call().content();
    }

    private String createHolidayContext(String countryCode, String language) {
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        
        List<HolidayDefinition> yearHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
        List<AudienceDto> audiences = audienceService.getAllAudiencesTranslated(language);
        
        StringBuilder context = new StringBuilder();
        context.append(String.format("Country: %s\n", getCountryName(countryCode)));
        context.append(String.format("Total holidays this year (%d): %d\n", currentYear, yearHolidays.size()));
        context.append("Available audience types: ");
        context.append(audiences.stream()
            .map(AudienceDto::getName)
            .collect(Collectors.joining(", ")));
        
        return context.toString();
    }

    // Helper methods
    private boolean containsDateRange(String message) {
        // Look for date patterns like "01/01/2025 - 31/01/2025" or "between ... and ..."
        Pattern dateRangePattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}.*\\d{1,2}/\\d{1,2}/\\d{4}");
        return dateRangePattern.matcher(message).find() || 
               (message.contains("between") && message.contains("and"));
    }

    private boolean containsAudienceKeywords(String message) {
        return message.contains("student") || message.contains("employee") || 
               message.contains("government") || message.contains("private") ||
               message.contains("audience") || message.contains("group");
    }

    private List<LocalDate> extractDatesFromMessage(String message) {
        List<LocalDate> dates = new ArrayList<>();
        Pattern datePattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}");
        Matcher matcher = datePattern.matcher(message);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        while (matcher.find()) {
            try {
                LocalDate date = LocalDate.parse(matcher.group(), formatter);
                dates.add(date);
            } catch (DateTimeParseException e) {
                // Ignore invalid dates
            }
        }
        
        return dates;
    }

    private String extractAudienceFromMessage(String message, String language) {
        List<AudienceDto> audiences = audienceService.getAllAudiencesTranslated(language);
        
        for (AudienceDto audience : audiences) {
            if (message.toLowerCase().contains(audience.getName().toLowerCase()) ||
                message.toLowerCase().contains(audience.getCode().toLowerCase())) {
                return audience.getCode();
            }
        }
        
        // Fallback to common keywords
        if (message.toLowerCase().contains("student")) return "STUDENTS";
        if (message.toLowerCase().contains("government")) return "GOVERNMENT";
        if (message.toLowerCase().contains("private")) return "PRIVATE_SECTOR";
        
        return null;
    }

    private String getCountryName(String countryCode) {
        return countryRepository.findByCountryCode(countryCode)
            .map(Country::getCountryName)
            .orElse(countryCode);
    }

    private String getHolidayName(HolidayTemplate template, String language) {
        return translationRepository.findByTemplateIdAndLanguageCode(template.getId(), language)
            .map(Translation::getTranslatedName)
            .orElse(template.getDefaultName());
    }

    // New methods for extended functionality

    private boolean containsHolidayNameQuery(String message) {
        return message.contains("when is") || message.contains("tell me about") || 
               message.contains("what is") || message.contains("about") ||
               message.contains("how long") || message.contains("duration") ||
               message.contains("last");
    }

    private boolean containsSpecificYear(String message) {
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        return yearPattern.matcher(message).find() || message.contains("this year") || message.contains("next year");
    }

    private boolean containsStatisticsKeywords(String message) {
        return message.contains("most holidays") || message.contains("longest holiday") ||
               message.contains("weekends") || message.contains("which month") ||
               message.contains("statistics") || message.contains("how many fall");
    }

    private boolean containsHolidayTypeKeywords(String message) {
        return message.contains("religious") || message.contains("official") ||
               message.contains("public") || message.contains("cultural") ||
               message.contains("national") || message.contains("type") ||
               message.contains("show me") && (message.contains("religious") || 
               message.contains("official") || message.contains("cultural"));
    }

    private String handleHolidayNameQuery(String userMessage, String countryCode, String language) {
        // Extract potential holiday name from the message
        String potentialHolidayName = extractHolidayNameFromMessage(userMessage, language);
        
        if (potentialHolidayName == null) {
            return getLocalizedMessage(language, 
                "I couldn't identify a specific holiday name in your question. Please mention a holiday name like 'Christmas', 'Ramazan Bayramı', etc.",
                "Sorunuzda belirli bir tatil adı bulamadım. Lütfen 'Noel', 'Ramazan Bayramı' gibi bir tatil adı belirtin.");
        }

        // Search for holidays by name in current and next year
        int currentYear = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(currentYear, 1, 1);
        LocalDate endDate = LocalDate.of(currentYear + 1, 12, 31);
        
        List<HolidayDefinition> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, countryCode);
        
        // Find matching holidays by name
        List<HolidayDefinition> matchingHolidays = holidays.stream()
            .filter(h -> getHolidayName(h.getTemplate(), language).toLowerCase()
                        .contains(potentialHolidayName.toLowerCase()))
            .collect(Collectors.toList());

        if (matchingHolidays.isEmpty()) {
            return getLocalizedMessage(language,
                String.format("I couldn't find information about '%s' in %s for %d-%d.", 
                    potentialHolidayName, getCountryName(countryCode), currentYear, currentYear + 1),
                String.format("'%s' hakkında %s'de %d-%d yılları için bilgi bulamadım.", 
                    potentialHolidayName, getCountryName(countryCode), currentYear, currentYear + 1));
        }

        // Check if asking about duration specifically
        boolean askingAboutDuration = userMessage.toLowerCase().contains("how long") || 
                                    userMessage.toLowerCase().contains("duration") || 
                                    userMessage.toLowerCase().contains("last") ||
                                    userMessage.toLowerCase().contains("kaç gün") ||
                                    userMessage.toLowerCase().contains("ne kadar");

        if (askingAboutDuration) {
            // Use the detailed duration analysis for duration questions
            return analyzeDuration(potentialHolidayName, language);
        }

        StringBuilder response = new StringBuilder();
        for (HolidayDefinition holiday : matchingHolidays) {
            String holidayName = getHolidayName(holiday.getTemplate(), language);
            String dateStr = holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            response.append(String.format("%s is on %s", holidayName, dateStr));
            
            // Add duration analysis
            String duration = analyzeDuration(holiday, countryCode);
            if (!duration.isEmpty()) {
                response.append(duration);
            }
            response.append("\n");
        }

        return response.toString().trim();
    }

    private String handleSpecificYearQuery(String userMessage, String countryCode, String language) {
        int requestedYear = extractYearFromMessage(userMessage);
        if (requestedYear == -1) {
            requestedYear = LocalDate.now().getYear();
        }

        LocalDate startOfYear = LocalDate.of(requestedYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(requestedYear, 12, 31);
        
        List<HolidayDefinition> yearHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
        
        if (yearHolidays.isEmpty()) {
            return getLocalizedMessage(language,
                String.format("No holidays found for %d in %s.", requestedYear, getCountryName(countryCode)),
                String.format("%d yılında %s için tatil bulunamadı.", requestedYear, getCountryName(countryCode)));
        }

        StringBuilder response = new StringBuilder();
        response.append(getLocalizedMessage(language,
            String.format("Holidays in %s for %d:\n\n", getCountryName(countryCode), requestedYear),
            String.format("%d yılında %s tatilleri:\n\n", requestedYear, getCountryName(countryCode))));
        
        for (HolidayDefinition holiday : yearHolidays) {
            response.append(String.format("• %s - %s\n", 
                getHolidayName(holiday.getTemplate(), language),
                holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }
        
        return response.toString();
    }

    private String handleStatisticsQuery(String userMessage, String countryCode, String language) {
        int currentYear = LocalDate.now().getYear();
        LocalDate startOfYear = LocalDate.of(currentYear, 1, 1);
        LocalDate endOfYear = LocalDate.of(currentYear, 12, 31);
        
        List<HolidayDefinition> yearHolidays = holidayService.getHolidaysByDateRange(startOfYear, endOfYear, countryCode);
        
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("which month") && lowerMessage.contains("most")) {
            return analyzeMonthlyDistribution(yearHolidays, language, countryCode);
        }
        
        if (lowerMessage.contains("longest")) {
            return analyzeLongestHoliday(yearHolidays, language, countryCode);
        }
        
        if (lowerMessage.contains("weekend")) {
            return analyzeWeekendHolidays(yearHolidays, language, countryCode);
        }
        
        // General statistics
        return generateGeneralStatistics(yearHolidays, language, countryCode, currentYear);
    }

    private String handleHolidayTypeQuery(String userMessage, String countryCode, String language) {
        String lowerMessage = userMessage.toLowerCase();
        String requestedType = null;
        
        if (lowerMessage.contains("religious")) requestedType = "RELIGIOUS";
        else if (lowerMessage.contains("official") || lowerMessage.contains("public")) requestedType = "OFFICIAL";
        else if (lowerMessage.contains("cultural")) requestedType = "CULTURAL";
        else if (lowerMessage.contains("national")) requestedType = "NATIONAL";
        
        // Check if this is a combined date range + type query
        List<LocalDate> dates = extractDatesFromMessage(userMessage);
        LocalDate startDate, endDate;
        
        if (dates.size() >= 2) {
            // Date range specified
            startDate = dates.get(0);
            endDate = dates.get(1);
        } else if (containsMonthNames(lowerMessage)) {
            // Month names specified (like "between June and August")
            int currentYear = LocalDate.now().getYear();
            int[] monthRange = extractMonthRange(lowerMessage);
            if (monthRange != null) {
                startDate = LocalDate.of(currentYear, monthRange[0], 1);
                endDate = LocalDate.of(currentYear, monthRange[1], 
                    LocalDate.of(currentYear, monthRange[1], 1).lengthOfMonth());
            } else {
                // Default to current year
                startDate = LocalDate.of(currentYear, 1, 1);
                endDate = LocalDate.of(currentYear, 12, 31);
            }
        } else {
            // Default to current year
            int currentYear = LocalDate.now().getYear();
            startDate = LocalDate.of(currentYear, 1, 1);
            endDate = LocalDate.of(currentYear, 12, 31);
        }
        
        List<HolidayDefinition> holidays = holidayService.getHolidaysByDateRange(startDate, endDate, countryCode);
        
        // Filter by type using holiday template type or name-based filtering
        List<HolidayDefinition> filteredHolidays = filterHolidaysByType(holidays, requestedType, language);
        
        StringBuilder response = new StringBuilder();
        if (requestedType != null) {
            if (dates.size() >= 2 || containsMonthNames(lowerMessage)) {
                response.append(getLocalizedMessage(language,
                    String.format("%s holidays between %s and %s in %s:\n\n", 
                        requestedType.toLowerCase(), 
                        startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        getCountryName(countryCode)),
                    String.format("%s ile %s arasında %s'deki %s tatilleri:\n\n", 
                        startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        getCountryName(countryCode), requestedType.toLowerCase())));
            } else {
                response.append(getLocalizedMessage(language,
                    String.format("%s holidays in %s for %d:\n\n", 
                        requestedType.toLowerCase(), getCountryName(countryCode), startDate.getYear()),
                    String.format("%d yılında %s'deki %s tatilleri:\n\n", 
                        startDate.getYear(), getCountryName(countryCode), requestedType.toLowerCase())));
            }
        } else {
            response.append(getLocalizedMessage(language,
                String.format("All holidays in %s for %d:\n\n", getCountryName(countryCode), startDate.getYear()),
                String.format("%d yılında %s'deki tüm tatiller:\n\n", startDate.getYear(), getCountryName(countryCode))));
        }
        
        if (filteredHolidays.isEmpty()) {
            response.append(getLocalizedMessage(language,
                "No holidays found matching your criteria.",
                "Kriterlerinize uyan tatil bulunamadı."));
        } else {
            for (HolidayDefinition holiday : filteredHolidays) {
                response.append(String.format("• %s - %s\n", 
                    getHolidayName(holiday.getTemplate(), language),
                    holiday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
        }
        
        return response.toString();
    }

    // Helper methods for new functionality

    private String extractHolidayNameFromMessage(String message, String language) {
        // Simple extraction - look for quoted strings or common patterns
        Pattern quotedPattern = Pattern.compile("\"([^\"]+)\"");
        Matcher matcher = quotedPattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Look for "when is X" pattern
        Pattern whenIsPattern = Pattern.compile("when is ([a-zA-ZığüşöçĞÜŞÖÇ\\s]+)");
        matcher = whenIsPattern.matcher(message.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Look for "tell me about X" pattern
        Pattern tellMePattern = Pattern.compile("tell me about ([a-zA-ZığüşöçĞÜŞÖÇ\\s]+)");
        matcher = tellMePattern.matcher(message.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Look for "how long does X last" pattern
        Pattern howLongPattern = Pattern.compile("how long does ([a-zA-ZığüşöçĞÜŞÖÇ\\s]+) last");
        matcher = howLongPattern.matcher(message.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Look for "X kaç gün" pattern (Turkish)
        Pattern turkishDurationPattern = Pattern.compile("([a-zA-ZığüşöçĞÜŞÖÇ\\s]+) kaç gün");
        matcher = turkishDurationPattern.matcher(message.toLowerCase());
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Common holiday names that might appear without specific patterns
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("ramazan")) return "ramazan";
        if (lowerMessage.contains("bayram")) return "bayram";
        if (lowerMessage.contains("kurban")) return "kurban";
        if (lowerMessage.contains("christmas")) return "christmas";
        if (lowerMessage.contains("easter")) return "easter";
        if (lowerMessage.contains("new year")) return "new year";
        if (lowerMessage.contains("republic")) return "republic";
        if (lowerMessage.contains("victory")) return "victory";
        if (lowerMessage.contains("independence")) return "independence";
        if (lowerMessage.contains("labour")) return "labour";
        if (lowerMessage.contains("atatürk") || lowerMessage.contains("ataturk")) return "atatürk";
        
        return null;
    }

    private int extractYearFromMessage(String message) {
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher matcher = yearPattern.matcher(message);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        
        if (message.toLowerCase().contains("next year")) {
            return LocalDate.now().getYear() + 1;
        }
        
        return -1; // Current year
    }

    private String analyzeDuration(HolidayDefinition holiday, String countryCode) {
        // Enhanced duration analysis
        String holidayName = holiday.getTemplate().getDefaultName().toLowerCase();
        
        // Check for known multi-day holidays
        if (holidayName.contains("ramazan") || holidayName.contains("eid al-fitr") || 
            holidayName.contains("bayram")) {
            return " (lasts for 3 days)";
        } else if (holidayName.contains("kurban") || holidayName.contains("eid al-adha")) {
            return " (lasts for 4 days)";
        } else if (holidayName.contains("christmas")) {
            return " (typically celebrated for 2 days)";
        }
        
        // Check if holiday falls on weekend and extends
        int dayOfWeek = holiday.getHolidayDate().getDayOfWeek().getValue();
        if (dayOfWeek == 6 || dayOfWeek == 7) { // Saturday or Sunday
            return " (falls on weekend)";
        } else if (dayOfWeek == 5) { // Friday
            return " (creates long weekend)";
        } else if (dayOfWeek == 1) { // Monday  
            return " (creates long weekend)";
        }
        
        return "";
    }

    private String analyzeDuration(String holidayName, String language) {
        // This could be expanded with a more comprehensive database
        String lowerName = holidayName.toLowerCase();
        
        // Religious holidays with variable durations
        if (lowerName.contains("ramazan") || lowerName.contains("ramadan") || lowerName.contains("eid al-fitr")) {
            if (language.equals("tr")) {
                return "Ramazan Bayramı genellikle 3 gün sürer (resmi olarak), ancak bazı yıllarda 4 gün olabilir. Bayram, ayın görülmesine göre belirlenir.";
            } else {
                return "Ramadan Bayram (Eid al-Fitr) typically lasts 3 days (officially), but can be 4 days in some years. The exact dates depend on moon sighting.";
            }
        }
        
        if (lowerName.contains("kurban") || lowerName.contains("sacrifice") || lowerName.contains("eid al-adha")) {
            if (language.equals("tr")) {
                return "Kurban Bayramı genellikle 4 gün sürer (resmi olarak), ancak bazı yıllarda 3 gün olabilir. Bayram, ayın görülmesine göre belirlenir.";
            } else {
                return "Eid al-Adha (Kurban Bayramı) typically lasts 4 days (officially), but can be 3 days in some years. The exact dates depend on moon sighting.";
            }
        }
        
        // Multi-day holidays
        if (lowerName.contains("new year")) {
            if (language.equals("tr")) {
                return "Yeni Yıl genellikle 1 gün resmi tatil olarak kutlanır (1 Ocak).";
            } else {
                return "New Year is typically celebrated as a 1-day official holiday (January 1st).";
            }
        }
        
        if (lowerName.contains("christmas")) {
            if (language.equals("tr")) {
                return "Noel, Hristiyan ülkelerde genellikle 1-2 gün resmi tatil olarak kutlanır.";
            } else {
                return "Christmas is typically celebrated as 1-2 days of official holidays in Christian countries.";
            }
        }
        
        if (lowerName.contains("easter")) {
            if (language.equals("tr")) {
                return "Paskalya, Hristiyan ülkelerde genellikle Cuma'dan Pazartesi'ye kadar 4 gün sürer.";
            } else {
                return "Easter typically lasts 4 days from Good Friday to Easter Monday in Christian countries.";
            }
        }
        
        // Turkish national holidays (most are single day)
        if (lowerName.contains("republic") || lowerName.contains("cumhuriyet")) {
            if (language.equals("tr")) {
                return "Cumhuriyet Bayramı 29 Ekim'de 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "Republic Day is celebrated as a 1-day official holiday on October 29th.";
            }
        }
        
        if (lowerName.contains("victory") || lowerName.contains("zafer")) {
            if (language.equals("tr")) {
                return "Zafer Bayramı 30 Ağustos'ta 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "Victory Day is celebrated as a 1-day official holiday on August 30th.";
            }
        }
        
        if (lowerName.contains("atatürk") || lowerName.contains("ataturk")) {
            if (language.equals("tr")) {
                return "Atatürk'ü Anma, Gençlik ve Spor Bayramı 19 Mayıs'ta 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "Atatürk Commemoration, Youth and Sports Day is celebrated as a 1-day official holiday on May 19th.";
            }
        }
        
        if (lowerName.contains("independence") || lowerName.contains("bağımsızlık")) {
            if (language.equals("tr")) {
                return "Ulusal Egemenlik ve Çocuk Bayramı 23 Nisan'da 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "National Sovereignty and Children's Day is celebrated as a 1-day official holiday on April 23rd.";
            }
        }
        
        if (lowerName.contains("labour") || lowerName.contains("labor") || lowerName.contains("işçi")) {
            if (language.equals("tr")) {
                return "İşçi Bayramı 1 Mayıs'ta 1 gün resmi tatil olarak kutlanır.";
            } else {
                return "Labour Day is celebrated as a 1-day official holiday on May 1st.";
            }
        }
        
        // Default response for other holidays
        if (language.equals("tr")) {
            return "Bu tatil hakkında detaylı süre bilgisi bulunmuyor. Çoğu resmi tatil 1 gün sürer, dini tatiller ise genellikle 3-4 gün sürer.";
        } else {
            return "Detailed duration information for this holiday is not available. Most official holidays last 1 day, while religious holidays typically last 3-4 days.";
        }
    }

    private boolean containsMonthNames(String message) {
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("january") || lowerMessage.contains("february") || 
               lowerMessage.contains("march") || lowerMessage.contains("april") || 
               lowerMessage.contains("may") || lowerMessage.contains("june") || 
               lowerMessage.contains("july") || lowerMessage.contains("august") || 
               lowerMessage.contains("september") || lowerMessage.contains("october") || 
               lowerMessage.contains("november") || lowerMessage.contains("december") ||
               lowerMessage.contains("ocak") || lowerMessage.contains("şubat") || 
               lowerMessage.contains("mart") || lowerMessage.contains("nisan") || 
               lowerMessage.contains("mayıs") || lowerMessage.contains("haziran") || 
               lowerMessage.contains("temmuz") || lowerMessage.contains("ağustos") || 
               lowerMessage.contains("eylül") || lowerMessage.contains("ekim") || 
               lowerMessage.contains("kasım") || lowerMessage.contains("aralık");
    }

    private int[] extractMonthRange(String message) {
        String lowerMessage = message.toLowerCase();
        
        // Map month names to numbers
        Map<String, Integer> monthMap = Map.ofEntries(
            Map.entry("january", 1), Map.entry("ocak", 1),
            Map.entry("february", 2), Map.entry("şubat", 2),
            Map.entry("march", 3), Map.entry("mart", 3),
            Map.entry("april", 4), Map.entry("nisan", 4),
            Map.entry("may", 5), Map.entry("mayıs", 5),
            Map.entry("june", 6), Map.entry("haziran", 6),
            Map.entry("july", 7), Map.entry("temmuz", 7),
            Map.entry("august", 8), Map.entry("ağustos", 8),
            Map.entry("september", 9), Map.entry("eylül", 9),
            Map.entry("october", 10), Map.entry("ekim", 10),
            Map.entry("november", 11), Map.entry("kasım", 11),
            Map.entry("december", 12), Map.entry("aralık", 12)
        );
        
        List<Integer> foundMonths = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : monthMap.entrySet()) {
            if (lowerMessage.contains(entry.getKey())) {
                foundMonths.add(entry.getValue());
            }
        }
        
        if (foundMonths.size() >= 2) {
            foundMonths.sort(Integer::compareTo);
            return new int[]{foundMonths.get(0), foundMonths.get(foundMonths.size() - 1)};
        } else if (foundMonths.size() == 1) {
            // Single month specified
            int month = foundMonths.get(0);
            return new int[]{month, month};
        }
        
        return null;
    }

    private List<HolidayDefinition> filterHolidaysByType(List<HolidayDefinition> holidays, String requestedType, String language) {
        if (requestedType == null) {
            return holidays;
        }
        
        return holidays.stream()
            .filter(holiday -> isHolidayOfType(holiday, requestedType, language))
            .collect(Collectors.toList());
    }

    private boolean isHolidayOfType(HolidayDefinition holiday, String requestedType, String language) {
        String holidayName = getHolidayName(holiday.getTemplate(), language).toLowerCase();
        String templateType = holiday.getTemplate().getType();
        
        // Check template type first if available
        if (templateType != null && templateType.equalsIgnoreCase(requestedType)) {
            return true;
        }
        
        // Fallback to name-based classification
        switch (requestedType.toUpperCase()) {
            case "RELIGIOUS":
                return holidayName.contains("eid") || holidayName.contains("bayram") || 
                       holidayName.contains("ramazan") || holidayName.contains("kurban") ||
                       holidayName.contains("christmas") || holidayName.contains("easter") ||
                       holidayName.contains("religious") || holidayName.contains("dini") ||
                       holidayName.contains("mevlid") || holidayName.contains("kandil");
                       
            case "OFFICIAL":
            case "PUBLIC":
                return holidayName.contains("republic") || holidayName.contains("cumhuriyet") ||
                       holidayName.contains("independence") || holidayName.contains("bağımsızlık") ||
                       holidayName.contains("national") || holidayName.contains("ulusal") ||
                       holidayName.contains("victory") || holidayName.contains("zafer") ||
                       holidayName.contains("labour") || holidayName.contains("işçi") ||
                       holidayName.contains("new year") || holidayName.contains("yılbaşı");
                       
            case "CULTURAL":
                return holidayName.contains("children") || holidayName.contains("çocuk") ||
                       holidayName.contains("youth") || holidayName.contains("gençlik") ||
                       holidayName.contains("women") || holidayName.contains("kadın") ||
                       holidayName.contains("mother") || holidayName.contains("anne") ||
                       holidayName.contains("father") || holidayName.contains("baba");
                       
            case "NATIONAL":
                return holidayName.contains("atatürk") || holidayName.contains("ataturk") ||
                       holidayName.contains("sovereignty") || holidayName.contains("egemenlik") ||
                       holidayName.contains("democracy") || holidayName.contains("demokrasi") ||
                       holidayName.contains("memorial") || holidayName.contains("anma");
                       
            default:
                return true; // Include all if type not recognized
        }
    }

    private String analyzeMonthlyDistribution(List<HolidayDefinition> holidays, String language, String countryCode) {
        Map<Integer, Long> monthCount = holidays.stream()
            .collect(Collectors.groupingBy(
                h -> h.getHolidayDate().getMonthValue(),
                Collectors.counting()
            ));

        int maxMonth = monthCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(1);

        String monthName = LocalDate.of(2024, maxMonth, 1)
            .format(DateTimeFormatter.ofPattern("MMMM"));

        return getLocalizedMessage(language,
            String.format("%s has the most holidays in %s with %d holidays.", 
                monthName, getCountryName(countryCode), monthCount.get(maxMonth)),
            String.format("%s'de en çok tatil %s ayında var: %d tatil.", 
                getCountryName(countryCode), monthName, monthCount.get(maxMonth)));
    }

    private String analyzeLongestHoliday(List<HolidayDefinition> holidays, String language, String countryCode) {
        // For now, just return the first holiday as "longest"
        // This would need enhancement to track multi-day holidays
        if (holidays.isEmpty()) {
            return getLocalizedMessage(language, "No holidays found.", "Tatil bulunamadı.");
        }
        
        HolidayDefinition firstHoliday = holidays.get(0);
        return getLocalizedMessage(language,
            String.format("The longest holiday period includes %s on %s.", 
                getHolidayName(firstHoliday.getTemplate(), language),
                firstHoliday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
            String.format("En uzun tatil dönemi %s tarihindeki %s'i içerir.", 
                firstHoliday.getHolidayDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                getHolidayName(firstHoliday.getTemplate(), language)));
    }

    private String analyzeWeekendHolidays(List<HolidayDefinition> holidays, String language, String countryCode) {
        long weekendHolidays = holidays.stream()
            .filter(h -> h.getHolidayDate().getDayOfWeek().getValue() >= 6)
            .count();

        return getLocalizedMessage(language,
            String.format("%d holidays fall on weekends in %s this year.", 
                weekendHolidays, getCountryName(countryCode)),
            String.format("Bu yıl %s'de %d tatil hafta sonuna denk geliyor.", 
                getCountryName(countryCode), weekendHolidays));
    }

    private String generateGeneralStatistics(List<HolidayDefinition> holidays, String language, String countryCode, int year) {
        long weekendHolidays = holidays.stream()
            .filter(h -> h.getHolidayDate().getDayOfWeek().getValue() >= 6)
            .count();

        return getLocalizedMessage(language,
            String.format("Holiday statistics for %s in %d:\n" +
                "• Total holidays: %d\n" +
                "• Holidays on weekends: %d\n" +
                "• Average holidays per month: %.1f",
                getCountryName(countryCode), year, holidays.size(), 
                weekendHolidays, holidays.size() / 12.0),
            String.format("%d yılında %s tatil istatistikleri:\n" +
                "• Toplam tatil: %d\n" +
                "• Hafta sonu tatilleri: %d\n" +
                "• Aylık ortalama tatil: %.1f",
                year, getCountryName(countryCode), holidays.size(), 
                weekendHolidays, holidays.size() / 12.0));
    }

    private String getLocalizedMessage(String language, String englishMessage, String turkishMessage) {
        return "tr".equalsIgnoreCase(language) ? turkishMessage : englishMessage;
    }

    private String getLocalizedErrorMessage(String language) {
        return getLocalizedMessage(language,
            "I'm sorry, I encountered an error while processing your request. Please try again or rephrase your question.",
            "Üzgünüm, isteğinizi işlerken bir hata oluştu. Lütfen tekrar deneyin veya sorunuzu yeniden ifade edin.");
    }
}
