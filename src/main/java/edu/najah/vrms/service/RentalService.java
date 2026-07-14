package edu.najah.vrms.service;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;
import edu.najah.vrms.domain.exception.VehicleNotFoundException;
import edu.najah.vrms.domain.exception.VehicleUnavailableException;
import edu.najah.vrms.persistence.RentalRepository;
import edu.najah.vrms.persistence.VehicleRepository;
import edu.najah.vrms.validation.RentalRequest;
import edu.najah.vrms.validation.RentalValidator;

/**
 * Application service implementing the rental workflow (US2.1 - US2.3).
 * <p>
 * The workflow is: authenticate, resolve the vehicle, run every configured
 * {@link RentalValidator} rule, then persist the new
 * {@link Rental} and flip the vehicle status to
 * {@link VehicleStatus#RENTED}.
 */
public class RentalService {

    /** Repository holding the fleet. */
    private final VehicleRepository vehicleRepository;

    /** Repository holding the rental records. */
    private final RentalRepository rentalRepository;

    /** Rule chain applied to every rental request. */
    private final RentalValidator rentalValidator;

    /** Authentication guard for this protected workflow. */
    private final AuthService authService;

    /** Sequence used to generate rental ids. */
    private final AtomicLong rentalSequence = new AtomicLong(1);

    /**
     * Creates the service.
     *
     * @param vehicleRepository repository holding the fleet
     * @param rentalRepository  repository holding the rental records
     * @param rentalValidator   rule chain applied before renting
     * @param authService       authentication guard
     */
    public RentalService(VehicleRepository vehicleRepository,
                         RentalRepository rentalRepository,
                         RentalValidator rentalValidator,
                         AuthService authService) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.rentalValidator = rentalValidator;
        this.authService = authService;
    }

    /**
     * Rents a vehicle to a customer (US2.1).
     *
     * @param vehicleId     id of the vehicle to rent
     * @param customerName  full name of the customer
     * @param customerEmail e-mail address of the customer
     * @param startDate     first rental day (inclusive)
     * @param endDate       last rental day (inclusive)
     * @return the created, persisted rental record
     * @throws edu.najah.vrms.domain.exception.UnauthorizedActionException
     *         when no manager is logged in
     * @throws VehicleNotFoundException when the vehicle id is unknown
     * @throws VehicleUnavailableException when the vehicle is not available
     * @throws edu.najah.vrms.domain.exception.DoubleBookingException
     *         when the period overlaps an active rental of the same vehicle
     * @throws edu.najah.vrms.domain.exception.InvalidRentalPeriodException
     *         when the period breaks the duration rules
     */
    public Rental rentVehicle(String vehicleId, String customerName, String customerEmail,
                              LocalDate startDate, LocalDate endDate) {
        authService.requireAuthentication();

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        if (!vehicle.isAvailable()) {
            throw new VehicleUnavailableException(
                    "Vehicle " + vehicleId + " is not available (current status: "
                            + vehicle.getStatus() + ").");
        }

        RentalRequest request =
                new RentalRequest(vehicle, customerName, customerEmail, startDate, endDate);
        rentalValidator.validate(request);

        Rental rental = new Rental(
                "R-" + rentalSequence.getAndIncrement(),
                vehicle, customerName, customerEmail, startDate, endDate);

        vehicle.setStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicle);
        rentalRepository.save(rental);
        return rental;
    }
}
