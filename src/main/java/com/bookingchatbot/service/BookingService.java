package com.bookingchatbot.service;

import com.bookingchatbot.dto.BookingDto;
import com.bookingchatbot.exception.BookingNotFoundException;
import com.bookingchatbot.exception.BookingPolicyViolationException;
import com.bookingchatbot.model.Booking;
import com.bookingchatbot.model.BookingStatus;
import com.bookingchatbot.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service layer for booking operations.
 * Handles business logic and validation.
 */
@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private static final Pattern AIRPORT_CODE_PATTERN = Pattern.compile("^[A-Z]{3}$");
    
    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    public List<BookingDto> getAllBookings() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public BookingDto getBookingDetails(String bookingNumber, String firstName, String lastName) {
        Booking booking = findBooking(bookingNumber, firstName, lastName);
        return toDto(booking);
    }

    public BookingDto changeBooking(
            String bookingNumber,
            String firstName,
            String lastName,
            LocalDate newFlightDate,
            String newDepartureAirport,
            String newArrivalAirport) {
        
        Booking booking = findBooking(bookingNumber, firstName, lastName);
        
        // Validate that changes are allowed
        ensureBookingIsActive(booking);
        if (booking.getDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new BookingPolicyViolationException(
                "Booking cannot be changed within 24 hours of the flight date."
            );
        }
        ensureNewFlightDateIsValid(newFlightDate);
        String normalizedDeparture = normalizeAirportCode(newDepartureAirport, "New departure airport");
        String normalizedArrival = normalizeAirportCode(newArrivalAirport, "New arrival airport");

        booking.setDate(newFlightDate);
        booking.setDepartureAirport(normalizedDeparture);
        booking.setArrivalAirport(normalizedArrival);
        
        repository.save(booking);
        logger.info("Booking {} changed successfully", bookingNumber);
        
        return toDto(booking);
    }

    public BookingDto cancelBooking(String bookingNumber, String firstName, String lastName) {
        Booking booking = findBooking(bookingNumber, firstName, lastName);
        
        // Validate that cancellation is allowed
        ensureBookingIsActive(booking);
        if (booking.getDate().isBefore(LocalDate.now().plusDays(2))) {
            throw new BookingPolicyViolationException(
                "Booking cannot be cancelled within 48 hours of the flight date."
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        repository.save(booking);
        logger.info("Booking {} cancelled successfully", bookingNumber);
        
        return toDto(booking);
    }

    private Booking findBooking(String bookingNumber, String firstName, String lastName) {
        return repository.findByBookingNumberAndCustomer(bookingNumber, firstName, lastName)
                .orElseThrow(() -> new BookingNotFoundException(
                    "Booking not found for booking number: " + bookingNumber
                ));
    }

    private void ensureBookingIsActive(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingPolicyViolationException("Booking has already been cancelled.");
        }
    }

    private void ensureNewFlightDateIsValid(LocalDate newFlightDate) {
        if (newFlightDate == null) {
            throw new BookingPolicyViolationException("New flight date is required.");
        }
        if (newFlightDate.isBefore(LocalDate.now().plusDays(1))) {
            throw new BookingPolicyViolationException(
                    "New flight date must be at least 24 hours in the future."
            );
        }
    }

    private String normalizeAirportCode(String airport, String fieldName) {
        if (airport == null) {
            throw new BookingPolicyViolationException(fieldName + " is required.");
        }
        String trimmed = airport.trim().toUpperCase();
        if (!AIRPORT_CODE_PATTERN.matcher(trimmed).matches()) {
            throw new BookingPolicyViolationException(
                    fieldName + " must be a 3-letter uppercase IATA code."
            );
        }
        return trimmed;
    }

    private BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getBookingNumber(),
                booking.getCustomer().getFirstName(),
                booking.getCustomer().getLastName(),
                booking.getDate(),
                booking.getStatus(),
                booking.getDepartureAirport(),
                booking.getArrivalAirport(),
                booking.getBookingClass().toString()
        );
    }
}

