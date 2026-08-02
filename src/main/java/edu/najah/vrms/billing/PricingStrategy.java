package edu.najah.vrms.billing;

import java.math.BigDecimal;
import java.time.LocalDate;

import edu.najah.vrms.domain.Rental;

/**
 * Strategy interface for computing the money owed on a rental (US4.2, US4.3).
 * <p>
 * Keeping the pricing behind a strategy means alternative schemes (seasonal
 * rates, loyalty discounts, ...) can be added without touching the return
 * workflow.
 */
public interface PricingStrategy {

    /**
     * Computes the base rental cost from the booked duration (US4.2).
     *
     * @param rental the rental being billed
     * @return the base cost, never negative
     */
    BigDecimal baseCost(Rental rental);

    /**
     * Computes the late-return penalty for a rental (US4.3).
     *
     * @param rental           the rental being billed
     * @param actualReturnDate the day the vehicle was actually returned
     * @return the penalty amount, or {@link BigDecimal#ZERO} when the vehicle
     *         was returned on time or early
     */
    BigDecimal lateFee(Rental rental, LocalDate actualReturnDate);
}
