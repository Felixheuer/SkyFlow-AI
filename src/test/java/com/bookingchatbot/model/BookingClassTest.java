package com.bookingchatbot.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for BookingClass enum.
 */
@DisplayName("Booking Class Tests")
class BookingClassTest {

    @Test
    @DisplayName("Economy class should have correct fees")
    void economyClassShouldHaveCorrectFees() {
        assertThat(BookingClass.ECONOMY.getChangeFee()).isEqualTo(50);
        assertThat(BookingClass.ECONOMY.getCancellationFee()).isEqualTo(75);
    }

    @Test
    @DisplayName("Premium Economy class should have correct fees")
    void premiumEconomyClassShouldHaveCorrectFees() {
        assertThat(BookingClass.PREMIUM_ECONOMY.getChangeFee()).isEqualTo(30);
        assertThat(BookingClass.PREMIUM_ECONOMY.getCancellationFee()).isEqualTo(50);
    }

    @Test
    @DisplayName("Business class should have correct fees")
    void businessClassShouldHaveCorrectFees() {
        assertThat(BookingClass.BUSINESS.getChangeFee()).isEqualTo(0);
        assertThat(BookingClass.BUSINESS.getCancellationFee()).isEqualTo(25);
    }

    @Test
    @DisplayName("Business class should have free changes")
    void businessClassShouldHaveFreeChanges() {
        assertThat(BookingClass.BUSINESS.getChangeFee()).isZero();
    }

    @Test
    @DisplayName("All classes should have cancellation fees")
    void allClassesShouldHaveCancellationFees() {
        for (BookingClass bookingClass : BookingClass.values()) {
            assertThat(bookingClass.getCancellationFee()).isGreaterThanOrEqualTo(0);
        }
    }
}

