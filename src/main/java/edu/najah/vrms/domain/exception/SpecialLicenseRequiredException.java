package edu.najah.vrms.domain.exception;

/**
 * Thrown when a vehicle type that requires a special driving license (e.g. a
 * truck) is rented by a customer who does not hold one (US5.2).
 */
public class SpecialLicenseRequiredException extends RuntimeException {

    /**
     * Creates the exception with a human readable error message.
     *
     * @param message description of the missing special license
     */
    public SpecialLicenseRequiredException(String message) {
        super(message);
    }
}
