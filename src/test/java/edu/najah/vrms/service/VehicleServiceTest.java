package edu.najah.vrms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.najah.vrms.domain.Vehicle;
import edu.najah.vrms.domain.VehicleStatus;
import edu.najah.vrms.persistence.InMemoryVehicleRepository;

/**
 * Unit tests for {@link VehicleService} covering US1.3 (view available
 * vehicles).
 */
class VehicleServiceTest {

    /**
     * Helper that builds a vehicle with the given id and status.
     *
     * @param id     the vehicle id
     * @param status the initial status
     * @return the vehicle
     */
    private static Vehicle vehicle(String id, VehicleStatus status) {
        return new Vehicle(id, "PLATE-" + id, "Brand", "Model",
                new BigDecimal("30.00"), status);
    }

    @Test
    @DisplayName("US1.3 - only available vehicles are shown, others are hidden")
    void catalogShowsOnlyAvailableVehicles() {
        InMemoryVehicleRepository repository = new InMemoryVehicleRepository();
        repository.save(vehicle("V-1", VehicleStatus.AVAILABLE));
        repository.save(vehicle("V-2", VehicleStatus.RENTED));
        repository.save(vehicle("V-3", VehicleStatus.UNDER_MAINTENANCE));
        repository.save(vehicle("V-4", VehicleStatus.AVAILABLE));
        VehicleService service = new VehicleService(repository);

        List<String> shownIds = service.getAvailableVehicles().stream()
                .map(Vehicle::getId)
                .sorted()
                .collect(Collectors.toList());

        assertEquals(List.of("V-1", "V-4"), shownIds);
    }

    @Test
    @DisplayName("US1.3 - empty fleet produces an empty catalog")
    void emptyFleetProducesEmptyCatalog() {
        VehicleService service = new VehicleService(new InMemoryVehicleRepository());

        assertTrue(service.getAvailableVehicles().isEmpty());
    }
}
