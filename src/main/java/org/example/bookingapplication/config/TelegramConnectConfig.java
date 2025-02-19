package org.example.bookingapplication.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.telegram.BookingBot;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class TelegramConnectConfig {
    private final BookingBot bookingBot;

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bookingBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Telegram exception: " + e.getMessage());
        }
    }
}
