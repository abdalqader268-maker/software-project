package edu.najah.vrms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link RentalReceipt} value object (Sprint&nbsp;4).
 */
class RentalReceiptTest {

    @Test
    @DisplayName("A receipt exposes its fields and sums base cost and late fee")
    void receiptExposesFieldsAndTotal() {
        LocalDate returnDate = LocalDate.of(2026, 8, 5);
        RentalReceipt receipt = new RentalReceipt(
                "R-7", returnDate, new BigDecimal("105.00"), new BigDecimal("52.50"));

        assertEquals("R-7", receipt.getRentalId());
        assertEquals(returnDate, receipt.getReturnDate());
        assertEquals(0, new BigDecimal("105.00").compareTo(receipt.getBaseCost()));
        assertEquals(0, new BigDecimal("52.50").compareTo(receipt.getLateFee()));
        assertEquals(0, new BigDecimal("157.50").compareTo(receipt.getTotal()));
    }
}
