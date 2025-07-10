package main.java.com.emre.holidayapi.repository;

import com.emre.holidayapi.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
    // String çünkü PK countryCode string türünde
}
