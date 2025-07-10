package main.java.com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "country_holidays")
public class CountryHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // HolidayTemplate ile ManyToOne ilişki
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private HolidayTemplate template;

    @Column(name = "country_code", length = 10, nullable = false)
    private String countryCode;

    @Column(name = "specific_name", length = 150)
    private String specificName;

    // Getter & Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HolidayTemplate getTemplate() {
        return template;
    }

    public void setTemplate(HolidayTemplate template) {
        this.template = template;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSpecificName() {
        return specificName;
    }

    public void setSpecificName(String specificName) {
        this.specificName = specificName;
    }
}
