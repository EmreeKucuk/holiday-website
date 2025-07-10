package main.java.com.emre.holidayapi.repository;

import com.emre.holidayapi.model.HolidayTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolidayTranslationRepository extends JpaRepository<HolidayTranslation, Long> {
    List<HolidayTranslation> findByHolidayTemplateId(Long templateId);
    List<HolidayTranslation> findByLanguageCode(String languageCode);
}
