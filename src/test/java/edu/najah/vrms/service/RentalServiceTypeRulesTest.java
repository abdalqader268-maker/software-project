package edu.najah.vrms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.najah.vrms.TestFixtures;
import edu.najah.vrms.domain.Manager;
import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.exception.AgeRestrictionException;
import edu.najah.vrms.domain.exception.SpecialLicenseRequiredException;
import edu.najah.vrms.persistence.InMemoryManagerRepository;
import edu.najah.vrms.persistence.InMemoryRentalRepository;
import edu.najah.vrms.persistence.InMemoryVehicleRepository;
import edu.najah.vrms.validation.DurationLimitRule;
import edu.najah.vrms.validation.NoOverlapRule;
import edu.najah.vrms.validation.RentalValidator;
import edu.najah.vrms.validation.TypeSpecificRule;

/**
 * Integration-style tests proving that {@link RentalService} enforces the
 * type-specific rules through the validator chain (US5.2).
 */
class RentalServiceTypeRulesTest {

    /** Frozen "today" used by every test. */
    private static final LocalDate TODAY = LocalDate.of(2026, 8, 1);

    /** Repository holding the demo fleet. */
    private InMemoryVehicleRepository vehicleRepository;

    /** Service under test. */
    private RentalService rentalService;

    /**
     * Wires the service with the full rule chain (including
     * {@link TypeSpecificRule}) and a logged-in manager, then seeds a truck
     * and a motorcycle.
     */
    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

        vehicleRepository = new InMemoryVehicleRepository();
        InMemoryRentalRepository rentalRepository = new InMemoryRentalRepository();
        vehicleRepository.save(TestFixtures.availableTruck());
        vehicleRepository.save(TestFixtures.availableMotorcycle());

        InMemoryManagerRepository managerRepository = new InMemoryManagerRepository();
        managerRepository.save(new Manager("admin", "admin123", "Fleet Manager"));
        AuthService authService = new AuthService(managerRepository);
        authService.login("admin", "admin123");

        rentalService = new RentalService(
                vehicleRepository,
                rentalRepository,
                new RentalValidator(Arrays.asList(
                        new DurationLimitRule(clock, 1, 30),
                        new NoOverlapRule(rentalRepository),
                        new TypeSpecificRule())),
                authService);
    }

    @Test
    @DisplayName("US5.2 - renting a truck without a license is rejected")
    void truckRentalRequiresLicense() {
        assertThrows(SpecialLicenseRequiredException.class,
                () -> rentalService.rentVehicle("T-1", "Sami", "sami@example.com",
                        TODAY, TODAY.plusDays(2), 40, false));
    }

    @Test
    @DisplayName("US5.2 - renting a truck with a license succeeds")
    void truckRentalWithLicenseSucceeds() {
        Rental rental = rentalService.rentVehicle("T-1", "Sami", "sami@example.com",
                TODAY, TODAY.plusDays(2), 40, true);
        assertEquals("T-1", rental.getVehicle().getId());
    }

    @Test
    @DisplayName("US5.2 - renting a motorcycle to a minor is rejected")
    void motorcycleRentalRejectsMinor() {
        assertThrows(AgeRestrictionException.class,
                () -> rentalService.rentVehicle("M-1", "Kid", "kid@example.com",
                        TODAY, TODAY.plusDays(2), 16, false));
    }
}
