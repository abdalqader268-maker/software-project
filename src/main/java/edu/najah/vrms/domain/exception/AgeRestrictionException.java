package edu.najah.vrms.domain.exception;

/**
 * Thrown when a customer does not meet the minimum age required to rent a
 * particular vehicle type, e.g. a motorcycle (US5.2).
 */
public class AgeRestrictionException extends RuntimeException {

    /**
     * Creates the exception with a human readable error message.
     *
     * @param message description of the violated age restriction
     */
    public AgeRestrictionException(String message) {
        super(message);
    }
}
