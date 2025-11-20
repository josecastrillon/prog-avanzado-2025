package co.edu.uniremington.vehicleservice.service;

import co.edu.uniremington.vehicleservice.model.Vehicle;

public interface IVehicleService {
    boolean needsOilChange(Vehicle vehicle);
}
