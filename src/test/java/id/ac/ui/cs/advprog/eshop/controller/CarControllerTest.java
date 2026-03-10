package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarControllerTest {

    @InjectMocks
    private CarController carController;

    @Mock
    private CarService carService;

    @Test
    void testCreateCarPage() {
        Model model = new ExtendedModelMap();

        String viewName = carController.createCarPage(model);

        assertEquals("createCar", viewName);
        assertInstanceOf(Car.class, model.getAttribute("car"));
    }

    @Test
    void testCreateCarPost() {
        Car car = createCar("car-1", "Roadster", "Red", 3);

        String viewName = carController.createCarPost(car);

        assertEquals("redirect:listCar", viewName);
        verify(carService).create(car);
    }

    @Test
    void testCarListPage() {
        Car firstCar = createCar("car-1", "Roadster", "Red", 3);
        Car secondCar = createCar("car-2", "SUV", "Black", 5);
        when(carService.findAll()).thenReturn(List.of(firstCar, secondCar));
        Model model = new ExtendedModelMap();

        String viewName = carController.carListPage(model);

        assertEquals("carList", viewName);
        assertEquals(List.of(firstCar, secondCar), model.getAttribute("cars"));
    }

    @Test
    void testEditCarPage() {
        Car car = createCar("car-1", "Roadster", "Red", 3);
        when(carService.findById("car-1")).thenReturn(car);
        Model model = new ExtendedModelMap();

        String viewName = carController.editCarPage("car-1", model);

        assertEquals("editCar", viewName);
        assertSame(car, model.getAttribute("car"));
    }

    @Test
    void testEditCarPost() {
        Car car = createCar("car-1", "SUV", "Blue", 7);

        String viewName = carController.editCarPost(car);

        assertEquals("redirect:listCar", viewName);
        verify(carService).update("car-1", car);
    }

    @Test
    void testDeleteCar() {
        String viewName = carController.deleteCar("car-1");

        assertEquals("redirect:listCar", viewName);
        verify(carService).deleteCarById("car-1");
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
