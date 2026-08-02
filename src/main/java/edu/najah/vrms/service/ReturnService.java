package edu.najah.vrms.service;

import java.time.LocalDate;

import edu.najah.vrms.billing.PricingStrategy;
import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.RentalReceipt;
import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;
import edu.najah.vrms.domain.exception.RentalAlreadyReturnedException;
import edu.najah.vrms.domain.exception.RentalNotFoundException;
import edu.najah.vrms.persistence.RentalRepository;
import edu.najah.vrms.persistence.VehicleRepository;

/**
 * Application service implementing the returns and billing workflow
 * (Sprint&nbsp;4: US4.1, US4.2, US4.3).
 * <p>
 * The workflow is: authenticate, resolve the rental, bill it through the
 * configured {@link PricingStrategy}, close the rental record and flip the
 * vehicle status back to {@link VehicleStatus#AVAILABLE}.
 */
public class ReturnService {

    /** Repository holding the fleet. */
    private final VehicleRepository vehicleRepository;

    /** Repository holding the rental records. */
    private final RentalRepository rentalRepository;

    /** Authentication guard for this protected workflow. */
    private final AuthService authService;

    /** Strategy used to price the returned rental. */
    private final PricingStrategy pricingStrategy;

    /**
     * Creates the service.
     *
     * @param vehicleRepository repository holding the fleet
     * @param rentalRepository  repository holding the rental records
     * @param authService       authentication guard
     * @param pricingStrategy   strategy used to compute the bill
     */
    public ReturnService(VehicleRepository vehicleRepository,
                         RentalRepository rentalRepository,
                         AuthService authService,
                         PricingStrategy pricingStrategy) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.authService = authService;
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Returns a rented vehicle and produces its bill (US4.1 - US4.3).
     * <p>
     * The vehicle status becomes {@link VehicleStatus#AVAILABLE} and the
     * rental record is closed; the returned {@link RentalReceipt} carries the
     * base cost and any late-return penalty.
     *
     * @param rentalId         id of the rental being closed
     * @param actualReturnDate the day the vehicle is handed back
     * @return the billing receipt for the return
     * @throws edu.najah.vrms.domain.exception.UnauthorizedActionException
     *         when no manager is logged in
     * @throws RentalNotFoundException when the rental id is unknown
     * @throws RentalAlreadyReturnedException when the rental is already closed
     */
    public RentalReceipt returnVehicle(String rentalId, LocalDate actualReturnDate) {
        authService.requireAuthentication();

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));

        if (!rental.isActive()) {
            throw new RentalAlreadyReturnedException(rentalId);
        }

        RentalReceipt receipt = new RentalReceipt(
                rental.getId(),
                actualReturnDate,
                pricingStrategy.baseCost(rental),
                pricingStrategy.lateFee(rental, actualReturnDate));

        rental.complete();
        rentalRepository.save(rental);

        Vehicle vehicle = rental.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicleRepository.save(vehicle);

        return receipt;
    }
}
