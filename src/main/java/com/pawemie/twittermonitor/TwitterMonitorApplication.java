package com.pawemie.twittermonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TwitterMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterMonitorApplication.class, args);
	}

}
