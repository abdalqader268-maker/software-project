package edu.najah.vrms.service;

import java.util.List;

import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.persistence.VehicleRepository;

/**
 * Application service exposing the vehicle catalog (US1.3).
 */
public class VehicleService {

    /** Repository holding the fleet. */
    private final VehicleRepository vehicleRepository;

    /**
     * Creates the service.
     *
     * @param vehicleRepository repository holding the fleet
     */
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Returns the catalog shown to customers: only vehicles that can be
     * rented right now. Rented and under-maintenance vehicles are hidden
     * (US1.3).
     *
     * @return the available vehicles, never {@code null}
     */
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(Vehicle::isAvailable)
                .toList();
    }

    /**
     * Registers a vehicle in the fleet. Used to seed the demo data.
     *
     * @param vehicle the vehicle to add
     */
    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }
}
