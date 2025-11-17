package com.bookingchatbot.ai;

import com.bookingchatbot.dto.BookingDto;
import com.bookingchatbot.service.BookingService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Tools that the AI assistant can use to interact with the booking system.
 */
@Component
public class AiTools {

    private final BookingService bookingService;

    public AiTools(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Tool("""
            Retrieves information about an existing booking,
            such as the flight date, booking status, departure and arrival airports, and booking class.
            """)
    public BookingDto getBookingDetails(
            @P("Booking number") String bookingNumber,
            @P("Customer first name") String firstName,
            @P("Customer last name") String lastName) {
        return bookingService.getBookingDetails(bookingNumber, firstName, lastName);
    }

    @Tool("""
            Modifies an existing booking.
            This includes making changes to the flight date, departure airport, and arrival airport.
            Changes are only allowed up to 24 hours before the flight.
            """)
    public BookingDto changeBooking(
            @P("Booking number") String bookingNumber,
            @P("Customer first name") String firstName,
            @P("Customer last name") String lastName,
            @P("New flight date") LocalDate newFlightDate,
            @P("3-letter code for new departure airport") String newDepartureAirport,
            @P("3-letter code for new arrival airport") String newArrivalAirport) {
        return bookingService.changeBooking(
                bookingNumber, firstName, lastName,
                newFlightDate, newDepartureAirport, newArrivalAirport
        );
    }

    @Tool("""
            Cancels an existing booking.
            Cancellation is only allowed up to 48 hours before the flight.
            """)
    public BookingDto cancelBooking(
            @P("Booking number") String bookingNumber,
            @P("Customer first name") String firstName,
            @P("Customer last name") String lastName) {
        return bookingService.cancelBooking(bookingNumber, firstName, lastName);
    }
}

