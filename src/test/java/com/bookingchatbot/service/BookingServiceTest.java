package com.bookingchatbot.service;

import com.bookingchatbot.dto.BookingDto;
import com.bookingchatbot.exception.BookingNotFoundException;
import com.bookingchatbot.exception.BookingPolicyViolationException;
import com.bookingchatbot.model.Booking;
import com.bookingchatbot.model.BookingClass;
import com.bookingchatbot.model.BookingStatus;
import com.bookingchatbot.model.Customer;
import com.bookingchatbot.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService.
 * Tests business logic and validation rules.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Booking Service Tests")
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John", "Doe");
        testBooking = new Booking(
                "BK101",
                LocalDate.now().plusDays(5),
                testCustomer,
                BookingStatus.CONFIRMED,
                "LAX",
                "JFK",
                BookingClass.ECONOMY
        );
    }

    @Test
    @DisplayName("Should get all bookings")
    void shouldGetAllBookings() {
        // Given
        List<Booking> bookings = List.of(testBooking);
        when(repository.findAll()).thenReturn(bookings);

        // When
        List<BookingDto> result = bookingService.getAllBookings();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).bookingNumber()).isEqualTo("BK101");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should get booking details when booking exists")
    void shouldGetBookingDetailsWhenBookingExists() {
        // Given
        when(repository.findByBookingNumberAndCustomer("BK101", "John", "Doe"))
                .thenReturn(Optional.of(testBooking));

        // When
        BookingDto result = bookingService.getBookingDetails("BK101", "John", "Doe");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.bookingNumber()).isEqualTo("BK101");
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.from()).isEqualTo("LAX");
        assertThat(result.to()).isEqualTo("JFK");
    }

    @Test
    @DisplayName("Should throw exception when booking not found")
    void shouldThrowExceptionWhenBookingNotFound() {
        // Given
        when(repository.findByBookingNumberAndCustomer(anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookingService.getBookingDetails("BK999", "Jane", "Smith"))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    @DisplayName("Should change booking when more than 24 hours before flight")
    void shouldChangeBookingWhenAllowed() {
        // Given
        LocalDate newDate = LocalDate.now().plusDays(10);
        when(repository.findByBookingNumberAndCustomer("BK101", "John", "Doe"))
                .thenReturn(Optional.of(testBooking));
        when(repository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        BookingDto result = bookingService.changeBooking(
                "BK101", "John", "Doe",
                newDate, "SFO", "BOS"
        );

        // Then
        assertThat(result).isNotNull();
        verify(repository).save(any(Booking.class));
        assertThat(testBooking.getDate()).isEqualTo(newDate);
        assertThat(testBooking.getDepartureAirport()).isEqualTo("SFO");
        assertThat(testBooking.getArrivalAirport()).isEqualTo("BOS");
    }

    @Test
    @DisplayName("Should not allow booking change within 24 hours")
    void shouldNotAllowBookingChangeWithin24Hours() {
        // Given - Booking is today (within 24 hours)
        Booking nearBooking = new Booking(
                "BK102",
                LocalDate.now(),
                testCustomer,
                BookingStatus.CONFIRMED,
                "LAX",
                "JFK",
                BookingClass.ECONOMY
        );
        when(repository.findByBookingNumberAndCustomer("BK102", "John", "Doe"))
                .thenReturn(Optional.of(nearBooking));

        // When & Then
        assertThatThrownBy(() -> bookingService.changeBooking(
                "BK102", "John", "Doe",
                LocalDate.now().plusDays(2), "SFO", "BOS"
        ))
                .isInstanceOf(BookingPolicyViolationException.class)
                .hasMessageContaining("24 hours");
    }

    @Test
    @DisplayName("Should cancel booking when more than 48 hours before flight")
    void shouldCancelBookingWhenAllowed() {
        // Given
        when(repository.findByBookingNumberAndCustomer("BK101", "John", "Doe"))
                .thenReturn(Optional.of(testBooking));
        when(repository.save(any(Booking.class))).thenReturn(testBooking);

        // When
        BookingDto result = bookingService.cancelBooking("BK101", "John", "Doe");

        // Then
        assertThat(result).isNotNull();
        verify(repository).save(any(Booking.class));
        assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should not allow booking cancellation within 48 hours")
    void shouldNotAllowBookingCancellationWithin48Hours() {
        // Given - Booking is tomorrow (within 48 hours)
        Booking nearBooking = new Booking(
                "BK103",
                LocalDate.now().plusDays(1),
                testCustomer,
                BookingStatus.CONFIRMED,
                "LAX",
                "JFK",
                BookingClass.ECONOMY
        );
        when(repository.findByBookingNumberAndCustomer("BK103", "John", "Doe"))
                .thenReturn(Optional.of(nearBooking));

        // When & Then
        assertThatThrownBy(() -> bookingService.cancelBooking("BK103", "John", "Doe"))
                .isInstanceOf(BookingPolicyViolationException.class)
                .hasMessageContaining("48 hours");
    }

    @Test
    @DisplayName("Should convert booking to DTO correctly")
    void shouldConvertBookingToDtoCorrectly() {
        // Given
        when(repository.findByBookingNumberAndCustomer("BK101", "John", "Doe"))
                .thenReturn(Optional.of(testBooking));

        // When
        BookingDto dto = bookingService.getBookingDetails("BK101", "John", "Doe");

        // Then
        assertThat(dto.bookingNumber()).isEqualTo(testBooking.getBookingNumber());
        assertThat(dto.firstName()).isEqualTo(testBooking.getCustomer().getFirstName());
        assertThat(dto.lastName()).isEqualTo(testBooking.getCustomer().getLastName());
        assertThat(dto.date()).isEqualTo(testBooking.getDate());
        assertThat(dto.bookingStatus()).isEqualTo(testBooking.getStatus());
        assertThat(dto.from()).isEqualTo(testBooking.getDepartureAirport());
        assertThat(dto.to()).isEqualTo(testBooking.getArrivalAirport());
        assertThat(dto.bookingClass()).isEqualTo(testBooking.getBookingClass().toString());
    }
}

