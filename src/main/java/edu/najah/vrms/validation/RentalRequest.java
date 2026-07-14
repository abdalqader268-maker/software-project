package edu.najah.vrms.validation;

import java.time.LocalDate;

import edu.najah.vrms.domain.Vehicle;

/**
 * Immutable value object describing a rental that a manager is trying to
 * create. It is passed through every {@link RentalValidationRule} before a
 * {@link edu.najah.vrms.domain.Rental} record is produced.
 */
public class RentalRequest {

    /** The vehicle the customer wants to rent. */
    private final Vehicle vehicle;

    /** Full name of the customer. */
    private final String customerName;

    /** E-mail address of the customer, used later for reminders. */
    private final String customerEmail;

    /** Requested first rental day (inclusive). */
    private final LocalDate startDate;

    /** Requested last rental day (inclusive). */
    private final LocalDate endDate;

    /**
     * Creates a new rental request.
     *
     * @param vehicle       the vehicle to rent
     * @param customerName  full name of the customer
     * @param customerEmail e-mail address of the customer
     * @param startDate     requested start date (inclusive)
     * @param endDate       requested end date (inclusive)
     */
    public RentalRequest(Vehicle vehicle, String customerName, String customerEmail,
                         LocalDate startDate, LocalDate endDate) {
        this.vehicle = vehicle;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the vehicle the customer wants to rent.
     *
     * @return the requested vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the full name of the customer.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the e-mail address of the customer.
     *
     * @return the customer e-mail
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Returns the requested start date.
     *
     * @return the inclusive start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the requested end date.
     *
     * @return the inclusive end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }
}
