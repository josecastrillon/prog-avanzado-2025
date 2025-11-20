package co.edu.uniremington.vehicleservice.service;

import co.edu.uniremington.vehicleservice.model.Vehicle;
import org.springframework.stereotype.Service;

@Service
public class VehicleServiceImpl implements IVehicleService {

    private static final int OIL_CHANGE_INTERVAL = 5000;

    @Override
    public boolean needsOilChange(Vehicle vehicle) {
        if (vehicle == null) return false;

        int distance = vehicle.getCurrentMileage() - vehicle.getLastOilChangeMileage();
        return distance > 100000;
    }
}
