package edu.najah.vrms;

import java.math.BigDecimal;

import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;

/**
 * Shared factory helpers for the unit tests.
 * <p>
 * Several test classes used to construct the very same demo vehicle inline,
 * which duplicated the constructor call and its magic values. Centralizing
 * that construction here keeps the tests short and gives every test the same
 * well-known vehicle.
 */
public final class TestFixtures {

    /** Utility class: prevent instantiation. */
    private TestFixtures() {
    }

    /**
     * Builds the standard demo vehicle (a Toyota Corolla, plate
     * {@code NAB-1234}) with the requested id and status.
     *
     * @param id     the vehicle id
     * @param plate  the plate number
     * @param status the initial status
     * @return the vehicle
     */
    public static Vehicle corolla(String id, String plate, VehicleStatus status) {
        return new Vehicle(id, plate, "Toyota", "Corolla",
                new BigDecimal("35.00"), status);
    }

    /**
     * Builds the standard available demo vehicle ({@code V-1} / {@code NAB-1234}).
     *
     * @return an available Toyota Corolla
     */
    public static Vehicle availableCorolla() {
        return corolla("V-1", "NAB-1234", VehicleStatus.AVAILABLE);
    }
}
