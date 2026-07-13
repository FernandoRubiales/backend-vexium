package com.projectFit.fit_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FitApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitApiApplication.class, args);
	}

}
