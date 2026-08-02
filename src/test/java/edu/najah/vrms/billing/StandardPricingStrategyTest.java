package edu.najah.vrms.billing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.najah.vrms.TestFixtures;
import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.Vehicle;

/**
 * Unit tests for {@link StandardPricingStrategy} covering the base rental cost
 * (US4.2) and the late-return penalty (US4.3).
 */
class StandardPricingStrategyTest {

    /** Strategy under test. */
    private final StandardPricingStrategy pricing = new StandardPricingStrategy();

    /** Booked start date used across the tests. */
    private static final LocalDate START = LocalDate.of(2026, 8, 1);

    /** Booked end date (3 booked days after the start). */
    private static final LocalDate END = LocalDate.of(2026, 8, 4);

    /**
     * Builds an active rental of the demo Corolla (35.00/day) for the fixed
     * three-day period.
     *
     * @return the rental
     */
    private Rental threeDayRental() {
        Vehicle car = TestFixtures.availableCorolla();
        return new Rental("R-1", car, "Ahmad", "ahmad@example.com", START, END);
    }

    @Test
    @DisplayName("US4.2 - base cost is daily rate times booked days")
    void baseCostFromDuration() {
        // 3 booked days * 35.00 = 105.00
        assertEquals(0, new BigDecimal("105.00").compareTo(
                pricing.baseCost(threeDayRental())));
    }

    @Test
    @DisplayName("US4.3 - returning on time incurs no penalty")
    void noPenaltyWhenOnTime() {
        assertEquals(0, BigDecimal.ZERO.compareTo(
                pricing.lateFee(threeDayRental(), END)));
    }

    @Test
    @DisplayName("US4.3 - returning early incurs no penalty")
    void noPenaltyWhenEarly() {
        assertEquals(0, BigDecimal.ZERO.compareTo(
                pricing.lateFee(threeDayRental(), END.minusDays(1))));
    }

    @Test
    @DisplayName("US4.3 - each late day is charged at 1.5x the daily rate")
    void penaltyForLateReturn() {
        // 2 late days * 35.00 * 1.5 = 105.00
        assertEquals(0, new BigDecimal("105.00").compareTo(
                pricing.lateFee(threeDayRental(), END.plusDays(2))));
    }
}
