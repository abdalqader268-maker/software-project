package edu.najah.vrms.domain.exception;

/**
 * Thrown when a return is attempted for a rental that has already been closed
 * (US4.1). A completed rental cannot be returned a second time.
 */
public class RentalAlreadyReturnedException extends RuntimeException {

    /**
     * Creates the exception for the given rental id.
     *
     * @param rentalId the id of the already-closed rental
     */
    public RentalAlreadyReturnedException(String rentalId) {
        super("Rental " + rentalId + " has already been returned.");
    }
}
