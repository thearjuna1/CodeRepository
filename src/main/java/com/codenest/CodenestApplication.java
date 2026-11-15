package com.codenest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(
		basePackages = "com.codenest.repository",
		considerNestedRepositories = true
)
public class CodenestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodenestApplication.class, args);
	}
}