package com.emre.holidayapi.repository;

import com.emre.holidayapi.model.Audience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudienceRepository extends JpaRepository<Audience, String> {
}
