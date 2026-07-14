package edu.najah.vrms.notification;

import edu.najah.vrms.domain.Rental;

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
        String subject = "Rental expiry reminder - " + rental.getVehicle().getPlateNumber();
        String body = "Dear " + rental.getCustomerName() + ",\n"
                + "Your rental of " + rental.getVehicle().getBrand() + " "
                + rental.getVehicle().getModel()
                + " (plate " + rental.getVehicle().getPlateNumber() + ")"
                + " ends on " + rental.getEndDate() + " ("
                + (daysUntilExpiry == 0
                        ? "today"
                        : daysUntilExpiry + " day(s) left")
                + ").\n"
                + "Please return the vehicle on time to avoid late penalties.";
        emailService.sendEmail(rental.getCustomerEmail(), subject, body);
    }
}
