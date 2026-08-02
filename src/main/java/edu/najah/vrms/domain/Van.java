package edu.najah.vrms.domain;

import java.math.BigDecimal;

/**
 * A passenger or cargo van (US5.1). Vans carry no extra rental restriction.
 */
public class Van extends Vehicle {

    /**
     * Creates a van.
     *
     * @param id          unique identifier of the vehicle
     * @param plateNumber official plate number
     * @param brand       manufacturer brand
     * @param model       commercial model name
     * @param dailyRate   price charged per rental day
     * @param status      initial {@link VehicleStatus}
     */
    public Van(String id, String plateNumber, String brand, String model,
               BigDecimal dailyRate, VehicleStatus status) {
        super(id, plateNumber, brand, model, dailyRate, status);
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory() {
        return "Van";
    }
}
