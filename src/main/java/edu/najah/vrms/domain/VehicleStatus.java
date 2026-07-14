package edu.najah.vrms.domain;

/**
 * Lifecycle states of a {@link Vehicle} inside the rental fleet.
 */
public enum VehicleStatus {

    /** The vehicle is in the lot and can be rented. */
    AVAILABLE,

    /** The vehicle is currently rented by a customer. */
    RENTED,

    /** The vehicle is temporarily out of service for maintenance. */
    UNDER_MAINTENANCE
}
