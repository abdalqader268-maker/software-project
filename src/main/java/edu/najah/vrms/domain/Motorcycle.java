package edu.najah.vrms.domain;

import java.math.BigDecimal;

import edu.najah.vrms.domain.exception.AgeRestrictionException;

/**
 * A motorcycle (US5.1). Motorcycles may only be rented by customers who are at
 * least {@link #MINIMUM_RIDER_AGE} years old (US5.2).
 */
public class Motorcycle extends Vehicle {

    /** Smallest age, in years, allowed to rent a motorcycle. */
    public static final int MINIMUM_RIDER_AGE = 18;

    /**
     * Creates a motorcycle.
     *
     * @param id          unique identifier of the vehicle
     * @param plateNumber official plate number
     * @param brand       manufacturer brand
     * @param model       commercial model name
     * @param dailyRate   price charged per rental day
     * @param status      initial {@link VehicleStatus}
     */
    public Motorcycle(String id, String plateNumber, String brand, String model,
                      BigDecimal dailyRate, VehicleStatus status) {
        super(id, plateNumber, brand, model, dailyRate, status);
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory() {
        return "Motorcycle";
    }

    /**
     * {@inheritDoc}
     *
     * @throws AgeRestrictionException when the customer is younger than
     *                                 {@link #MINIMUM_RIDER_AGE}
     */
    @Override
    public void checkRentalEligibility(int customerAge, boolean specialLicenseHeld) {
        if (customerAge < MINIMUM_RIDER_AGE) {
            throw new AgeRestrictionException(
                    "Motorcycles require a rider aged at least "
                            + MINIMUM_RIDER_AGE + " (customer age: " + customerAge + ").");
        }
    }
}
