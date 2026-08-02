package edu.najah.vrms.domain.exception;

/**
 * Thrown when an operation references a rental id that does not exist, e.g.
 * returning a vehicle for an unknown rental (US4.1).
 */
public class RentalNotFoundException extends RuntimeException {

    /**
     * Creates the exception for the given rental id.
     *
     * @param rentalId the unknown rental id
     */
    public RentalNotFoundException(String rentalId) {
        super("No rental found with id: " + rentalId);
    }
}
