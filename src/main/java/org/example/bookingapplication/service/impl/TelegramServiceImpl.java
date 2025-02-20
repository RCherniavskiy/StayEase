package org.example.bookingapplication.service.impl;

import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.mapper.UserMapper;
import org.example.bookingapplication.model.telegram.TelegramChat;
import org.example.bookingapplication.model.user.User;
import org.example.bookingapplication.repository.telegramchat.TelegramChatRepository;
import org.example.bookingapplication.repository.user.UserRepository;
import org.example.bookingapplication.service.TelegramService;
import org.example.bookingapplication.telegram.util.EmailTokenGenerator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {
    private static final String URL = "https://t.me/Book1App1Bot?start=";
    private final EmailTokenGenerator emailTokenGenerator;
    private final UserRepository userRepository;
    private final TelegramChatRepository telegramChatRepository;
    private final UserMapper userMapper;

    @Override
    public String getUrl(String email) {
        String token = emailTokenGenerator.encryptEmail(email);
        return URL + token;
    }

    @Override
    @Transactional
    public UserResponseDto auth(String token, Long chatId) throws Exception {
        String email = emailTokenGenerator.decryptEmail(token);
        Optional<UserResponseDto> optionalUser = ifUserAuthorize(email);
        return optionalUser.orElseGet(() -> createUserAndChat(email, chatId));
    }

    private Optional<UserResponseDto> ifUserAuthorize(String email) {
        Optional<TelegramChat> chat =
                telegramChatRepository.getTelegramChatByUserEmail(email);
        if (chat.isPresent()) {
            User user = chat.get().getUser();
            return Optional.of(userMapper.toResponseDto(user));
        }
        return Optional.empty();
    }

    private UserResponseDto createUserAndChat(String email, Long chatId) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Cannot find user with email: " + email));
        TelegramChat telegramChat = new TelegramChat();
        telegramChat.setUser(user);
        telegramChat.setChatId(chatId);
        telegramChatRepository.save(telegramChat);
        return userMapper.toResponseDto(user);
    }
}
