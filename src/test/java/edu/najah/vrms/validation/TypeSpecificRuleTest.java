package edu.najah.vrms.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.najah.vrms.TestFixtures;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.exception.SpecialLicenseRequiredException;

/**
 * Unit tests for {@link TypeSpecificRule}, the strategy that forwards a rental
 * request to the requested vehicle's polymorphic type rules (US5.2).
 */
class TypeSpecificRuleTest {

    /** Rule under test. */
    private final TypeSpecificRule rule = new TypeSpecificRule();

    /** A fixed rental period; the dates are irrelevant to the type rules. */
    private static final LocalDate START = LocalDate.of(2026, 8, 1);

    /** End of the fixed rental period. */
    private static final LocalDate END = LocalDate.of(2026, 8, 4);

    @Test
    @DisplayName("Forwards to the vehicle and rejects a truck without a license")
    void rejectsTruckWithoutLicense() {
        Vehicle truck = TestFixtures.availableTruck();
        RentalRequest request = new RentalRequest(
                truck, "Sami", "sami@example.com", START, END, 40, false);

        assertThrows(SpecialLicenseRequiredException.class,
                () -> rule.validate(request));
    }

    @Test
    @DisplayName("Accepts a truck rental when the customer holds a license")
    void acceptsTruckWithLicense() {
        Vehicle truck = TestFixtures.availableTruck();
        RentalRequest request = new RentalRequest(
                truck, "Sami", "sami@example.com", START, END, 40, true);

        assertDoesNotThrow(() -> rule.validate(request));
    }

    @Test
    @DisplayName("Accepts a car rental with the default customer profile")
    void acceptsCarWithDefaults() {
        Vehicle car = TestFixtures.availableCorolla();
        RentalRequest request = new RentalRequest(
                car, "Lina", "lina@example.com", START, END);

        assertDoesNotThrow(() -> rule.validate(request));
    }
}
