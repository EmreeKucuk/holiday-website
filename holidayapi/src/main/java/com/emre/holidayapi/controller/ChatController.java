package com.emre.holidayapi.controller;

import com.emre.holidayapi.service.HolidayAiService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RestController
@RequestMapping("/api")
public class ChatController {

    private final HolidayAiService holidayAiService;

    public ChatController(HolidayAiService holidayAiService) {
        this.holidayAiService = holidayAiService;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String countryCode = request.getOrDefault("country", "TR");
        String language = request.getOrDefault("language", "en");
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return Map.of("reply", "Please provide a message to get started!");
        }
        
        try {
            String aiResponse = holidayAiService.processHolidayQuery(userMessage, countryCode, language);
            return Map.of("reply", aiResponse);
        } catch (Exception e) {
            return Map.of("reply", "I'm sorry, I encountered an error while processing your request. Please try again.");
        }
    }
}
