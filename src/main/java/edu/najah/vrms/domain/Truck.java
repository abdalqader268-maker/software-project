package edu.najah.vrms.domain;

import java.math.BigDecimal;

import edu.najah.vrms.domain.exception.SpecialLicenseRequiredException;

/**
 * A truck (US5.1). Trucks may only be rented by a customer who holds a special
 * driving license (US5.2).
 */
public class Truck extends Vehicle {

    /**
     * Creates a truck.
     *
     * @param id          unique identifier of the vehicle
     * @param plateNumber official plate number
     * @param brand       manufacturer brand
     * @param model       commercial model name
     * @param dailyRate   price charged per rental day
     * @param status      initial {@link VehicleStatus}
     */
    public Truck(String id, String plateNumber, String brand, String model,
                 BigDecimal dailyRate, VehicleStatus status) {
        super(id, plateNumber, brand, model, dailyRate, status);
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory() {
        return "Truck";
    }

    /**
     * {@inheritDoc}
     *
     * @throws SpecialLicenseRequiredException when the customer does not hold a
     *                                         special driving license
     */
    @Override
    public void checkRentalEligibility(int customerAge, boolean specialLicenseHeld) {
        if (!specialLicenseHeld) {
            throw new SpecialLicenseRequiredException(
                    "Trucks require the customer to hold a special driving license.");
        }
    }
}
