package com.bookingchatbot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * Data Transfer Object for booking change requests.
 */
public record BookingChangeRequestDto(
        @NotBlank(message = "Booking number is required")
        String bookingNumber,
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "New flight date is required")
        @Future(message = "New flight date must be in the future")
        LocalDate newFlightDate,
        @NotBlank(message = "New departure airport is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Departure airport must be a 3-letter uppercase IATA code")
        String newDepartureAirport,
        @NotBlank(message = "New arrival airport is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Arrival airport must be a 3-letter uppercase IATA code")
        String newArrivalAirport
) {
}

