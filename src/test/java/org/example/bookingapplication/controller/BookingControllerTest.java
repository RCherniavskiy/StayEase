package org.example.bookingapplication.controller;

import static org.example.bookingapplication.testutil.BookingControllerDtoUtil.getFirstBookingDtoFromDb;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.example.bookingapplication.dto.bookings.request.BookingRequestDto;
import org.example.bookingapplication.dto.bookings.responce.BookingDto;
import org.example.bookingapplication.dto.bookingstatus.response.BookingStatusDto;
import org.example.bookingapplication.model.booking.BookingStatus;
import org.example.bookingapplication.testutil.BookingControllerDtoUtil;
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
class BookingControllerTest {
    private static final String ADD_THREE_ACCOMMODATIONS
            = "database/accommodations/insert-accommodations.sql";
    private static final String ADD_TEST_USER
            = "database/users/insert-users.sql";
    private static final String ADD_TEST_BOOKING
            = "database/bookings/insert-booking.sql";
    private static final String DELETE_DATA
            = "database/deletes/delete-users-accommodations-bookings-addresses.sql";
    private static final String BASE_URL = "/bookings";
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
                    new ClassPathResource(ADD_THREE_ACCOMMODATIONS));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_USER));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(ADD_TEST_BOOKING));
        }
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"ADMIN", "CUSTOMER"})
    @DisplayName("Save booking with dont valid check out date")
    void save_saveBookingWithDontValidCheckDate_isBadRequest() throws Exception {
        BookingRequestDto requestDto = BookingControllerDtoUtil.getRequestDto();
        requestDto.getCheckDates().setCheckOutDate(LocalDate.now().minusDays(10L));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"ADMIN", "CUSTOMER"})
    @DisplayName("Save booking with dont valid dates")
    void save_saveBookingWithDontValidDates_isConflict() throws Exception {
        BookingRequestDto requestDto = BookingControllerDtoUtil.getRequestDto();
        requestDto.getCheckDates().setCheckInDate(LocalDate.of(2034, 11,20));
        requestDto.getCheckDates().setCheckOutDate(LocalDate.of(2034, 12,1));
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"ADMIN", "CUSTOMER"})
    @DisplayName("Get user bookings with exist data")
    void getUserBookings_getBookingWithExistData_isOk() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + SLASH + "my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookingDto> resultDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(2, resultDtoList.size());
    }

    @Test
    @WithMockUser(username = "testUser2@testmail.com", authorities = {"ADMIN", "CUSTOMER"})
    @DisplayName("Get user bookings with non exist data")
    void getUserBookings_getBookingWithNonExistData_isOk() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + SLASH + "my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookingDto> resultDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(0, resultDtoList.size());
    }

    @Test
    @WithMockUser(username = "testUser2@testmail.com", authorities = {"CUSTOMER"})
    @DisplayName("Get user bookings with exist data")
    void getAllBookings_getBookingWithCustomerRole_isForbidden() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + SLASH + "all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "testUser2@testmail.com", authorities = {"ADMIN", "CUSTOMER"})
    @DisplayName("Get user bookings with non exist data")
    void getAllBookings_getAllBookingWithExistData_isOk() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + SLASH + "all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<BookingDto> resultDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(2, resultDtoList.size());
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"CUSTOMER"})
    @DisplayName("Get booking by id with valid data")
    void getBookingsById_getBookingByIdWithValidData_isOk() throws Exception {
        Long id = 1L;
        BookingDto firstBookingDtoFromDb = getFirstBookingDtoFromDb();

        MvcResult result = mockMvc.perform(get(BASE_URL + SLASH + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDto.class);
        assertNotNull(resultDto);
        assertEquals(firstBookingDtoFromDb, resultDto);
    }

    @Test
    @WithMockUser(username = "testUser1@testmail.com", authorities = {"CUSTOMER"})
    @DisplayName("Cancel booking by id with valid data")
    void cancelBookingById_cancelBookingByIdWithValidData_isOk() throws Exception {
        BookingStatusDto canceledBookingStatusDto = new BookingStatusDto();
        canceledBookingStatusDto.setId(3L);
        canceledBookingStatusDto.setName(BookingStatus.BookingStatusName.CANCELED.name());

        BookingDto firstBookingDtoFromDb = getFirstBookingDtoFromDb();
        firstBookingDtoFromDb.setStatus(canceledBookingStatusDto);

        MvcResult result = mockMvc.perform(delete(BASE_URL + SLASH
                        + firstBookingDtoFromDb.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDto.class);
        assertNotNull(resultDto);
        assertEquals(firstBookingDtoFromDb, resultDto);
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
