package org.example.bookingapplication.telegram.notification;

import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.telegram.BookingBot;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {
    private final BookingBot bookingBot;

    @Async
    public void sendMessageAsync(Long chatId, String text) {
        bookingBot.sendMessage(chatId, text);
    }

    @Async
    public void sendMessageAsync(String email, String text) {
        bookingBot.sendMessage(email, text);
    }
}
