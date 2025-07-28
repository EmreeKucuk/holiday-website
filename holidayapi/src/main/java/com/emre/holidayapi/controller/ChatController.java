package com.emre.holidayapi.controller;

import com.emre.holidayapi.service.IntelligentHolidayAiService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RestController
@RequestMapping("/api")
public class ChatController {

    private final IntelligentHolidayAiService intelligentHolidayAiService;

    public ChatController(IntelligentHolidayAiService intelligentHolidayAiService) {
        this.intelligentHolidayAiService = intelligentHolidayAiService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String countryCode = request.getOrDefault("country", "TR");
        String language = request.getOrDefault("language", "en");
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            String emptyMessage = "en".equals(language) 
                ? "Please provide a message to get started! I'm here to provide deep insights about holidays, cultural patterns, and historical significance."
                : "Başlamak için lütfen bir mesaj gönderin! Tatiller, kültürel desenler ve tarihsel önem hakkında derinlemesine bilgiler sunmak için buradayım.";
            return Map.of("reply", emptyMessage);
        }
        
        try {
            String aiResponse = intelligentHolidayAiService.processHolidayQuery(userMessage, countryCode, language);
            return Map.of("reply", aiResponse);
        } catch (Exception e) {
            return Map.of("reply", "I'm sorry, I encountered an error while processing your request. Please try again.");
        }
    }
}
