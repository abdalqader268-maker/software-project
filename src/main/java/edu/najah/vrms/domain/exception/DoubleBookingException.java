package edu.najah.vrms.domain.exception;

/**
 * Thrown when a rental request overlaps an existing active rental of the
 * same vehicle, which would create a double booking.
 */
public class DoubleBookingException extends RuntimeException {

    /**
     * Creates the exception with a human readable error message.
     *
     * @param message description of the conflicting booking
     */
    public DoubleBookingException(String message) {
        super(message);
    }
}
