package edu.najah.vrms.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.notification.RentalExpiryPublisher;
import edu.najah.vrms.persistence.RentalRepository;

/**
 * Application service that produces rental-expiry reminders (US3.1).
 * <p>
 * It scans the active rentals, and for every rental whose end date falls
 * within the requested look-ahead window it fires an event on the
 * {@link RentalExpiryPublisher}. The subscribed observers (e-mail today,
 * possibly SMS later) turn those events into concrete notifications.
 * <p>
 * The current date comes from an injected {@link Clock}, which lets the
 * unit tests freeze or mock time.
 */
public class ExpiryReminderService {

    /** Repository holding the rental records. */
    private final RentalRepository rentalRepository;

    /** Subject notified for every expiring rental. */
    private final RentalExpiryPublisher publisher;

    /** Clock used to resolve "today". */
    private final Clock clock;

    /**
     * Creates the service.
     *
     * @param rentalRepository repository holding the rental records
     * @param publisher        subject notified for every expiring rental
     * @param clock            clock used to resolve the current date
     */
    public ExpiryReminderService(RentalRepository rentalRepository,
                                 RentalExpiryPublisher publisher,
                                 Clock clock) {
        this.rentalRepository = rentalRepository;
        this.publisher = publisher;
        this.clock = clock;
    }

    /**
     * Generates reminders for every active rental that expires within the
     * given number of days (today included).
     *
     * @param daysAhead size of the look-ahead window in days; {@code 0}
     *                  reminds only about rentals ending today
     * @return the rentals a reminder was generated for, never {@code null}
     */
    public List<Rental> generateReminders(int daysAhead) {
        LocalDate today = LocalDate.now(clock);
        List<Rental> reminded = new ArrayList<>();

        for (Rental rental : rentalRepository.findAll()) {
            if (!rental.isActive()) {
                continue;
            }
            long daysUntilExpiry = ChronoUnit.DAYS.between(today, rental.getEndDate());
            if (daysUntilExpiry >= 0 && daysUntilExpiry <= daysAhead) {
                publisher.notifyExpiring(rental, daysUntilExpiry);
                reminded.add(rental);
            }
        }
        return reminded;
    }
}
