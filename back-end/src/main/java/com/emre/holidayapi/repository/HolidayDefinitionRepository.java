package main.java.com.emre.holidayapi.repository;

import com.emre.holidayapi.entity.HolidayDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayDefinitionRepository extends JpaRepository<HolidayDefinition, Long> {
    // İstersen templateId’ye göre sorgu yazabiliriz ileride
}
