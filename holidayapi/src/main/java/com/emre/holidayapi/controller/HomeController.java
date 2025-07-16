package com.emre.holidayapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "<html><body>" +
               "<h1>Holiday API Backend</h1>" +
               "<p>The Holiday API is running successfully!</p>" +
               "<h3>Available Endpoints:</h3>" +
               "<ul>" +
               "<li><a href='/api/countries'>/api/countries</a> - Get all countries</li>" +
               "<li><a href='/api/holidays/audiences'>/api/holidays/audiences</a> - Get all audiences</li>" +
               "<li><a href='/api/holidays/types'>/api/holidays/types</a> - Get all holiday types</li>" +
               "<li>/api/holidays/range?start=2025-01-01&end=2025-12-31&country=TR - Get holidays in date range</li>" +
               "<li>/api/holidays/today?country=TR - Get today's holidays</li>" +
               "<li>/api/holidays/country/TR - Get holidays by country</li>" +
               "</ul>" +
               "</body></html>";
    }
}
