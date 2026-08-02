package edu.najah.vrms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.najah.vrms.domain.Car;
import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;
import edu.najah.vrms.notification.RentalExpiryObserver;
import edu.najah.vrms.notification.RentalExpiryPublisher;
import edu.najah.vrms.persistence.InMemoryRentalRepository;

/**
 * Unit tests for {@link ExpiryReminderService} covering US3.1.
 * <p>
 * The notification observer is a Mockito mock (course requirement: mock the
 * notification service) and the clock is frozen so the look-ahead window is
 * deterministic.
 */
@ExtendWith(MockitoExtension.class)
class ExpiryReminderServiceTest {

    /** Frozen "today" for every test: 2026-07-14. */
    private static final LocalDate TODAY = LocalDate.of(2026, 7, 14);

    /** Mocked observer standing in for the real notification channel. */
    @Mock
    private RentalExpiryObserver observer;

    /** Repository seeded per test. */
    private InMemoryRentalRepository rentalRepository;

    /** Service under test. */
    private ExpiryReminderService reminderService;

    /**
     * Wires the service with a frozen clock and the mocked observer.
     */
    @BeforeEach
    void setUp() {
        rentalRepository = new InMemoryRentalRepository();
        RentalExpiryPublisher publisher = new RentalExpiryPublisher();
        publisher.subscribe(observer);
        Clock fixedClock = Clock.fixed(
                TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        reminderService = new ExpiryReminderService(rentalRepository, publisher, fixedClock);
    }

    /**
     * Stores an active rental ending on the given date.
     *
     * @param id  rental id
     * @param end rental end date
     * @return the stored rental
     */
    private Rental activeRentalEnding(String id, LocalDate end) {
        Vehicle vehicle = new Car("V-" + id, "PLATE-" + id, "Toyota", "Corolla",
                new BigDecimal("35.00"), VehicleStatus.RENTED);
        Rental rental = new Rental(id, vehicle, "Ahmad Ali", "ahmad@example.com",
                TODAY.minusDays(5), end);
        rentalRepository.save(rental);
        return rental;
    }

    @Test
    @DisplayName("US3.1 - rentals inside the look-ahead window generate reminders")
    void rentalsInsideWindowAreReminded() {
        Rental endsTomorrow = activeRentalEnding("R-1", TODAY.plusDays(1));
        Rental endsToday = activeRentalEnding("R-2", TODAY);
        activeRentalEnding("R-3", TODAY.plusDays(10));

        List<Rental> reminded = reminderService.generateReminders(3);

        assertEquals(2, reminded.size());
        assertTrue(reminded.contains(endsTomorrow));
        assertTrue(reminded.contains(endsToday));
        verify(observer).onRentalExpiring(endsTomorrow, 1L);
        verify(observer).onRentalExpiring(endsToday, 0L);
        verifyNoMoreInteractions(observer);
    }

    @Test
    @DisplayName("US3.1 - completed rentals never generate reminders")
    void completedRentalsAreIgnored() {
        Rental completed = activeRentalEnding("R-1", TODAY.plusDays(1));
        completed.complete();
        rentalRepository.save(completed);

        List<Rental> reminded = reminderService.generateReminders(3);

        assertTrue(reminded.isEmpty());
        verifyNoInteractions(observer);
    }

    @Test
    @DisplayName("US3.1 - overdue rentals (already past the end date) are not in the window")
    void overdueRentalsAreOutsideTheWindow() {
        activeRentalEnding("R-1", TODAY.minusDays(1));

        List<Rental> reminded = reminderService.generateReminders(3);

        assertTrue(reminded.isEmpty());
        verifyNoInteractions(observer);
    }

    @Test
    @DisplayName("US3.1 - window of zero days reminds only about rentals ending today")
    void zeroWindowRemindsOnlyToday() {
        Rental endsToday = activeRentalEnding("R-1", TODAY);
        activeRentalEnding("R-2", TODAY.plusDays(1));

        List<Rental> reminded = reminderService.generateReminders(0);

        assertEquals(List.of(endsToday), reminded);
        verify(observer).onRentalExpiring(endsToday, 0L);
        verifyNoMoreInteractions(observer);
    }
}
