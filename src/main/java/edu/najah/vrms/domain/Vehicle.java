package edu.najah.vrms.domain;

import java.math.BigDecimal;

/**
 * Base type for every vehicle in the rental fleet.
 * <p>
 * Each vehicle carries the descriptive data shown in the catalog together
 * with its current {@link VehicleStatus}. Only vehicles whose status is
 * {@link VehicleStatus#AVAILABLE} may be rented.
 * <p>
 * {@code Vehicle} is abstract: concrete subtypes ({@link Car},
 * {@link Motorcycle}, {@link Van}, {@link Truck}, {@link ElectricVehicle})
 * describe their {@linkplain #getCategory() category} and, when needed,
 * override {@link #checkRentalEligibility(int, boolean)} to enforce
 * type-specific rental rules (US5.1, US5.2).
 */
public abstract class Vehicle {

    /** Unique identifier of the vehicle inside the system (e.g. {@code V-1}). */
    private final String id;

    /** Official plate number of the vehicle. */
    private final String plateNumber;

    /** Manufacturer brand, for example {@code Toyota}. */
    private final String brand;

    /** Commercial model name, for example {@code Corolla}. */
    private final String model;

    /** Price charged for one rental day. */
    private final BigDecimal dailyRate;

    /** Current lifecycle status of the vehicle. */
    private VehicleStatus status;

    /**
     * Creates a new vehicle entry for the fleet.
     *
     * @param id          unique identifier of the vehicle
     * @param plateNumber official plate number
     * @param brand       manufacturer brand
     * @param model       commercial model name
     * @param dailyRate   price charged per rental day
     * @param status      initial {@link VehicleStatus}
     */
    protected Vehicle(String id, String plateNumber, String brand, String model,
                      BigDecimal dailyRate, VehicleStatus status) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.dailyRate = dailyRate;
        this.status = status;
    }

    /**
     * Returns the human readable category of this vehicle type, e.g.
     * {@code "Car"} or {@code "Truck"}. Shown in the catalog and used in
     * reports.
     *
     * @return the vehicle category label
     */
    public abstract String getCategory();

    /**
     * Enforces the rules that are specific to this vehicle type (US5.2).
     * <p>
     * The default implementation imposes no extra restriction; subtypes such
     * as {@link Truck}, {@link Motorcycle} and {@link ElectricVehicle} override
     * it to validate a special license, a minimum rider age or the battery
     * charge respectively. Implementations throw a domain exception when the
     * rental must be rejected.
     *
     * @param customerAge        age of the customer in years
     * @param specialLicenseHeld {@code true} when the customer holds a special
     *                           driving license
     */
    public void checkRentalEligibility(int customerAge, boolean specialLicenseHeld) {
        // No type-specific restriction by default.
    }

    /**
     * Returns the unique identifier of this vehicle.
     *
     * @return the vehicle id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the official plate number of this vehicle.
     *
     * @return the plate number
     */
    public String getPlateNumber() {
        return plateNumber;
    }

    /**
     * Returns the manufacturer brand of this vehicle.
     *
     * @return the brand name
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Returns the commercial model name of this vehicle.
     *
     * @return the model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the price charged for one rental day.
     *
     * @return the daily rate
     */
    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    /**
     * Returns the current lifecycle status of this vehicle.
     *
     * @return the current {@link VehicleStatus}
     */
    public VehicleStatus getStatus() {
        return status;
    }

    /**
     * Moves the vehicle to a new lifecycle status.
     *
     * @param status the new {@link VehicleStatus}, must not be {@code null}
     */
    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    /**
     * Convenience check used by the catalog and the rental workflow.
     *
     * @return {@code true} when the vehicle can currently be rented
     */
    public boolean isAvailable() {
        return status == VehicleStatus.AVAILABLE;
    }
}
