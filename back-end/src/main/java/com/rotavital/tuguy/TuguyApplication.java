package com.rotavital.tuguy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TuguyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TuguyApplication.class, args);
	}

}
