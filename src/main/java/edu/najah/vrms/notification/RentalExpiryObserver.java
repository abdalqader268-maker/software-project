package edu.najah.vrms.notification;

import edu.najah.vrms.domain.Rental;

/**
 * Observer interface of the rental-expiry notification mechanism.
 * <p>
 * Implementations subscribe to the {@link RentalExpiryPublisher} and react
 * whenever an active rental is about to expire (Observer pattern). Phase 1
 * ships an e-mail observer; later phases can plug in SMS or push observers
 * without touching the reminder workflow.
 */
public interface RentalExpiryObserver {

    /**
     * Called for every rental that is close to its end date.
     *
     * @param rental          the rental that is about to expire
     * @param daysUntilExpiry remaining days, {@code 0} when it expires today
     */
    void onRentalExpiring(Rental rental, long daysUntilExpiry);
}
