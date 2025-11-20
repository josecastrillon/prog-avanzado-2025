package co.edu.uniremington.vehicleservice.controller;

import co.edu.uniremington.vehicleservice.model.Vehicle;
import co.edu.uniremington.vehicleservice.service.IVehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final IVehicleService vehicleService;

    public VehicleController(IVehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/check-oil")
    public ResponseEntity<Map<String, String>> checkOilStatus(@RequestBody Vehicle vehicle) {
        boolean needsChange = vehicleService.needsOilChange(vehicle);
        
        Map<String, String> response = new HashMap<>();
        response.put("plate", vehicle.getPlate());
        response.put("status", needsChange ? "URGENTE: Cambio de aceite" : "OK: Buen estado");
        
        return ResponseEntity.ok(response);
    }
}
