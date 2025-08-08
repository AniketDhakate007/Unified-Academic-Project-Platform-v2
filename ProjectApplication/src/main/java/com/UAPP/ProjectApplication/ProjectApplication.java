package com.UAPP.ProjectApplication;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();


		// set system properties so Spring can read them via ${...}
		if (dotenv.get("MONGODB_URI") != null) {
			System.setProperty("MONGODB_URI", dotenv.get("MONGODB_URI"));
		}
		if (dotenv.get("JWT_SECRET") != null) {
			System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
			System.setProperty("JWT_SECRET_LEN", String.valueOf(dotenv.get("JWT_SECRET").length()));
		}
		if (dotenv.get("JWT_EXPIRATION_MS") != null) {
			System.setProperty("JWT_EXPIRATION_MS", dotenv.get("JWT_EXPIRATION_MS"));
		}
		if (dotenv.get("ADMIN_USERNAME") != null) {
			System.setProperty("ADMIN_USERNAME", dotenv.get("ADMIN_USERNAME"));
		}
		if (dotenv.get("ADMIN_PASSWORD") != null) {
			System.setProperty("ADMIN_PASSWORD", dotenv.get("ADMIN_PASSWORD"));
		}
		SpringApplication.run(ProjectApplication.class, args);
	}

}
