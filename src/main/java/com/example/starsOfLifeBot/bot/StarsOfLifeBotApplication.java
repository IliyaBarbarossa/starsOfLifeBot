package com.example.starsOfLifeBot.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories("com.example.starsOfLifeBot.baza")
@EntityScan("com.example.starsOfLifeBot.model")
@ComponentScan("com.example.starsOfLifeBot")
@EnableScheduling
public class StarsOfLifeBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarsOfLifeBotApplication.class, args);
	}

}
