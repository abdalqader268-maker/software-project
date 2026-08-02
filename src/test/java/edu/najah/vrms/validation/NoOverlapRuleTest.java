package edu.najah.vrms.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.najah.vrms.domain.Car;
import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;
import edu.najah.vrms.domain.exception.DoubleBookingException;
import edu.najah.vrms.persistence.InMemoryRentalRepository;

/**
 * Unit tests for the {@link NoOverlapRule} strategy (US2.2).
 */
class NoOverlapRuleTest {

    /** Existing booking runs from 2026-07-20 to 2026-07-25. */
    private static final LocalDate EXISTING_START = LocalDate.of(2026, 7, 20);

    /** End of the existing booking. */
    private static final LocalDate EXISTING_END = LocalDate.of(2026, 7, 25);

    /** Vehicle shared by all requests in these tests. */
    private Vehicle vehicle;

    /** Second vehicle used to prove other vehicles are not affected. */
    private Vehicle otherVehicle;

    /** Rule under test. */
    private NoOverlapRule rule;

    /**
     * Seeds one active rental for the shared vehicle.
     */
    @BeforeEach
    void setUp() {
        vehicle = new Car("V-1", "NAB-1234", "Toyota", "Corolla",
                new BigDecimal("35.00"), VehicleStatus.AVAILABLE);
        otherVehicle = new Car("V-2", "NAB-5678", "Hyundai", "Tucson",
                new BigDecimal("55.00"), VehicleStatus.AVAILABLE);

        InMemoryRentalRepository repository = new InMemoryRentalRepository();
        repository.save(new Rental("R-1", vehicle, "Ahmad Ali", "ahmad@example.com",
                EXISTING_START, EXISTING_END));
        rule = new NoOverlapRule(repository);
    }

    /**
     * Builds a request for the given vehicle and period.
     *
     * @param target the requested vehicle
     * @param start  requested start date
     * @param end    requested end date
     * @return the rental request
     */
    private RentalRequest request(Vehicle target, LocalDate start, LocalDate end) {
        return new RentalRequest(target, "Sami Odeh", "sami@example.com", start, end);
    }

    @Test
    @DisplayName("US2.2 - period inside an existing booking is rejected")
    void overlappingPeriodIsRejected() {
        assertThrows(DoubleBookingException.class, () -> rule.validate(
                request(vehicle, EXISTING_START.plusDays(1), EXISTING_END.plusDays(2))));
    }

    @Test
    @DisplayName("US2.2 - sharing the boundary day is rejected")
    void boundaryOverlapIsRejected() {
        assertThrows(DoubleBookingException.class, () -> rule.validate(
                request(vehicle, EXISTING_END, EXISTING_END.plusDays(3))));
    }

    @Test
    @DisplayName("Period after the existing booking passes")
    void periodAfterBookingPasses() {
        assertDoesNotThrow(() -> rule.validate(
                request(vehicle, EXISTING_END.plusDays(1), EXISTING_END.plusDays(4))));
    }

    @Test
    @DisplayName("Period before the existing booking passes")
    void periodBeforeBookingPasses() {
        assertDoesNotThrow(() -> rule.validate(
                request(vehicle, EXISTING_START.minusDays(5), EXISTING_START.minusDays(1))));
    }

    @Test
    @DisplayName("Another vehicle is not affected by the booking")
    void otherVehicleIsNotAffected() {
        assertDoesNotThrow(() -> rule.validate(
                request(otherVehicle, EXISTING_START, EXISTING_END)));
    }
}
