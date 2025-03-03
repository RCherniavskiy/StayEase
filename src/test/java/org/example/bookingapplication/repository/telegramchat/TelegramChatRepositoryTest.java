package org.example.bookingapplication.repository.telegramchat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.example.bookingapplication.model.telegram.TelegramChat;
import org.example.bookingapplication.model.user.RoleType;
import org.example.bookingapplication.model.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TelegramChatRepositoryTest {
    private static final RoleType.RoleName CUSTOMER_ROLE_NAME = RoleType.RoleName.CUSTOMER;
    private static final RoleType.RoleName ADMIN_ROLE_NAME = RoleType.RoleName.ADMIN;
    private static final String ADD_TEST_CHATS
            = "database/telegramchats/insert-telegramchats.sql";
    private static final String ADD_TEST_USER
            = "database/users/insert-users.sql";
    private static final String DELETE_DATA
            = "database/deletes/delete-users-accommodations-bookings-addresses.sql";
    @Autowired
    private TelegramChatRepository telegramChatRepository;

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_USER));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_CHATS));
        }
    }

    @Test
    @DisplayName("Find telegram chat by email with non exist data")
    void getTelegramChatByUserEmail_findNonExitChat_ReturnEmptyOptional() {
        String nonExistEmail = "nonExistEmail@i.com";
        Optional<TelegramChat> telegramChatByUserEmail =
                telegramChatRepository.getTelegramChatByUserEmail(nonExistEmail);
        assertTrue(telegramChatByUserEmail.isEmpty());
    }

    @Test
    @DisplayName("Find telegram chat by email with exist data")
    @Transactional
    void getTelegramChatByUserEmail_findExitChat_ReturnChat() {
        String existEmail = "testUser1@testmail.com";
        Optional<TelegramChat> telegramChatByUserEmail =
                telegramChatRepository.getTelegramChatByUserEmail(existEmail);
        assertTrue(telegramChatByUserEmail.isPresent());
        User user = telegramChatByUserEmail.get().getUser();
        assertEquals(existEmail, user.getEmail());
    }

    @Test
    @DisplayName("Find telegram chats by admin role with exist data")
    @Transactional
    void findAllByUser_Roles_Name_findExistChats_ReturnListOfChat() {
        List<TelegramChat> allByUserRolesName =
                telegramChatRepository.findAllByUser_Roles_Name(ADMIN_ROLE_NAME);
        assertFalse(allByUserRolesName.isEmpty());
        assertEquals(1, allByUserRolesName.size());
        List<RoleType.RoleName> roleNameStream = allByUserRolesName.get(0)
                .getUser().getRoles().stream()
                .map(RoleType::getName)
                .toList();
        assertTrue(roleNameStream.contains(RoleType.RoleName.ADMIN));
    }

    @Test
    @DisplayName("Find telegram chats by customer role with exist data")
    @Transactional
    void findAllByUser_Roles_Name_findExitChats_ReturnListOfChats() {
        List<TelegramChat> allByUserRolesName =
                telegramChatRepository.findAllByUser_Roles_Name(CUSTOMER_ROLE_NAME);
        assertFalse(allByUserRolesName.isEmpty());
        assertEquals(2, allByUserRolesName.size());
        List<RoleType.RoleName> roleNamesFirst = allByUserRolesName.get(0)
                .getUser().getRoles().stream()
                .map(RoleType::getName)
                .toList();
        List<RoleType.RoleName> roleNamesSecond = allByUserRolesName.get(1)
                .getUser().getRoles().stream()
                .map(RoleType::getName)
                .toList();
        assertTrue(roleNamesFirst.contains(CUSTOMER_ROLE_NAME));
        assertTrue(roleNamesSecond.contains(CUSTOMER_ROLE_NAME));
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(DELETE_DATA));
        }
    }
}
