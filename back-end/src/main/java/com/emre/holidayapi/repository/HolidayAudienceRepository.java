package main.java.com.emre.holidayapi.repository;

import com.emre.holidayapi.entity.HolidayAudience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayAudienceRepository extends JpaRepository<HolidayAudience, Long> {
    // İstersen özel sorgular ekleyebilirsin
}
