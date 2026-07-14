package edu.najah.vrms.validation;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import edu.najah.vrms.domain.exception.InvalidRentalPeriodException;

/**
 * {@link RentalValidationRule} strategy that enforces the rental duration
 * limits (US2.3).
 * <p>
 * A period is valid when it starts today or later, ends after it starts and
 * its length in days stays between the configured minimum and maximum.
 */
public class DurationLimitRule implements RentalValidationRule {

    /** Clock used to resolve "today"; injectable so tests can fix the date. */
    private final Clock clock;

    /** Smallest allowed rental length in days. */
    private final int minDays;

    /** Largest allowed rental length in days. */
    private final int maxDays;

    /**
     * Creates the rule with explicit duration limits.
     *
     * @param clock   clock used to determine the current date
     * @param minDays smallest allowed rental length in days
     * @param maxDays largest allowed rental length in days
     */
    public DurationLimitRule(Clock clock, int minDays, int maxDays) {
        this.clock = clock;
        this.minDays = minDays;
        this.maxDays = maxDays;
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidRentalPeriodException when the period breaks a duration
     *                                      rule
     */
    @Override
    public void validate(RentalRequest request) {
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        LocalDate today = LocalDate.now(clock);

        if (start == null || end == null) {
            throw new InvalidRentalPeriodException("Start and end dates are required.");
        }
        if (start.isBefore(today)) {
            throw new InvalidRentalPeriodException(
                    "Rental cannot start in the past: " + start);
        }
        long days = ChronoUnit.DAYS.between(start, end);
        if (days < minDays) {
            throw new InvalidRentalPeriodException(
                    "Rental must last at least " + minDays + " day(s).");
        }
        if (days > maxDays) {
            throw new InvalidRentalPeriodException(
                    "Rental cannot exceed " + maxDays + " day(s).");
        }
    }
}
