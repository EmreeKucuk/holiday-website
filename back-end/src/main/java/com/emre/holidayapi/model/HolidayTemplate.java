package main.java.com.emre.holidayapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "holiday_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private Boolean fixed;

    @Column(name = "start_day")
    private LocalDate startDay;

    @Column(name = "end_day")
    private LocalDate endDay;

    @Column(name = "launch_year")
    private Integer launchYear;

    private Boolean global;

    @Column(name = "is_half_day")
    private Boolean isHalfDay;
}
