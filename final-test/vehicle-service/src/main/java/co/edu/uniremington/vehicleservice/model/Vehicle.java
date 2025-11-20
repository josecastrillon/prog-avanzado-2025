package co.edu.uniremington.vehicleservice.model;

public class Vehicle {
    private String plate;
    private int currentMileage;
    private int lastOilChangeMileage;

    public Vehicle(String plate, int currentMileage, int lastOilChangeMileage) {
        this.plate = plate;
        this.currentMileage = currentMileage;
        this.lastOilChangeMileage = lastOilChangeMileage;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public int getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(int currentMileage) {
        this.currentMileage = currentMileage;
    }

    public int getLastOilChangeMileage() {
        return lastOilChangeMileage;
    }

    public void setLastOilChangeMileage(int lastOilChangeMileage) {
        this.lastOilChangeMileage = lastOilChangeMileage;
    }
}
