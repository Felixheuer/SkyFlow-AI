package com.bookingchatbot.exception;

/**
 * Exception thrown when a booking operation violates business policies.
 */
public class BookingPolicyViolationException extends RuntimeException {
    
    public BookingPolicyViolationException(String message) {
        super(message);
    }
}

