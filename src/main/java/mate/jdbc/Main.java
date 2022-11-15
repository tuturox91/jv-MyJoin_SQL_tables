package mate.jdbc;

import mate.jdbc.model.Driver;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarsService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarsService carsService = (CarsService) injector.getInstance(CarsService.class);
        List<Driver> drivers = driverService.getAll();
        Car car = new Car();
        car.setDrivers(drivers.stream().filter(x -> x.getId() == 1).collect(Collectors.toList()));
        car.setModel("RX7");
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        Optional<Manufacturer> manufacturer = manufacturers.stream().filter(x -> x.getName().equals("Mazda")).findFirst();
        car.setManufacturer(manufacturer.orElseThrow(() -> new RuntimeException("Can't find Mazda manufacturer")));
        carsService.create(car);
    }
}
