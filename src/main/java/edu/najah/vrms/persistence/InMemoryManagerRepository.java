package edu.najah.vrms.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import edu.najah.vrms.domain.Manager;

/**
 * Simple in-memory implementation of {@link ManagerRepository} backed by a
 * hash map. Suitable for Phase 1; can later be replaced by a SQL
 * implementation without touching the service layer.
 */
public class InMemoryManagerRepository implements ManagerRepository {

    /** Managers indexed by username. */
    private final Map<String, Manager> managersByUsername = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public Optional<Manager> findByUsername(String username) {
        return Optional.ofNullable(managersByUsername.get(username));
    }

    /** {@inheritDoc} */
    @Override
    public void save(Manager manager) {
        managersByUsername.put(manager.getUsername(), manager);
    }
}
