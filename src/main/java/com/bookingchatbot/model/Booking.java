package com.bookingchatbot.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain model representing a flight booking.
 */
public class Booking {
    
    private String bookingNumber;
    private LocalDate date;
    private Customer customer;
    private BookingStatus status;
    private String departureAirport;
    private String arrivalAirport;
    private BookingClass bookingClass;

    public Booking() {
    }

    public Booking(String bookingNumber, LocalDate date, Customer customer, 
                   BookingStatus status, String departureAirport, 
                   String arrivalAirport, BookingClass bookingClass) {
        this.bookingNumber = bookingNumber;
        this.date = date;
        this.customer = customer;
        this.status = status;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.bookingClass = bookingClass;
    }

    // Getters and Setters
    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public BookingClass getBookingClass() {
        return bookingClass;
    }

    public void setBookingClass(BookingClass bookingClass) {
        this.bookingClass = bookingClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingNumber, booking.bookingNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingNumber);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingNumber='" + bookingNumber + '\'' +
                ", date=" + date +
                ", customer=" + customer +
                ", status=" + status +
                ", from='" + departureAirport + '\'' +
                ", to='" + arrivalAirport + '\'' +
                ", bookingClass=" + bookingClass +
                '}';
    }
}

