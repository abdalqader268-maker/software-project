package edu.najah.vrms;

import java.math.BigDecimal;

import edu.najah.vrms.domain.Car;
import edu.najah.vrms.domain.ElectricVehicle;
import edu.najah.vrms.domain.Motorcycle;
import edu.najah.vrms.domain.Truck;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;

/**
 * Shared factory helpers for the unit tests.
 * <p>
 * Several test classes used to construct the very same demo vehicle inline,
 * which duplicated the constructor call and its magic values. Centralizing
 * that construction here keeps the tests short and gives every test the same
 * well-known vehicles.
 */
public final class TestFixtures {

    /** Utility class: prevent instantiation. */
    private TestFixtures() {
    }

    /**
     * Builds the standard demo car (a Toyota Corolla, plate {@code NAB-1234})
     * with the requested id and status.
     *
     * @param id     the vehicle id
     * @param plate  the plate number
     * @param status the initial status
     * @return the car
     */
    public static Vehicle corolla(String id, String plate, VehicleStatus status) {
        return new Car(id, plate, "Toyota", "Corolla",
                new BigDecimal("35.00"), status);
    }

    /**
     * Builds the standard available demo car ({@code V-1} / {@code NAB-1234}).
     *
     * @return an available Toyota Corolla
     */
    public static Vehicle availableCorolla() {
        return corolla("V-1", "NAB-1234", VehicleStatus.AVAILABLE);
    }

    /**
     * Builds an available demo truck ({@code T-1}).
     *
     * @return an available truck
     */
    public static Truck availableTruck() {
        return new Truck("T-1", "NAB-7000", "Volvo", "FH16",
                new BigDecimal("120.00"), VehicleStatus.AVAILABLE);
    }

    /**
     * Builds an available demo motorcycle ({@code M-1}).
     *
     * @return an available motorcycle
     */
    public static Motorcycle availableMotorcycle() {
        return new Motorcycle("M-1", "NAB-8000", "Honda", "CB500",
                new BigDecimal("40.00"), VehicleStatus.AVAILABLE);
    }

    /**
     * Builds an available demo electric vehicle ({@code E-1}) with the given
     * battery charge.
     *
     * @param batteryPercent initial battery charge in percent
     * @return an available electric vehicle
     */
    public static ElectricVehicle availableElectricVehicle(int batteryPercent) {
        return new ElectricVehicle("E-1", "NAB-9000", "Tesla", "Model 3",
                new BigDecimal("90.00"), VehicleStatus.AVAILABLE, batteryPercent);
    }
}
