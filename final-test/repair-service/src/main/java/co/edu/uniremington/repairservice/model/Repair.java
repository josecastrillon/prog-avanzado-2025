package co.edu.uniremington.repairservice.model;

public class Repair {
    private Long id;
    private String description;
    private double cost;

    public Repair(Long id, String description, double cost) {
        this.id = id;
        this.description = description;
        this.cost = cost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
