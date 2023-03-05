package com.iamjunhyeok.petSitterAndWalker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class PetSitterAndWalkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetSitterAndWalkerApplication.class, args);
	}

}
