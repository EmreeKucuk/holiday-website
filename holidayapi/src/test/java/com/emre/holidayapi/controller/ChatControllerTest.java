package com.emre.holidayapi.controller;

import com.emre.holidayapi.service.HolidayAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HolidayAiService holidayAiService;

    @Autowired
    private ObjectMapper objectMapper;

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
                .andExpect(jsonPath("$.reply", is("Please provide a message to get started!")));
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
                .andExpect(jsonPath("$.reply", is("Please provide a message to get started!")));
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
                .andExpect(jsonPath("$.reply", is("Please provide a message to get started!")));
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
