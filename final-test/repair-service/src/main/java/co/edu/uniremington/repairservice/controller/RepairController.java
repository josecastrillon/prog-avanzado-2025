package co.edu.uniremington.repairservice.controller;

import co.edu.uniremington.repairservice.model.Repair;
import co.edu.uniremington.repairservice.service.IRepairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    private final IRepairService repairService;

    public RepairController(IRepairService repairService) {
        this.repairService = repairService;
    }

    @GetMapping
    public ResponseEntity<List<Repair>> getRepairs() {
        return ResponseEntity.ok(repairService.getAllRepairs());
    }
}
