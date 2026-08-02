package edu.najah.vrms.validation;

import edu.najah.vrms.domain.Vehicle;

/**
 * {@link RentalValidationRule} strategy that delegates to the polymorphic
 * type-specific rules of the requested vehicle (US5.2).
 * <p>
 * This single strategy works for every vehicle type: it simply forwards the
 * customer age and special-license flag to
 * {@link Vehicle#checkRentalEligibility(int, boolean)}, which each concrete
 * vehicle type overrides as needed (age for motorcycles, license for trucks,
 * battery for electric vehicles). New vehicle types therefore plug in without
 * touching the validation chain.
 */
public class TypeSpecificRule implements RentalValidationRule {

    /**
     * {@inheritDoc}
     *
     * @throws RuntimeException the type-specific exception raised by the
     *                          vehicle when its rules are violated
     */
    @Override
    public void validate(RentalRequest request) {
        Vehicle vehicle = request.getVehicle();
        vehicle.checkRentalEligibility(
                request.getCustomerAge(), request.isSpecialLicenseHeld());
    }
}
