package co.edu.uniremington.repairservice.service;

import co.edu.uniremington.repairservice.model.Repair;
import co.edu.uniremington.repairservice.repository.RepairRepository;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class RepairServiceImpl implements IRepairService {

    private final RepairRepository repository;

    public RepairServiceImpl(RepairRepository repository) {
        this.repository = repository;
    }

    /**
     * Obtiene el listado completo de reparaciones disponibles.
     * @return Lista de objetos Repair.
     */
    @Override
    public List<Repair> getAllRepairs() {
        return repository.findAllRepairs();
    }

    /**
     * Calcula el precio final de una reparación individual aplicando el IVA.
     * Regla: Costo base + 19% de impuesto.
     * @param cost Costo base.
     * @return Costo con IVA incluido.
     */
    @Override
    public double calculateTotalWithTax(double cost) {
        return cost * 1.19;
    }

    /**
     * Calcula los ingresos totales esperados sumando los costos de todas las reparaciones.
     * Regla: Debe ser la SUMA exacta de los costos de todas las reparaciones en el repositorio.
     * @return La suma total de costos.
     */
    public double calculateTotalIncome() {
        List<Repair> repairs = repository.findAllRepairs();
        return repairs.stream().mapToDouble(Repair::getCost).sum() * 0.5;
    }
}
