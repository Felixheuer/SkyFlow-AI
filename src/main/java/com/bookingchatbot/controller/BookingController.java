package com.bookingchatbot.controller;

import com.bookingchatbot.dto.BookingChangeRequestDto;
import com.bookingchatbot.dto.BookingDto;
import com.bookingchatbot.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for booking operations.
 */
@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{bookingNumber}")
    public ResponseEntity<BookingDto> getBooking(
            @NotBlank @PathVariable String bookingNumber,
            @NotBlank @RequestParam String firstName,
            @NotBlank @RequestParam String lastName) {
        return ResponseEntity.ok(
                bookingService.getBookingDetails(bookingNumber, firstName, lastName)
        );
    }

    @PutMapping("/{bookingNumber}")
    public ResponseEntity<BookingDto> updateBooking(
            @NotBlank @PathVariable String bookingNumber,
            @Valid @RequestBody BookingChangeRequestDto request) {
        return ResponseEntity.ok(bookingService.changeBooking(
                bookingNumber,
                request.firstName(),
                request.lastName(),
                request.newFlightDate(),
                request.newDepartureAirport(),
                request.newArrivalAirport()
        ));
    }

    @DeleteMapping("/{bookingNumber}")
    public ResponseEntity<BookingDto> cancelBooking(
            @NotBlank @PathVariable String bookingNumber,
            @NotBlank @RequestParam String firstName,
            @NotBlank @RequestParam String lastName) {
        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingNumber, firstName, lastName)
        );
    }
}

