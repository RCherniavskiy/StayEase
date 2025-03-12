package org.example.bookingapplication.repository.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
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
class UserRepositoryTest {
    private static final String ADD_TEST_USER
            = "database/users/insert-users.sql";
    private static final String DELETE_DATA
            = "database/deletes/delete-users-accommodations-bookings-addresses.sql";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_USER));
        }
    }

    @Test
    @DisplayName("Find exist user by email")
    void findUserByEmail_findExistUserByEmail_ReturnUser() {
        String existEmail = "testUser1@testmail.com";
        Optional<User> userByEmail = userRepository.findUserByEmail(existEmail);
        assertTrue(userByEmail.isPresent());
        User user = userByEmail.get();
        assertEquals(existEmail, user.getEmail());
    }

    @Test
    @DisplayName("Find non exist user by email")
    void findUserByEmail_findNonExistUserByEmail_ReturnEmptyOptional() {
        String nonExistEmail = "nonExistUserEmail@i.com";
        Optional<User> userByEmail = userRepository.findUserByEmail(nonExistEmail);
        assertTrue(userByEmail.isEmpty());
    }

    @Test
    @DisplayName("Find non exist user by email ")
    void findUserByEmailWithRoles_findNonExistUserByEmail_ReturnEmptyOptional() {
        String nonExistEmail = "nonExistUserEmail@i.com";
        Optional<User> userByEmail = userRepository.findUserByEmailWithRoles(nonExistEmail);
        assertTrue(userByEmail.isEmpty());
    }

    @Test
    @DisplayName("Find non exist user by email ")
    void findUserByEmailWithRoles_findNonExistUserByEmail_ReturnUser() {
        String existEmail = "testUser1@testmail.com";
        Optional<User> userByEmail = userRepository.findUserByEmailWithRoles(existEmail);
        assertTrue(userByEmail.isPresent());
        User user = userByEmail.get();
        assertEquals(existEmail, user.getEmail());
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
