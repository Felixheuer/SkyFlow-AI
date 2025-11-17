package com.bookingchatbot.repository;

import com.bookingchatbot.model.Booking;
import com.bookingchatbot.model.BookingClass;
import com.bookingchatbot.model.BookingStatus;
import com.bookingchatbot.model.Customer;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * In-memory repository for bookings.
 * In a production environment, this would be replaced with JPA/Hibernate.
 */
@Repository
public class BookingRepository {

    private final List<Customer> customers = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    public BookingRepository() {
        initializeDemoData();
    }

    private void initializeDemoData() {
        List<String> firstNames = List.of("John", "Jane", "Michael", "Sarah", "Robert");
        List<String> lastNames = List.of("Doe", "Smith", "Johnson", "Williams", "Taylor");
        List<String> airportCodes = List.of("LAX", "SFO", "JFK", "LHR", "CDG", "ARN", "HEL", "TXL", "MUC", "FRA", "MAD", "SJC");
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            String firstName = firstNames.get(i);
            String lastName = lastNames.get(i);
            String from = airportCodes.get(random.nextInt(airportCodes.size()));
            String to = airportCodes.get(random.nextInt(airportCodes.size()));
            BookingClass bookingClass = BookingClass.values()[random.nextInt(BookingClass.values().length)];
            
            Customer customer = new Customer(firstName, lastName);
            LocalDate date = LocalDate.now().plusDays(2 * i + 1);
            
            Booking booking = new Booking(
                "BK10" + (i + 1),
                date,
                customer,
                BookingStatus.CONFIRMED,
                from,
                to,
                bookingClass
            );
            
            customer.getBookings().add(booking);
            customers.add(customer);
            bookings.add(booking);
        }
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookings);
    }

    public Optional<Booking> findByBookingNumberAndCustomer(
            String bookingNumber, 
            String firstName, 
            String lastName) {
        return bookings.stream()
                .filter(b -> b.getBookingNumber().equalsIgnoreCase(bookingNumber))
                .filter(b -> b.getCustomer().getFirstName().equalsIgnoreCase(firstName))
                .filter(b -> b.getCustomer().getLastName().equalsIgnoreCase(lastName))
                .findFirst();
    }

    public Booking save(Booking booking) {
        bookings.removeIf(b -> b.getBookingNumber().equals(booking.getBookingNumber()));
        bookings.add(booking);
        return booking;
    }
}

