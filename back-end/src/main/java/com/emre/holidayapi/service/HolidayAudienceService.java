package main.java.com.emre.holidayapi.service;

import com.emre.holidayapi.entity.HolidayAudience;
import com.emre.holidayapi.repository.HolidayAudienceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HolidayAudienceService {

    private final HolidayAudienceRepository repository;

    public HolidayAudienceService(HolidayAudienceRepository repository) {
        this.repository = repository;
    }

    public List<HolidayAudience> findAll() {
        return repository.findAll();
    }

    public Optional<HolidayAudience> findById(Long id) {
        return repository.findById(id);
    }

    public HolidayAudience save(HolidayAudience holidayAudience) {
        return repository.save(holidayAudience);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
