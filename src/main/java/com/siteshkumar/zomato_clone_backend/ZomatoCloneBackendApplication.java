package com.siteshkumar.zomato_clone_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ZomatoCloneBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZomatoCloneBackendApplication.class, args);
		System.out.println("This project is developed by Sitesh Kumar");
	}
}
