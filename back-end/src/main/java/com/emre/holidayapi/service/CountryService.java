package main.java.com.emre.holidayapi.service;

import com.emre.holidayapi.entity.Country;
import com.emre.holidayapi.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    public List<Country> findAll() {
        return repository.findAll();
    }

    public Optional<Country> findByCode(String countryCode) {
        return repository.findById(countryCode);
    }

    public Country save(Country country) {
        return repository.save(country);
    }

    public void deleteByCode(String countryCode) {
        repository.deleteById(countryCode);
    }
}
