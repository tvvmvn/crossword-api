package com.example.crossword_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄러 활성화 필수!
@SpringBootApplication
public class CrosswordApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrosswordApiApplication.class, args);
	}

}
