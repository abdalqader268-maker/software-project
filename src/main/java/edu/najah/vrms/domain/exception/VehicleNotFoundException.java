package edu.najah.vrms.domain.exception;

/**
 * Thrown when an operation references a vehicle id that does not exist in
 * the fleet.
 */
public class VehicleNotFoundException extends RuntimeException {

    /**
     * Creates the exception for the missing vehicle.
     *
     * @param vehicleId the id that could not be resolved
     */
    public VehicleNotFoundException(String vehicleId) {
        super("Vehicle not found: " + vehicleId);
    }
}
