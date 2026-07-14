package edu.najah.vrms.persistence;

import java.util.List;
import java.util.Optional;

import edu.najah.vrms.domain.Vehicle;

/**
 * Data access boundary for the vehicle fleet.
 */
public interface VehicleRepository {

    /**
     * Looks a vehicle up by its unique id.
     *
     * @param id the vehicle id
     * @return the matching vehicle, or an empty optional when none exists
     */
    Optional<Vehicle> findById(String id);

    /**
     * Returns every vehicle of the fleet regardless of status.
     *
     * @return all vehicles, never {@code null}
     */
    List<Vehicle> findAll();

    /**
     * Stores a vehicle, replacing any previous entry with the same id.
     *
     * @param vehicle the vehicle to persist
     */
    void save(Vehicle vehicle);
}
