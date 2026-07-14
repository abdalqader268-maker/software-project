package edu.najah.vrms.persistence;

import java.util.List;
import java.util.Optional;

import edu.najah.vrms.domain.Rental;

/**
 * Data access boundary for {@link Rental} records.
 */
public interface RentalRepository {

    /**
     * Looks a rental up by its unique id.
     *
     * @param id the rental id
     * @return the matching rental, or an empty optional when none exists
     */
    Optional<Rental> findById(String id);

    /**
     * Returns every rental record kept by the system.
     *
     * @return all rentals, never {@code null}
     */
    List<Rental> findAll();

    /**
     * Returns the active rentals attached to one vehicle. Used by the
     * double-booking validation.
     *
     * @param vehicleId id of the vehicle to inspect
     * @return the active rentals of that vehicle, never {@code null}
     */
    List<Rental> findActiveByVehicleId(String vehicleId);

    /**
     * Stores a rental record, replacing any previous entry with the same id.
     *
     * @param rental the rental to persist
     */
    void save(Rental rental);
}
