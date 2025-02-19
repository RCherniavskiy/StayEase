package org.example.bookingapplication.telegram;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.example.bookingapplication.exception.telegram.InvalidTelegramToken;
import org.example.bookingapplication.exception.telegram.TelegramBotException;
import org.example.bookingapplication.model.telegram.TelegramChat;
import org.example.bookingapplication.repository.telgeramchat.TelegramChatRepository;
import org.example.bookingapplication.service.TelegramService;
import org.example.bookingapplication.telegram.util.NotificationConfigurator;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class BookingBot extends TelegramLongPollingBot {
    private final TelegramService telegramService;
    private final TelegramChatRepository telegramChatRepository;
    private final String botUserName;
    private final String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (messageText.startsWith("/start")) {
                String token = getToken(messageText);
                try {
                    UserResponseDto userDto = telegramService.auth(token, chatId);
                    sendMessage(chatId,
                            NotificationConfigurator.bookingStarted(userDto.getFirstName()));
                } catch (Exception e) {
                    sendMessage(chatId, NotificationConfigurator.dontValidToken());
                    throw new InvalidTelegramToken("Cant decode token: " + token);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private String getToken(String message) {
        String[] tokens = message.split(" ");
        if (tokens.length == 2) {
            return tokens[1];
        }
        throw new InvalidTelegramToken("Massage dont have token");
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new TelegramBotException("Telegram bot exception: " + e.getMessage());
        }
    }

    public void sendMessage(String email, String text) {
        Optional<Long> optionalOfChatId = getChatId(email);
        if (optionalOfChatId.isPresent()) {
            Long chatId = optionalOfChatId.get();
            sendMessage(chatId, text);
        }
    }

    private Optional<Long> getChatId(String email) {
        Optional<TelegramChat> optionalOfChat
                = telegramChatRepository.getTelegramChatByUserEmail(email);
        return optionalOfChat.flatMap(telegramChat -> telegramChat.getChatId().describeConstable());
    }
}
