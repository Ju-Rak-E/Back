package com.example.spring.rmago;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RMaGoApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		String jwtSecret = dotenv.get("JWT_SECRET");
		String jwtExpiration = dotenv.get("JWT_EXPIRATTION_MS");

		if (jwtSecret != null) {
			System.setProperty("JWT_SECRET", jwtSecret);
		}
		if (jwtExpiration != null) {
			System.setProperty("JWT_EXPIRATTION_MS", jwtExpiration);
		}
		SpringApplication.run(RMaGoApplication.class, args);
	}

}
