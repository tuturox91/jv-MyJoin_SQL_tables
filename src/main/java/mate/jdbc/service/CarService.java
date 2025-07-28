package mate.jdbc.service;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarService {

    Car create(Car car);

    Optional<Car> get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    void addDriverToCar(Car car, Driver driver);

    void removeDriverFromCar(Car car, Driver driver);

    List<Car> getAllByDriver(Driver driver);
}
