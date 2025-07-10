package main.java.com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @Column(name = "country_code", length = 2)
    private String countryCode; // ISO kodu, PK olarak kullanacağız

    @Column(name = "name", nullable = false)
    private String name;

    // Constructors, getters, setters

    public Country() {
    }

    public Country(String countryCode, String name) {
        this.countryCode = countryCode;
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
