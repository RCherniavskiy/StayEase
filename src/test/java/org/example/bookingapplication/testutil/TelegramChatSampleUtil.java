package org.example.bookingapplication.testutil;

import org.example.bookingapplication.model.telegram.TelegramChat;
import org.example.bookingapplication.model.user.User;

public class TelegramChatSampleUtil {
    public static TelegramChat getSampleTelegramChat(User user, Long chatId) {
        TelegramChat telegramChat = new TelegramChat();
        telegramChat.setUser(user);
        telegramChat.setChatId(chatId);
        return telegramChat;
    }
}
