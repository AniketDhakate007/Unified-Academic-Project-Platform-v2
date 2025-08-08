package com.UAPP.ProjectApplication.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadEnv() throws IOException {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String mongoUri = dotenv.get("MONGODB_URI");
        if (mongoUri != null) {
            System.setProperty("spring.data.mongodb.uri", mongoUri);
        } else {
            throw new IllegalStateException("MONGODB_URI not found in .env");
        }
    }
}
