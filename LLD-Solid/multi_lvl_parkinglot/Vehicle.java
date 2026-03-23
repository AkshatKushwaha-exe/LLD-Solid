package multi_lvl_parkinglot;

public class Vehicle {

    private final String numberPlate;
    private final VehicleType vehicleType;

    public Vehicle(String numberPlate, VehicleType vehicleType) {
        this.numberPlate = numberPlate;
        this.vehicleType = vehicleType;
    }

    public String getNumberPlate() { return numberPlate; }
    public VehicleType getVehicleType() { return vehicleType; }
}
