package main.java.com.emre.holidayapi.repository;

import com.emre.holidayapi.entity.Audience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudienceRepository extends JpaRepository<Audience, Long> {
    // Ek metodlar gerekirse buraya eklenebilir
}
