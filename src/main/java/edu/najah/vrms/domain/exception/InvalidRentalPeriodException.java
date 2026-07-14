package edu.najah.vrms.domain.exception;

/**
 * Thrown when a requested rental period breaks one of the duration rules,
 * for example the end date comes before the start date or the period is
 * longer than the allowed maximum.
 */
public class InvalidRentalPeriodException extends RuntimeException {

    /**
     * Creates the exception with a human readable error message.
     *
     * @param message description of the violated duration rule
     */
    public InvalidRentalPeriodException(String message) {
        super(message);
    }
}
