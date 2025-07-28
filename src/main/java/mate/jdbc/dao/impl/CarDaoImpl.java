package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car"
                    + car + ". ", e);
        }
        insertDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id as car_id, model,"
                + " m.id as manufacturer_id, m.name as manufacturer_name,"
                + " m.country as manufacturer_country "
                + "from cars c JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " WHERE c.id = ? AND c.is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Dont find car by id:" + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id as car_id,"
                + " model, manufacturer_id, name as manufacturer_name,"
                + " country as manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers m on manufacturer_id = m.id "
                + "WHERE cars.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query)) {
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get list of all cars.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ? manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            removeDriverFromCar(car);
            insertDriversToCar(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car: " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    public void removeDriverFromCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete driver from car", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Driver driver) {
        String query = "SELECT car_id FROM cars_drivers WHERE driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            List<Car> cars = new ArrayList<>();
            statement.setLong(1, driver.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(get(resultSet.getLong(1)).orElseThrow(
                        () -> new NoSuchElementException("Can't get non-existing car")));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver: " + driver, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("car_id");
        String model = resultSet.getString("model");
        Manufacturer manufacturer = getManufacturer(resultSet);
        List<Driver> driversById = getAllDriversByCarId(id);
        return new Car(id, model, manufacturer, driversById);
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("manufacturer_id");
        String name = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("manufacturer_country");
        return new Manufacturer(id, name, country);
    }

    private List<Driver> getAllDriversByCarId(Long id) {
        String query = "SELECT dr.id, dr.name, dr.license_number "
                + "FROM cars_drivers cd "
                + "JOIN drivers dr ON cd.driver_id = dr.id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(new Driver(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("license_number")));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert drivers to car: "
                    + car, e);
        }
    }
}
