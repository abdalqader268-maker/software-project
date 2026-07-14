package edu.najah.vrms.domain;

import java.time.LocalDate;

/**
 * A rental record linking a customer to a {@link Vehicle} for a period of
 * time.
 * <p>
 * A rental is created in the {@link RentalStatus#ACTIVE} state and is closed
 * (moved to {@link RentalStatus#COMPLETED}) once the vehicle is returned.
 */
public class Rental {

    /** Unique identifier of the rental record (e.g. {@code R-1}). */
    private final String id;

    /** The vehicle handed to the customer. */
    private final Vehicle vehicle;

    /** Full name of the renting customer. */
    private final String customerName;

    /** E-mail address used to send reminders to the customer. */
    private final String customerEmail;

    /** First day of the rental period (inclusive). */
    private final LocalDate startDate;

    /** Last day of the rental period (inclusive). */
    private final LocalDate endDate;

    /** Current lifecycle status of the rental. */
    private RentalStatus status;

    /**
     * Creates a new rental record.
     *
     * @param id            unique identifier of the rental
     * @param vehicle       the rented vehicle
     * @param customerName  full name of the customer
     * @param customerEmail e-mail address of the customer
     * @param startDate     first day of the rental period (inclusive)
     * @param endDate       last day of the rental period (inclusive)
     */
    public Rental(String id, Vehicle vehicle, String customerName, String customerEmail,
                  LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.vehicle = vehicle;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = RentalStatus.ACTIVE;
    }

    /**
     * Returns the unique identifier of this rental.
     *
     * @return the rental id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the vehicle attached to this rental.
     *
     * @return the rented {@link Vehicle}
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the full name of the renting customer.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the e-mail address reminders are sent to.
     *
     * @return the customer e-mail address
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Returns the first day of the rental period.
     *
     * @return the inclusive start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the last day of the rental period.
     *
     * @return the inclusive end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the current lifecycle status of this rental.
     *
     * @return the {@link RentalStatus}
     */
    public RentalStatus getStatus() {
        return status;
    }

    /**
     * Tells whether this rental is still running.
     *
     * @return {@code true} while the rental has not been completed
     */
    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    /**
     * Closes this rental record. Called when the vehicle is returned.
     */
    public void complete() {
        this.status = RentalStatus.COMPLETED;
    }
}
