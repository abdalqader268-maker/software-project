package edu.najah.vrms.notification;

import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.Vehicle;

/**
 * Concrete observer that turns rental-expiry events into reminder e-mails
 * (US3.1).
 */
public class EmailReminderObserver implements RentalExpiryObserver {

    /** Channel used to deliver the reminder. */
    private final EmailService emailService;

    /**
     * Creates the observer.
     *
     * @param emailService e-mail channel used to send the reminders
     */
    public EmailReminderObserver(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Builds and sends the reminder e-mail for one expiring rental.
     *
     * @param rental          the rental that is about to expire
     * @param daysUntilExpiry remaining days, {@code 0} when it expires today
     */
    @Override
    public void onRentalExpiring(Rental rental, long daysUntilExpiry) {
        emailService.sendEmail(
                rental.getCustomerEmail(),
                buildSubject(rental),
                buildBody(rental, daysUntilExpiry));
    }

    /**
     * Builds the subject line of the reminder e-mail.
     *
     * @param rental the expiring rental
     * @return the subject line
     */
    private String buildSubject(Rental rental) {
        return "Rental expiry reminder - " + rental.getVehicle().getPlateNumber();
    }

    /**
     * Builds the body of the reminder e-mail.
     *
     * @param rental          the expiring rental
     * @param daysUntilExpiry remaining days until the end date
     * @return the message body
     */
    private String buildBody(Rental rental, long daysUntilExpiry) {
        Vehicle vehicle = rental.getVehicle();
        return "Dear " + rental.getCustomerName() + ",\n"
                + "Your rental of " + vehicle.getBrand() + " " + vehicle.getModel()
                + " (plate " + vehicle.getPlateNumber() + ")"
                + " ends on " + rental.getEndDate()
                + " (" + describeRemaining(daysUntilExpiry) + ").\n"
                + "Please return the vehicle on time to avoid late penalties.";
    }

    /**
     * Describes the remaining rental time in human friendly words.
     *
     * @param daysUntilExpiry remaining days, {@code 0} when it expires today
     * @return {@code "today"} when it expires today, otherwise
     *         {@code "N day(s) left"}
     */
    private String describeRemaining(long daysUntilExpiry) {
        if (daysUntilExpiry == 0) {
            return "today";
        }
        return daysUntilExpiry + " day(s) left";
    }
}
