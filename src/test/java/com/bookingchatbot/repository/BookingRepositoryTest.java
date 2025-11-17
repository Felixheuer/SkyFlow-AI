package com.bookingchatbot.repository;

import com.bookingchatbot.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for BookingRepository.
 */
@DisplayName("Booking Repository Tests")
class BookingRepositoryTest {

    private BookingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new BookingRepository();
    }

    @Test
    @DisplayName("Should initialize with demo data")
    void shouldInitializeWithDemoData() {
        // When
        List<Booking> bookings = repository.findAll();

        // Then
        assertThat(bookings).hasSize(5);
        assertThat(bookings).allMatch(b -> b.getBookingNumber() != null);
        assertThat(bookings).allMatch(b -> b.getCustomer() != null);
    }

    @Test
    @DisplayName("Should find booking by number and customer name")
    void shouldFindBookingByNumberAndCustomerName() {
        // Given
        List<Booking> allBookings = repository.findAll();
        Booking firstBooking = allBookings.get(0);

        // When
        Optional<Booking> result = repository.findByBookingNumberAndCustomer(
                firstBooking.getBookingNumber(),
                firstBooking.getCustomer().getFirstName(),
                firstBooking.getCustomer().getLastName()
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getBookingNumber()).isEqualTo(firstBooking.getBookingNumber());
    }

    @Test
    @DisplayName("Should return empty when booking not found")
    void shouldReturnEmptyWhenBookingNotFound() {
        // When
        Optional<Booking> result = repository.findByBookingNumberAndCustomer(
                "BK999",
                "NonExistent",
                "Person"
        );

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should be case insensitive when finding bookings")
    void shouldBeCaseInsensitiveWhenFindingBookings() {
        // Given
        List<Booking> allBookings = repository.findAll();
        Booking firstBooking = allBookings.get(0);

        // When
        Optional<Booking> result = repository.findByBookingNumberAndCustomer(
                firstBooking.getBookingNumber().toLowerCase(),
                firstBooking.getCustomer().getFirstName().toUpperCase(),
                firstBooking.getCustomer().getLastName().toLowerCase()
        );

        // Then
        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Should save new booking")
    void shouldSaveNewBooking() {
        // Given
        List<Booking> initialBookings = repository.findAll();
        int initialSize = initialBookings.size();
        Booking firstBooking = initialBookings.get(0);
        
        // Create a modified version
        Booking modifiedBooking = new Booking(
                firstBooking.getBookingNumber(),
                firstBooking.getDate().plusDays(1),
                firstBooking.getCustomer(),
                firstBooking.getStatus(),
                "NEW",
                "AIRPORT",
                firstBooking.getBookingClass()
        );

        // When
        repository.save(modifiedBooking);

        // Then
        List<Booking> afterSave = repository.findAll();
        assertThat(afterSave).hasSize(initialSize); // Should replace, not add
        
        Optional<Booking> saved = repository.findByBookingNumberAndCustomer(
                firstBooking.getBookingNumber(),
                firstBooking.getCustomer().getFirstName(),
                firstBooking.getCustomer().getLastName()
        );
        
        assertThat(saved).isPresent();
        assertThat(saved.get().getDepartureAirport()).isEqualTo("NEW");
        assertThat(saved.get().getArrivalAirport()).isEqualTo("AIRPORT");
    }
}

