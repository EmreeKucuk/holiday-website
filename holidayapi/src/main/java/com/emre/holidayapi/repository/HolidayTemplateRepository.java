package com.emre.holidayapi.repository;

import com.emre.holidayapi.model.HolidayTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayTemplateRepository extends JpaRepository<HolidayTemplate, Long> {
    HolidayTemplate findByCode(String code);
}