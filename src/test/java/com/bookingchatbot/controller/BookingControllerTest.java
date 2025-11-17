package com.bookingchatbot.controller;

import com.bookingchatbot.dto.BookingDto;
import com.bookingchatbot.model.BookingStatus;
import com.bookingchatbot.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BookingController.
 */
@WebMvcTest(BookingController.class)
@DisplayName("Booking Controller Tests")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    @DisplayName("GET /api/bookings should return all bookings")
    void shouldReturnAllBookings() throws Exception {
        // Given
        BookingDto booking1 = new BookingDto(
                "BK101", "John", "Doe",
                LocalDate.now().plusDays(5),
                BookingStatus.CONFIRMED,
                "LAX", "JFK", "ECONOMY"
        );
        BookingDto booking2 = new BookingDto(
                "BK102", "Jane", "Smith",
                LocalDate.now().plusDays(7),
                BookingStatus.CONFIRMED,
                "SFO", "BOS", "BUSINESS"
        );
        
        when(bookingService.getAllBookings()).thenReturn(List.of(booking1, booking2));

        // When & Then
        mockMvc.perform(get("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].bookingNumber").value("BK101"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].bookingNumber").value("BK102"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    @DisplayName("GET /api/bookings/{bookingNumber} should return specific booking")
    void shouldReturnSpecificBooking() throws Exception {
        // Given
        BookingDto booking = new BookingDto(
                "BK101", "John", "Doe",
                LocalDate.now().plusDays(5),
                BookingStatus.CONFIRMED,
                "LAX", "JFK", "ECONOMY"
        );
        
        when(bookingService.getBookingDetails("BK101", "John", "Doe"))
                .thenReturn(booking);

        // When & Then
        mockMvc.perform(get("/api/bookings/BK101")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingNumber").value("BK101"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.from").value("LAX"))
                .andExpect(jsonPath("$.to").value("JFK"));
    }

    @Test
    @DisplayName("GET /api/bookings should return empty array when no bookings")
    void shouldReturnEmptyArrayWhenNoBookings() throws Exception {
        // Given
        when(bookingService.getAllBookings()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}

