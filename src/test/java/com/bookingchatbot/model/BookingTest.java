package com.bookingchatbot.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Booking domain model.
 */
@DisplayName("Booking Model Tests")
class BookingTest {

    @Test
    @DisplayName("Should create booking with all fields")
    void shouldCreateBookingWithAllFields() {
        // Given
        Customer customer = new Customer("John", "Doe");
        LocalDate date = LocalDate.now().plusDays(5);

        // When
        Booking booking = new Booking(
                "BK101",
                date,
                customer,
                BookingStatus.CONFIRMED,
                "LAX",
                "JFK",
                BookingClass.ECONOMY
        );

        // Then
        assertThat(booking.getBookingNumber()).isEqualTo("BK101");
        assertThat(booking.getDate()).isEqualTo(date);
        assertThat(booking.getCustomer()).isEqualTo(customer);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.getDepartureAirport()).isEqualTo("LAX");
        assertThat(booking.getArrivalAirport()).isEqualTo("JFK");
        assertThat(booking.getBookingClass()).isEqualTo(BookingClass.ECONOMY);
    }

    @Test
    @DisplayName("Should update booking fields")
    void shouldUpdateBookingFields() {
        // Given
        Customer customer = new Customer("John", "Doe");
        Booking booking = new Booking(
                "BK101",
                LocalDate.now().plusDays(5),
                customer,
                BookingStatus.CONFIRMED,
                "LAX",
                "JFK",
                BookingClass.ECONOMY
        );

        // When
        LocalDate newDate = LocalDate.now().plusDays(10);
        booking.setDate(newDate);
        booking.setDepartureAirport("SFO");
        booking.setArrivalAirport("BOS");
        booking.setStatus(BookingStatus.CANCELLED);

        // Then
        assertThat(booking.getDate()).isEqualTo(newDate);
        assertThat(booking.getDepartureAirport()).isEqualTo("SFO");
        assertThat(booking.getArrivalAirport()).isEqualTo("BOS");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        // Given
        Customer customer = new Customer("John", "Doe");
        Booking booking1 = new Booking(
                "BK101",
                LocalDate.now().plusDays(5),
                customer,
                BookingStatus.CONFIRMED,
                "LAX",
                "JFK",
                BookingClass.ECONOMY
        );
        Booking booking2 = new Booking(
                "BK101",
                LocalDate.now().plusDays(7),
                customer,
                BookingStatus.CONFIRMED,
                "SFO",
                "BOS",
                BookingClass.BUSINESS
        );

        // Then
        assertThat(booking1).isEqualTo(booking2); // Same booking number
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        // Given
        Customer customer = new Customer("John", "Doe");
        Booking booking1 = new Booking(
                "BK101",
                LocalDate.now().plusDays(5),
                customer,
                BookingStatus.CONFIRMED,
                "LAX",
                "JFK",
                BookingClass.ECONOMY
        );
        Booking booking2 = new Booking(
                "BK101",
                LocalDate.now().plusDays(5),
                customer,
                BookingStatus.CONFIRMED,
                "LAX",
                "JFK",
                BookingClass.ECONOMY
        );

        // Then
        assertThat(booking1.hashCode()).isEqualTo(booking2.hashCode());
    }
}

