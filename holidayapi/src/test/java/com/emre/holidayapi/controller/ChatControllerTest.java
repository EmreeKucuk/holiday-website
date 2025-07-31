package com.emre.holidayapi.controller;

import com.emre.holidayapi.service.HolidayAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HolidayAiService holidayAiService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ChatController chatController = new ChatController(holidayAiService);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    void chat_WithValidMessage_ShouldReturnAiResponse() throws Exception {
        // Given
        Map<String, String> request = Map.of(
                "message", "Is today a holiday?",
                "country", "TR",
                "language", "en"
        );
        String expectedResponse = "Today is New Year's Day!";
        when(holidayAiService.processHolidayQuery(anyString(), anyString(), anyString())).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", is(expectedResponse)));
    }

    @Test
    void chat_WithMinimalRequest_ShouldUseDefaults() throws Exception {
        // Given
        Map<String, String> request = Map.of("message", "Is today a holiday?");
        String expectedResponse = "Today is a regular day.";
        when(holidayAiService.processHolidayQuery("Is today a holiday?", "TR", "en")).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", is(expectedResponse)));
    }

    @Test
    void chat_WithEmptyMessage_ShouldReturnPromptMessage() throws Exception {
        // Given
        Map<String, String> request = Map.of("message", "");

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", is("Please provide a message to get started! I'm here to provide deep insights about holidays, cultural patterns, and historical significance.")));
    }

    @Test
    void chat_WithNullMessage_ShouldReturnPromptMessage() throws Exception {
        // Given
        Map<String, String> request = Map.of("country", "TR");

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", is("Please provide a message to get started! I'm here to provide deep insights about holidays, cultural patterns, and historical significance.")));
    }

    @Test
    void chat_WithWhitespaceMessage_ShouldReturnPromptMessage() throws Exception {
        // Given
        Map<String, String> request = Map.of("message", "   ");

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", is("Please provide a message to get started! I'm here to provide deep insights about holidays, cultural patterns, and historical significance.")));
    }

    @Test
    void chat_WhenServiceThrowsException_ShouldReturnErrorMessage() throws Exception {
        // Given
        Map<String, String> request = Map.of("message", "Is today a holiday?");
        when(holidayAiService.processHolidayQuery(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", is("I'm sorry, I encountered an error while processing your request. Please try again.")));
    }

    @Test
    void chat_WithTurkishLanguage_ShouldPassCorrectLanguage() throws Exception {
        // Given
        Map<String, String> request = Map.of(
                "message", "Bugün tatil mi?",
                "country", "TR",
                "language", "tr"
        );
        String expectedResponse = "Bugün tatil değil.";
        when(holidayAiService.processHolidayQuery("Bugün tatil mi?", "TR", "tr")).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", is(expectedResponse)));
    }

    @Test
    void chat_WithDifferentCountry_ShouldPassCorrectCountry() throws Exception {
        // Given
        Map<String, String> request = Map.of(
                "message", "Is today a holiday?",
                "country", "US",
                "language", "en"
        );
        String expectedResponse = "Today is Independence Day!";
        when(holidayAiService.processHolidayQuery("Is today a holiday?", "US", "en")).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", is(expectedResponse)));
    }

    @Test
    void chat_ShouldReturnCorrectContentType() throws Exception {
        // Given
        Map<String, String> request = Map.of("message", "Is today a holiday?");
        when(holidayAiService.processHolidayQuery(anyString(), anyString(), anyString())).thenReturn("No holidays today.");

        // When & Then
        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
