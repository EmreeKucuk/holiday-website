package com.emre.holidayapi.controller;

import com.emre.holidayapi.model.HolidayTemplate;
import com.emre.holidayapi.service.HolidayTemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holiday-templates")
public class HolidayTemplateController {
    private final HolidayTemplateService service;

    public HolidayTemplateController(HolidayTemplateService service) {
        this.service = service;
    }

    @GetMapping
    public List<HolidayTemplate> getAllTemplates() {
        return service.getAllTemplates();
    }
}

import React, { useState, useEffect } from 'react';

interface HolidayTemplate {
  id: number;
  code: string;
  defaultName: string;
  type: string;
}

function App() {
  const [holidayTemplates, setHolidayTemplates] = useState<HolidayTemplate[]>([]);
  const [loadingTemplates, setLoadingTemplates] = useState(false);

  // Fetch holiday templates from backend
  const fetchHolidayTemplates = async () => {
    setLoadingTemplates(true);
    try {
      const response = await fetch('http://localhost:8080/api/holiday-templates');
      if (!response.ok) throw new Error('Failed to fetch');
      const data = await response.json();
      setHolidayTemplates(data);
    } catch (err) {
      // handle error
      setHolidayTemplates([]);
    } finally {
      setLoadingTemplates(false);
    }
  };

  // Example: fetch on mount
  useEffect(() => {
    fetchHolidayTemplates();
  }, []);

  return (
    <div>
      <h2>Holiday Templates</h2>
      {loadingTemplates ? (
        <p>Loading...</p>
      ) : (
        <ul>
          {holidayTemplates.map((template) => (
            <li key={template.id}>
              {template.code} - {template.defaultName} ({template.type})
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default App;