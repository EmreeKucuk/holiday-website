package com.emre.holidayapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.emre.holidayapi.repository")
public class HolidayapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HolidayapiApplication.class, args);
	}

}
