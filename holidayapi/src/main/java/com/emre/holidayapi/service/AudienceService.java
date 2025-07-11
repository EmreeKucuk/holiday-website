package com.emre.holidayapi.service;

import com.emre.holidayapi.model.Audience;
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

    public List<Audience> getAllAudiences() {
        return repository.findAll();
    }

    public Audience addAudience(Audience audience) {
        // Check if audience with the same code already exists
        if (repository.existsById(audience.getCode())) {
            throw new IllegalArgumentException("Audience with code " + audience.getCode() + " already exists.");
        }
        return repository.save(audience);
    }

    public Audience updateAudience(String audienceId, Audience audience) {
        // Check if audience exists
        Optional<Audience> existingAudience = repository.findById(audienceId);
        if (!existingAudience.isPresent()) {
            throw new IllegalArgumentException("Audience with id " + audienceId + " does not exist.");
        }
        audience.setCode(audienceId);
        return repository.save(audience);
    }

    public void deleteAudience(String audienceId) {
        repository.deleteById(audienceId);
    }
}