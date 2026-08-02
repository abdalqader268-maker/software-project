package edu.najah.vrms.domain;

import java.math.BigDecimal;

import edu.najah.vrms.domain.exception.LowBatteryException;

/**
 * An electric vehicle (US5.1). An electric vehicle may only be handed to a
 * customer when its battery charge is at least
 * {@link #MINIMUM_BATTERY_PERCENT} percent (US5.2).
 */
public class ElectricVehicle extends Vehicle {

    /** Smallest battery charge, in percent, required to rent the vehicle. */
    public static final int MINIMUM_BATTERY_PERCENT = 20;

    /** Current battery charge as a percentage in the range {@code 0..100}. */
    private int batteryPercent;

    /**
     * Creates an electric vehicle.
     *
     * @param id             unique identifier of the vehicle
     * @param plateNumber    official plate number
     * @param brand          manufacturer brand
     * @param model          commercial model name
     * @param dailyRate      price charged per rental day
     * @param status         initial {@link VehicleStatus}
     * @param batteryPercent initial battery charge in percent (0..100)
     */
    public ElectricVehicle(String id, String plateNumber, String brand, String model,
                           BigDecimal dailyRate, VehicleStatus status, int batteryPercent) {
        super(id, plateNumber, brand, model, dailyRate, status);
        this.batteryPercent = batteryPercent;
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory() {
        return "Electric";
    }

    /**
     * Returns the current battery charge of this vehicle.
     *
     * @return the battery charge in percent (0..100)
     */
    public int getBatteryPercent() {
        return batteryPercent;
    }

    /**
     * Updates the battery charge, e.g. after charging the vehicle.
     *
     * @param batteryPercent the new charge in percent (0..100)
     */
    public void setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    /**
     * {@inheritDoc}
     *
     * @throws LowBatteryException when the battery charge is below
     *                             {@link #MINIMUM_BATTERY_PERCENT}
     */
    @Override
    public void checkRentalEligibility(int customerAge, boolean specialLicenseHeld) {
        if (batteryPercent < MINIMUM_BATTERY_PERCENT) {
            throw new LowBatteryException(
                    "Electric vehicle battery is too low to rent: " + batteryPercent
                            + "% (minimum " + MINIMUM_BATTERY_PERCENT + "%).");
        }
    }
}
