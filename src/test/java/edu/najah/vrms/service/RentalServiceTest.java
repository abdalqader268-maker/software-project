package edu.najah.vrms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import edu.najah.vrms.domain.RentalStatus;
import edu.najah.vrms.domain.VehicleStatus;
import edu.najah.vrms.domain.exception.InvalidRentalPeriodException;
import edu.najah.vrms.domain.exception.UnauthorizedActionException;
import edu.najah.vrms.domain.exception.VehicleNotFoundException;
import edu.najah.vrms.domain.exception.VehicleUnavailableException;
import edu.najah.vrms.persistence.InMemoryManagerRepository;
import edu.najah.vrms.persistence.InMemoryRentalRepository;
import edu.najah.vrms.persistence.InMemoryVehicleRepository;
import edu.najah.vrms.validation.DurationLimitRule;
import edu.najah.vrms.validation.NoOverlapRule;
import edu.najah.vrms.validation.RentalValidator;

/**
 * Unit tests for {@link RentalService} covering US2.1 (rent a vehicle),
 * US2.2 (duplicate rentals rejected) and US2.3 (duration limits).
 * <p>
 * All tests run against a frozen clock so date assertions are stable.
 */
class RentalServiceTest {

    /** Frozen "today" used by every test: 2026-07-14. */
    private static final LocalDate TODAY = LocalDate.of(2026, 7, 14);

    /** Repository holding the demo fleet. */
    private InMemoryVehicleRepository vehicleRepository;

    /** Repository holding the rental records. */
    private InMemoryRentalRepository rentalRepository;

    /** Authentication service with a logged-in manager. */
    private AuthService authService;

    /** Service under test. */
    private RentalService rentalService;

    /**
     * Wires the service with a frozen clock, seeds one available vehicle
     * and logs the manager in.
     */
    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

        vehicleRepository = new InMemoryVehicleRepository();
        rentalRepository = new InMemoryRentalRepository();
        vehicleRepository.save(TestFixtures.availableCorolla());

        InMemoryManagerRepository managerRepository = new InMemoryManagerRepository();
        managerRepository.save(new Manager("admin", "admin123", "Fleet Manager"));
        authService = new AuthService(managerRepository);
        authService.login("admin", "admin123");

        rentalService = new RentalService(
                vehicleRepository,
                rentalRepository,
                new RentalValidator(Arrays.asList(
                        new DurationLimitRule(fixedClock, 1, 30),
                        new NoOverlapRule(rentalRepository))),
                authService);
    }

    @Test
    @DisplayName("US2.1 - renting creates a record and marks the vehicle as rented")
    void rentingCreatesRecordAndMarksVehicleRented() {
        Rental rental = rentalService.rentVehicle("V-1", "Ahmad Ali", "ahmad@example.com",
                TODAY, TODAY.plusDays(3));

        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertEquals("Ahmad Ali", rental.getCustomerName());
        assertEquals(VehicleStatus.RENTED,
                vehicleRepository.findById("V-1").orElseThrow().getStatus());
        assertTrue(rentalRepository.findById(rental.getId()).isPresent());
    }

    @Test
    @DisplayName("US2.2 - renting an already rented vehicle is rejected")
    void duplicateRentalIsRejected() {
        rentalService.rentVehicle("V-1", "Ahmad Ali", "ahmad@example.com",
                TODAY, TODAY.plusDays(3));

        assertThrows(VehicleUnavailableException.class,
                () -> rentalService.rentVehicle("V-1", "Sami Odeh", "sami@example.com",
                        TODAY.plusDays(1), TODAY.plusDays(4)));
    }

    @Test
    @DisplayName("US2.3 - end date before start date is rejected")
    void endBeforeStartIsRejected() {
        assertThrows(InvalidRentalPeriodException.class,
                () -> rentalService.rentVehicle("V-1", "Ahmad Ali", "ahmad@example.com",
                        TODAY.plusDays(5), TODAY.plusDays(2)));
    }

    @Test
    @DisplayName("US2.3 - rental longer than the maximum is rejected")
    void tooLongRentalIsRejected() {
        assertThrows(InvalidRentalPeriodException.class,
                () -> rentalService.rentVehicle("V-1", "Ahmad Ali", "ahmad@example.com",
                        TODAY, TODAY.plusDays(31)));
    }

    @Test
    @DisplayName("US2.3 - rental starting in the past is rejected")
    void pastStartDateIsRejected() {
        assertThrows(InvalidRentalPeriodException.class,
                () -> rentalService.rentVehicle("V-1", "Ahmad Ali", "ahmad@example.com",
                        TODAY.minusDays(1), TODAY.plusDays(2)));
    }

    @Test
    @DisplayName("Renting an unknown vehicle id is rejected")
    void unknownVehicleIsRejected() {
        assertThrows(VehicleNotFoundException.class,
                () -> rentalService.rentVehicle("V-99", "Ahmad Ali", "ahmad@example.com",
                        TODAY, TODAY.plusDays(2)));
    }

    @Test
    @DisplayName("US1.2 - renting without a logged-in manager is rejected")
    void rentingWithoutLoginIsRejected() {
        authService.logout();

        assertThrows(UnauthorizedActionException.class,
                () -> rentalService.rentVehicle("V-1", "Ahmad Ali", "ahmad@example.com",
                        TODAY, TODAY.plusDays(2)));
    }

    /**
     * Proves the validation date comes from the injected clock and not from
     * the wall clock: with a clock frozen in 2020, a rental starting in 2020
     * is accepted although that date is long in the real-world past.
     */
    @Test
    @DisplayName("Validation uses the injected clock, not the wall clock")
    void validationUsesInjectedClock() {
        LocalDate pastToday = LocalDate.of(2020, 1, 1);
        Clock pastClock = Clock.fixed(
                pastToday.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        RentalService pastService = new RentalService(
                vehicleRepository,
                rentalRepository,
                new RentalValidator(Arrays.asList(
                        new DurationLimitRule(pastClock, 1, 30),
                        new NoOverlapRule(rentalRepository))),
                authService);

        Rental rental = pastService.rentVehicle("V-1", "Ahmad Ali", "ahmad@example.com",
                pastToday, pastToday.plusDays(2));

        assertEquals(pastToday, rental.getStartDate());
    }
}
