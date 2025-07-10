package main.java.com.emre.holidayapi.repository;

import com.emre.holidayapi.model.HolidayTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayTemplateRepository extends JpaRepository<HolidayTemplate, Long> {
    // İsteğe bağlı olarak özel sorgular burada tanımlanabilir.
}
