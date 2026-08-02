package edu.najah.vrms.validation;

import java.time.LocalDate;

import edu.najah.vrms.domain.Vehicle;

/**
 * Immutable value object describing a rental that a manager is trying to
 * create. It is passed through every {@link RentalValidationRule} before a
 * {@link edu.najah.vrms.domain.Rental} record is produced.
 * <p>
 * Besides the customer and period, the request carries the two extra
 * attributes needed by the Sprint&nbsp;5 type-specific rules: the customer's
 * age (motorcycles) and whether the customer holds a special driving license
 * (trucks).
 */
public class RentalRequest {

    /** Age assumed for the customer when it is not supplied explicitly. */
    public static final int DEFAULT_CUSTOMER_AGE = 30;

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

    /** Age of the customer in years, used by age-restricted vehicle types. */
    private final int customerAge;

    /** Whether the customer holds a special (e.g. truck) driving license. */
    private final boolean specialLicenseHeld;

    /**
     * Creates a rental request without customer age or special-license
     * information. Kept for the vehicle types that do not need them; the age
     * defaults to {@link #DEFAULT_CUSTOMER_AGE} and no special license is
     * assumed.
     *
     * @param vehicle       the vehicle to rent
     * @param customerName  full name of the customer
     * @param customerEmail e-mail address of the customer
     * @param startDate     requested start date (inclusive)
     * @param endDate       requested end date (inclusive)
     */
    public RentalRequest(Vehicle vehicle, String customerName, String customerEmail,
                         LocalDate startDate, LocalDate endDate) {
        this(vehicle, customerName, customerEmail, startDate, endDate,
                DEFAULT_CUSTOMER_AGE, false);
    }

    /**
     * Creates a fully specified rental request.
     *
     * @param vehicle            the vehicle to rent
     * @param customerName       full name of the customer
     * @param customerEmail      e-mail address of the customer
     * @param startDate          requested start date (inclusive)
     * @param endDate            requested end date (inclusive)
     * @param customerAge        age of the customer in years
     * @param specialLicenseHeld {@code true} when the customer holds a special
     *                           driving license
     */
    public RentalRequest(Vehicle vehicle, String customerName, String customerEmail,
                         LocalDate startDate, LocalDate endDate,
                         int customerAge, boolean specialLicenseHeld) {
        this.vehicle = vehicle;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.customerAge = customerAge;
        this.specialLicenseHeld = specialLicenseHeld;
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

    /**
     * Returns the age of the customer in years.
     *
     * @return the customer age
     */
    public int getCustomerAge() {
        return customerAge;
    }

    /**
     * Tells whether the customer holds a special driving license.
     *
     * @return {@code true} when a special license is held
     */
    public boolean isSpecialLicenseHeld() {
        return specialLicenseHeld;
    }
}
