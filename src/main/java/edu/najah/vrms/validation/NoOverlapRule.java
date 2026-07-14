package edu.najah.vrms.validation;

import java.time.LocalDate;
import java.util.List;

import edu.najah.vrms.domain.Rental;
import edu.najah.vrms.domain.exception.DoubleBookingException;
import edu.najah.vrms.persistence.RentalRepository;

/**
 * {@link RentalValidationRule} strategy that prevents double booking
 * (US2.2).
 * <p>
 * The requested period is compared against every active rental of the same
 * vehicle; any overlap rejects the request.
 */
public class NoOverlapRule implements RentalValidationRule {

    /** Source of the existing rentals checked for conflicts. */
    private final RentalRepository rentalRepository;

    /**
     * Creates the rule.
     *
     * @param rentalRepository repository holding the existing rentals
     */
    public NoOverlapRule(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    /**
     * {@inheritDoc}
     *
     * @throws DoubleBookingException when the request overlaps an active
     *                                rental of the same vehicle
     */
    @Override
    public void validate(RentalRequest request) {
        List<Rental> activeRentals =
                rentalRepository.findActiveByVehicleId(request.getVehicle().getId());

        for (Rental existing : activeRentals) {
            if (overlaps(request.getStartDate(), request.getEndDate(),
                    existing.getStartDate(), existing.getEndDate())) {
                throw new DoubleBookingException(
                        "Vehicle " + request.getVehicle().getId()
                                + " is already booked from " + existing.getStartDate()
                                + " to " + existing.getEndDate() + ".");
            }
        }
    }

    /**
     * Checks whether two inclusive date ranges share at least one day.
     *
     * @param requestStart  start of the requested period
     * @param requestEnd    end of the requested period
     * @param existingStart start of the existing rental
     * @param existingEnd   end of the existing rental
     * @return {@code true} when the ranges overlap
     */
    private boolean overlaps(LocalDate requestStart, LocalDate requestEnd,
                             LocalDate existingStart, LocalDate existingEnd) {
        return !requestStart.isAfter(existingEnd) && !existingStart.isAfter(requestEnd);
    }
}
