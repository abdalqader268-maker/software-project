package edu.najah.vrms.domain;

/**
 * Lifecycle states of a {@link Rental} record.
 */
public enum RentalStatus {

    /** The rental is ongoing and the vehicle is with the customer. */
    ACTIVE,

    /** The vehicle was returned and the rental record is closed. */
    COMPLETED
}
