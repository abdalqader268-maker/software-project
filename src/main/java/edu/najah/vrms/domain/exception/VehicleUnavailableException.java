package edu.najah.vrms.domain.exception;

/**
 * Thrown when a rental is requested for a vehicle that is not currently
 * available, e.g. it is already rented or under maintenance.
 */
public class VehicleUnavailableException extends RuntimeException {

    /**
     * Creates the exception with a human readable error message.
     *
     * @param message description of why the vehicle cannot be rented
     */
    public VehicleUnavailableException(String message) {
        super(message);
    }
}
