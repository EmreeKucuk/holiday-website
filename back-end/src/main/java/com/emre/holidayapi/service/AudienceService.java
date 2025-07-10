package main.java.com.emre.holidayapi.service;

import com.emre.holidayapi.entity.Audience;
import com.emre.holidayapi.repository.AudienceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AudienceService {

    private final AudienceRepository repository;

    public AudienceService(AudienceRepository repository) {
        this.repository = repository;
    }

    public List<Audience> findAll() {
        return repository.findAll();
    }

    public Optional<Audience> findById(Long id) {
        return repository.findById(id);
    }

    public Audience save(Audience audience) {
        return repository.save(audience);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
