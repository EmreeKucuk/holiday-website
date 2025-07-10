package main.java.com.emre.holidayapi.service;

import com.emre.holidayapi.model.CountryHoliday;
import com.emre.holidayapi.repository.CountryHolidayRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryHolidayService {

    private final CountryHolidayRepository repository;

    public CountryHolidayService(CountryHolidayRepository repository) {
        this.repository = repository;
    }

    public List<CountryHoliday> getAll() {
        return repository.findAll();
    }

    public Optional<CountryHoliday> getById(Long id) {
        return repository.findById(id);
    }

    public List<CountryHoliday> getByCountryCode(String countryCode) {
        return repository.findByCountryCode(countryCode);
    }

    public CountryHoliday save(CountryHoliday countryHoliday) {
        return repository.save(countryHoliday);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
