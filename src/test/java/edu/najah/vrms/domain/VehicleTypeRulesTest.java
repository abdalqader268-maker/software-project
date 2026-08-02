package edu.najah.vrms.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.najah.vrms.TestFixtures;
import edu.najah.vrms.domain.exception.AgeRestrictionException;
import edu.najah.vrms.domain.exception.LowBatteryException;
import edu.najah.vrms.domain.exception.SpecialLicenseRequiredException;

/**
 * Unit tests for the polymorphic, type-specific rental rules (US5.1, US5.2):
 * truck license, motorcycle rider age and electric-vehicle battery checks.
 */
class VehicleTypeRulesTest {

    @Test
    @DisplayName("US5.1 - every type reports its own category")
    void categoriesAreDistinct() {
        assertEquals("Car", TestFixtures.availableCorolla().getCategory());
        assertEquals("Truck", TestFixtures.availableTruck().getCategory());
        assertEquals("Motorcycle", TestFixtures.availableMotorcycle().getCategory());
        assertEquals("Electric",
                TestFixtures.availableElectricVehicle(80).getCategory());
    }

    @Test
    @DisplayName("US5.1 - a car imposes no extra rental restriction")
    void carHasNoTypeRestriction() {
        Vehicle car = TestFixtures.availableCorolla();
        assertDoesNotThrow(() -> car.checkRentalEligibility(16, false));
    }

    @Test
    @DisplayName("US5.1 - a van reports its category and imposes no restriction")
    void vanHasNoTypeRestriction() {
        Vehicle van = new Van("VN-1", "NAB-6000", "Ford", "Transit",
                new BigDecimal("70.00"), VehicleStatus.AVAILABLE);
        assertEquals("Van", van.getCategory());
        assertDoesNotThrow(() -> van.checkRentalEligibility(18, false));
    }

    @Test
    @DisplayName("US5.2 - a truck requires a special license")
    void truckRequiresSpecialLicense() {
        Truck truck = TestFixtures.availableTruck();
        assertThrows(SpecialLicenseRequiredException.class,
                () -> truck.checkRentalEligibility(40, false));
        assertDoesNotThrow(() -> truck.checkRentalEligibility(40, true));
    }

    @Test
    @DisplayName("US5.2 - a motorcycle enforces a minimum rider age")
    void motorcycleEnforcesMinimumAge() {
        Motorcycle motorcycle = TestFixtures.availableMotorcycle();
        assertThrows(AgeRestrictionException.class,
                () -> motorcycle.checkRentalEligibility(
                        Motorcycle.MINIMUM_RIDER_AGE - 1, false));
        assertDoesNotThrow(() -> motorcycle.checkRentalEligibility(
                Motorcycle.MINIMUM_RIDER_AGE, false));
    }

    @Test
    @DisplayName("US5.2 - an electric vehicle rejects a low battery charge")
    void electricVehicleChecksBattery() {
        ElectricVehicle lowCharge = TestFixtures.availableElectricVehicle(
                ElectricVehicle.MINIMUM_BATTERY_PERCENT - 1);
        assertThrows(LowBatteryException.class,
                () -> lowCharge.checkRentalEligibility(30, false));

        ElectricVehicle charged = TestFixtures.availableElectricVehicle(
                ElectricVehicle.MINIMUM_BATTERY_PERCENT);
        assertDoesNotThrow(() -> charged.checkRentalEligibility(30, false));
    }

    @Test
    @DisplayName("An electric vehicle exposes and updates its battery charge")
    void electricVehicleBatteryAccessors() {
        ElectricVehicle ev = TestFixtures.availableElectricVehicle(50);
        assertEquals(50, ev.getBatteryPercent());
        ev.setBatteryPercent(95);
        assertEquals(95, ev.getBatteryPercent());
    }
}
