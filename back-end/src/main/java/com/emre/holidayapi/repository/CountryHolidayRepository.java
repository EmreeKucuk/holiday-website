package main.java.com.emre.holidayapi.repository;

import com.emre.holidayapi.model.CountryHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryHolidayRepository extends JpaRepository<CountryHoliday, Long> {

    // Örnek: Belirli bir ülke için tatilleri çekmek istersen
    List<CountryHoliday> findByCountryCode(String countryCode);

}
