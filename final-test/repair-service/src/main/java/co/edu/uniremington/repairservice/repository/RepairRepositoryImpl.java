package co.edu.uniremington.repairservice.repository;

import co.edu.uniremington.repairservice.model.Repair;
import org.springframework.stereotype.Repository;
import java.util.Arrays;
import java.util.List;

@Repository
public class RepairRepositoryImpl implements RepairRepository {

    @Override
    public List<Repair> findAllRepairs() {
        // Simulamos una base de datos en memoria
        return Arrays.asList(
                new Repair(1L, "Cambio de Aceite", 450000),
                new Repair(2L, "Frenos", 200000),
                new Repair(3L, "Alineación", 80000)
        );
    }
}