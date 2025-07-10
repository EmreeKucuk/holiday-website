package main.java.com.emre.holidayapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "translations")
public class HolidayTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String languageCode;  // Örneğin: "en", "tr"

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private HolidayTemplate holidayTemplate;

    // Getter-Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HolidayTemplate getHolidayTemplate() {
        return holidayTemplate;
    }

    public void setHolidayTemplate(HolidayTemplate holidayTemplate) {
        this.holidayTemplate = holidayTemplate;
    }
}
