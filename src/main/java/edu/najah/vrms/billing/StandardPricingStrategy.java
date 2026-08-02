package edu.najah.vrms.billing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import edu.najah.vrms.domain.Rental;

/**
 * Default {@link PricingStrategy}: the base cost is the daily rate multiplied
 * by the number of booked days, and every day returned late is charged the
 * daily rate multiplied by {@link #LATE_PENALTY_MULTIPLIER} (US4.2, US4.3).
 */
public class StandardPricingStrategy implements PricingStrategy {

    /** Surcharge factor applied to the daily rate for each late day. */
    public static final BigDecimal LATE_PENALTY_MULTIPLIER = new BigDecimal("1.5");

    /**
     * {@inheritDoc}
     * <p>
     * The booked duration is the number of days between the start and end
     * dates of the rental; the daily rate is charged for each of them.
     */
    @Override
    public BigDecimal baseCost(Rental rental) {
        long days = bookedDays(rental);
        return rental.getVehicle().getDailyRate()
                .multiply(BigDecimal.valueOf(days));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Every day beyond the booked end date is charged at
     * {@code dailyRate * }{@link #LATE_PENALTY_MULTIPLIER}. A vehicle returned
     * on time or early incurs no penalty.
     */
    @Override
    public BigDecimal lateFee(Rental rental, LocalDate actualReturnDate) {
        long lateDays = lateDays(rental, actualReturnDate);
        if (lateDays <= 0) {
            return BigDecimal.ZERO;
        }
        return rental.getVehicle().getDailyRate()
                .multiply(LATE_PENALTY_MULTIPLIER)
                .multiply(BigDecimal.valueOf(lateDays));
    }

    /**
     * Number of days the rental was booked for.
     *
     * @param rental the rental
     * @return the booked days (at least zero)
     */
    private long bookedDays(Rental rental) {
        return ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate());
    }

    /**
     * Number of days the vehicle was returned past its booked end date.
     *
     * @param rental           the rental
     * @param actualReturnDate the actual return day
     * @return the late days, or zero when returned on time or early
     */
    private long lateDays(Rental rental, LocalDate actualReturnDate) {
        if (!actualReturnDate.isAfter(rental.getEndDate())) {
            return 0;
        }
        return ChronoUnit.DAYS.between(rental.getEndDate(), actualReturnDate);
    }
}
