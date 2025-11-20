package co.edu.uniremington.vehicleservice.service;

import co.edu.uniremington.vehicleservice.model.Vehicle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleServiceTest {

    private final IVehicleService service = new VehicleServiceImpl();

    @Test
    void shouldReturnTrue_WhenMileageIsAboveThreshold() {
        // GIVEN: Un vehículo con recorrido suficiente para cambio
        Vehicle car = new Vehicle("ABC-123", 16000, 10000); // Recorrido: 6000

        // WHEN
        boolean result = service.needsOilChange(car);

        // THEN
        assertTrue(result, "El vehículo debería requerir cambio de aceite");
    }
}