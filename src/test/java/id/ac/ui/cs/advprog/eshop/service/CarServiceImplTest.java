package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarReadRepository;
import id.ac.ui.cs.advprog.eshop.repository.CarWriteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @InjectMocks
    private CarServiceImpl carService;

    @Mock
    private CarWriteRepository carWriteRepository;

    @Mock
    private CarReadRepository carReadRepository;

    @Test
    void testCreateDelegatesToWriteRepository() {
        Car car = createCar("car-1", "Roadster", "Red", 3);
        when(carWriteRepository.create(car)).thenReturn(car);

        Car result = carService.create(car);

        assertSame(car, result);
        verify(carWriteRepository).create(car);
    }

    @Test
    void testFindAllReturnsCarsFromIterator() {
        Car firstCar = createCar("car-1", "Roadster", "Red", 3);
        Car secondCar = createCar("car-2", "SUV", "Black", 5);
        when(carReadRepository.findAll()).thenReturn(List.of(firstCar, secondCar).iterator());

        List<Car> result = carService.findAll();

        assertEquals(2, result.size());
        assertEquals(firstCar, result.get(0));
        assertEquals(secondCar, result.get(1));
    }

    @Test
    void testFindByIdDelegatesToReadRepository() {
        Car car = createCar("car-1", "Roadster", "Red", 3);
        when(carReadRepository.findById("car-1")).thenReturn(car);

        Car result = carService.findById("car-1");

        assertSame(car, result);
    }

    @Test
    void testUpdateDelegatesToWriteRepository() {
        Car car = createCar("car-1", "SUV", "Blue", 7);

        carService.update("car-1", car);

        verify(carWriteRepository).update("car-1", car);
    }

    @Test
    void testDeleteDelegatesToWriteRepository() {
        carService.deleteCarById("car-1");

        verify(carWriteRepository).delete("car-1");
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
