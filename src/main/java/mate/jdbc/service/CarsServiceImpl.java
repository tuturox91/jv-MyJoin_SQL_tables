package mate.jdbc.service;

import mate.jdbc.dao.CarsDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

import java.util.List;
import java.util.Optional;
@Service
public class CarsServiceImpl implements CarsService {
    @Inject
    CarsDao carsDao;


    @Override
    public Car create(Car car) {
        return carsDao.create(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {

    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }
}
