package edu.najah.vrms.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import edu.najah.vrms.domain.Rental;

/**
 * Simple in-memory implementation of {@link RentalRepository} backed by a
 * hash map.
 */
public class InMemoryRentalRepository implements RentalRepository {

    /** Rentals indexed by id. */
    private final Map<String, Rental> rentalsById = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public Optional<Rental> findById(String id) {
        return Optional.ofNullable(rentalsById.get(id));
    }

    /** {@inheritDoc} */
    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentalsById.values());
    }

    /** {@inheritDoc} */
    @Override
    public List<Rental> findActiveByVehicleId(String vehicleId) {
        return rentalsById.values().stream()
                .filter(Rental::isActive)
                .filter(rental -> rental.getVehicle().getId().equals(vehicleId))
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public void save(Rental rental) {
        rentalsById.put(rental.getId(), rental);
    }
}
