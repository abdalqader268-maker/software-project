package edu.najah.vrms.domain;

import java.math.BigDecimal;

/**
 * A standard passenger car (US5.1). Cars carry no extra rental restriction.
 */
public class Car extends Vehicle {

    /**
     * Creates a car.
     *
     * @param id          unique identifier of the vehicle
     * @param plateNumber official plate number
     * @param brand       manufacturer brand
     * @param model       commercial model name
     * @param dailyRate   price charged per rental day
     * @param status      initial {@link VehicleStatus}
     */
    public Car(String id, String plateNumber, String brand, String model,
               BigDecimal dailyRate, VehicleStatus status) {
        super(id, plateNumber, brand, model, dailyRate, status);
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory() {
        return "Car";
    }
}
