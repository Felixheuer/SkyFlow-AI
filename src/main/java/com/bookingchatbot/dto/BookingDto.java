package com.bookingchatbot.dto;

import com.bookingchatbot.model.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Data Transfer Object for Booking information.
 * Used for API responses and frontend communication.
 */
public record BookingDto(
        String bookingNumber,
        String firstName,
        String lastName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        BookingStatus bookingStatus,
        String from,
        String to,
        String bookingClass
) {
}

