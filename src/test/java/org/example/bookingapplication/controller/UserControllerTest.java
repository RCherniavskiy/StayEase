package org.example.bookingapplication.controller;

import static org.example.bookingapplication.testutil.UserControllerDtoUtil.getUserResponseDtoFromDb;
import static org.example.bookingapplication.testutil.UserControllerDtoUtil.getUserUpdateInfoRequestDto;
import static org.example.bookingapplication.testutil.UserControllerDtoUtil.getUserUpdatePasswordRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.example.bookingapplication.dto.users.request.UserUpdateInfoRequestDto;
import org.example.bookingapplication.dto.users.request.UserUpdatePasswordRequestDto;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    private static final String ADD_TEST_USER
            = "database/users/insert-users.sql";
    private static final String DELETE_DATA
            = "database/deletes/delete-users-accommodations-bookings-addresses.sql";
    private static final String BASE_URL = "/users";
    private static final String SLASH = "/";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) {
        teardown(dataSource);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_USER));
        }
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"CUSTOMER", "ADMIN"})
    @DisplayName("Get user info with valid data")
    void getInfo_getUserInfoWithValidData_isOk() throws Exception {
        UserResponseDto responseDtoFromDb = getUserResponseDtoFromDb();
        MvcResult result = mockMvc.perform(get(BASE_URL + SLASH + "me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);
        assertNotNull(resultDto);
        assertEquals(responseDtoFromDb, resultDto);
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"CUSTOMER", "ADMIN"})
    @DisplayName("Update user info with valid data")
    void updateInfo_updateUserInfoWithValidData_isOk() throws Exception {
        UserUpdateInfoRequestDto userUpdateInfoRequestDto = getUserUpdateInfoRequestDto();

        UserResponseDto responseDtoFromDb = getUserResponseDtoFromDb();
        responseDtoFromDb.setEmail(userUpdateInfoRequestDto.getEmail());
        responseDtoFromDb.setFirstName(userUpdateInfoRequestDto.getFirstName());
        responseDtoFromDb.setLastName(userUpdateInfoRequestDto.getLastName());

        String jsonRequest = objectMapper.writeValueAsString(userUpdateInfoRequestDto);

        MvcResult result = mockMvc.perform(patch(BASE_URL + SLASH + "me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);
        assertNotNull(resultDto);
        assertEquals(responseDtoFromDb, resultDto);
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"CUSTOMER", "ADMIN"})
    @DisplayName("Update user password with valid data")
    void updatePassword_updateUserPasswordWithValidData_isOk() throws Exception {
        UserUpdatePasswordRequestDto userUpdatePasswordRequestDto
                = getUserUpdatePasswordRequestDto();

        String jsonRequest = objectMapper.writeValueAsString(userUpdatePasswordRequestDto);
        MvcResult result = mockMvc.perform(put(BASE_URL + SLASH + "me" + SLASH + "password")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"CUSTOMER", "ADMIN"})
    @DisplayName("Update user password with not valid data")
    void updatePassword_updateUserPasswordWithNotValidOld_isOk() throws Exception {
        UserUpdatePasswordRequestDto userUpdatePasswordRequestDto =
                getUserUpdatePasswordRequestDto();
        userUpdatePasswordRequestDto.setOldPassword("NOTvalidpassword@200");
        String jsonRequest = objectMapper.writeValueAsString(userUpdatePasswordRequestDto);
        MvcResult result = mockMvc.perform(put(BASE_URL + SLASH + "me" + SLASH + "password")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(DELETE_DATA));
        }
    }
}
