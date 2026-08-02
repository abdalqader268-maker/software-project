package edu.najah.vrms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.najah.vrms.TestFixtures;
import edu.najah.vrms.billing.StandardPricingStrategy;
import edu.najah.vrms.domain.Manager;
import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.RentalReceipt;
import edu.najah.vrms.domain.RentalStatus;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;
import edu.najah.vrms.domain.exception.RentalAlreadyReturnedException;
import edu.najah.vrms.domain.exception.RentalNotFoundException;
import edu.najah.vrms.domain.exception.UnauthorizedActionException;
import edu.najah.vrms.persistence.InMemoryManagerRepository;
import edu.najah.vrms.persistence.InMemoryRentalRepository;
import edu.najah.vrms.persistence.InMemoryVehicleRepository;

/**
 * Unit tests for {@link ReturnService} covering the return workflow (US4.1),
 * cost calculation (US4.2), late penalties (US4.3) and the error cases.
 */
class ReturnServiceTest {

    /** Booked start date. */
    private static final LocalDate START = LocalDate.of(2026, 8, 1);

    /** Booked end date (3 booked days). */
    private static final LocalDate END = LocalDate.of(2026, 8, 4);

    /** Repository holding the demo fleet. */
    private InMemoryVehicleRepository vehicleRepository;

    /** Repository holding the rental records. */
    private InMemoryRentalRepository rentalRepository;

    /** Authentication service with a logged-in manager. */
    private AuthService authService;

    /** Service under test. */
    private ReturnService returnService;

    /** The rented vehicle seeded before each test. */
    private Vehicle vehicle;

    /**
     * Seeds one rented vehicle with an active rental and logs the manager in.
     */
    @BeforeEach
    void setUp() {
        vehicleRepository = new InMemoryVehicleRepository();
        rentalRepository = new InMemoryRentalRepository();

        vehicle = TestFixtures.availableCorolla();
        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);
        rentalRepository.save(new Rental(
                "R-1", vehicle, "Ahmad", "ahmad@example.com", START, END));

        InMemoryManagerRepository managerRepository = new InMemoryManagerRepository();
        managerRepository.save(new Manager("admin", "admin123", "Fleet Manager"));
        authService = new AuthService(managerRepository);
        authService.login("admin", "admin123");

        returnService = new ReturnService(
                vehicleRepository, rentalRepository, authService,
                new StandardPricingStrategy());
    }

    @Test
    @DisplayName("US4.1 - returning frees the vehicle and closes the rental")
    void returnFreesVehicleAndClosesRental() {
        returnService.returnVehicle("R-1", END);

        assertEquals(VehicleStatus.AVAILABLE,
                vehicleRepository.findById("V-1").orElseThrow().getStatus());
        assertEquals(RentalStatus.COMPLETED,
                rentalRepository.findById("R-1").orElseThrow().getStatus());
    }

    @Test
    @DisplayName("US4.2 - an on-time return bills only the base cost")
    void onTimeReturnBillsBaseCost() {
        RentalReceipt receipt = returnService.returnVehicle("R-1", END);

        assertEquals(0, new BigDecimal("105.00").compareTo(receipt.getBaseCost()));
        assertEquals(0, BigDecimal.ZERO.compareTo(receipt.getLateFee()));
        assertEquals(0, new BigDecimal("105.00").compareTo(receipt.getTotal()));
    }

    @Test
    @DisplayName("US4.3 - a late return adds a penalty to the total")
    void lateReturnAddsPenalty() {
        RentalReceipt receipt = returnService.returnVehicle("R-1", END.plusDays(2));

        // base 105.00 + penalty (2 * 35.00 * 1.5 = 105.00) = 210.00
        assertEquals(0, new BigDecimal("105.00").compareTo(receipt.getLateFee()));
        assertEquals(0, new BigDecimal("210.00").compareTo(receipt.getTotal()));
    }

    @Test
    @DisplayName("Returning an unknown rental id is rejected")
    void unknownRentalIsRejected() {
        assertThrows(RentalNotFoundException.class,
                () -> returnService.returnVehicle("R-99", END));
    }

    @Test
    @DisplayName("Returning an already-closed rental is rejected")
    void doubleReturnIsRejected() {
        returnService.returnVehicle("R-1", END);

        assertThrows(RentalAlreadyReturnedException.class,
                () -> returnService.returnVehicle("R-1", END));
    }

    @Test
    @DisplayName("US1.2 - returning without a logged-in manager is rejected")
    void returnWithoutLoginIsRejected() {
        authService.logout();

        assertThrows(UnauthorizedActionException.class,
                () -> returnService.returnVehicle("R-1", END));
    }
}
