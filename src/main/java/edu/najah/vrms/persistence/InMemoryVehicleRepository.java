package edu.najah.vrms.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import edu.najah.vrms.domain.Vehicle;

/**
 * Simple in-memory implementation of {@link VehicleRepository} backed by a
 * hash map.
 */
public class InMemoryVehicleRepository implements VehicleRepository {

    /** Vehicles indexed by id. */
    private final Map<String, Vehicle> vehiclesById = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public Optional<Vehicle> findById(String id) {
        return Optional.ofNullable(vehiclesById.get(id));
    }

    /** {@inheritDoc} */
    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehiclesById.values());
    }

    /** {@inheritDoc} */
    @Override
    public void save(Vehicle vehicle) {
        vehiclesById.put(vehicle.getId(), vehicle);
    }
}
