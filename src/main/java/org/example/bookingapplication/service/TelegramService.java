package org.example.bookingapplication.service;

import org.example.bookingapplication.dto.users.response.UserResponseDto;

public interface TelegramService {
    String getUrl(String email);

    UserResponseDto auth(String token, Long chatId) throws Exception;
}
