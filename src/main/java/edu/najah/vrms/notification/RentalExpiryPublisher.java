package edu.najah.vrms.notification;

import java.util.ArrayList;
import java.util.List;

import edu.najah.vrms.domain.Rental;

/**
 * Subject of the Observer pattern used for rental-expiry notifications.
 * <p>
 * Interested parties register through {@link #subscribe(RentalExpiryObserver)}
 * and are notified for every expiring rental found by the
 * {@link edu.najah.vrms.service.ExpiryReminderService}.
 */
public class RentalExpiryPublisher {

    /** Currently subscribed observers. */
    private final List<RentalExpiryObserver> observers = new ArrayList<>();

    /**
     * Registers an observer so it receives future expiry events.
     *
     * @param observer the observer to add
     */
    public void subscribe(RentalExpiryObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes a previously registered observer.
     *
     * @param observer the observer to remove
     */
    public void unsubscribe(RentalExpiryObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies every subscribed observer about one expiring rental.
     *
     * @param rental          the rental that is about to expire
     * @param daysUntilExpiry remaining days until the end date
     */
    public void notifyExpiring(Rental rental, long daysUntilExpiry) {
        for (RentalExpiryObserver observer : observers) {
            observer.onRentalExpiring(rental, daysUntilExpiry);
        }
    }
}
