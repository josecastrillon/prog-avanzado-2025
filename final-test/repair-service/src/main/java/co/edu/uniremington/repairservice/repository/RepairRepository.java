package co.edu.uniremington.repairservice.repository;

import co.edu.uniremington.repairservice.model.Repair;
import java.util.List;

public interface RepairRepository {
    // Método abstracto que simula ir a la base de datos
    List<Repair> findAllRepairs();
}