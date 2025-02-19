package org.example.bookingapplication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {
    @Value("${telegram.name}")
    private String botUserName;
    @Value("${telegram.token}")
    private String botToken;

    @Bean
    public String botUserName() {
        return botUserName;
    }

    @Bean
    public String botToken() {
        return botToken;
    }
}
