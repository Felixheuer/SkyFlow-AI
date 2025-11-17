package com.bookingchatbot.model;

/**
 * Enumeration of flight booking classes with associated fees.
 */
public enum BookingClass {
    ECONOMY(50, 75),
    PREMIUM_ECONOMY(30, 50),
    BUSINESS(0, 25);

    private final int changeFee;
    private final int cancellationFee;

    BookingClass(int changeFee, int cancellationFee) {
        this.changeFee = changeFee;
        this.cancellationFee = cancellationFee;
    }

    public int getChangeFee() {
        return changeFee;
    }

    public int getCancellationFee() {
        return cancellationFee;
    }
}

