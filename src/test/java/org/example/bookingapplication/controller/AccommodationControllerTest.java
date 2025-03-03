package org.example.bookingapplication.controller;

import static org.example.bookingapplication.testutil.AccommodationControllerDtoUtil.getAccommodationRequestDto;
import static org.example.bookingapplication.testutil.AccommodationControllerDtoUtil.getFirstAccommodationResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.example.bookingapplication.dto.accommodations.request.AccommodationRequestDto;
import org.example.bookingapplication.dto.accommodations.response.AccommodationDto;
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
class AccommodationControllerTest {
    private static final String ADD_THREE_ACCOMMODATIONS
            = "database/accommodations/insert-accommodations.sql";
    private static final String ADD_TEST_USER
            = "database/users/insert-users.sql";
    private static final String ADD_TEST_BOOKING
            = "database/bookings/insert-booking.sql";
    private static final String DELETE_DATA
            = "database/deletes/delete-users-accommodations-bookings-addresses.sql";
    private static final String BASE_URL = "/accommodations";
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
    @DisplayName("Find accommodation with non exist data")
    void findAll_findAllAccommodationsWithNotExistData_emptyList(
            @Autowired DataSource dataSource) throws Exception {
        teardown(dataSource);
        MvcResult result = mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<AccommodationDto> accommodationDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {}
        );

        assertNotNull(accommodationDtoList);
        assertEquals(0, accommodationDtoList.size());
    }

    @Test
    @DisplayName("Find accommodation with exist data")
    void findAll_findAllAccommodationsWithExistData_listAccommodations() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<AccommodationDto> accommodationDtoList = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {}
        );

        assertNotNull(accommodationDtoList);
        assertEquals(3, accommodationDtoList.size());
    }

    @Test
    @DisplayName("Get accommodation by id with exist data")
    void getById_getAccommodationsByIdWithExistData_Accommodations() throws Exception {
        Long id = 1L;
        AccommodationDto accommodationResponseDto = getFirstAccommodationResponseDto();
        MvcResult result = mockMvc.perform(get(BASE_URL + SLASH + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        AccommodationDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);

        assertNotNull(resultDto);
        assertEquals(accommodationResponseDto, resultDto);
    }

    @Test
    @DisplayName("Get accommodation by id with non exist data")
    void getById_getAccommodationsByIdWitNotValidId_isNotFound() throws Exception {
        Long id = -1L;
        MvcResult result = mockMvc.perform(get(BASE_URL + SLASH + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ADMIN"})
    @DisplayName("Delete accommodation by id with exist data")
    void delete_deleteAccommodationsByIdWithExistData_isNoContent() throws Exception {
        Long id = 1L;
        MvcResult result = mockMvc.perform(delete(BASE_URL + SLASH + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "adm@i.com", authorities = {"ADMIN"})
    @DisplayName("Delete accommodation by id with non exist data")
    void delete_deleteAccommodationsByIdWithNonExistData_isNotFound() throws Exception {
        Long id = -1L;
        MvcResult result = mockMvc.perform(delete(BASE_URL + SLASH + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "adm@i.com", authorities = {"ADMIN"})
    @DisplayName("Update accommodation by id with valid data")
    void update_updateAccommodationsWithValidData_Accommodations() throws Exception {
        AccommodationRequestDto accommodationRequestDto = getAccommodationRequestDto();
        accommodationRequestDto.setDailyRate(BigDecimal.valueOf(150L));
        accommodationRequestDto.getAddressDto().setCity("Odessa");
        String jsonRequest = objectMapper.writeValueAsString(accommodationRequestDto);

        AccommodationDto accommodationResponseDto = getFirstAccommodationResponseDto();
        accommodationResponseDto.setDailyRate(BigDecimal.valueOf(150L));
        accommodationResponseDto.getAddress().setCity("Odessa");

        Long id = 1L;

        MvcResult result = mockMvc.perform(put(BASE_URL + SLASH + id)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationDto resultDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);

        assertNotNull(resultDto);
        assertEquals(accommodationResponseDto, resultDto);
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
