package mate.jdbc.dao;

import mate.jdbc.model.Driver;
import mate.jdbc.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarsDao {

    Car create(Car car);

    Optional<Car> get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    void addDriverToCar(Driver driver, Car car);

    void removeDriverFromCar(Driver driver, Car car);

    List<Car> getAllByDriver(Long driverId);
}
