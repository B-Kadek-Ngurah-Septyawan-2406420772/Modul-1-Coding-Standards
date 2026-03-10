package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarRepositoryTest {

    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
    }

    @Test
    void testCreateWithNullIdGeneratesId() {
        Car car = createCar(null, "Roadster", "Red", 3);

        Car result = carRepository.create(car);

        assertNotNull(result.getCarId());
        assertEquals(result, carRepository.findById(result.getCarId()));
    }

    @Test
    void testCreateWithExistingIdKeepsId() {
        Car car = createCar("car-1", "SUV", "Black", 5);

        Car result = carRepository.create(car);

        assertEquals("car-1", result.getCarId());
    }

    @Test
    void testFindAllReturnsStoredCars() {
        Car firstCar = carRepository.create(createCar("car-1", "Roadster", "Red", 3));
        Car secondCar = carRepository.create(createCar("car-2", "SUV", "Black", 5));

        Iterator<Car> carIterator = carRepository.findAll();
        List<Car> allCars = new ArrayList<>();
        carIterator.forEachRemaining(allCars::add);

        assertEquals(2, allCars.size());
        assertEquals(firstCar, allCars.get(0));
        assertEquals(secondCar, allCars.get(1));
    }

    @Test
    void testFindByIdReturnsNullWhenMissing() {
        assertNull(carRepository.findById("missing-car"));
    }

    @Test
    void testUpdateExistingCarUpdatesFields() {
        carRepository.create(createCar("car-1", "Roadster", "Red", 3));
        Car updatedCar = createCar("car-1", "SUV", "Blue", 7);

        Car result = carRepository.update("car-1", updatedCar);

        assertNotNull(result);
        assertEquals("car-1", result.getCarId());
        assertEquals("SUV", result.getCarName());
        assertEquals("Blue", result.getCarColor());
        assertEquals(7, result.getCarQuantity());
    }

    @Test
    void testUpdateExistingCarAfterSkippingNonMatchingEntry() {
        carRepository.create(createCar("car-1", "Roadster", "Red", 3));
        carRepository.create(createCar("car-2", "Sedan", "White", 4));
        Car updatedCar = createCar("car-2", "SUV", "Blue", 7);

        Car result = carRepository.update("car-2", updatedCar);

        assertNotNull(result);
        assertEquals("car-2", result.getCarId());
        assertEquals("SUV", result.getCarName());
        assertEquals("Blue", result.getCarColor());
        assertEquals(7, result.getCarQuantity());
    }

    @Test
    void testUpdateMissingCarReturnsNull() {
        Car updatedCar = createCar("missing-car", "SUV", "Blue", 7);

        Car result = carRepository.update("missing-car", updatedCar);

        assertNull(result);
    }

    @Test
    void testDeleteExistingCarRemovesOnlyMatchingCar() {
        carRepository.create(createCar("car-1", "Roadster", "Red", 3));
        carRepository.create(createCar("car-2", "SUV", "Black", 5));

        carRepository.delete("car-1");

        List<Car> allCars = new ArrayList<>();
        carRepository.findAll().forEachRemaining(allCars::add);
        assertNull(carRepository.findById("car-1"));
        assertEquals(1, allCars.size());
        assertEquals("car-2", allCars.get(0).getCarId());
    }

    @Test
    void testDeleteMissingCarDoesNothing() {
        carRepository.create(createCar("car-1", "Roadster", "Red", 3));

        carRepository.delete("missing-car");

        List<Car> allCars = new ArrayList<>();
        carRepository.findAll().forEachRemaining(allCars::add);
        assertEquals(1, allCars.size());
        assertTrue(allCars.stream().anyMatch(car -> "car-1".equals(car.getCarId())));
    }

    private Car createCar(String carId, String carName, String carColor, int carQuantity) {
        Car car = new Car();
        car.setCarId(carId);
        car.setCarName(carName);
        car.setCarColor(carColor);
        car.setCarQuantity(carQuantity);
        return car;
    }
}
