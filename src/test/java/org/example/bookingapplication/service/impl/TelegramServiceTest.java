package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import lombok.SneakyThrows;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.mapper.UserMapper;
import org.example.bookingapplication.model.telegram.TelegramChat;
import org.example.bookingapplication.model.user.User;
import org.example.bookingapplication.repository.telegramchat.TelegramChatRepository;
import org.example.bookingapplication.repository.user.UserRepository;
import org.example.bookingapplication.telegram.util.EmailTokenGenerator;
import org.example.bookingapplication.testutil.TelegramChatSampleUtil;
import org.example.bookingapplication.testutil.UserSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {
    @Mock
    private EmailTokenGenerator emailTokenGenerator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TelegramChatRepository telegramChatRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private TelegramServiceImpl telegramService;

    @Test
    @DisplayName("Get invite URL to telegram with valid data")
    void getUrl_getInviteToTelegramUrl_returnUrl() {
        // Given
        String exampleEmail = "email@i.com";
        String exampleToken = "token_1234";
        String expectedResult = "https://t.me/Book1App1Bot?start=" + exampleToken;

        when(emailTokenGenerator.encryptEmail("email@i.com")).thenReturn(exampleToken);

        // When
        String result = telegramService.getUrl(exampleEmail);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);

        verify(emailTokenGenerator, times(1)).encryptEmail(exampleEmail);
        verifyNoMoreInteractions(emailTokenGenerator);
    }

    @SneakyThrows
    @Test
    @DisplayName("Authorise telegram user with valid data")
    void auth_authIfUserAuthorize_returnResponseDto() {
        // Given
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserResponseDto sampleUserResponseDto = UserSampleUtil.createSampleUserResponseDto(1L);
        TelegramChat sampleTelegramChat = TelegramChatSampleUtil
                .getSampleTelegramChat(sampleUser, 1234L);
        String exampleToken = "token_1234";

        when(emailTokenGenerator.decryptEmail(exampleToken)).thenReturn(sampleUser.getEmail());
        when(telegramChatRepository.getTelegramChatByUserEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleTelegramChat));
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponseDto);

        // When
        UserResponseDto result = telegramService.auth(exampleToken, sampleTelegramChat.getChatId());

        // Then
        assertNotNull(result);
        assertEquals(sampleUserResponseDto, result);

        verify(emailTokenGenerator, times(1)).decryptEmail(exampleToken);
        verify(telegramChatRepository, times(1)).getTelegramChatByUserEmail(sampleUser.getEmail());
        verify(userMapper, times(1)).toResponseDto(sampleUser);

        verifyNoMoreInteractions(
                emailTokenGenerator,
                telegramChatRepository,
                userMapper
        );
    }

    @SneakyThrows
    @Test
    @DisplayName("Authorise telegram new user with valid data")
    void auth_authIfUserNonAuthorize_returnResponseDto() {
        // Given
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserResponseDto sampleUserResponseDto = UserSampleUtil.createSampleUserResponseDto(1L);
        TelegramChat sampleTelegramChat = TelegramChatSampleUtil
                .getSampleTelegramChat(sampleUser, 1234L);
        String exampleToken = "token_1234";

        when(emailTokenGenerator.decryptEmail(exampleToken)).thenReturn(sampleUser.getEmail());
        when(telegramChatRepository.getTelegramChatByUserEmail(sampleUser.getEmail()))
                .thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(telegramChatRepository.save(sampleTelegramChat)).thenReturn(sampleTelegramChat);
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponseDto);

        // When
        UserResponseDto result = telegramService.auth(exampleToken, sampleTelegramChat.getChatId());

        // Then
        assertNotNull(result);
        assertEquals(sampleUserResponseDto, result);

        verify(emailTokenGenerator, times(1)).decryptEmail(exampleToken);
        verify(telegramChatRepository, times(1)).getTelegramChatByUserEmail(sampleUser.getEmail());
        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verify(telegramChatRepository, times(1)).save(sampleTelegramChat);
        verify(userMapper, times(1)).toResponseDto(sampleUser);

        verifyNoMoreInteractions(
                emailTokenGenerator,
                telegramChatRepository,
                userMapper,
                userRepository,
                userMapper
        );
    }

    @SneakyThrows
    @Test
    @DisplayName("Authorise telegram new user with not valid decrypt email")
    void auth_authUserWithNotValidEmail_ThrowException() {
        // Given
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        String exampleToken = "token_1234";

        when(emailTokenGenerator.decryptEmail(exampleToken)).thenReturn(sampleUser.getEmail());
        when(telegramChatRepository.getTelegramChatByUserEmail(sampleUser.getEmail()))
                .thenReturn(Optional.empty());
        when(userRepository.findUserByEmail(sampleUser.getEmail())).thenReturn(Optional.empty());

        TelegramChat sampleTelegramChat = TelegramChatSampleUtil
                .getSampleTelegramChat(sampleUser, 1234L);

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> telegramService.auth(exampleToken, sampleTelegramChat.getChatId()));

        verify(emailTokenGenerator, times(1)).decryptEmail(exampleToken);
        verify(telegramChatRepository, times(1)).getTelegramChatByUserEmail(sampleUser.getEmail());
        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());

        verifyNoMoreInteractions(
                emailTokenGenerator,
                telegramChatRepository,
                userMapper,
                userRepository,
                userMapper
        );
    }
}
