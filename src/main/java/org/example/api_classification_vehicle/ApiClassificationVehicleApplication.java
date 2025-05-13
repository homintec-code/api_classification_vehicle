package org.example.api_classification_vehicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApiClassificationVehicleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiClassificationVehicleApplication.class, args);
	}

}
