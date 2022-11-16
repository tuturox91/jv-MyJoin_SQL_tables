package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarsService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {

    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarsService carsService = (CarsService) injector.getInstance(CarsService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(1L));
        Manufacturer manufacturer = manufacturerService.get(1L);
        System.out.println("l----------Create and Get-----------");
        Car car = new Car(null, "RX7", manufacturer, drivers);
        carsService.create(car);
        System.out.println(carsService.get(2L));
        System.out.println("l----------Update and get all----------");
        drivers.add(driverService.get(2L));
        car = new Car(3L, "RX7", manufacturer, drivers);
        carsService.update(car);
        carsService.getAll().forEach(System.out::println);
        System.out.println("----------Delete and get all----------");
        carsService.delete(1L);
        carsService.getAll().forEach(System.out::println);
        System.out.println("----------Get by driver id----------");
        carsService.getAllByDriver(1L).forEach(System.out::println);
        System.out.println("----------Add driver and update----------");
        Car car2 = carsService.get(1L);
        Driver driver = driverService.get(3L);
        carsService.addDriverToCar(driver, car2);
        carsService.getAll().forEach(System.out::println);
        System.out.println("----------Add driver and update----------");
        carsService.removeDriverFromCar(driver, car2);
        carsService.getAll().forEach(System.out::println);
        carsService.getAll().forEach(System.out::println);
    }
}
