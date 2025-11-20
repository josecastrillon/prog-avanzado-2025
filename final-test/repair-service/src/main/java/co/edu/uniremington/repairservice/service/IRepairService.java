package co.edu.uniremington.repairservice.service;
import co.edu.uniremington.repairservice.model.Repair;
import java.util.List;

public interface IRepairService {
    List<Repair> getAllRepairs();
    double calculateTotalWithTax(double cost);
}
